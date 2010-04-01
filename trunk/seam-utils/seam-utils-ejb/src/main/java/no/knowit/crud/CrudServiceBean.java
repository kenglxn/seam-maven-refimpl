/**
This file is part of javaee-patterns.

javaee-patterns is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

javaee-patterns is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.opensource.org/licenses/gpl-2.0.php>.

 * Copyright (c) 22. June 2009 Adam Bien, blog.adam-bien.com
 * http://press.adam-bien.com
 * 
 * Modified by Leif Olsen
 *   Added a lot of code from Crank, the Java Framework for CRUD and Validation: http://code.google.com/p/krank/
 *   Actually added some code of my own :-)
 */
package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;

/**
 * An implementation of the generic CrudService.<br/>
 * 
 * @author adam-bien.com
 * @author Leif Olsen
 */
@Stateless(name = CrudService.NAME)
public class CrudServiceBean implements CrudService {

	//protected Logger log = Logger.getLogger(this.getClass());
	protected static Logger log = Logger.getLogger(CrudServiceBean.class);

	@PersistenceContext
	EntityManager entityManager;

	// 'C'
	public <T> T persist(T entity) {
		getEntityManager().persist(entity);
		getEntityManager().flush();
		getEntityManager().refresh(entity);
		return entity;
	}

	public <T> Collection<T> persist(Collection<T> entities) {
		if(entities != null) {
	    Collection<T> persistedResults = new ArrayList<T>(entities.size());
  		for (T entity : entities) {
  			persistedResults.add(persist(entity));
  		}
      return persistedResults;
		}
		return null;
	}

	// 'R'
	public <T> T find(Class<T> entityClass, Object id) {
		return (T) getEntityManager().find(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass) {
		assert entityClass != null : "The 'entityClass' parameter can not be null"; //TODO: throw IllegalArgumentException
		return getEntityManager().createQuery("from " + entityClass.getName()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass, int startPosition, int maxResult) {
		assert entityClass != null : "The 'entityClass' parameter can not be null"; //TODO: throw IllegalArgumentException
		
		return getEntityManager().createQuery("from " + entityClass.getName())
			.setFirstResult(startPosition).setMaxResults(maxResult).getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(T example, boolean distinct, boolean any) {
		Query query = createExampleQuery(example, true, distinct, any);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(T example, boolean distinct, boolean any,	int startPosition, int maxResult) {
		Query query = createExampleQuery(example, true, distinct, any);
		return query.setFirstResult(startPosition).setMaxResults(maxResult).getResultList();
	}

	// 'U'
	public <T> T merge(T entity) {
		return (T) getEntityManager().merge(entity);
	}

	public <T> Collection<T> merge(Collection<T> entities) {
		assert entities != null : "The 'entities' parameter can not be null"; //TODO: throw IllegalArgumentException 
		Collection<T> mergedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			mergedResults.add(merge(entity));
		}
		return mergedResults;
	}

	// 'D'
	public void remove(final Object entity) {
		EntityManager em = getEntityManager();
		Object managedEntity = em.contains(entity) ? entity : em.merge(entity);
		em.remove(managedEntity);
	}

	public void remove(Class<?> entityClass, Object id) {
		Object ref = getEntityManager().getReference(entityClass, id);
		getEntityManager().remove(ref);
	}

	public void remove(Collection<Object> entities) {
		assert entities != null : "The 'entities' parameter can not be null"; //TODO: throw IllegalArgumentException
		for (Object entity : entities) {
			remove(entity);
		}
	}

	public void remove(final Object example, boolean any) {
		Query query = createExampleQuery(example, false, false, any);
		query.executeUpdate();
	}

	// C or U :-)
	public <T> T store(T entity) {
		assert entity != null : "The 'entity' parameter can not be null"; //TODO: throw IllegalArgumentException

		Object id = getIdentity(entity);
		if (!log.isDebugEnabled()) {
			if (id != null) {
				return find(entity.getClass(), id) == null ? persist(entity) : merge(entity);
			} 
			else {
				return persist(entity);
			}
		} 
		else {
			if (id != null) {
				if (find(entity.getClass(), id) == null) {
					T e = persist(entity);
					log.debug("CrudService.store: persisted new entity, got id: "	+ getIdentity(e));
					return e;
				} else {
					log.debug("CrudService.store: merge existing entity with id: " + id);
					return merge(entity);
				}
			} 
			else {
				T e = persist(entity);
				log.debug("CrudService.store: persisted new entity, got id: "	+ getIdentity(e));
				return e;
			}
		}
	}

	public <T> Collection<T> store(Collection<T> entities) {
		assert entities != null : "The 'entities' parameter can not be null"; //TODO: throw IllegalArgumentException
		Collection<T> storedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			storedResults.add(store(entity));
		}
		return storedResults;
	}

	public <T> T refresh(final T transientEntity) {
		EntityManager em = getEntityManager();
		T managedEntity = em.contains(transientEntity) ? transientEntity : em.merge(transientEntity);
		em.refresh(managedEntity);
		return managedEntity;
	}

	public <T> T refresh(Class<T> entityClass, Object id) {
		EntityManager em = getEntityManager();
		T managedEntity = em.find(entityClass, id);
		em.refresh(managedEntity);
		return managedEntity;
	}

	public <T> Collection<T> refresh(Collection<T> entities) {
		assert entities != null : "The 'entities' parameter can not be null"; //TODO: throw IllegalArgumentException
		Collection<T> refreshedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			refreshedResults.add(refresh(entity));
		}
		return refreshedResults;
	}

	public boolean isManaged(Object entity) {
		return entity != null && getEntityManager().contains(entity);
	}

	public void flush() {
		getEntityManager().flush();
	}

	public void clear() {
		getEntityManager().clear();
	}

	public void flushAndClear() {
		EntityManager em = getEntityManager();
		em.flush();
		em.clear();
	}

	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName) {
		return this.entityManager.createNamedQuery(namedQueryName).getResultList();
	}

	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		return findWithNamedQuery(namedQueryName, parameters, 0);
	}

	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String queryName, int resultLimit) {
		return this.entityManager.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
	}

	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
		Set<Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.entityManager.createNamedQuery(namedQueryName);
		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}
		for (Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(String sql, Class<T> type) {
		return this.entityManager.createNativeQuery(sql, type).getResultList();
	}

	/**
	 * @return the entitymanager wired to this service
	 * @throws java.lang.IllegalStateException
	 *           if EntityManager has not been set on this service before usage
	 */
	protected EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("EntityManager has not been set on service before usage");
		}
		return entityManager;
	}

	/**
	 * This is a simple form of query by example. Produces a SELECT or DELETE
	 * query based on non null property values in the <code>example</code>
	 * parameter
	 * 
	 * The limitations are:
	 * <ul>
	 * <li>Only one @Id annotation</li>
	 * <li>We can not e.g. handle arrays and object references while generating JPQL</li>
	 * </ul>
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected Query createExampleQuery(final Object example, boolean select, boolean distinct, boolean any) {
		assert example != null : "The 'example' parameter can not be null"; //TODO: throw IllegalArgumentException

		BeanMap beanMap = new BeanMap(example); // Map<String, Object> beanMap = new BeanMap(example);
		
		String jpql = createJPQL(example.getClass().getName(), beanMap, select, distinct, any);
		
		Query query = getEntityManager().createQuery(jpql);
		
		Set<Entry<String, Object>> properties = beanMap.entrySet();
		int n = 1;
		for (Entry<String, Object> entry : properties) {
			Object value = entry.getValue();
			if (value != null) {
				Class<?> type = value.getClass();
				if (type != null && (type.isPrimitive() || OBJECT_PRIMITIVES.indexOf(type.getName()) > -1)) {
					query.setParameter(n++, value);
				}
			}
		}
		return query;
	}

	// ------------------------------------
	// Utility methods
	// TODO: move to separate package/class
	// ------------------------------------
	private static final List<String> OBJECT_PRIMITIVES = Arrays.asList(
			"java.lang.String",    "java.lang.Boolean",  "java.lang.Byte",
			"java.lang.Character", "java.lang.Double",   "java.lang.Float",
			"java.lang.Integer",   "java.lang.Long",     "java.lang.Number",
			"java.lang.Short",     "java.util.Currency", "java.util.Date",
			"java.sql.Date",       "java.sql.Time",      "java.sql.Timestamp" );

	protected static boolean hasIdentity(final Object entity) {
		return getIdentity(entity) != null ? true : false;
	}

	protected static Object getIdentity(final Object entity) {
		String identityName = getIdentityPropertyName(entity.getClass());
		BeanMap beanMap = new BeanMap(entity);
		return beanMap.get(identityName);
	}

	protected static String getIdentityPropertyName(final Class<?> clazz) {
		String idPropertyName = searchFieldsForIndentity(clazz);
		if (idPropertyName == null) {
			idPropertyName = searchMethodsForIdentity(clazz);
		}
		return idPropertyName != null ? idPropertyName : "id";
	}

	/**
	 * Copy from org.crank.crud.GenericDaoUtils
	 */
	protected static String searchFieldsForIndentity(final Class<?> clazz) {
		String pkName = null;
		for (Field field : clazz.getDeclaredFields()) {
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				pkName = field.getName();
				break;
			}
		}
		if (pkName == null && clazz.getSuperclass() != null) {
			pkName = searchFieldsForIndentity((Class<?>) clazz.getSuperclass());
		}
		return pkName;
	}

  /**
   * Copy from org.crank.crud.GenericDaoUtils
   */
	protected static String searchMethodsForIdentity(final Class<?> clazz) {
		String pkName = null;
		for (Method method : clazz.getDeclaredMethods()) {
			Id id = method.getAnnotation(Id.class);
			if (id != null) {
				pkName = method.getName().substring(4);
				pkName = method.getName().substring(3, 4).toLowerCase() + pkName;
				break;
			}
		}
		if (pkName == null && clazz.getSuperclass() != null) {
			pkName = searchMethodsForIdentity(clazz.getSuperclass());
		}
		return pkName;
	}
	
	protected static Map<String, Field> fieldsForQuery(final Object example) {
	  Map<String, Field> fields = new HashMap<String, Field>(); //new BeanMap(example);
	  
	  Class<?> clazz = example.getClass();
	  for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
	    for ( Field field : clazz.getDeclaredFields() ) {
	      if(!ignore(field)) {
	        fields.put(field.getName(), field);
	      }
	    }
	  }

	  // Must run i separate loop
    clazz = example.getClass();
    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
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
	 * Copied/Modified from org.jboss.seam.persistence.ManagedEntityWrapper
	 */
  protected static boolean ignore(Field field) {
    return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
        || field.isAnnotationPresent(Transient.class);
  }

  /**
   * Modified from org.jboss.seam.persistence.ManagedEntityWrapper
   */
  protected static boolean ignore(Method method) {
    return Modifier.isTransient(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
        || method.isAnnotationPresent(Transient.class);
  }

	/**
	 * Creates a parameterized SELECT or DELETE JPQL query based on non null
	 * property values, exclusive transient and static values, in the <code>fields</code> parameter
	 * 
	 * @return The created JPQL string
	 */
	protected static String createJPQL(final String entityClass, 
			final Map<String, Object> fields, boolean select, boolean distinct,	boolean any) {

		assert (entityClass != null) : "The 'entityClass' parameter can not be null!"; //TODO: throw IllegalArgumentException

		final StringBuilder jpql = new StringBuilder((select ? String.format(
				"SELECT %s e", (distinct ? "DISTINCT" : "")) : "DELETE")
			)
			.append(String.format(" FROM %s e", entityClass));
		
		final StringBuilder debugData = new StringBuilder();

		Set<Entry<String, Object>> properties = fields.entrySet();
		boolean where = false;
		String operator = (any ? "OR" : "AND");
		int n = 1;
		
		for (Entry<String, Object> entry : properties) {
			String propertyName = entry.getKey();
			Object value = entry.getValue();

			if (value != null) {
				Class<?> type = value.getClass();
				if (type != null) {
					int k = OBJECT_PRIMITIVES.indexOf(type.getName());
					if (type.isPrimitive() || k > -1) {
					  
						String equals = (k == 0 ? (value.toString().indexOf('%') > -1 ? "LIKE" : "=") : "="); // 0 == java.lang.String
						if (!where) {
							where = true;
							jpql.append(String.format(" where e.%s %s ?%d", propertyName, equals, n));
						} else {
							jpql.append(String.format(" %s e.%s %s ?%d", operator, propertyName, equals, n));
						}
						if(log.isDebugEnabled()) {
							debugData.append("\n\t[?" + n + " = " + value + ']');
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
}
