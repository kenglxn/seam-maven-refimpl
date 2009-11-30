package no.knowit.seam.statefulaction;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import no.knowit.seam.model.Message;

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