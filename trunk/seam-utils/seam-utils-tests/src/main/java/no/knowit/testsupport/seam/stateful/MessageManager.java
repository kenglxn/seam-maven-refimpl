package no.knowit.testsupport.seam.stateful;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import no.knowit.testsupport.model.Message;

@Local
public interface MessageManager {
	public abstract void addMessage(Message movie);
	public abstract void delete();
	public abstract void deleteAllMessages();
	public abstract void destroy();
	public abstract EntityManager getEntityManager();
	public abstract void findMessages();
	public abstract void select();
}