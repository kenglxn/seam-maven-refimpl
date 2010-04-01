package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
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
    String entityName = null;
    if(entityClass != null) {
      Entity entity = entityClass.getAnnotation(Entity.class);
      if(entity == null) {
        entityName = entityClass.getSimpleName();
      }
      else {
        entityName = entity.name();
        if(entityName == null || entityName.length() < 1) {
          entityName = entityClass.getSimpleName();
        }
      }
    }
    return entityName;
  }

  /**
   * Get members (fields or methods) annotated with @Id
   * @param entityClass
   * @return A list of attributes annotated with @Id 
   */
  public static List<Member> getIdentity(final Class<?> entityClass) {
    return ReflectionUtils.searchMembersForAnnotation(Id.class, entityClass);
  }
  
  /*
  public static String getIdentityPropertyName(final Class<?> clazz) {
    String idPropertyName = searchFieldsForId(clazz);
    if (idPropertyName == null) {
      idPropertyName = searchMethodsForId(clazz);
    }
    return idPropertyName;
  }
  */
  
  /*
  public static boolean hasIdentity(final Object entity) {
    Object id = getIdentityValue(entity);
    if(id == null) return false;
    return id instanceof Number && ((Number)id).longValue() >= 0 ? true : false;
  }

  public static Object getIdentityValue(final Object entity) {
    if(entity == null) return null;
    String identityName = getIdentityPropertyName(entity.getClass());
    
    
    BeanMap beanMap = new BeanMap(entity);
    return beanMap.get(identityName);
  }
  */
  

  public static Map<String, Field> getQueryableFields(final Class<?> entityClass) {
    Map<String, Field> fields = new HashMap<String, Field>();

    // TODO: check if class i transient, i.e. abstract
    Class<?> clazz = entityClass;
    for ( ; clazz != Object.class; clazz = clazz.getSuperclass()) {
      for ( Field field : clazz.getDeclaredFields() ) {
        if(!ignore(field)) {
          fields.put(field.getName(), field);
        }
      }
    }
    
    // Must run i separate loop
    clazz = entityClass;
    for ( ; clazz != Object.class; clazz = clazz.getSuperclass()) {
      for( Method method : clazz.getDeclaredMethods()) {
        if( ignore(method)) {
          String fieldName = method.getName().substring(4);
          fieldName = method.getName().substring(3, 4).toLowerCase() + fieldName;
          fields.remove(fieldName);
        }
      }
    }
    return fields;
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
    
    Map<String, Field> fields = getQueryableFields(exampleEntity.getClass());
    return createJPQL(exampleEntity, fields, select, distinct, any);
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
      Object value = field != null ? getFieldValue(field, exampleEntity) : null;
      
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
  
  /**
   * Copied/Modified from org.jboss.seam.util.Reflections
   */
  private static Object getFieldValue(Field field, Object target) throws Exception {
    boolean accessible = field.isAccessible();
    try {
      field.setAccessible(true);
      return field.get(target);
    } 
    catch (IllegalArgumentException iae) {
      String message = "Could not get field value by reflection: " + field.getName() + " on: "
          + target.getClass().getName();
      throw new IllegalArgumentException(message, iae);
    } 
    finally {
      field.setAccessible(accessible);
    }
  }

}
