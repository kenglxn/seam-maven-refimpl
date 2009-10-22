package openejb;

import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.transaction.NoTransaction;

import javax.transaction.SystemException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;

@BypassInterceptors
public class ReallyNoTransaction
        extends NoTransaction {

    @Override
    public void begin () throws NotSupportedException, SystemException {

    }

    @Override
    public void commit () throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
                                 IllegalStateException, SystemException {

    }

    @Override
    public int getStatus () throws SystemException {
        return Status.STATUS_NO_TRANSACTION;
    }

    @Override
    public void rollback () throws IllegalStateException, SecurityException, SystemException {

    }

    @Override
    public void setRollbackOnly () throws IllegalStateException, SystemException {

    }

    @Override
    public void setTransactionTimeout (final int pTimeout) throws SystemException {

    }

    @Override
    public void registerSynchronization (final Synchronization sync) {

    }

}
