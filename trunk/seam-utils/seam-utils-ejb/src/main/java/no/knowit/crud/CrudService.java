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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

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

  /**
   * The name of this service
   */
  String NAME = "crudService";

  /**
   * An empty {@link java.util.HashMap}
   */
  Map<String, Object> EMPTY_PARAMETER_MAP = new HashMap<String, Object>();

  /**
   * Constant that can be used as a parameter value for <code>firstResult</code> and
   * <code>maxResults</code> in finder methods to indicate that parameter value should be ignored
   */
  int IGNORE_BOUNDARY = -1;

  /**
   * Make an entity instance managed and persistent.
   * <p>
   * The passed entity is persisted first, then the {@link javax.persistence.EntityManager#flush()}
   * method is invoked. This forces the EntityManager to flush its cache to the database. The state
   * of the cached entities will be written to the database with one or more <code>INSERT</code>
   * statements — but not committed yet. Either the database or the <code>EntityManager</code> will
   * have to compute any technical primary key now. After the
   * {@link javax.persistence.EntityManager#flush()} invocation, the entity is going to be refreshed
   * by invoking {@link javax.persistence.EntityManager#refresh(Object)}. The state of the entity is
   * overwritten with the state in the database. Finally, the fresh entity is returned to the
   * caller. This strange behavior is sometimes required to force the JPA provider to update the
   * technical key in the entity instance. The <code>persist</code>, <code>flush</code>, and
   * <code>refresh</code> sequence further enforces the update of the <code>@Id</code> computed in
   * the database. It is not backed by the spec, but it works with the popular providers.
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
  <T> T persist(T entity);

  /**
   * Make a collection of entity instances managed and persistent.
   * <p>
   * Each entity in the collection is persisted first, then the
   * {@link javax.persistence.EntityManager#flush()} method is invoked. This forces the
   * EntityManager to flush its cache to the database. The state of the cached entities will be
   * written to the database with one or more <code>INSERT</code> statements — but not committed
   * yet. Either the database or the <code>EntityManager</code> will have to compute any technical
   * primary key now. After the {@link javax.persistence.EntityManager#flush()} invocation, the
   * entity is going to be refreshed by invoking
   * {@link javax.persistence.EntityManager#refresh(Object)}. The state of the entity is overwritten
   * with the state in the database. Finally, the fresh entity is returned to the caller. This
   * strange behavior is sometimes required to force the JPA provider to update the technical key in
   * the entity instance. The <code>persist</code>, <code>flush</code>, and <code>refresh</code>
   * sequence further enforces the update of the <code>@Id</code> computed in the database. It is
   * not backed by the spec, but it works with the popular providers.
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
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws PersistenceException if the flush fails
   * @see CrudService#persist(Object)
   */
  <T> Collection<T> persistCollection(Collection<T> entities);

  /**
   * <p>
   * Get an instance, whose state may be lazily fetched. If the requested instance does not exist in
   * the database, throws {@link EntityNotFoundException} when the instance state is first accessed.
   * (The persistence provider runtime is permitted to throw {@link EntityNotFoundException} when
   * {@link #getReference} is called.) The application should not expect that the instance state
   * will be available upon detachment, unless it was accessed by the application while the entity
   * manager was open.
   * </p>
   * 
   * @param entityClass the entity class to get a reference to
   * @param primaryKey the primary key to the referenced entity
   * @return the found entity instance
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if the first argument does
   *           not denote an entity type or the second
   *           argument is not a valid type for that
   *           entity's primary key
   * @throws EntityNotFoundException if the entity state
   *           cannot be accessed
   * @see javax.persistence.EntityManager#getReference(Class, Object)
   */
  <T> T getReference(Class<T> entityClass, Object primaryKey);


  /**
   * Find by primary key.
   * 
   * @param entityClass the entity class to find an instance of
   * @param primaryKey the primary key to find the entity by
   * @return the found entity instance or null if the entity does not exist
   * @throws IllegalArgumentException if the first argument does
   *           not denote an entity type or the second
   *           argument is not a valid type for that
   *           entity's primary key
   * @see javax.persistence.EntityManager#find
   */
  <T> T find(Class<T> entityClass, Object primaryKey);

  // --------------------
  // Find with Query
  // --------------------
  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} for executing a query in the Java
   * Persistence query language, executes the (select) query and return the results as a List of
   * entities.
   * </p>
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
  <T> List<T> findWithQuery(String jpql);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} for executing a query in the Java
   * Persistence query language, binds all arguments in <code>parameters</code>, if any, to
   * corresponding named parameters in the JPQL query, executes the (select) query and return the
   * results as a list of entities. The number of entities returned is limited by the
   * <code>firstResult</code> and <code>maxResults</code> parameters. A negative value for
   * <code>firstResult</code> indicates that <code>firstResult</code> should be ignored and
   * {@link Query#setFirstResult(int)} is not called. Likewise a negative value for
   * <code>maxResults</code> indicates that <code>maxResults</code> should be ignored and
   * {@link Query#setMaxResults(int)} is not called.
   * </p>
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @return a list of entities
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   * @see javax.persistence.Query#setParameter(String, Object)
   * @see javax.persistence.Query#getResultList()
   */
  <T> List<T> findWithQuery(String jpql, Map<String, Object> parameters, int firstResult,
      int maxResults);

  // ----------------------
  // Find with Named Query
  // ----------------------
  /**
   * Creates an instance of {@link javax.persistence.Query} for executing a named query (in the Java
   * Persistence query
   * language or in native SQL), executes the query and return the query results as a list of
   * entities.
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
  <T> List<T> findWithNamedQuery(String queryName);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.NamedQuery} for executing a named query (in the
   * Java Persistence query language or in native SQL), binds all arguments in
   * <code>parameters</code>, if any, to corresponding named parameters in the named query, executes
   * the (select) query and return the query results as a list of entities. The number of entities
   * returned is limited by the <code>firstResult</code> and <code>maxResults</code> parameters. A
   * negative value for <code>firstResult</code> indicates that <code>firstResult</code> should be
   * ignored and {@link Query#setFirstResult(int)} is not called. Likewise a negative value for
   * <code>maxResults</code> indicates that <code>maxResults</code> should be ignored and
   * {@link Query#setMaxResults(int)} is not called.
   * </p>
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @return a list of entities
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @see javax.persistence.EntityManager#createNamedQuery(String)
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   * @see javax.persistence.Query#setParameter(String, Object)
   * @see javax.persistence.Query#getResultList()
   */
  <T> List<T> findWithNamedQuery(String queryName, Map<String, Object> parameters,
      int firstResult, int maxResults);

  // --------------------
  // Find by Native Query
  // --------------------
  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL select statement,
   * executes the query and return the query results as a List.
   * </p>
   * 
   * @param sql the native SQL query to execute
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL select statement,
   * binds all arguments in <code>parameters</code>, if any, to corresponding named parameters in
   * the native SQL, then executes the query and return the query results as a List of entities. The
   * number of entities returned is limited by the <code>firstResult</code> and
   * <code>maxResults</code> parameters. A negative value for <code>firstResult</code> indicates
   * that <code>firstResult</code> should be ignored and {@link Query#setFirstResult(int)} is not
   * called. Likewise a negative value for <code>maxResults</code> indicates that
   * <code>maxResults</code> should be ignored and {@link Query#setMaxResults(int)} is not called.
   * </p>
   * 
   * @param sql the native SQL query to execute
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, Map<String, Object> parameters, int firstResult,
      int maxResults);

  /**
   * Creates an instance of {@link javax.persistence.Query} using a native SQL statement that
   * retrieves a single entity
   * type,
   * executes the query and return the query results as a List. The type of results is determined
   * by the <code>resultClass</code> parameter
   * 
   * @param sql the native SQL query to execute
   * @param resultClass the class of the resulting instance(s)
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, Class<T> resultClass);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL statement that
   * retrieves a single entity type, binds all arguments in <code>parameters</code>, if any, to
   * corresponding named parameters in the native SQL, executes the query and return the query
   * results as a List. The type of results is determined by the <code>resultClass</code> parameter.
   * The number of entities returned is limited by the <code>firstResult</code> and
   * <code>maxResults</code> parameters. A negative value for <code>firstResult</code> indicates
   * that <code>firstResult</code> should be ignored and {@link Query#setFirstResult(int)} is not
   * called. Likewise a negative value for <code>maxResults</code> indicates that
   * <code>maxResults</code> should be ignored and {@link Query#setMaxResults(int)} is not called.
   * </p>
   * 
   * @param sql the native SQL query to execute
   * @param resultClass the class of the resulting instance(s)
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored
   *          ({@link Query#setMaxResults(int)} is not called)
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, Class<T> resultClass, Map<String, Object> parameters,
      int firstResult, int maxResults);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL statement that
   * retrieves a result set with multiple entity types, executes the query and return the query
   * results as a List.
   * </p>
   * <p>
   * For example, if we want to create a <code>SqlResultSetMapping</code> for the <code>User</code>
   * entity, then we can use the &#064;SqlResultSetMapping annotation as follows:
   * </p>
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
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL statement that
   * retrieves a result set with multiple entity types, binds all arguments in
   * <code>parameters</code>, if any, to corresponding named parameters in the native SQL, executes
   * the query and return the query results as a List. The number of entities returned is limited by
   * the <code>firstResult</code> and <code>maxResults</code> parameters. A negative value for
   * <code>firstResult</code> indicates that <code>firstResult</code> should be ignored and
   * {@link Query#setFirstResult(int)} is not called. Likewise a negative value for
   * <code>maxResults</code> indicates that <code>maxResults</code> should be ignored and
   * {@link Query#setMaxResults(int)} is not called.
   * </p>
   * <p>
   * For example, if we want to create a <code>SqlResultSetMapping</code> for the <code>User</code>
   * entity, then we can use the &#064;SqlResultSetMapping annotation as follows:
   * </p>
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
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @return a list of results
   */
  <T> List<T> findByNativeQuery(String sql, String resultSetMapping,
      Map<String, Object> parameters, int firstResult, int maxResults);

  // ---------------
  // Find by Example
  // ---------------
  /**
   * Find entities based on an example entity.
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   *          primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param distinct whether the query should be distinct or not
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   *          <code>false</code> if the query should be an <b>"AND"</b> query.
   * @return a list of entities
   */
  <T> List<T> findByExample(T example, boolean distinct, boolean any);

  /**
   * <p>
   * Find entities based on an example entity. The number of entities returned is limited by the
   * <code>firstResult</code> and <code>maxResults</code> parameters. A negative value for
   * <code>firstResult</code> indicates that <code>firstResult</code> should be ignored and
   * {@link Query#setFirstResult(int)} is not called. Likewise a negative value for
   * <code>maxResults</code> indicates that <code>maxResults</code> should be ignored and
   * {@link Query#setMaxResults(int)} is not called.
   * </p>
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   *          primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param distinct Whether the query should be distinct or not
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   *          <code>false</code> if the query should be an <b>"AND"</b> query.
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @return a list of entities
   */
  <T> List<T> findByExample(T example, boolean distinct, boolean any,
      int firstResult, int maxResults);

  // ---------------
  // Find with Type
  // ---------------
  /**
   * <p>
   * Find all entities of a particular type by generating a select query; <strong>
   * <code>"SELECT e FROM Entity e"</code></strong>, where <code>Entity</code> is the given
   * <code>entityClass</code> parameter.
   * </p>
   * 
   * @param entityClass the entity class to find instances of
   * @return a list of entities
   * @throws IllegalArgumentException if produced query string is not valid
   */
  <T> List<T> findWithType(Class<T> entityClass);

  /**
   * <p>
   * Find all entities of a particular type by generating a select query; <strong>
   * <code>"SELECT e FROM Entity e"</code></strong>, where <code>Entity</code> is the given
   * <code>entityClass</code> parameter. The number of entities returned is limited by the
   * <code>firstResult</code> and <code>maxResults</code> parameters. A negative value for
   * <code>firstResult</code> indicates that <code>firstResult</code> should be ignored and
   * {@link Query#setFirstResult(int)} is not called. Likewise a negative value for
   * <code>maxResults</code> indicates that <code>maxResults</code> should be ignored and
   * {@link Query#setMaxResults(int)} is not called.
   * </p>
   * 
   * @param entityClass the entity class to find instances of
   * @param firstResult position of the first result to be returned by the query, numbered from 0. A
   *          negative value indicates that <code>firstResult</code> should be ignored and
   *          {@link Query#setFirstResult(int)} is not called
   * @param maxResults the maximum number of entities that should be returned by the query. A
   *          negative value indicates that <code>maxResults</code> should be ignored and
   *          {@link Query#setMaxResults(int)} is not called
   * @return a list of entities
   * @throws IllegalArgumentException if produced query string is not valid
   * @see CrudService#findWithType(Class)
   */
  <T> List<T> findWithType(Class<T> entityClass, int firstResult, int maxResults);

  // ---------------
  // Create Query
  // ---------------
  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} for executing a query in the Java
   * Persistence query language, binds all arguments in <code>parameters</code>, if any, to
   * corresponding named parameters in the JPQL query.
   * </p>
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @return the {@link javax.persistence.Query} instance
   * @see javax.persistence.EntityManager#createQuery(String)
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   * @see javax.persistence.Query#setParameter(String, Object)
   */
  Query createQuery(String jpql, Map<String, Object> parameters);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.NamedQuery} for executing a named query (in the
   * Java Persistence query language or in native SQL), binds all arguments in
   * <code>parameters</code>, if any, to corresponding named parameters in the named query.
   * </p>
   * 
   * @param queryName the name of the named query
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @return the {@link javax.persistence.Query} instance
   * @throws IllegalArgumentException if a query has not been
   *           defined with the given name
   * @throws IllegalStateException if called for a Java
   *           Persistence query language UPDATE or DELETE statement
   * @see javax.persistence.EntityManager#createNamedQuery(String)
   * @see javax.persistence.Query#setFirstResult(int)
   * @see javax.persistence.Query#setMaxResults(int)
   * @see javax.persistence.Query#setParameter(String, Object)
   * @see javax.persistence.Query#getResultList()
   */
  Query createNamedQuery(String jpql, Map<String, Object> parameters);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL select statement,
   * binds all arguments in <code>parameters</code>, if any, to corresponding named parameters in
   * the native SQL.
   * </p>
   * 
   * @param sql the native SQL query to execute
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @return the {@link javax.persistence.Query} instance
   */
  Query createNativeQuery(String sql, Map<String, Object> parameters);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL statement that
   * retrieves a single entity type, binds all arguments in <code>parameters</code>, if any, to
   * corresponding named parameters in the native SQL. The type of results is determined by the
   * <code>resultClass</code> parameter.
   * </p>
   * 
   * @param sql the native SQL query to execute
   * @param resultClass the class of the resulting instance(s)
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @return the {@link javax.persistence.Query} instance
   */
  Query createNativeQuery(String sql, Class<?> resultClass, Map<String, Object> parameters);

  /**
   * <p>
   * Creates an instance of {@link javax.persistence.Query} using a native SQL statement that
   * retrieves a result set with multiple entity types, binds all arguments in
   * <code>parameters</code>, if any, to corresponding named parameters in the native SQL.
   * </p>
   * <p>
   * For example, if we want to create a <code>SqlResultSetMapping</code> for the <code>User</code>
   * entity, then we can use the &#064;SqlResultSetMapping annotation as follows:
   * </p>
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
   * @param parameters a map with arguments to bind to named parameters in the query. Use a
   *          {@link CrudService#EMPTY_PARAMETER_MAP} if you don't have any parameters to bind
   * @return the {@link javax.persistence.Query} instance
   */
  Query createNativeQuery(String sql, String resultSetMapping, Map<String, Object> parameters);

  // -------

  /**
   * <p>
   * Merge the state of the given entity into the current persistence context, returning (a
   * potentially different object) the persisted entity. Basics - merge will take an exiting
   * 'detached' entity and merge its properties onto an existing entity. After merge this method
   * will call flush to synchronize the persistence context to the underlying database.
   * </p>
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
   * @throws IllegalArgumentException if instance is not an
   *           entity or is a removed entity
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#merge
   * @see javax.persistence.EntityManager#flush()
   */
  <T> T merge(T entity);

  /**
   * <p>
   * Merge a collection of entities, returning (a collection of potentially different objects) the
   * persisted entities. Basics - merge will take an exiting 'detached' entity and merge its
   * properties onto an existing entity. After the entities in the collection are merged, this
   * method will call flush to synchronize the persistence context to the underlying database.
   * </p>
   * 
   * @param entities A collection of entities
   * @return a collection of entities with the merged state
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if the <code>entities</code> parameter is null.
   * @throws IllegalArgumentException if an element in the collection is not an entity
   *           or is a removed entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#merge
   * @see javax.persistence.EntityManager#flush()
   */
  <T> Collection<T> mergeCollection(Collection<T> entities);

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
   * <p>
   * Remove an entity from persistent storage in the database. After remove this method will call
   * flush to synchronize the persistence context to the underlying database.
   * </p>
   * 
   * @param entityClass the entity class of the object to delete
   * @param id the Primary Key of the object to delete.
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#getReference
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws IllegalArgumentException if instance is not an
   *           entity or is a removed entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws javax.persistence.PersistenceException if the flush fails
   */
  void remove(Class<?> type, Object id);

  /**
   * <p>
   * Remove an entity from persistent storage in the database. After remove this method will call
   * flush to synchronize the persistence context to the underlying database.
   * </p>
   * 
   * @param entity the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#merge
   * @see javax.persistence.EntityManager#flush()
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws IllegalArgumentException if instance is not an
   *           entity or is a removed entity
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws javax.persistence.PersistenceException if the flush fails
   */
  void remove(Object entity);

  /**
   * <p>
   * Remove a collection of entities from persistent storage in the database. After the entities are
   * removed this method will call flush to synchronize the persistence context to the underlying
   * database.
   * </p>
   * 
   * @param entities collection of entities to remove
   * @throws IllegalArgumentException if <code>entities</code> parameter is null
   * @throws IllegalArgumentException if one of the elements in the <code>entities</code> collection
   *           is not an entity or is a removed entity
   * @throws IllegalStateException if this EntityManager has been closed
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws javax.persistence.PersistenceException if the flush fails
   */
  void removeCollection(Collection<Object> entities);

  /**
   * Remove all entities where conditions in the <code>example</code> parameter matches.
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   *          primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query,
   *          <code>false</code> if the query should be an <b>"AND"</b> query.
   */
  void removeByExample(Object example, boolean any);

  /**
   * <p>
   * Remove all instances of the specified class by executing a DELETE query, e.g. <br />
   * <code>delete e from Entity e</code>
   * </p>
   * 
   * @throws IllegalArgumentException if <code>entityClass</code> parameter is null
   * @throws IllegalArgumentException if not an entity
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws TransactionRequiredException if invoked on a
   *           container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is
   *           no transaction.
   * @throws javax.persistence.PersistenceException if the flush fails
   * @param entityClass The class to remove instances for
   */
  void removeWithType(Class<?> entityClass);

  /**
   * <p>
   * Persist or merge an entity. If the entity is already persisted then the state of the given
   * entity is merged into the current persistence context, otherwise the entity instance is
   * persisted (made managed and persistent). After store this method will call flush to synchronize
   * the persistence context to the underlying database.
   * </p>
   * 
   * @param entity the entity to persist or merge
   * @return the persisted entity
   * @see CrudService#persist(Object entity)
   * @see CrudService#merge(Object entity)
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is no transaction.
   * @throws javax.persistence.PersistenceException if the flush fails
   */
  <T> T store(T entity);

  /**
   * <p>
   * Persist or merge a collection of entities. If an entity in the collection is already persisted
   * then the state of the given entity is merged into the current persistence context, otherwise
   * the entity instance is persisted (made managed and persistent). For each entity in the
   * collection this method will call flush to synchronize the persistence context to the underlying
   * database.
   * </p>
   * 
   * @param entities a collection of entities to persist or merge
   * @return a collection of stored entities
   * @see CrudService#persist(Object entity)
   * @see CrudService#merge(CObject entity)
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws IllegalArgumentException if <code>entities</code> parameter is null
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   *           PersistenceContextType.TRANSACTION and there is no transaction.
   * @throws IllegalArgumentException if one of the elements in the collection is not an entity
   * @throws javax.persistence.PersistenceException if the flush fails
   */
  <T> Collection<T> storeCollection(Collection<T> entities);

  /**
   * <p>
   * Count entity instances by executing a COUNT query, e.g. <br />
   * <code>select count(*) from Entity</code>
   * </p>
   * 
   * @param entityClass the entity class to count instances of
   * @return the number of entities
   */
  int count(final Class<?> entityClass);

  /**
   * <p>
   * Refresh the state of an entity, with the given id, from the database overwriting changes made
   * to the entity, if any. Before refresh this method will get the entity instance by calling
   * {@link EntityManager#find(entityClass, id)}.
   * </p>
   * 
   * @param entityClass the entity type to refresh.
   * @param id the entity identity to refresh.
   * @return the refreshed entity
   * @throws IllegalStateException if this EntityManager has been closed.
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
   * @see javax.persistence.EntityManager#refresh(Object)
   * @see javax.persistence.EntityManager#find(Class, Object)
   */

  <T> T refresh(Class<T> entityClass, Object id);

  /**
   * <p>
   * Refresh the state of the instance from the database overwriting changes made to the entity, if
   * any. If the entity is not in the 'managed' state, this method will get the entity instance by
   * calling {@link EntityManager#find(entityClass, id)}, then refresh.
   * </p>
   * 
   * @param transientEntity the transient entity to refresh
   * @return the refreshed entity
   * @see javax.persistence.EntityManager#refresh(Object)
   * @see javax.persistence.EntityManager#find(Class, Object)
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
   * <p>
   * Refresh the state of a collection of entities from the database overwriting changes to the
   * entities, if any. If an entity in the collectionis not in the 'managed' state, this method will
   * get the entity instance by calling {@link EntityManager#find(entityClass, id)}, then refresh.
   * </p>
   * 
   * @param transientEntities a collection of transient entities to refresh
   * @return a collection of refreshed entities
   * @see javax.persistence.EntityManager#refresh(Object)
   * @see javax.persistence.EntityManager#find(Class, Object)
   * @see CrudService#refresh(Object)
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
  <T> Collection<T> refreshCollection(Collection<T> transientEntities);

  /**
   * <p>
   * Synchronize the persistence context to the underlying database.
   * </p>
   * <p>
   * If there are managed entities with changes pending, a flush is guaranteed to occur in two
   * situations. The first is when the transaction commits. A flush of any required changes will
   * occur before the database transaction has completed. The only other time a flush is guaranteed
   * to occur is when the entity manager flush() operation is invoked. This method allows developers
   * to manually trigger the same process that the entity manager internally uses to flush the
   * persistence context.
   * </p>
   * <p>
   * A flush basically consists of three components: new entities that need to be persisted, changed
   * entities that need to be updated, and removed entities that need to be deleted from the
   * database. All of this information is managed by the persistence context. It maintains links to
   * all of the managed entities that will be created or changed as well as the list of entities
   * that need to be removed.
   * </p>
   * <p>
   * When a flush occurs, the entity manager first iterates over the managed entities and looks for
   * new entities that have been added to relationships with cascade persist enabled. This is
   * logically equivalent to invoking <code>persist()</code> again on each managed entity just
   * before the flush occurs. The entity manager also checks to ensure the integrity of all of the
   * relationships. If an entity points to another entity that is not managed or has been removed,
   * then an exception may be thrown.
   * </p>
   * 
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws TransactionRequiredException if there is no transaction
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#flush()
   */
  void flush();

  /**
   * <p>
   * Clear the persistence context, causing all managed entities to become detached. Changes made to
   * entities that have not been flushed to the database will not be persisted.
   * </p>
   * <p>
   * This is usually required only for application-managed and extended persistence contexts that
   * are long-lived and have grown too large in size. For example, consider an application-managed
   * entity manager that issues a query returning several hundred entity instances. Once changes are
   * made to a handful of these instances and the transaction is committed, you have left in memory
   * hundreds of objects that you have no intention of changing any further. If you don't want to
   * close the persistence context, then you need to be able to clear out the managed entities, or
   * else the persistence context will continue to grow over time.
   * </p>
   * 
   * @throws IllegalStateException if this EntityManager has been closed.
   * @see javax.persistence.EntityManager#clear()
   */
  void clear();

  /**
   * Write anything to db that is pending operation and clear it.
   * 
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws TransactionRequiredException if there is
   *           no transaction
   * @throws PersistenceException if the flush fails
   * @see javax.persistence.EntityManager#flush()
   * @see javax.persistence.EntityManager#clear()
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
   * <p>
   * Get an entity into 'managed' state. If the entity isn't already in the 'managed' state, this
   * method will make the entity 'managed' by calling {@link EntityManager#find(entityClass, id)}
   * </p>
   * 
   * @param transientEntity the entity to get into managed state
   * @return the managed entity
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if not an entity
   */
  <T> T getManagedEntity(final T transientEntity);

  /**
   * Get the EntityManager wired to this service
   * 
   * @return the EntityManager wired to this service
   * @throws java.lang.IllegalStateException
   *           if EntityManager has not been set on this service before usage
   */
  EntityManager getEntityManager();

  /**
   * @param <T>
   * @param transientEntity
   * @param recursion
   * @param attributes
   * @return
   */
  <T> T touchRelations(T transientEntity, int recursion, String... attributes);

}
