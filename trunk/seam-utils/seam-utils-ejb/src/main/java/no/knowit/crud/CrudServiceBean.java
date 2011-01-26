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
 *   Added a lot of code from Crank, http://code.google.com/p/krank/
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
  protected EntityManager em;

  // 'C'
  @Override
  public <T> T persist(final T entity) {
    em.persist(entity);
    em.flush(); // force the SQL insert and db constraints and triggers to execute
    em.refresh(entity); //re-read the state (after the trigger executes)
    return entity;
  }

  @Override
  public <T> Collection<T> persistCollection(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> persistedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      persistedResults.add(persist(entity));
    }
    return persistedResults;
  }

  // 'R'
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
    return em.getReference(entityClass, primaryKey);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> T find(final Class<T> entityClass, final Object primaryKey) {
    return em.find(entityClass, primaryKey);
  }

  // ---------------
  // Find with Query
  // ---------------
  @Override
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findWithQuery(final String jpql) {
    return em.createQuery(jpql).getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findWithQuery(final String jpql, final Map<String, Object> parameters,
      final int firstResult, final int maxResults) {

    final Query query = createQuery(jpql, parameters);
    setFirstResultAndMaxResults(query, firstResult, maxResults);
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findWithNamedQuery(final String queryName) {
    return em.createNamedQuery(queryName).getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findWithNamedQuery(final String queryName,
      final Map<String, Object> parameters, final int firstResult, final int maxResults) {

    final Query query = createNamedQuery(queryName, parameters);
    return setFirstResultAndMaxResults(query, firstResult, maxResults).getResultList();
  }

  // --------------------
  // Find by Native Query
  // --------------------
  @SuppressWarnings("unchecked")
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNativeQuery(final String sql) {
    return em.createNativeQuery(sql).getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByNativeQuery(final String sql, final Map<String, Object> parameters,
      final int firstResult, final int maxResults) {

    final Query query = createNativeQuery(sql, parameters);
    return setFirstResultAndMaxResults(query, firstResult, maxResults).getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final Class<T> resultClass) {
    return em.createNativeQuery(sql, resultClass).getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final Class<T> resultClass,
      final Map<String, Object> parameters, final int firstResult, final int maxResults) {

    final Query query = createNativeQuery(sql, resultClass, parameters);
    return setFirstResultAndMaxResults(query, firstResult, maxResults).getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final String resultSetMapping) {
    return em.createNativeQuery(sql, resultSetMapping).getResultList();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(final String sql, final String resultSetMapping,
      final Map<String, Object> parameters, final int firstResult, final int maxResults) {

    final Query query = createNativeQuery(sql, resultSetMapping, parameters);
    return setFirstResultAndMaxResults(query, firstResult, maxResults).getResultList();
  }

  // ---------------
  // Find with Type
  // ---------------
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findWithType(final Class<T> entityClass) {
    return findWithType(entityClass, -1, -1);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findWithType(final Class<T> entityClass,
      final int firstResult, final int maxResults) {

    final Query query = em.createQuery(CrudServiceUtils.createSelectJpql(entityClass));
    return setFirstResultAndMaxResults(query, firstResult, maxResults).getResultList();
  }

  // ---------------
  // Find by Example
  // ---------------
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> List<T> findByExample(final T example, final boolean distinct, final boolean any) {
    return findByExample(example, distinct, any, -1, -1);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  @SuppressWarnings("unchecked")
  public <T> List<T> findByExample(final T example, final boolean distinct, final boolean any,
      final int firstResult, final int maxResults) {

    final Query query = createExampleQuery(example, true, distinct, any);
    setFirstResultAndMaxResults(query, firstResult, maxResults);
    return query.getResultList();
  }

  // ---------------
  // Create Query
  // ---------------
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public Query createQuery(final String jpql, final Map<String, Object> parameters) {
    final Query query = em.createQuery(jpql);
    return setQueryParameters(query, parameters);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public Query createNamedQuery(final String jpql, final Map<String, Object> parameters) {

    final Query query = em.createNamedQuery(jpql);
    return setQueryParameters(query, parameters);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public Query createNativeQuery(final String sql, final Map<String, Object> parameters) {

    final Query query = em.createNativeQuery(sql);
    return setQueryParameters(query, parameters);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public Query createNativeQuery(final String sql, final Class<?> resultClass,
      final Map<String, Object> parameters) {

    final Query query = em.createNativeQuery(sql, resultClass);
    return setQueryParameters(query, parameters);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public Query createNativeQuery(final String sql, final String resultSetMapping,
      final Map<String, Object> parameters) {

    final Query query = em.createNativeQuery(sql, resultSetMapping);
    return setQueryParameters(query, parameters);
  }

  // 'U'
  @Override
  public <T> T merge(final T entity) {
    final T mergedEntity = em.merge(entity);
    em.flush();
    return mergedEntity;
  }

  @Override
  public <T> Collection<T> mergeCollection(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> mergedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      mergedResults.add(em.merge(entity));
    }
    em.flush();
    return mergedResults;
  }

  @Override
  public int executeUpdate(final String jpql) {
    return em.createQuery(jpql).executeUpdate();
  }

  @Override
  public int executeUpdateByNativeQuery(final String sql) {
    return em.createNativeQuery(sql).executeUpdate();
  }

  // 'D'
  @Override
  public void remove(final Class<?> entityClass, final Object id) {
    final Object ref = em.getReference(entityClass, id);
    em.remove(ref);
    em.flush();
  }

  @Override
  public void remove(final Object entity) {
    final Object ref = getManagedEntity(entity);
    em.remove(ref);
    em.flush();
  }

  @Override
  public void removeCollection(final Collection<Object> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    for (final Object entity : entities) {
      final Object ref = getManagedEntity(entity);
      em.remove(ref);
    }
    em.flush();
  }

  @Override
  public void removeByExample(final Object example, final boolean any) {
    final Query query = createExampleQuery(example, false, false, any);
    query.executeUpdate();
  }

  @Override
  public void removeWithType(final Class<?> entityClass) {
    em.createQuery(CrudServiceUtils.createDeleteJpql(entityClass)).executeUpdate();
  }

  // C or U :-)
  @Override
  public <T> T store(final T entity) {
    if (!log.isDebugEnabled()) {
      return hasIdentity(entity) ? merge(entity) : persist(entity);
    }

    // Debug
    final StringBuffer debugMessage = new StringBuffer("CrudService.store: ");
    try {
      T e;
      if (hasIdentity(entity)) {
        e = merge(entity);
        debugMessage.append("Merged existing entity with identity: ");
      }
      else {
        e = persist(entity);
        debugMessage.append("Persisted new entity. Got identity: ");
      }
      debugMessage.append(identityToString(e));
      return e;
    }
    finally {
      log.debug(debugMessage);
    }
  }

  @Override
  public <T> Collection<T> storeCollection(final Collection<T> entities) {
    if (entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    final Collection<T> storedResults = new ArrayList<T>(entities.size());
    for (final T entity : entities) {
      storedResults.add(store(entity));
    }
    return storedResults;
  }

  //~ C.R.U.D

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public int count(final Class<?> entityClass) {
    final Long result = (Long) em.createQuery(CrudServiceUtils.createCountJpql(entityClass))
    .getSingleResult();

    return result.intValue();
  }

  @Override
  public <T> T refresh(final Class<T> entityClass, final Object id) {
    final T managedEntity = em.find(entityClass, id);
    em.refresh(managedEntity);
    return managedEntity;
  }

  @Override
  public <T> T refresh(final T transientEntity) {
    final T managedEntity = getManagedEntity(transientEntity);
    em.refresh(managedEntity);
    return managedEntity;
  }

  @Override
  public <T> Collection<T> refreshCollection(final Collection<T> transientEntities) {
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
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public boolean isManaged(final Object entity) {
    return entity != null && getEntityManager().contains(entity);
  }

  @Override
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public <T> T getManagedEntity(final T transientEntity) {
    return em.contains(transientEntity) ? transientEntity
        : (T) em.find(transientEntity.getClass(), CrudServiceUtils.getIdValues(transientEntity)
            .get(0));
  }

  @Override
  public void flush() {
    em.flush();
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public void clear() {
    em.clear();
  }

  @Override
  public void flushAndClear() {
    em.flush();
    em.clear();
  }

  @Override
  public EntityManager getEntityManager() {
    if (em == null) {
      throw new IllegalStateException("EntityManager has not been set on service before usage");
    }
    return em;
  }

  protected boolean hasIdentity(final Object entity) {
    final List<Object> ids = CrudServiceUtils.getIdValues(entity);
    for (final Object id : ids) {
      if (id != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * This is a simple form of query by example. Produces a SELECT or DELETE
   * query based on non null property values in the <code>example</code> parameter
   * The limitations are:
   * <ul>
   * <li>The entity must have at least one @Id annotation</li>
   * <li>Only primitives can be part of the query(Integer, String etc.)
   * <li>Can not handle embedded classes, object references, arrays and collections while generating
   * JPQL</li>
   * </ul>
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
    try {
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
      return query;
    }
    finally {
      if (log.isDebugEnabled()) {
        log.debug(debugData);
      }
    }
  }

  private Query setQueryParameters(final Query query, final Map<String, Object> parameters) {
    if (parameters != null) {
      final Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      for (final Entry<String, Object> entry : rawParameters) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
    }
    return query;
  }

  private Query setFirstResultAndMaxResults(final Query query,
      final int firstResult, final int maxResults) {

    if (firstResult > -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query.setMaxResults(maxResults);
    }
    return query;
  }

  private String identityToString(final Object entity) {
    final List<Member> id = CrudServiceUtils.getIdAnnotations(entity.getClass());
    final StringBuffer sb = new StringBuffer();
    for (final Member member : id) {
      sb.append(member.getName())
      .append('=')
      .append(ReflectionUtils.get(member, entity))
      .append(", ");
    }
    return sb.delete(sb.length() - 2, sb.length() - 1).toString();
  }

}
