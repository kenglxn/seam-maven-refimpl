/**
 * This file is part of javaee-patterns.
 *
 * javaee-patterns is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * javaee-patterns is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.opensource.org/licenses/gpl-2.0.php>.
 *
 * Copyright (c) 22. June 2009 Adam Bien, blog.adam-bien.com
 * http://press.adam-bien.com
 * 
 * Modified by Leif Olsen
 *   Added a lot of code from Crank, the Java Framework for CRUD and Validation: http://code.google.com/p/krank/
 *   Actually added some code of my own :-)
 */
package no.knowit.crud;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import no.knowit.util.ReflectionUtils;

import org.apache.log4j.Logger;

/**
 * An implementation of the generic CrudService, a.k.a. DAO, a.k.a. Repository.<br/>
 * See chapter 4 in Real World Java EE Patterns
 * 
 * @author http://code.google.com/p/krank/
 * @author adam-bien.com
 * @author Leif Olsen
 */
@Stateless(name = CrudService.NAME)
public class CrudServiceBean implements CrudService {
  private static final Logger log = Logger.getLogger(CrudServiceBean.class);
  private static final String PARAM_NOT_NULL = "The \"%s\" parameter can not be null";

  @PersistenceContext
  protected EntityManager entityManager;

  // 'C'
  @Override
  public void persist(final Object entity) {
    getEntityManager().persist(entity);
  }

  @Override
  public void persist(final Collection<Object> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    for (final Object entity : entities) {
      persist(entity);
    }
  }

  @Override
  public <T> T create(final T entity) {
    getEntityManager().persist(entity);
    getEntityManager().flush();
    getEntityManager().refresh(entity);
    return entity;
  }

  @Override
  public <T> Collection<T> create(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> persistedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      persistedResults.add(create(entity));
    }
    return persistedResults;
  }

  // 'R'
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> T find(final Class<T> entityClass, final Object id) {
    return getEntityManager().find(entityClass, id);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> find(final Class<T> entityClass) {
    return find(entityClass, -1, -1);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> find(final Class<T> entityClass, final int firstResult, final int maxResults) {
    final Query query = getEntityManager()
    .createQuery(CrudServiceUtils.createSelectJpql(entityClass));

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> find(final T example, final boolean distinct, final boolean any) {
    return find(example, distinct, any, -1, -1);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> find(final T example, final boolean distinct, final boolean any,
      final int firstResult, final int maxResults) {

    final Query query = createExampleQuery(example, true, distinct, any);
    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNamedQuery(final String queryName) {
    return entityManager.createNamedQuery(queryName).getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNamedQuery(final String queryName, final int firstResult,
      final int maxResults) {
    final Query query = entityManager.createNamedQuery(queryName);

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }
    return query.getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNamedQuery(final String queryName, final Map<String, Object> parameters) {
    return findByNamedQuery(queryName, parameters, -1, -1);
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNamedQuery(final String queryName, final Map<String, Object> parameters,
      final int firstResult, final int maxResults) {

    final Query query = entityManager.createNamedQuery(queryName);

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }

    final Set<Entry<String, Object>> rawParameters = parameters.entrySet();
    for (final Entry<String, Object> entry : rawParameters) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNativeQuery(final String sql) {
    return entityManager.createNativeQuery(sql).getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNativeQuery(final String sql, final int firstResult,
      final int maxResults) {

    final Query query = entityManager.createNativeQuery(sql);

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final Class<T> resultClass) {
    return entityManager.createNativeQuery(sql, resultClass).getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final Class<T> resultClass,
      final int firstResult, final int maxResults) {

    final Query query = entityManager.createNativeQuery(sql, resultClass);

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final String resultSetMapping) {
    return entityManager.createNativeQuery(sql, resultSetMapping)
    .getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final String resultSetMapping,
      final int firstResult, final int maxResults) {

    final Query query = entityManager.createNativeQuery(sql, resultSetMapping);

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  // 'U'
  @Override
  public <T> T merge(final T entity) {
    return getEntityManager().merge(entity);
  }

  @Override
  public <T> Collection<T> merge(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> mergedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      mergedResults.add(merge(entity));
    }
    return mergedResults;
  }

  @Override
  public <T> T update(final T entity) {
    final T mergedEntity = getEntityManager().merge(entity);
    getEntityManager().flush();
    getEntityManager().refresh(entity);
    return mergedEntity;
  }

  @Override
  public <T> Collection<T> update(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> updatedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      updatedResults.add(update(entity));
    }
    return updatedResults;
  }

  // 'D'
  @Override
  public void remove(final Object entity) {
    final EntityManager em = getEntityManager();
    final Object managedEntity = em.contains(entity) ? entity : em.merge(entity);
    em.remove(managedEntity);
  }

  @Override
  public void remove(final Class<?> entityClass) {
    getEntityManager().createQuery(CrudServiceUtils.createDeleteJpql(entityClass)).executeUpdate();
  }

  @Override
  public void remove(final Class<?> entityClass, final Object id) {
    final Object ref = getEntityManager().getReference(entityClass, id);
    getEntityManager().remove(ref);
  }

  @Override
  public void remove(final Collection<Object> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    for (final Object entity : entities) {
      remove(entity);
    }
  }

  @Override
  public void remove(final Object example, final boolean any) {
    final Query query = createExampleQuery(example, false, false, any);
    query.executeUpdate();
  }

  // C or U :-)
  @Override
  public <T> T store(final T entity) {
    if (entity == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entity"));
    }
    //Object id = getIdentity(entity);
    final Object id = CrudServiceUtils.getIdValues(entity).get(0);
    if (!log.isDebugEnabled()) {
      if (id != null) {
        return find(entity.getClass(), id) == null ? create(entity) : merge(entity);
      }
      else {
        return create(entity);
      }
    }

    // Debug
    if (id != null) {
      if (find(entity.getClass(), id) == null) {
        final T e = create(entity);
        log.debug("CrudService.store: persisted new entity, got id: " +
            CrudServiceUtils.getIdValues(entity).get(0));
        return e;
      }
      else {
        log.debug("CrudService.store: merge existing entity with id: " + id);
        return merge(entity);
      }
    }
    else {
      final T e = create(entity);
      log.debug("CrudService.store: persisted new entity, got id: " +
          CrudServiceUtils.getIdValues(entity).get(0));
      return e;
    }
  }

  @Override
  public <T> Collection<T> store(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> storedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      storedResults.add(store(entity));
    }
    return storedResults;
  }

  // end C.R.U.D

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public int count(final Class<?> entityClass) {
    final Long result = (Long) getEntityManager().createQuery(
        CrudServiceUtils.createCountJpql(entityClass)).getSingleResult();

    return result.intValue();
  }

  @Override
  public <T> T refresh(final T transientEntity) {
    final EntityManager em = getEntityManager();
    final T managedEntity = em.contains(transientEntity) ? transientEntity
        : em.merge(transientEntity);

    em.refresh(managedEntity);
    return managedEntity;
  }

  @Override
  public <T> Collection<T> refresh(final Collection<T> transientEntities) {
    if (transientEntities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> refreshedResults = new ArrayList<T>(transientEntities.size());
    for (final T transientEntity : transientEntities) {
      refreshedResults.add(refresh(transientEntity));
    }
    return refreshedResults;
  }

  @Override
  public <T> T refresh(final Class<T> entityClass, final Object id) {
    final EntityManager em = getEntityManager();
    final T managedEntity = em.find(entityClass, id);
    em.refresh(managedEntity);
    return managedEntity;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public boolean isManaged(final Object entity) {
    return entity != null && getEntityManager().contains(entity);
  }

  @Override
  public void flush() {
    getEntityManager().flush();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public void clear() {
    getEntityManager().clear();
  }

  @Override
  public void flushAndClear() {
    final EntityManager em = getEntityManager();
    em.flush();
    em.clear();
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
   *   <li>The entity must have at least one @Id annotation</li>
   *   <li>Only primitives can be part of the query(Integer, String etc.)
   *   <li>Can not handle embedded classes, object references, arrays and collections while generating JPQL</li>
   * </ul>
   * 
   */
  protected Query createExampleQuery(final Object example, final boolean select,
      final boolean distinct, final boolean any) {
    if (example == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "example"));
    }
    final String jpql = CrudServiceUtils.createJpql(example, select, distinct, any);
    Map<String, Member> attributes = CrudServiceUtils.findQueryableAttributes(example.getClass());
    attributes = CrudServiceUtils.reduceQueryableAttributesToPopulatedFields(example, attributes);

    final StringBuilder debugData = new StringBuilder();

    if (log.isDebugEnabled()) {
      debugData.append(example.getClass().getSimpleName() + " class query parameters: ");
    }

    final Query query = getEntityManager().createQuery(jpql);
    final Set<Entry<String, Member>> properties = attributes.entrySet();
    for (final Entry<String, Member> entry : properties) {
      final String property = entry.getKey();
      final Member member = entry.getValue();
      final Object value = member != null ? ReflectionUtils.get(member, example) : null;
      query.setParameter(property, value);

      if (log.isDebugEnabled()) {
        debugData.append("[:" + property + " = " + value + "] ");
      }
    }

    if (log.isDebugEnabled()) {
      log.debug(debugData);
    }

    return query;
  }

}
