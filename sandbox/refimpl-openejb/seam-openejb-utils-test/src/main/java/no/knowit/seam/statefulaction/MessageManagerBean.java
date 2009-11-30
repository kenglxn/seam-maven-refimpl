package no.knowit.seam.statefulaction;


import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import no.knowit.seam.model.Message;

import java.io.Serializable;
import java.util.List;

@Stateful
@Name("messageManager")
@Scope(ScopeType.SESSION)

public class MessageManagerBean implements Serializable, MessageManager {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

  @DataModel
  private List<Message> messageList;
  
  @DataModelSelection
  @Out(required=false)
  private Message message;
  
	@Override
  @Factory("messageList")
	public void findMessages() {
		assert entityManager != null;
		messageList = entityManager.createQuery("from Message m order by m.datetime desc").getResultList();
	}

	@Override
  public void select() {
		if (message != null)
			message.setRead(true);
	}

	@Override
	public void delete() {
		if (message != null) {
			messageList.remove(message);
			entityManager.remove(message);
			message = null;
		}
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public void addMessage(Message message) {
		entityManager.persist(message);
	}
	
	@Override
	public void deleteAllMessages() {
		assert entityManager != null;
		entityManager.createQuery("delete from Message").executeUpdate();
	}

	@Override
  @Remove 
  @Destroy
  public void destroy() {
  }	
}
