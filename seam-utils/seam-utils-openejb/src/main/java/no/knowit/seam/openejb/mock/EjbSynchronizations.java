package no.knowit.seam.openejb.mock;

import static org.jboss.seam.annotations.Install.MOCK;

import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * <p>
 * Receives JTA transaction completion notifications from 
 * the EJB container, and passes them on to the registered
 * Synchronizations. This implementation
 * is fully aware of container managed transactions and is 
 * able to register Synchronizations for the container 
 * transaction.
 * </p>
 * <p>
 * LOO-20091209: Had to extend this class due to following exception:
 *   <code><pre>
 *   java.lang.AssertionError: javax.el.ELException: org.jboss.seam.InstantiationException: Could not instantiate Seam component: org.jboss.seam.transaction.synchronizations
 *   .
 *   .
 *   Caused by: javax.naming.NameNotFoundException: Name "/EjbSynchronizations/Local" not found.
 *   </pre></code>
 * </p>
 * 
 * @author Gavin King
 *
 */
@Stateful
@Name("org.jboss.seam.transaction.synchronizations")
@Scope(ScopeType.EVENT)
@Install(precedence=MOCK, dependencies="org.jboss.seam.transaction.ejbTransaction")
@BypassInterceptors
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EjbSynchronizations 
	extends org.jboss.seam.transaction.EjbSynchronizations 
	implements org.jboss.seam.transaction.LocalEjbSynchronizations, SessionSynchronization {
	
  @Remove
  @Destroy
  public void destroy() {
  }
}

