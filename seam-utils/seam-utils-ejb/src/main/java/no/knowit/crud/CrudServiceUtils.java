package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
   * Checks if an entity class is annotated with @Id
   * @param entityClass The class to check for @Id annotation
   * @return true if one or more attributes (field or method) has @Id annotation
   */
  public static boolean hasId(final Class<?> entityClass) {
    return ReflectionUtils.searchMembersForFirstAnnotation(Id.class, entityClass) != null;
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

  public static Map<String, Member> findQueryableAttributes(final Class<?> entityClass) {
    
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    Map<String, Member> attributes = new HashMap<String, Member>();
    Member id = ReflectionUtils.searcFieldsForFirstAnnotation(Id.class, entityClass);
    if(id != null) {
      attributes.putAll(findQueryableFields(entityClass));
    }
    else {
      id = ReflectionUtils.searcMethodsForFirstAnnotation(Id.class, entityClass);
      if(id != null) {
        attributes.putAll(findQueryableProperties(entityClass));
      }
    }
    return attributes;
  }
  
  public static Map<String, Field> findQueryableFields(final Class<?> entityClass) {
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    Map<String, Field> fields = new HashMap<String, Field>();
    
    for (Class<?> clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
      if(!ignore(clazz)) {
        for ( Field field : clazz.getDeclaredFields() ) {
          if(!ignore(field)) {
            fields.put(field.getName(), field);
          }
        }
      }
    }
    log.debug(entityClass.getSimpleName() +  " class, queryable fields: " + fields.keySet());
    return fields;
  }

  public static Map<String, Method> findQueryableProperties(final Class<?> entityClass) {
    if(entityClass == null) {
      throw new IllegalArgumentException("The entityClass parameter can not be null");
    }
    
    Map<String, Method> methods = new HashMap<String, Method>();
    Map<String, Method> getters = new HashMap<String, Method>();
    Map<String, Method> setters = new HashMap<String, Method>();
    
    for (Class<?> clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
      if(!ignore(clazz)) {
        for(Method method : clazz.getDeclaredMethods()) {
          String propertyName = ReflectionUtils.getPropertyName(method);
          
          if(propertyName != null) {
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
    
    methods.putAll(setters);
    methods.putAll(getters);  // getters are more important than setters - so add them last
                              // TODO: Keep both setters and getters
    
    Set<Entry<String, Method>> properties = getters.entrySet();
    for (Entry<String, Method> entry : properties) {
      String propertyName = entry.getKey();
      Method method = entry.getValue();

      if(ignore(method)) {
        methods.remove(propertyName);
      }
    }

    properties = setters.entrySet();
    for (Entry<String, Method> entry : properties) {
      String propertyName = entry.getKey();
      Method method = entry.getValue();

      if(ignore(method)) {
        methods.remove(propertyName);
      }
    }
    
    log.debug(entityClass.getSimpleName() + " class, queryable methods: " + methods.keySet());
    return methods;    
  }

  public static Map<String, Member> reduceQueryableAttributesToPopulatedFields(
      final Object exampleEntity, final Map<String, Member> attributes) {
    
    Map<String, Member> populatedAttributes = new HashMap<String, Member>();
    Set<Entry<String, Member>> properties = attributes.entrySet();
    for (Entry<String, Member> entry : properties) {
      String propertyName = entry.getKey();
      Member member = entry.getValue();
      Object value = member != null ? ReflectionUtils.getAttributeValue(member, exampleEntity) : null;
      
      if (value != null) {
        Class<?> type = value.getClass();
        if (type != null) {
          int k = OBJECT_PRIMITIVES.indexOf(type.getName());
          if (type.isPrimitive() || k > -1) {
            populatedAttributes.put(propertyName, member);
          }
        }
      }
    }    
    return populatedAttributes;
  }
  
  /**
   * Creates a parameterized SELECT or DELETE JPQL query based on non null
   * field values in the <code>exampleEntity</code> parameter, 
   * exclusive transient and static values.
   * 
   * @return The created JPQL string
   */
  public static String createJpql(final Object exampleEntity, boolean select, 
      boolean distinct, boolean any) {

    if(exampleEntity == null)
      throw new IllegalStateException("exampleEntity parameter can not be null.");
    
    if(!isEntity(exampleEntity.getClass()))
      throw new IllegalStateException("exampleEntity parameter must be an @Entity.");
    
    Map<String, Member> attributes = findQueryableAttributes(exampleEntity.getClass());
    attributes = reduceQueryableAttributesToPopulatedFields(exampleEntity, attributes);
    return createJpql(exampleEntity, attributes, select, distinct, any);
  }

  
  private static String createJpql(final Object exampleEntity, final Map<String, Member> attributes, 
      boolean select, boolean distinct, boolean any) {

    final String entityClassName = exampleEntity.getClass().getName();
    final StringBuilder jpql = new StringBuilder((select ? distinct ?
      "SELECT DISTINCT e" : "SELECT e" : "DELETE")
    )
    .append(String.format(" FROM %s e", entityClassName));
    
    final StringBuilder debugData = new StringBuilder();

    // TODO: remove redundant code use reduceQueryableAttributesToPopulatedAttributes method
    Set<Entry<String, Member>> properties = attributes.entrySet();
    boolean where = false;
    String operator = (any ? "OR" : "AND");
    
    for (Entry<String, Member> entry : properties) {
      String propertyName = entry.getKey();
      Member member = entry.getValue();
      Object value = member != null ? ReflectionUtils.getAttributeValue(member, exampleEntity) : null;
      
      if (value != null) {
        Class<?> type = value.getClass();
        if (type != null) {
          int k = OBJECT_PRIMITIVES.indexOf(type.getName());
          if (type.isPrimitive() || k > -1) {
            
            String equals = (k == 0 ? (value.toString().indexOf('%') > -1 ? "LIKE" : "=") : "="); // 0 => java.lang.String
            if (!where) {
              where = true;
              jpql.append(String.format(" WHERE e.%s %s :%s", propertyName, equals, propertyName));
            } else {
              jpql.append(String.format(" %s e.%s %s :%s", operator, propertyName, equals, propertyName));
            }
            if(log.isDebugEnabled()) {
              debugData.append(String.format("[e.%s %s %s ] ", propertyName, equals, value));
            }
          }
        }
      }
    }
    if(log.isDebugEnabled()) {
      debugData.insert(0, "createJPQL, jpql = \n\t[" + jpql.toString() + "]\n\t");
      log.debug(debugData);
    }
    return jpql.toString();
  }

  /**
   * Copied/Modified from org.jboss.seam.persistence.ManagedEntityWrapper
   */
  private static boolean ignore(final Field field) {
    return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
        || Modifier.isNative(field.getModifiers())    || Modifier.isFinal(field.getModifiers())     
        || field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Version.class);
  }

  /**
   * Modified from org.jboss.seam.persistence.ManagedEntityWrapper
   * If you have a final field then you should'nt use get/set methods that field
   */
  private static boolean ignore(final Method method) {
    return Modifier.isTransient(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
        || Modifier.isNative(method.getModifiers())    || Modifier.isFinal(method.getModifiers())
        || method.isAnnotationPresent(Transient.class) || method.isAnnotationPresent(Version.class);
  }

  /**
   * If a class is not an annotated  @Entity or  @MappedSuperclass then  
   * the class is transient and we should ignore all fields in that class
   */
  private static boolean ignore(final Class<?> clazz) {
    return !(
      clazz.isAnnotationPresent(MappedSuperclass.class) || clazz.isAnnotationPresent(Entity.class)
    );
  }

}
