package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;

import no.knowit.util.ReflectionUtils;

import org.apache.log4j.Logger;

/**
 * @author LeifOO
 * @author org.crank.crud
 */
public class CrudServiceUtils {
  private static Logger log = Logger.getLogger(CrudServiceUtils.class);

  public static final List<String> OBJECT_PRIMITIVES = Arrays.asList(
    "java.lang.String",    "java.lang.Boolean",  "java.lang.Byte",
    "java.lang.Character", "java.lang.Double",   "java.lang.Float",
    "java.lang.Integer",   "java.lang.Long",     "java.lang.Number",
    "java.lang.Short",     "java.util.Currency", "java.util.Date",
    "java.sql.Date",       "java.sql.Time",      "java.sql.Timestamp" );

  /**
   * Checks if a class is an entity
   * @param entityClass the entity class to check for @Entity annotation
   * @return true if the class is annotated with @Entity
   */
  public static boolean isEntity(final Class<?> entityClass) {
    if(entityClass == null) return false;
    return entityClass.isAnnotationPresent(Entity.class) ? true : false;
  }

  /**
   * Get entity name.
   * Copy from org.crank.crud.GenericDaoUtils
   * @return the entity name
   */
  public static String getEntityName(final Class<?> entityClass) {
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    Entity entity = entityClass.getAnnotation(Entity.class);
    if(entity == null) {
      throw new IllegalArgumentException(
          "The entityClass parameter is not a valid entity class. " +
          "An entity should be annotated with the @Entity annotation");
    }
    String entityName = entity.name();
    return entityName == null || entityName.length() < 1 ? entityClass.getSimpleName() : entityName;
  }

  /**
   * Get members (fields or methods) annotated with @Id
   * @param entityClass
   * @return A list of attributes annotated with @Id 
   */
  public static List<Member> getIdAnnotations(final Class<?> entityClass) {
    return ReflectionUtils.searchMembersForAnnotation(Id.class, entityClass);
  }

  /**
   * Get entity identity based on field(s) or method(s) annotated with @Id 
   * @param entity
   * @return
   */
  public static List<Object> getIdValues(final Object entity) {
    if(entity == null) {
      throw new IllegalArgumentException("The entity parameter can not be null");
    }
    List<Member> id = getIdAnnotations(entity.getClass());
    if(id.size() < 1) {
      throw new IllegalArgumentException(
          "Could not get identity. No @Id annotation found on class: " + entity.getClass());
    }
    List<Object> result = new ArrayList<Object>(id.size());
    for (Member member : id) {
      result.add(ReflectionUtils.getAttributeValue(member, entity));
    }
    return result;
  }

  
  
  //----------------------
  // rest is untested code
  //----------------------
  public static Map<String, Member> getQueryableAttributes(final Class<?> entityClass) {
    
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    Map<String, Member> attributes = new HashMap<String, Member>();
    Member id = ReflectionUtils.searcFieldsForFirstAnnotation(Id.class, entityClass);
    if(id != null) {
      attributes.putAll(getQueryableFields(entityClass));
    }
    else {
      id = ReflectionUtils.searcMethodsForFirstAnnotation(Id.class, entityClass);
      if(id != null) {
        attributes.putAll(getQueryableProperties(entityClass));
      }
    }
    return attributes;
  }
  
  public static Map<String, Field> getQueryableFields(final Class<?> entityClass) {
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    Map<String, Field> fields = new HashMap<String, Field>();
    
    for (Class<?> clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
      // If class is abstract then we should ignore all fields
      if(!Modifier.isAbstract(clazz.getModifiers())) {
        for ( Field field : clazz.getDeclaredFields() ) {
          if(!ignore(field)) {
            fields.put(field.getName(), field);
          }
        }
      }
    }
    log.debug("Queryable attributes, fields: " + fields.keySet());
    return fields;
  }

  public static Map<String, Method> getQueryableProperties(final Class<?> entityClass) {
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    
    Map<String, Method> methods = new HashMap<String, Method>();
    Map<String, Method> getters = new HashMap<String, Method>();
    Map<String, Method> setters = new HashMap<String, Method>();
    
    for (Class<?> clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
      // If class is abstract then we should ignore all fields
      if(!Modifier.isAbstract(clazz.getModifiers())) {
        for(Method method : clazz.getDeclaredMethods()) {
          String propertyName = ReflectionUtils.getPropertyName(method);
          
          if(propertyName != null) {
            
            if(ignore(method) || propertyName.equals("class")) {
              getters.remove(propertyName);
              setters.remove(propertyName);
            }
            else {
              String methodName = method.getName();
              if(methodName.startsWith("get") || methodName.startsWith("is")) {
                if(!getters.containsKey(propertyName)) 
                  getters.put(propertyName, method);
              }
              else {
                if(!setters.containsKey(propertyName)) 
                  setters.put(propertyName, method);
              }
            }
          }
        }
      }
    }
    methods.putAll(getters);
    methods.putAll(setters);

    log.debug("Queryable attributes, methods: " + methods.keySet());
    return methods;    
  }
  
  /**
   * Creates a parameterized SELECT or DELETE JPQL query based on non null
   * field values in the <code>exampleEntity</code> parameter, 
   * exclusive transient and static values.
   * 
   * @return The created JPQL string
   */
  public static String createExampleJPQL(final Object exampleEntity, boolean select, 
      boolean distinct, boolean any) throws Exception {

    if(exampleEntity == null)
      throw new IllegalStateException("exampleEntity parameter can not be null.");
    
    if(!isEntity(exampleEntity.getClass()))
      throw new IllegalStateException("exampleEntity parameter must be an @Entity.");
    
    Map<String, Member> fields = getQueryableAttributes(exampleEntity.getClass());
    //return createJPQL(exampleEntity, fields, select, distinct, any);
    return null;
  }
  
  /**
   * Creates a parameterized SELECT or DELETE JPQL query based on non null
   * property values, exclusive transient and static values, in the <code>fields</code> parameter
   * 
   * @return The created JPQL string
   */
  private static String createJPQL(final Object exampleEntity, final Map<String, Field> fields, 
      boolean select, boolean distinct, boolean any) throws Exception {

    String entityClassName = exampleEntity.getClass().getName();

    final StringBuilder jpql = new StringBuilder((select ? String.format(
        "SELECT %s e", (distinct ? "DISTINCT" : "")) : "DELETE")
      )
      .append(String.format(" FROM %s e", entityClassName));
    
    final StringBuilder debugData = new StringBuilder();

    
    Set<Entry<String, Field>> properties = fields.entrySet();
    boolean where = false;
    String operator = (any ? "OR" : "AND");
    int n = 1;
    
    for (Entry<String, Field> entry : properties) {
      String propertyName = entry.getKey();
      Field field = entry.getValue();
      Object value = field != null ? ReflectionUtils.getFieldValue(field, exampleEntity) : null;
      
      if (value != null) {
        Class<?> type = value.getClass();
        if (type != null) {
          int k = OBJECT_PRIMITIVES.indexOf(type.getName());
          if (type.isPrimitive() || k > -1) {
            
            String equals = (k == 0 ? (field.toString().indexOf('%') > -1 ? "LIKE" : "=") : "="); // 0 == java.lang.String
            if (!where) {
              where = true;
              jpql.append(String.format(" where e.%s %s ?%d", propertyName, equals, n));
            } else {
              jpql.append(String.format(" %s e.%s %s ?%d", operator, propertyName, equals, n));
            }
            if(log.isDebugEnabled()) {
              debugData.append("\n\t[?" + n + " = " + field + ']');
            }
            n++;
          }
        }
      }
    }
    if(log.isDebugEnabled()) {
      debugData.insert(0, "createJPQL, jpql = \n\t[" + jpql.toString() + "]");
      log.debug(debugData);
    }
    return jpql.toString();
  }

  /**
   * Copied/Modified from org.jboss.seam.persistence.ManagedEntityWrapper
   */
  private static boolean ignore(Field field) {
    return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
        || field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Version.class);
  }

  /**
   * Modified from org.jboss.seam.persistence.ManagedEntityWrapper
   */
  private static boolean ignore(Method method) {
    return Modifier.isTransient(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
        || method.isAnnotationPresent(Transient.class) || method.isAnnotationPresent(Version.class);
  }
}
