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
 */
package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;

/**
 * A minimalistic implementation of the generic CrudService.
 * 
 * @author adam-bien.com
 */
@Stateless(name="crudService")
public class CrudServiceBean implements CrudService {

	protected Logger log = Logger.getLogger(this.getClass());
	//protected static Logger log = Logger.getLogger(CrudServiceBean.class);
	
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
		Collection<T> persistedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			persistedResults.add(persist(entity));		
		}
		return persistedResults;
	}
	
	// 'R'
	public <T> T find(Class<T> entityClass, Object id) {
		return (T)getEntityManager().find(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass) {
    return getEntityManager().createQuery("from " + entityClass.getName()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass, int startPosition, int maxResult) {
    return getEntityManager().createQuery("from " + entityClass.getName())
      .setFirstResult(startPosition)
      .setMaxResults(maxResult)
    	.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(T example, boolean distinct, boolean any) {
		Query query = createExampleQuery(example, true, distinct, any);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
  public <T> List<T> find(T example, boolean distinct, boolean any, int startPosition, int maxResult) {
		Query query = createExampleQuery(example, true, distinct, any);
		return query
	    .setFirstResult(startPosition)
	    .setMaxResults(maxResult)
			.getResultList();
  }
	
	// 'U'
	public <T> T merge(T entity) {
		return (T)getEntityManager().merge(entity);
	}

	public <T> Collection<T> merge(Collection<T> entities) {
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
		for (Object entity : entities) {
			remove(entity);
		}
	}

	public void remove(final Object example, boolean any) {
		Query query = createExampleQuery(example, false, false, any);
		query.executeUpdate();
	}

	// :-)
	public <T> T store(T entity) {
		assert entity != null;
		
		Object id = getIdentity(entity);
		if(!log.isDebugEnabled()) {			
			if(id != null) {
				return find(entity.getClass(), id) == null ? persist(entity) : merge(entity);
			}
			else {
				return persist(entity);
			}
		}
		else {
			if(id != null) {
				if(find(entity.getClass(), id) == null) {
					T e = persist(entity);
					log.debug("CrudService.store: persisted new entity, got id: " + getIdentity(e));
					return e;
				}
				else {
					log.debug("CrudService.store: merge existing entity with id: " + id);
					return merge(entity);
				}
			}
			else {
				T e = persist(entity);
				log.debug("CrudService.store: persisted new entity, got id: " + getIdentity(e));
				return e;
			}
		}
	}
	
	public <T> Collection<T> store(Collection<T> entities) {
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

	
	public List findWithNamedQuery(String namedQueryName) {
		return this.entityManager.createNamedQuery(namedQueryName).getResultList();
	}

	public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		return findWithNamedQuery(namedQueryName, parameters, 0);
	}

	public List findWithNamedQuery(String queryName, int resultLimit) {
		return this.entityManager.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
	}

	public <T> List<T> findByNativeQuery(String sql, Class<T> type) {
		return this.entityManager.createNativeQuery(sql, type).getResultList();
	}

	public List findWithNamedQuery(String namedQueryName,	Map<String, Object> parameters, int resultLimit) {
		Set<Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.entityManager.createNamedQuery(namedQueryName);
		if (resultLimit > 0)
			query.setMaxResults(resultLimit);
		for (Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}
	
  /**
   * @return the entitymanager wired to this service
   * @throws java.lang.IllegalStateException if EntityManager has not been set on this service before usage
   */
  protected EntityManager getEntityManager() {
    if (entityManager == null) 
      throw new IllegalStateException("EntityManager has not been set on service before usage");

    return entityManager;
  }

  
	private static final List<String> OBJECT_PRIMITIVES = Arrays.asList(
			"java.lang.String", "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",	"java.lang.Double", 
			"java.lang.Float",  "java.lang.Integer", "java.lang.Long", "java.lang.Number"   , "java.lang.Short" ,	
			"java.util.Date" );
  
  /**
   * This is a simple form of query by example. Produces a SELECT or DELETE query
   * 
   * The limitations are:
   * <ul>
   * <li>Only  one @Id annotation</li>
   * <li>We can not e.g. handle arrays and object references while generating JPQL</li>
   * </ul>
   * 
   * Our intension is to make this method more advanced/flexible later 
   * 
   * @param example
   * @param select
   * @param distinct
   * @param any
   * @return
   */
	protected Query createExampleQuery(Object example, boolean select, boolean distinct, boolean any) {
		
		final StringBuilder jpql = new StringBuilder(	
			(select ? String.format("SELECT %s e", (distinct ? "DISTINCT" : "")) : "DELETE") 
		)
		.append( String.format(" FROM %s e", example.getClass().getName()) );
		
		
		int n = 1;
		List<Object> values = new ArrayList<Object>();
		if(example != null) {
			BeanMap beanMap = new BeanMap(example);
			Set keys = beanMap.keySet();
			Iterator i = keys.iterator();
			String operator = (any ? "OR" : "AND");
			boolean where = false;
			
			while (i.hasNext()) {
				String propertyName = (String) i.next();
				Class type = beanMap.getType(propertyName);
				if(type != null) {
					Object value = beanMap.get(propertyName);
					int k = OBJECT_PRIMITIVES.indexOf(type.getName());
					if(value != null && (type.isPrimitive() || k > -1)) {
						values.add(value);
						String equals = (k==0 ? "LIKE" : "=");  // 0 -> String
						if(!where) {
							where = true;
							jpql.append(String.format(" where e.%s %s ?%d", propertyName, equals, n));
						}
						else {
							jpql.append(String.format(" %s e.%s %s ?%d", operator, propertyName , equals, n));
						}
						n++;
					}
				}
			}			
		}

		Query query = getEntityManager().createQuery(jpql.toString());

		if(log.isDebugEnabled()) {
			// The jpql string in it's valid form is not needed anymore - so we use it for debug output
			jpql.append("]\n");
		}
		
		n = 1;
		for (Object v : values) {
			if(log.isDebugEnabled()) {
				jpql.append("\t[?" + n + " = " + v + "]\n");
			}
			query.setParameter(n++, v);
		}
		
		if(log.isDebugEnabled()) {
			log.debug("createExampleQuery, jpql = \n\t[" + jpql.toString() );
		}
		return query;
	}


  
  // -------------------------------
  // Utility methods
  // TODO: move to separate package
  // -------------------------------

  protected static boolean hasIdentity(Object entity) {
		return getIdentity(entity) != null ? true : false;
  }

  protected static Object getIdentity(Object entity) {
  	String identityName = getIdentityPropertyName(entity.getClass());
		BeanMap beanMap = new BeanMap(entity);
		return beanMap.get(identityName);
		
//		try {
//			return PropertyUtils.getNestedProperty(entity, identityName);
//		} catch (Exception e) {
//			log.fatal("PropertyUtils.getNestedProperty", e); 
//		}
//		return null;
  }
  
	protected static String getIdentityPropertyName(Class<?> clazz) {
		String idPropertyName = searchFieldsForIndentity(clazz);
		if(idPropertyName == null) {
			idPropertyName = searchMethodsForIdentity(clazz);
		}
		return idPropertyName != null ? idPropertyName : "id";
	}
	
	protected static String searchFieldsForIndentity(Class<?> clazz) {
		String pkName = null;
		
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field field : fields) {
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

	protected static String searchMethodsForIdentity(Class<?> clazz) {
		String pkName = null;
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			Id id = method.getAnnotation(Id.class);
			if (id != null) {
				pkName = method.getName().substring(4);
				pkName = method.getName().substring(3, 4).toLowerCase()	+ pkName;
				break;
			}
		}
		if (pkName == null && clazz.getSuperclass() != null) {
			pkName = searchMethodsForIdentity(clazz.getSuperclass());
		}
		return pkName;
	}

	

}
