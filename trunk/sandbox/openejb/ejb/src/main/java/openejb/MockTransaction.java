package openejb2;

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

@Name("org.jboss.seam.transaction.transaction_container")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.MOCK)
@BypassInterceptors
public class MockTransaction
        extends Transaction {

    public static String EJB_CONTEXT_NAME = "java:comp.ejb3/EJBContext";

    public static final String STANDARD_EJB_CONTEXT_NAME = "java:comp/EJBContext";

    @Override
    protected org.jboss.seam.transaction.UserTransaction createCMTTransaction ()
            throws NamingException {

        return new CMTTransaction(getEJBContext ());
    }

    public static EJBContext getEJBContext ()
            throws NamingException {

        try {
            return (EJBContext) BootstrapEnvironment.getStaticInitialContext ()
                    .lookup (EJB_CONTEXT_NAME);
        }
        catch (NameNotFoundException nnfe) {
            return (EJBContext) BootstrapEnvironment.getStaticInitialContext ()
                    .lookup (STANDARD_EJB_CONTEXT_NAME);
        }
    }

    @Override
    protected UserTransaction getUserTransaction ()
            throws NamingException {

        InitialContext context = BootstrapEnvironment.getStaticInitialContext ();

        try {
            return (UserTransaction) context.lookup ("java:comp/UserTransaction");
        }
        catch (NameNotFoundException nnfe) {
            try {
                //Embedded JBoss has no java:comp/UserTransaction
                UserTransaction ut = (UserTransaction) context.lookup ("UserTransaction");
                ut.getStatus (); //for glassfish, which can return an unusable UT
                return ut;
            }
            catch (Exception e) {
                throw nnfe;
            }
        }
    }
}
