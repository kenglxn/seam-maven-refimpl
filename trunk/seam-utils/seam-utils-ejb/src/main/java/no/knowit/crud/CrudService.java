package no.knowit.crud;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

/**
 * Generic CrudService interface, a.k.a. DAO, a.k.a. Repository.<br/>
 * 
 * @author http://code.google.com/p/krank/
 * @author adam-bien.com
 * @author Leif Olsen
 */
@Local
public interface CrudService {
  
  static final String NAME = "crudService";

	/**
	 * Make an entity instance managed and persistent. 
	 * After persist this method will call flush and refresh to make sure the entity is in sync.
	 * 
   * @param entity the entity to persist
   * @return the entity with the persisted state
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
   * @see javax.persistence.EntityManager#persist(Object)
	 */
	public <T> T persist(T entity);

	/**
	 * Make a collection of entities managed and persistent. 
   * After persist this method will call flush and refresh to make sure the entity is in sync.
	 * 
   * @param entities A collection of entities to persist
   * @return a collection of entities with the persisted state
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
   * @see javax.persistence.EntityManager#persist(Object)
   * @see CrudService#persist(Object)
	 */
	public <T> Collection<T> persist(Collection<T> entities);

  /**
   * Find by primary key.
   *
   * @param entityClass the entity class to find an instance of 
   * @param id the primary key to find the entity by
   * @return the found entity instance or null if the entity does not exist
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
   * @param entityClass the entity class to find instances of 
   * @return a list of entities
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
   * @return a list of entities
   * @throws java.lang.IllegalArgumentException if <code>startPosition</code>
   *         or <code>maxResult</code> is negative.
   * @throws IllegalStateException if this EntityManager has been closed
   */
  public <T> List<T> find(Class<T> entityClass, int startPosition, int maxResult);
  
  /**
   * <p>Find entities based on an example entity.</p> 
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param distinct whether  the query should be distinct or not 
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query, 
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.  
   * @return a list of entities
   */
  public <T> List<T> find(T example, boolean distinct, boolean any);

  /**
   * <p>Find entities based on an example entity. The number of entities 
   * returned is limited by the <code>startPosition</code> and 
   * <code>maxResult</code> parameters.</p>
   * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param distinct Whether  the query should be distinct or not 
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query, 
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.  
   * @param startPosition position of the first result to be returned by the query, numbered from 0
   * @param maxResult the maximum number of entities that should be returned by the query
   * @return a list of entities
   */
  public <T> List<T> find(T example, boolean distinct, boolean any, int startPosition, int maxResult);
  
  
  
  @SuppressWarnings("unchecked")
  public List findByNamedQuery(String namedQueryName);

  @SuppressWarnings("unchecked")
  public List findByNamedQuery(String namedQueryName, Map<String, Object> parameters);

  @SuppressWarnings("unchecked")
  public List findByNamedQuery(String queryName, int startPOsition, int maxResult);

  @SuppressWarnings("unchecked")
  public List findByNamedQuery(String namedQueryName, Map<String, Object> parameters, 
      int startPosition, int maxResult);

  public <T> List<T> findByNativeQuery(String sql, Class<T> type);

  
	/**
	 * Merge the state of the given entity into the current persistence context, 
	 * returning (a potentially different object) the persisted entity.
	 * entity. 
	 * 
	 * @param entity the entity instance to merge
   * @return the instance that the state was merged to
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
	 * @return a collection of entities with the merged state
	 * 
	 */
	public <T> Collection<T> merge(Collection<T> entities);


	/**
	 * Remove an entity from persistent storage in the database.  
	 * If the entity is not in the 'managed' state, it is merged 
	 * into the persistent context, then removed.
	 * 
	 * @param entity the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#merge
	 */
	public void remove(Object entity);

	/**
	 * Remove all instances of the specified class
   * @throws IllegalArgumentException if <code>entityClass</code> parameter is null
   * @throws java.lang.IllegalStateException if this EntityManager has been closed
   * @throws TransactionRequiredException if there is  no transaction
	 * @param entityClass The class to remove instances for
	 */
	public void remove(Class<?> entityClass);
	
	/**
	 * Remove an object from persistent storage in the database. 
	 * 
	 * @param entityClass the entity class of the object to delete
	 * @param id the Primary Key of the object to delete.
   * @see javax.persistence.EntityManager#remove
   * @see javax.persistence.EntityManager#getReference
	 */
	public void remove(Class<?> entityClass, Object id);

	/**
	 * Remove a collection of entities from persistent storage in the database.
	 * 
	 * @param entities r collection of entities to remove
	 */
	public void remove(Collection<Object> entities);


	/**
   * <p>Remove all entities where conditions in the <code>example</code> parameter matches.</p> 
	 * 
   * @param example an entity instantiated with the fields to match. Only non <code>null</code>
   * 	primitives (e.g. String, Integer, Date) will be used to construct the query.
   * @param any <code>true</code> if the query should produce an <b>"OR"</b> query, 
   * 	<code>false</code> if the query should be an <b>"AND"</b> query.  
	 */
	public void remove(Object example, boolean any);

	
  /**
   * Persist or merge an entity. If the entity is already persisted then the state of the given 
   * entity is merged into the current persistence context, otherwise the entity instance is 
   * persisted (made managed and persistent).
   * @param entity the entity to persist or merge
   * @return the persisted entity
   * @see javax.persistence.EntityManager#persist(Object entity)
   * @see javax.persistence.EntityManager#merge(Object entity)
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   * 	PersistenceContextType.TRANSACTION and there is no transaction.
   */
	public <T> T store(T entity);
	
	/**
	 * Persist or merge a collection of entities. If an entity in the collection is already persisted 
	 * then the state of the given entity is merged into the current persistence context, otherwise 
	 * the entity instance is persisted (made managed and persistent).
	 * @param entities a collection of entities to persist or merge
	 * @return a collection of stored entities
   * @see javax.persistence.EntityManager#persist(Object entity)
   * @see javax.persistence.EntityManager#merge(Object entity)
   * @throws IllegalStateException if this EntityManager has been closed.
   * @throws IllegalArgumentException if not an entity
   * @throws TransactionRequiredException if invoked on a container-managed entity manager of type
   *  PersistenceContextType.TRANSACTION and there is no transaction.
	 */
	public <T> Collection<T> store(Collection<T> entities);


	/**
	 * <p>Count entity instances</p> 
   * @param entityClass the entity class to count instances of
	 * @return the number of entities
	 */
	public int count(final Class<?> entityClass);

	/**
	 * Refresh an entity that may have changed in another
	 * thread/transaction.  If the entity is not in the 
	 * 'managed' state, it is first merged into the persistent
	 * context, then refreshed.
	 * 
	 * @param transientEntity the Object to refresh
	 * @see javax.persistence.EntityManager#refresh(Object)
	 */
	public <T> T refresh(T transientEntity);
	
	/**
	 * Refresh an entity that may have changed in another
	 * thread/transaction.  If the entity is not in the 
	 * 'managed' state, it is first merged into the persistent
	 * context, then refreshed.
	 * 
	 * @param entityClass the entity type to refresh.
	 * @param id the entity identity to refresh.
   * @see javax.persistence.EntityManager#refresh(Object)
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
	 * @see CrudService#flush()
   * @see javax.persistence.EntityManager#flush()
   * @see CrudService#clear()
   * @see javax.persistence.EntityManager#clear()
	 */
	public void flushAndClear();

  /**
   * Check if the instance belongs to the current persistence context.
   * @param entity the entity to check
   * @return true if the instance belongs to the current persistence context.
   * @see javax.persistence.EntityManager#contains(Object)
   */
  public boolean isManaged(Object entity);
}