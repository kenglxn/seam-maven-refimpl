package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
import static no.knowit.util.ReflectionUtils.OBJECT_PRIMITIVES;  

import org.apache.log4j.Logger;

/**
 * @author LeifOO
 */
public class CrudServiceUtils {
  private final static String PARAM_NOT_NULL = "The \"%s\" parameter can not be null";
  private static Logger log = Logger.getLogger(CrudServiceUtils.class);

  /**
   * Checks if a class is an entity. A class annotated with @Entity is recognized as an entity class.
   * @param entityClass the entity class to check for @Entity annotation
   * @return true if the class is annotated with @Entity
   */
  public static boolean isEntity(final Class<?> entityClass) {
    if(entityClass == null) {
      return false;
    }
    return entityClass.isAnnotationPresent(Entity.class) ? true : false;
  }

  /**
   * Get entity name.
   * Copy from org.crank.crud.GenericDaoUtils
   * @return the entity name
   */
  public static String getEntityName(final Class<?> entityClass) {
    if(entityClass == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entityClass"));
    }
    Entity entity = entityClass.getAnnotation(Entity.class);
    if(entity == null) {
      throw new IllegalArgumentException(
          "The entityClass parameter is not a valid entity class. " +
          "An entity should be annotated with @Entity");
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
   * Get entity identity based on field(s) or method(s) annotated with the @Id annotation 
   * @param entity
   * @return
   */
  public static List<Object> getIdValues(final Object entity) {
    if(entity == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entity"));
    }
    List<Member> id = getIdAnnotations(entity.getClass());
    if(id.size() < 1) {
      throw new IllegalArgumentException(
          "Could not get identity. No @Id annotation found on class: " + entity.getClass());
    }
    List<Object> result = new ArrayList<Object>(id.size());
    for (Member member : id) {
      result.add(ReflectionUtils.get(member, entity));
    }
    return result;
  }

  /**
   * Find attributes that can be used in JPQL.
   * @param entityClass the class to search for queryable attributes; fields or set/get methods 
   * dependent on annotation. The method also searches trough the inheritance hierarchy for 
   * queryable attributes. 
   * @return a map with attributes to use in JPQL
   */
  public static Map<String, Member> findQueryableAttributes(final Class<?> entityClass) {
    
    if(entityClass == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entityClass"));
    }
    Map<String, Member> attributes = new HashMap<String, Member>();
    Member id = ReflectionUtils.searcFieldsForFirstAnnotation(Id.class, entityClass);
    if(id != null) {
      // This entity (and all other entities in the inheritance tree) uses field annotation
      attributes.putAll(findQueryableFields(entityClass));
    }
    else {
      // Property annotation (setter/getter annotated)
      id = ReflectionUtils.searcMethodsForFirstAnnotation(Id.class, entityClass);
      if(id != null) {
        attributes.putAll(findQueryableProperties(entityClass));
      }
    }
    return attributes;
  }

  /**
   * Inspects an entity instance and removes attributes from the map where the entity's 
   * corresponding field is null.
   * @param exampleEntity
   * @param attributes
   * @return
   */
  public static Map<String, Member> reduceQueryableAttributesToPopulatedFields(
      final Object exampleEntity, final Map<String, Member> attributes) {
    
    Map<String, Member> populatedAttributes = new HashMap<String, Member>();
    for (Entry<String, Member> entry : attributes.entrySet()) {
      String propertyName = entry.getKey();
      Member member = entry.getValue();
      Object value = member != null ? ReflectionUtils.get(member, exampleEntity) : null;
      
      if (value != null) {
        Class<?> type = value.getClass();
        if (type != null) {
          // TODO: Should be possible to query embedded classes, arrays, collections and maps
          if (type.isPrimitive() || type.isEnum() || OBJECT_PRIMITIVES.indexOf(type) > -1) {
            populatedAttributes.put(propertyName, member);
          }
        }
      }
    }    
    return populatedAttributes;
  }

  /**
   * Creates a parameterized SELECT or DELETE JPQL query based on non null
   * attribute values in the <code>exampleEntity</code> parameter. 
   * 
   * @param exampleEntity the entity instance to use to create the query
   * @param select if the value is true, a select query will be generated.
   * @param distinct if the value is true, a select distinct query will be generated
   * @param any if the value is true, attributes used in the <code>where</code> clause will be "or'ed"
   * @return The created JPQL string
   */
  public static String createJpql(final Object exampleEntity, boolean select, 
      boolean distinct, boolean any) {

    if(exampleEntity == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "exampleEntity"));
    }
    
    if(!isEntity(exampleEntity.getClass())) {
      throw new IllegalStateException("The \"exampleEntity\" parameter must be an @Entity.");
    }
    
    Map<String, Member> attributes = findQueryableAttributes(exampleEntity.getClass());
    attributes = reduceQueryableAttributesToPopulatedFields(exampleEntity, attributes);
    return createJpql(exampleEntity, attributes, select, distinct, any);
  }

  /**
   * Creates a parameterized SELECT or DELETE JPQL query based on 
   * key/value pairs in the <code>attributes</code> map. 
   * 
   * @param exampleEntity thenentity instance to use to create the query
   * @param attributes a map with attributes that should be used to generate the <code>where</code>
   *        clause. Attrubute must correspond with the entity's attributes. 
   * @param select if the value is true, a select query will be generated.
   * @param distinct if the value is true, a select distinct query will be generated
   * @param any if the value is true, attributes used in the query will be "ored"
   * @return The created JPQL string
   */
  public static String createJpql(final Object exampleEntity, final Map<String, Member> attributes, 
      boolean select, boolean distinct, boolean any) {

    final String entityClassName = exampleEntity.getClass().getName();
    final StringBuilder jpql = new StringBuilder(
      (select ? distinct ? "SELECT DISTINCT e" : "SELECT e" : "DELETE"))
      .append(String.format(" FROM %s e", entityClassName));
    
    final StringBuilder debugData = new StringBuilder();

    // TODO: remove redundant code use reduceQueryableAttributesToPopulatedAttributes method
    boolean where = false;
    String operator = (any ? "OR" : "AND");
    
    Set<Entry<String, Member>> properties = attributes.entrySet();
    for (Entry<String, Member> entry : properties) {
      String propertyName = entry.getKey();
      Member member = entry.getValue();
      Object value = member != null ? ReflectionUtils.get(member, exampleEntity) : null;
      
      if (value != null) {
        Class<?> type = value.getClass();
        if (type != null) {
          int k = OBJECT_PRIMITIVES.indexOf(type);
          if (type.isPrimitive() || type.isEnum() || k > -1) {
            String equals = (k == 0 ? (value.toString().indexOf('%') > -1 ? "LIKE" : "=") : "="); // k=0 => java.lang.String
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

  private static Map<String, Field> findQueryableFields(final Class<?> entityClass) {
    Map<String, Field> fields = new HashMap<String, Field>();
    for (Class<?> clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
      if(!ignore(clazz)) {
        for (Field field : clazz.getDeclaredFields()) {
          if(!ignore(field)) {
            fields.put(field.getName(), field);
          }
        }
      }
    }
    log.debug(entityClass.getSimpleName() +  " class, queryable fields: " + fields.keySet());
    return fields;
  }

  private static Map<String, Method> findQueryableProperties(final Class<?> entityClass) {
    final Map<String, Method> methods = new HashMap<String, Method>();
    final Map<String, Method> getters = new HashMap<String, Method>();
    final Map<String, Method> setters = new HashMap<String, Method>();
    
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
    
    for (Entry<String, Method> entry : getters.entrySet()) {
      String propertyName = entry.getKey();
      Method method = entry.getValue();

      if(ignore(method)) {
        methods.remove(propertyName);
      }
    }

    for (Entry<String, Method> entry : setters.entrySet()) {
      String propertyName = entry.getKey();
      Method method = entry.getValue();

      if(ignore(method)) {
        methods.remove(propertyName);
      }
    }
    
    log.debug(entityClass.getSimpleName() + " class, queryable methods: " + methods.keySet());
    return methods;    
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
   * If you have a final field then you should'nt use get/set methods for that field
   */
  private static boolean ignore(final Method method) {
    return Modifier.isTransient(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
        || Modifier.isNative(method.getModifiers())    || Modifier.isFinal(method.getModifiers())
        || method.isAnnotationPresent(Transient.class) || method.isAnnotationPresent(Version.class);
  }

  /**
   * If a class is not an annotated  with @Entity or  @MappedSuperclass then  
   * the class is transient and we should ignore all fields in that class, 
   * given that the class is a part of an entity's inheritance hierarchy
   */
  private static boolean ignore(final Class<?> clazz) {
    return !(
      clazz.isAnnotationPresent(MappedSuperclass.class) || clazz.isAnnotationPresent(Entity.class)
    );
  }
}
