package no.knowit.seam.openejb.mock;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.UserTransaction;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.CMTTransaction;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.ScopeType;

import javax.ejb.EJBContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.InitialContext;

/**
 * Copied from http://seamframework.org/Community/UsingOpenEJBForIntegrationTesting
 * @author LeifOO
 *
 */
@Name("org.jboss.seam.transaction.transaction_container")
//@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.MOCK)
@BypassInterceptors
public class MockTransaction extends Transaction {
	
	private static final LogProvider log = Logging.getLogProvider(MockTransaction.class); 


	public static String EJB_CONTEXT_NAME = "java:comp.ejb3/EJBContext";
	public static final String STANDARD_EJB_CONTEXT_NAME = "java:comp/EJBContext";

	@Override
	protected org.jboss.seam.transaction.UserTransaction createCMTTransaction() throws NamingException {
		
		log.debug("**** MockTransaction.createCMTTransaction");
		
		return new CMTTransaction(getEJBContext());
	}

	public static EJBContext getEJBContext() throws NamingException {
		
		try {
			log.debug("**** try: MockTransaction.getEJBContext, EJB_CONTEXT_NAME -> " + EJB_CONTEXT_NAME);
			
			return (EJBContext) AbstractSeamOpenEjbTest.getStaticInitialContext().lookup(EJB_CONTEXT_NAME);
		} 
		catch (NameNotFoundException nnfe) {
			log.debug("**** catch: MockTransaction.getEJBContext, STANDARD_EJB_CONTEXT_NAME -> " + STANDARD_EJB_CONTEXT_NAME);

			return (EJBContext) AbstractSeamOpenEjbTest.getStaticInitialContext().lookup(STANDARD_EJB_CONTEXT_NAME);
		}
	}

	@Override
	protected UserTransaction getUserTransaction() throws NamingException {
		
		InitialContext context = AbstractSeamOpenEjbTest.getStaticInitialContext();
		try {
			log.debug("**** try: MockTransaction.getUserTransaction -> context.lookup(java:comp/UserTransaction) ");

			return (UserTransaction) context.lookup("java:comp/UserTransaction");
		} 
		catch (NameNotFoundException nnfe) {
			log.debug("**** catch: MockTransaction.getUserTransaction -> context.lookup(UserTransaction) ");
			try {
				// Embedded JBoss has no java:comp/UserTransaction
				UserTransaction ut = (UserTransaction) context.lookup("UserTransaction");
				ut.getStatus(); // for glassfish, which can return an unusable UT
				return ut;
			} 
			catch (Exception e) {
				throw nnfe;
			}
		}
	}
}
