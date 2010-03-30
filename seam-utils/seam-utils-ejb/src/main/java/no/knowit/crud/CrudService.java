package no.knowit.crud;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

@Local
public interface CrudService {

	/**
	 * Make an entity instance managed and persistent. 
	 * After persist this method will call flush and refresh to make shure the entity is in sync.
	 * 
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws EntityExistsException if the entity already exists.
   *         (The EntityExistsException may be thrown when the persist
   *         operation is invoked, or the EntityExistsException or
   *         another PersistenceException may be thrown at flush or commit
   *         time.)
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a
   *         container-managed entity manager of type
   *         PersistenceContextType.TRANSACTION and there is
   *         no transaction.
   * @throws PersistenceException if the flush fails
	 * @param entity the entity to persist
   * @see javax.persistence.EntityManager#persist(Object)
	 */
	public <T> T persist(T entity);

	/**
	 * Make a collection of entities managed and persistent. 
	 * Basics - persist will take the entity and put it into the db.
	 * 
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws EntityExistsException if an entity in the collection already exists.
   *         (The EntityExistsException may be thrown when the persist
   *         operation is invoked, or the EntityExistsException or
   *         another PersistenceException may be thrown at flush or commit
   *         time.)
   * @throws IllegalArgumentException if an element in the collection is not an entity
   * @throws TransactionRequiredException if invoked on a
   *         container-managed entity manager of type
   *         PersistenceContextType.TRANSACTION and there is
   *         no transaction.
   * @throws PersistenceException if the flush fails
	 * @param entities A collection of entities to persist
   * @see javax.persistence.EntityManager#persist(Object)
	 */
	public <T> Collection<T> persist(Collection<T> entities);

  /**
   * Finds an entity by the entitys primary key.<br/>
   *
   * @param entityClass the entity class to find an instance of 
   * @param id the primary key to find the entity by
   * @return The entity instance or null if the entity does not exist
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if the first argument does
   *         not denote an entity type or the second
   *         argument is not a valid type for that
   *         entity's primary key
   * @see javax.persistence.EntityManager#find
   */
	public <T> T find(Class<T> entityClass, Object id);

  /**
   * Find all entities of a particular type. 
   * This is similar to the JPQL statement: <br/>
   * <code>select e from Entity e as e</code>
   * @param entityClass
   * @return A list of populated entities
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if produced query string is not valid
   */
  public <T> List<T> find(Class<T> entityClass);
  
  
  /**
   * <p> Find all entities of a particular type. The number of entities 
   * returned is limited by the <code>startPosition</code> and 
   * <code>maxResult</code> parameters.</p>
   * @param startPosition position of the first result to be returned by the query, numbered from 0
   * @param maxResult the maximum number of entities that should be returned by the query
   * @return A list of populated entities
   * @throws java.lang.IllegalArgumentException if <code>startPosition</code>
   *         or <code>maxResult</code> is negative.
   * @throws IllegalStateException if this EntityManager has been closed
   */
  public <T> List<T> find(Class<T> entityClass, int startPosition, int maxResult);
  
  /**
   * <p> Find all entities of a particular type matching conditions 
   * in the <code>example</code> parameter.</p> 
   * 
   * @param example An entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer) vill be used to construct the query.
   * @param distinct Whether  the query should be distinct or not 
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query, 
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.  
   * @return A list of populated entities
   */
  public <T> List<T> find(T example, boolean distinct, boolean any);

  /**
   * <p> Find all entities of a particular type matching conditions 
   * in the <code>example</code> parameter. The number of entities 
   * returned is limited by the <code>startPosition</code> and 
   * <code>maxResult</code> parameters.</p>
   * 
   * @param example An entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer) vill be used to construct the query.
   * @param distinct Whether  the query should be distinct or not 
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query, 
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.  
   * @param startPosition position of the first result to be returned by the query, numbered from 0
   * @param maxResult the maximum number of entities that should be returned by the query
   * @return A list of populated entities
   */
  public <T> List<T> find(T example, boolean distinct, boolean any, int startPosition, int maxResult);
  
  
	/**
	 * Merge the state of the given entity into the current persistence context, 
	 * returning (a potentially different object) the persisted entity.
	 * entity. 
	 * 
	 * @param entity The entity instance to merge
   * @return The entity with the merged state
   * @see javax.persistence.EntityManager#merge
	 */
	public <T> T merge(T entity);
	
	/**
	 * Merge the collection of entities, returning (a collection of potentially different objects) the
	 * persisted entities. Basics - merge will take
	 * an exiting 'detached' entity and merge its properties onto an existing
	 * entity. The entity with the merged state is returned.
	 * 
	 * @param entities A collection of entities
	 * @return a collection of managed entities
	 * 
	 */
	public <T> Collection<T> merge(Collection<T> entities);


	/**
	 * Remove an entity from persistent storage in the database.  
	 * If the entity is not in the 'managed' state, it is merged 
	 * into the persistent context, then removed.
	 * 
	 * @param entity The object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#merge
	 */
	public void remove(Object entity);

	/**
	 * Remove an object from persistent storage in the database. 
	 * 
	 * @param entityClass The entity class of the object to delete
	 * @param id The Primary Key of the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#getReference
	 */
	public void remove(Class<?> entityClass, Object id);

	/**
	 * Remove a collection of entities from persistent storage in the database.
	 * 
	 * @param entities A collection of entities to delete
	 */
	public void remove(Collection<Object> entities);


	/**
   * <p>Remove all entities matching conditions in the <code>example</code> parameter.</p> 
	 * 
   * @param example An entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer) vill be used to construct the query.
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query, 
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.  
	 */
	public void remove(Object example, boolean any);

	
  /**
   * Make an entity instance managed and persistent.
   * If the entity is already persisted then the state of the given entity
   * is merged into the current persistence context.
   * @param entity The entity to persist or merge
   * @return The persisted entity
   * @see javax.persistence.EntityManager#persist(Object entity)
   * @see javax.persistence.EntityManager#merge(Object entity)
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   * 	PersistenceContextType.TRANSACTION and there is no transaction.
   */

	public <T> T store(T entity);
	
	/**
	 * Make a collection of entities managed and persistent.
	 * 
	 * @param entities A collection of entities to persist or merge
	 */
	public <T> Collection<T> store(Collection<T> entities);


	/**
	 * Refresh an entity that may have changed in another
	 * thread/transaction.  If the entity is not in the 
	 * 'managed' state, it is first merged into the persistent
	 * context, then refreshed.
	 * 
	 * @param transientEntity The Object to refresh.
	 */
	public <T> T refresh(T transientEntity);
	
	/**
	 * Refresh an entity that may have changed in another
	 * thread/transaction.  If the entity is not in the 
	 * 'managed' state, it is first merged into the persistent
	 * context, then refreshed.
	 * 
	 * @param entityClass The entity type to refresh.
	 * @param id The entity identity to refresh.
	 */
	public <T> T refresh(Class<T> entityClass, Object id);


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
   * @throws IllegalStateException if this EntityManager has been closed
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
   * @throws IllegalStateException if this EntityManager has been closed
   * @see javax.persistence.EntityManager#clear()
   */
	void clear();

	/**
	 * Write anything to db that is pending operation and clear it.
	 */
	public void flushAndClear();

  /**
   * Check if the instance belongs to the current persistence context.
   * @param entity the entity to check
   * @return true if the instance belongs to the current persistence context.
   * @see javax.persistence.EntityManager#contains(Object)
   */
  public boolean isManaged(Object entity);
  
  
	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName);

  @SuppressWarnings("unchecked")
	public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters);

  @SuppressWarnings("unchecked")
	public List findWithNamedQuery(String queryName, int resultLimit);

	public <T> List<T> findByNativeQuery(String sql, Class<T> type);

  @SuppressWarnings("unchecked")
	public List findWithNamedQuery(String namedQueryName,	Map<String, Object> parameters, int resultLimit);

}