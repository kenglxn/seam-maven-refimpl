package openejb;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Expressions;

import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;

public class ContainerWhiteBoxTest
        extends BootstrapEnvironment {

    /**
     * ********* Methods ***********
     */

    protected static <T> T getContextComponent (final Class<T> pClass) {

        return (T) Component.getInstance (pClass);
    }

    protected static <T> T getContextComponent (final String pName) {

        return (T) Component.getInstance (pName);
    }

    protected static <T> T getContextComponent (final String pName,
                                                final ScopeType pScope) {

        return (T) Component.getInstance (pName, pScope);
    }

    /**
     * Call a method binding
     */
    protected Object invokeMethod (final String pMethodExpression) {

        return Expressions.instance ().createMethodExpression (pMethodExpression).invoke ();
    }

    /**
     * Evaluate (get) a value binding
     */
    protected Object getValue (final String pValueExpression) {

        return Expressions.instance ().createValueExpression (pValueExpression).getValue ();
    }

    /**
     * Set a value binding
     */
    protected void setValue (final String pValueExpression,
                             final Object pValue) {

        Expressions.instance ().createValueExpression (pValueExpression).setValue (pValue);
    }

    /**
     * ********* Database Component Test Class ***********
     */

    public abstract class DBComponentTest
            extends ComponentTest {

        /**
         * ********* Fields ***********
         */

        private EntityTransaction transaction;

        private EntityManager em;

        /**
         * ********* Setup ***********
         */

        @Override
        public void run () throws Exception {

            super.run ();
            cleanEM ();
        }

        @Override
        protected final void testComponents () throws Exception {
            initEM ();
            testDBComponents ();
        }

        protected abstract void testDBComponents () throws Exception;

        protected void initEM () {

            em = (EntityManager) Component.getInstance ("em");
            transaction = em.getTransaction ();
            transaction.begin ();
            transaction.setRollbackOnly ();
        }

        protected void cleanEM () {

            if (transaction != null && transaction.isActive ()) {
                transaction.rollback ();
            }
        }

        /**
         * ********* Get/Set ***********
         */

        public EntityManager getEm () {
            return em;
        }

        public void setEm (final EntityManager pEm) {
            em = pEm;
        }

        public EntityTransaction getTransaction () {
            return transaction;
        }

        public void setTransaction (final EntityTransaction pTransaction) {
            transaction = pTransaction;
        }
    }
}
