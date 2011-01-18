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
 *   Added a lot of code from Crank, the Java Framework for CRUD and Validation:
 *   http://code.google.com/p/krank/
 *   Actually added some code of my own :-)
 */

package no.knowit.crud;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

/**
 * Generic CrudService interface, a.k.a. DAO, a.k.a. Repository.<br/>
 * See chapter 4 in Real World Java EE Patterns
 * 
 * @author http://code.google.com/p/krank/
 * @author adam-bien.com
 * @author Leif Olsen
 */
@Local
public interface CrudService {

  String NAME = "crudService";

  /**
   * Make an entity instance managed and persistent.
   * 
   * @param entity the entity to persist
   * @throws EntityExistsException if the entity already exists.
   *           (The EntityExistsException may be thrown when the persist
   *           operation is invoked, or the EntityExistsException or
   *           another PersistenceException may be thrown at flush or commit
   *           time.)
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @see javax.persistence.EntityManager#persist(Object)
   */
  void persist(Object entity);

  /**
   * Make a collection of entity instances managed and persistent.
   * 
   * @param entities A collection of entities to persist
   * @throws IllegalArgumentException if the <code>entities</code> parameter is null.
   * @throws EntityExistsException if an entity in the collection already exists.
   *           (The EntityExistsException may be thrown when the persist
   *           operation is invoked, or the EntityExistsException or
   *           another PersistenceException may be thrown at flush or commit
   *           time.)
   * @throws IllegalArgumentException if an element in the collection is not an entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @see javax.persistence.EntityManager#persist(Object)
   */
  void persist(Collection<Object> entities);

  /**
   * Make an entity instance managed and persistent.
   * <p>
   * The passed entity is persisted first, then the <code>flush</code> method is invoked. This
   * forces the EntityManager to flush its cache to the database. The state of the cached entities
   * will be written to the database with one or more <code>INSERT</code> statements — but not
   * committed yet. Either the database or the <code>EntityManager</code> will have to compute any
   * technical primary key now. After the <code>fush</code> invocation, the entity is going to be
   * <code>refreshed</code>. The state of the entity is overwritten with the state in the database.
   * Finally, the fresh entity is returned to the caller. This strange behavior is sometimes
   * required to force the JPA provider to update the technical key in the entity instance. The
   * <code>persist</code>, <code>flush</code>, and <code>refresh</code> sequence further enforces
   * the update of the <code>@Id</code> computed in the database. It is not backed by the spec, but
   * it works with the popular providers.
   * </p>
   * 
   * @param entity the entity to persist
   * @return the entity with the persisted state
   * @throws EntityExistsException if the entity already exists.
   *           (The EntityExistsException may be thrown when the persist
   *           operation is invoked, or the EntityExistsException or
   *           another PersistenceException may be thrown at flush or commit
   *           time.)
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#persist(Object)
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#refresh(Object)
   */
  <T> T create(T entity);

  /**
   * Make a collection of entity instances managed and persistent.
   * <p>
   * Each entity in the collection is persisted first, then the <code>flush</code> method is
   * invoked. This forces the EntityManager to flush its cache to the database. The state of the
   * cached entities will be written to the database with one or more <code>INSERT</code> statements
   * — but not committed yet. Either the database or the <code>EntityManager</code> will have to
   * compute any technical primary key now. After the <code>fush</code> invocation, the entity is
   * going to be <code>refreshed</code>. The state of the entity is overwritten with the state in
   * the database. Finally, the fresh entity is returned to the caller. This strange behavior is
   * sometimes required to force the JPA provider to update the technical key in the entity
   * instance. The <code>persist</code>, <code>flush</code>, and <code>refresh</code> sequence
   * further enforces the update of the <code>@Id</code> computed in the database. It is not backed
   * by the spec, but it works with the popular providers.
   * </p>
   * 
   * @param entities A collection of entities to persist
   * @return a collection of entities with the persisted state
   * @throws IllegalArgumentException if the <code>entities</code> parameter is null.
   * @throws EntityExistsException if an entity in the collection already exists.
   *           (The EntityExistsException may be thrown when the persist
   *           operation is invoked, or the EntityExistsException or
   *           another PersistenceException may be thrown at flush or commit
   *           time.)
   * @throws IllegalArgumentException if an element in the collection is not an entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws PersistenceException if the flush fails
   * @see CrudService#create(Object)
   */
  <T> Collection<T> create(Collection<T> entities);

  /**
   * Find by primary key.
   *
   * @param entityClass the entity class to find an instance of
   * @param id the primary key to find the entity by
   * @return the found entity instance or null if the entity does not exist
   * @throws IllegalArgumentException if the first argument does
   *         not denote an entity type or the second
   *         argument is not a valid type for that
   *         entity's primary key
   * @see javax.persistence.EntityManager#find
   */
  <T> T find(Class<T> entityClass, Object id);

  /**
   * Find all entities of a particular type by generating a select query;
   * <strong><code>"SELECT e FROM Entity e"</code></strong>, where <code>Entity</code>
   * is the given <code>entityClass</code> parameter.
   * @param entityClass the entity class to find instances of
   * @return a list of entities
   * @throws IllegalArgumentException if produced query string is not valid
   */
  <T> List<T> find(Class<T> entityClass);

  /**
   * Find all entities of a particular type by generating a select query;
   * <strong><code>"SELECT e FROM Entity e"</code></strong>, where <code>Entity</code> is the given
   * <code>entityClass</code> parameter. The number of entities
   * returned is limited by the <code>startPosition</code> and <code>maxResult</code> parameters.
   * 
   * @param entityClass the entity class to find instances of
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of entities
   * @see CrudService#find(Class)
   */
  <T> List<T> find(Class<T> entityClass, int firstResult, int maxResults);

  /**
   * <p>
   * Find entities based on an example entity.
   * </p>
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   *          primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param distinct whether the query should be distinct or not
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   *          <code>false</code> if the query should be an <b>"AND"</b> query.
   * @return a list of entities
   */
  <T> List<T> find(T example, boolean distinct, boolean any);

  /**
   * <p>
   * Find entities based on an example entity. The number of entities returned is limited by the
   * <code>firstResult</code> and <code>maxResults</code> parameters.
   * </p>
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   *          primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param distinct Whether the query should be distinct or not
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   *          <code>false</code> if the query should be an <b>"AND"</b> query.
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of entities
   */
  <T> List<T> find(T example, boolean distinct, boolean any, int firstResult, int maxResults);

  /**
   * Creates an instance of Query for executing a query in the Java Persistence query
   * language, executes the (select) query and return the results as a List
   * of entities.
   * 
   * @param jpql the query in the Java Persistence query language
   * @return a list of entities
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#getResultList()
   */
  <T> List<T> findByQuery(String jpql);

  /**
   * Creates an instance of Query for executing a query in the Java Persistence query
   * language, executes (select) query and return the results as a List of entities. The number of
   * entities returned is
   * limited by the <code>firstResult</code> and <code>maxResults</code> parameters.
   * 
   * @param jpql the query in the Java Persistence query language
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of entities
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @throws java.lang.IllegalArgumentException if <code>firstResult</code> or
   *           <code>maxResults</code> is negative.
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#getResultList()
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   */
  <T> List<T> findByQuery(String jpql, int firstResult, int maxResults);

  /**
   * Creates an instance of Query for executing a query in the Java Persistence query
   * language, binds all arguments in <code>parameters</code> to corresponding
   * named parameters in the JPQL query, then executes the (select) query and return the
   * results as a List of entities.
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query
   * @return a list of entities
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @throws IllegalArgumentException if parameter name does not
   *           correspond to parameter in query string
   *           or argument is of incorrect type
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#getResultList()
   * @see javax.persistence.Query#setParameter(String, Object)
   */
  <T> List<T> findByQuery(String jpql, Map<String, Object> parameters);

  /**
   * Creates an instance of Query for executing a query in the Java Persistence query language,
   * binds all arguments in <code>parameters</code> to corresponding named parameters in the JPQL
   * query, then executes the (select) query and
   * return the results as a List of entities. The number of entities returned is limited by the
   * <code>firstResult</code> and <code>maxResults</code> parameters.
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of entities
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   * @see javax.persistence.Query#getResultList()
   * @see javax.persistence.Query#setParameter(String, Object)
   */
  <T> List<T> findByQuery(String jpql, Map<String, Object> parameters, int firstResult,
      int maxResults);

  /**
   * Creates an instance of Query for executing a named query (in the Java Persistence query
   * language), executes the query and return the query results as a List of entities.
   * 
   * @param queryName the name of the named query
   * @return a list of entities
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @see javax.persistence.EntityManager#createNamedQuery(String)
   * @see javax.persistence.Query#getResultList()
   */
  <T> List<T> findByNamedQuery(String queryName);

  /**
   * Creates an instance of Query for executing a named query (in the Java Persistence query
   * language), binds all arguments in <code>parameters</code> to corresponding
   * named parameters in the named query, then executes the query and return the query
   * results as a List of entities.
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query
   * @return a list of entities
   * @throws IllegalArgumentException if the <code>parameters</code> parameter is null.
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @throws IllegalArgumentException if parameter name does not
   *           correspond to parameter in query string
   *           or argument is of incorrect type
   * @see javax.persistence.EntityManager#createNamedQuery(String)
   * @see javax.persistence.Query#getResultList()
   * @see javax.persistence.Query#setParameter(String, Object)
   */
  <T> List<T> findByNamedQuery(String queryName, Map<String, Object> parameters);

  /**
   * Creates an instance of Query for executing a named query (in the Java Persistence query
   * language), executes the query and return the query results as a List of entities. The number of
   * entities returned is limited by the <code>firstResult</code> and <code>maxResults</code>
   * parameters.
   * 
   * @param queryName the name of the named query
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of entities
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @see javax.persistence.EntityManager#createNamedQuery(String)
   * @see javax.persistence.Query#getResultList()
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   */
  <T> List<T> findByNamedQuery(String queryName, int firstResult, int maxResults);

  /**
   * Creates an instance of Query for executing a named query (in the Java Persistence query
   * language or in native SQL), binds all arguments in <code>parameters</code> to corresponding
   * named parameters in the named query, then executes a SELECT query and return the query
   * results as a List of entities. The number of entities returned is limited by the
   * <code>firstResult</code> and <code>maxResults</code> parameters.
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of entities
   * @throws IllegalArgumentException if the <code>parameters</code> parameter is null.
   */
  <T> List<T> findByNamedQuery(String queryName, Map<String, Object> parameters,
      int firstResult, int maxResults);

  /**
   * Creates a dynamic query using a native SQL statement with UPDATE or DELETE,
   * executes the query and return the query results as a List.
   * 
   * @param sql the native SQL query to execute
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql);

  /**
   * Creates a dynamic query using a native SQL statement with UPDATE or DELETE,
   * executes the query and return the query results as a List. The number of results returned is
   * limited by the <code>firstResult</code> and <code>maxResulst</code> parameters.
   * 
   * @param sql the native SQL query to execute
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, int firstResult, int maxResults);

  /**
   * Creates a dynamic query using a native SQL statement that retrieves a single entity type,
   * executes the query and return the query results as a List. The type of results is determined
   * by the <code>resultClass</code> parameter
   * 
   * @param sql the native SQL query to execute
   * @param resultClass the class of the resulting instance(s)
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, Class<T> resultClass);

  /**
   * Creates a dynamic query using a native SQL statement that retrieves a single entity type,
   * executes the query and return the query results as a List. The type of results is determined
   * by the <code>resultClass</code> parameter. The number of results returned is
   * limited by the <code>firstResult</code> and <code>maxResults</code> parameters.
   * 
   * @param sql the native SQL query to execute
   * @param resultClass the class of the resulting instance(s)
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, Class<T> resultClass,
      int firstResult, int maxResults);

  /**
   * Creates a dynamic query using a native SQL statement that retrieves a result set with multiple
   * entity types, executes the query and return the query results as a List.<br/>
   * For example, if we want to create a <code>SqlResultSetMapping</code> for the <code>User</code>
   * entity, then we can use the &#064;SqlResultSetMapping annotation as follows:
   * 
   * <pre>
   * <code>&#064;SqlResultSetMapping(name = "UserResults",
   *   entities = &#064;EntityResult(
   *     entityClass = org.mydomain.model.User.class))</code>
   * </pre>
   * <p>
   * Then we can specify the mapping in the Query as follows:
   * </p>
   * 
   * <pre>
   * <code>List<?> result = findByNativeQuery(
   *   "SELECT user_id, first_name, last_name "
   * + "FROM   users "
   * + "WHERE  user_id IN "
   * + "      (SELECT seller_id FROM items "
   * + "       GROUP BY seller_id HAVING COUNT(*) > 1)", "UserResults")</code>
   * </pre>
   * 
   * @param sql the native SQL query to execute
   * @param resultSetMapping the name of the result set mapping
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, String resultSetMapping);

  /**
   * Creates a dynamic query using a native SQL statement that retrieves a result set with multiple
   * entity types, executes the query and return the query results as a List. The number of results
   * returned is limited by the <code>firstResult</code> and <code>maxResults</code> parameters.
   * For example, if we want to create a <code>SqlResultSetMapping</code> for the <code>User</code>
   * entity, then we can use the &#064;SqlResultSetMapping annotation as follows:
   * 
   * <pre>
   * <code>&#064;SqlResultSetMapping(name = "UserResults",
   *   entities = &#064;EntityResult(
   *     entityClass = org.mydomain.model.User.class))</code>
   * </pre>
   * <p>
   * Then we can specify the mapping in the Query as follows:
   * </p>
   * 
   * <pre>
   * <code>List<?> result = findByNativeQuery(
   *   "SELECT user_id, first_name, last_name "
   * + "FROM   users "
   * + "WHERE  user_id IN "
   * + "      (SELECT seller_id FROM items "
   * + "       GROUP BY seller_id HAVING COUNT(*) > 1)", "UserResults")</code>
   * </pre>
   * 
   * @param sql the native SQL query to execute
   * @param resultSetMapping the name of the result set mapping
   * @param firstResult position of the first result to be returned by the query, numbered from 0
   * @param maxResults the maximum number of entities that should be returned by the query
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, String resultSetMapping,
      int firstResult, int maxResults);

  /**
   * Merge the state of the given entity into the current persistence context,
   * returning (a potentially different object) the persisted entity.
   * 
   * @param entity the entity instance to merge
   * @return the instance that the state was merged to
   * @see javax.persistence.EntityManager#merge
   * @throws IllegalArgumentException if not an entity
   *           or if a detached entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  <T> T merge(T entity);

  /**
   * Merge the collection of entities, returning (a collection of potentially different objects) the
   * persisted entities. Basics - merge will take an exiting 'detached' entity and merge its
   * properties onto an existing entity. The entity with the merged state is returned.
   * 
   * @param entities A collection of entities
   * @return a collection of entities with the merged state
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if the <code>entities</code> parameter is null.
   * @throws IllegalArgumentException if an element in the <code>entities</code> collection is not
   *           an entity or if a detached entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @see CrudService#merge(T entity)
   */
  <T> Collection<T> merge(Collection<T> entities);

  /**
   * Merge the state of the given entity into the current persistence context, returning (a
   * potentially different object) the persisted entity. Basics - merge will take an exiting
   * 'detached' entity and merge its properties onto an existing entity. After merge this method
   * will call flush to make sure the entity is in sync with the database.
   * 
   * @param entity the entity instance to merge
   * @return the instance that the state was merged to
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if not an entity
   *           or if a detached entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#merge
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#refresh(Object)
   */
  <T> T update(T entity);

  /**
   * Merge the collection of entities, returning (a collection of potentially different objects) the
   * persisted entities. Basics - merge will take an exiting 'detached' entity and merge its
   * properties onto an existing entity. After the entities in the collection are merged, this
   * method will call flush to make sure the entities are in sync with the database.
   * 
   * @param entities A collection of entities
   * @return a collection of entities with the merged state
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if the <code>entities</code> parameter is null.
   * @throws IllegalArgumentException if an element in the collection is not an entity
   *           or if a detached entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws PersistenceException if the flush fails
   * @see CrudService#update(T entity)
   * @see javax.persistence.EntityManager#merge
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#refresh(Object)
   */
  <T> Collection<T> update(Collection<T> entities);

  /**
   * Create an instance of Query for executing a Java Persistence query update or delete statement,
   * then executes the update or delete statement.
   * 
   * @param jpql a Java Persistence query language query string
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalStateException if called for a Java Persistence query language SELECT statement
   * @throws IllegalArgumentException if query string is not valid
   * @throws TransactionRequiredException if there is no transaction
   * @return the number of entities updated or deleted
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#executeUpdate()
   */
  int executeUpdate(String jpql);

  /**
   * Create an instance of Query for executing a native SQL update or delete statement, then
   * executes the update or delete statement.
   * 
   * @param sql a native SQL query string
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalStateException if called for a native SQL SELECT statement
   * @throws TransactionRequiredException if there is no transaction
   * @return the number of rows updated or deleted
   * @see javax.persistence.EntityManager#createNativeQuery(String)
   * @see javax.persistence.Query#executeUpdate()
   */
  int executeUpdateByNativeQuery(String sql);

  /**
   * Remove an entity from persistent storage in the database.
   * If the entity is not in the 'managed' state, it is merged
   * into the persistent context, then removed.
   * 
   * @param entity the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#merge
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  void remove(Object entity);

  /**
   * Remove all instances of the specified class
   * 
   * @throws IllegalArgumentException if <code>entityClass</code> parameter is null
   * @throws IllegalArgumentException if not an entity
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @param entityClass The class to remove instances for
   */
  void remove(Class<?> entityClass);

  /**
   * Remove an entity from persistent storage in the database.
   * 
   * @param entityClass the entity class of the object to delete
   * @param id the Primary Key of the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#getReference
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  void remove(Class<?> entityClass, Object id);

  /**
   * Remove a collection of entities from persistent storage in the database.
   * 
   * @param entities collection of entities to remove
   * @throws IllegalArgumentException if the <code>entities</code> parameter is null.
   * @throws IllegalArgumentException if one of the elements in the <code>entities</code> collection
   *           is not an entity
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  void remove(Collection<Object> entities);


  /**
   * <p>Remove all entities where conditions in the <code>example</code> parameter matches.</p>
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.
   */
  void remove(Object example, boolean any);

  /**
   * Remove an entity from persistent storage in the database. If the entity is not in the 'managed'
   * state, it is merged into the persistent context, then removed. After remove this method will
   * call flush to make sure the entity is in sync with the database.
   * 
   * @param entity the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#merge
   * @see javax.persistence.EntityManager#flush()
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  void delete(Object entity);

  /**
   * Remove an entity from persistent storage in the database. After remove this method will call
   * flush to make sure the entity is in sync with the database.
   * 
   * @param entityClass the entity class of the object to delete
   * @param id the Primary Key of the object to delete.
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#getReference
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  void delete(Class<?> type, Object id);

  /**
   * <p>
   * Remove a collection of entities from persistent storage in the database. After the entities are
   * removed this method will call flush to make sure the entities are in sync with the database.
   * </p>
   * 
   * @param entities collection of entities to remove
   * @throws IllegalArgumentException if <code>entities</code> parameter is null
   * @throws IllegalArgumentException if one of the elements in the <code>entities</code> collection
   *           is not an entity
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   */
  void delete(Collection<Object> entities);

  /**
   * Remove all entities where conditions in the <code>example</code> parameter matches.
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   *          primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   *          <code>false</code> if the query should be an <b>"AND"</b> query.
   */
  void delete(Object example, boolean any);

  /**
   * Persist or merge an entity. If the entity is already persisted then the state of the given
   * entity is merged into the current persistence context, otherwise the entity instance is
   * persisted (made managed and persistent).
   * 
   * @param entity the entity to persist or merge
   * @return the persisted entity
   * @see javax.persistence.EntityManager#persist(Object entity)
   * @see javax.persistence.EntityManager#merge(Object entity)
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is no transaction.
   */
  <T> T store(T entity);

  /**
   * Persist or merge a collection of entities. If an entity in the collection is already persisted
   * then the state of the given entity is merged into the current persistence context, otherwise
   * the entity instance is persisted (made managed and persistent).
   * 
   * @param entities a collection of entities to persist or merge
   * @return a collection of stored entities
   * @see javax.persistence.EntityManager#persist(Object entity)
   * @see javax.persistence.EntityManager#merge(Object entity)
   * @throws IllegalArgumentException if <code>entities</code> parameter is null
   * @throws IllegalArgumentException if one of the elements in the <code>entities</code> collection
   *           is not an entity
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is no transaction.
   */
  <T> Collection<T> store(Collection<T> entities);


  /**
   * <p>Count entity instances</p>
   * @param entityClass the entity class to count instances of
   * @return the number of entities
   */
  int count(final Class<?> entityClass);

  /**
   * Refresh an entity that may have changed in another
   * thread/transaction. If the entity is not in the
   * 'managed' state, it is first merged into the persistent
   * context, then refreshed.
   * 
   * @param transientEntity the transient entity to refresh
   * @return the refreshed entity
   * @see javax.persistence.EntityManager#refresh(Object)
   * @throws IllegalArgumentException if instance is not an
   *           entity or is a removed entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws EntityNotFoundException if the entity no longer
   *           exists in the database.
   */
  <T> T refresh(T transientEntity);

  /**
   * Refresh a collection of entities that may have changed in another
   * thread/transaction. If the entity is not in the
   * 'managed' state, it is first merged into the persistent
   * context, then refreshed.
   * 
   * @param transientEntities a collection of transient entities to refresh
   * @return a collection of refreshed entities
   * @see javax.persistence.EntityManager#refresh(Object)
   * @throws IllegalArgumentException if <code>transientEntities</code> parameter is null
   * @throws IllegalArgumentException if instance in the <code>transientEntities</code> collection
   *           is not an entity or is a removed entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws EntityNotFoundException if the entity no longer
   *           exists in the database.
   */
  <T> Collection<T> refresh(Collection<T> transientEntities);

  /**
   * Refresh an entity that may have changed in another
   * thread/transaction. If the entity is not in the
   * 'managed' state, it is first merged into the persistent
   * context, then refreshed.
   * 
   * @param entityClass the entity type to refresh.
   * @param id the entity identity to refresh.
   * @see javax.persistence.EntityManager#refresh(Object)
   * @throws IllegalArgumentException if the first argument does
   *           not denote an entity type or the second
   *           argument is not a valid type for that
   *           entity's primary key
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws EntityNotFoundException if the entity no longer
   *           exists in the database.
   */
  <T> T refresh(Class<T> entityClass, Object id);


  /**
   * <p>Synchronize the persistence context to the underlying database.</p>
   * <p>
   * If there are managed entities with changes pending, a flush is guaranteed
   * to occur in two situations. The first is when the transaction commits. A
   * flush of any required changes will occur before the database transaction
   * has completed. The only other time a flush is guaranteed to occur is when
   * the entity manager flush() operation is invoked. This method allows
   * developers to manually trigger the same process that the entity manager
   * internally uses to flush the persistence context.</p>
   * <p>
   * A flush basically consists of three components: new entities that need to
   * be persisted, changed entities that need to be updated, and removed
   * entities that need to be deleted from the database. All of this information
   * is managed by the persistence context. It maintains links to all of the
   * managed entities that will be created or changed as well as the list of
   * entities that need to be removed.</p>
   * <p>
   * When a flush occurs, the entity manager first iterates over the managed
   * entities and looks for new entities that have been added to relationships
   * with cascade persist enabled. This is logically equivalent to invoking
   * <code>persist()</code> again on each managed entity just before the flush
   * occurs. The entity manager also checks to ensure the integrity of all of
   * the relationships. If an entity points to another entity that is not
   * managed or has been removed, then an exception may be thrown.</p>
   *
   * @throws TransactionRequiredException if there is no transaction
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#flush()
   */
  void flush();

  /**
   * <p>Clear the persistence context, causing all managed
   * entities to become detached. Changes made to entities that
   * have not been flushed to the database will not be persisted.</p>
   * <p>
   * This is usually required only for application-managed and extended
   * persistence contexts that are long-lived and have grown too large in size.
   * For example, consider an application-managed entity manager that issues a
   * query returning several hundred entity instances. Once changes are made
   * to a handful of these instances and the transaction is committed, you have
   * left in memory hundreds of objects that you have no intention of changing
   * any further. If you don't want to close the persistence context, then you
   * need to be able to clear out the managed entities, or else the persistence
   * context will continue to grow over time. </p>
   *
   * @see javax.persistence.EntityManager#clear()
   */
  void clear();

  /**
   * Write anything to db that is pending operation and clear it.
   * 
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#clear()
   * @throws TransactionRequiredException if there is
   *           no transaction
   * @throws PersistenceException if the flush fails
   */
  void flushAndClear();

  /**
   * Check if the instance belongs to the current persistence context.
   * 
   * @param entity the entity to check
   * @return true if the instance belongs to the current persistence context.
   * @see javax.persistence.EntityManager#contains(Object)
   * @throws IllegalArgumentException if not an entity
   */
  boolean isManaged(Object entity);

  /**
   * Get the entitymanager wired to this service
   * 
   * @return the entitymanager wired to this service
   * @throws java.lang.IllegalStateException
   *           if EntityManager has not been set on this service before usage
   */
  EntityManager getEntityManager();
}
