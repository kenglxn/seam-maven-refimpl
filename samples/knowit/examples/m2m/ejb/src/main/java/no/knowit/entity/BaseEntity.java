/**
 *. 
 */
package no.knowit.entity;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import java.io.Serializable;
import java.util.UUID;

/**
 * Abstract entity class.
 * This class contains the common attributes that each entity class in the domain 
 * model will inherit - except for the primary key field(s).
 *  
 * @author Leif Olsen.
 *
 */

@MappedSuperclass
public abstract class BaseEntity<ID> implements Serializable {

  private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
  private ID id;
  
  @Version 
  private Long version;
  
  @Column(unique=true, nullable=false, updatable=false, length = 36)
  private String uuid = UUID.randomUUID().toString();
  
	/**
   * @return the persistence id (primary key)
   */
  public ID getId() {
    return id;
  }
  
  /**
   * @param id the id to set
   */
  public void setId(ID id) {
    this.id = id;
  }
  
  /**
   * @return the entity version
   */
  public Long getVersion() {
    return this.version;
  }

  /**
   * 
   * @return
   */
  public String getUuid()
  {
  	return uuid;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final BaseEntity that = (BaseEntity) o;
    return uuid.equals(that.uuid);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getClass().getName())
    	.append("{id='").append(id).append("', ")
    	.append("uuid='").append(uuid).append("', ");
    return sb.toString();    
  }
  
}