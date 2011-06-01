package no.knowit.seam.example.test;

import org.apache.cactus.ServletTestCase;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.database.DatabaseModule;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;

public abstract class AbstractUnitilisedJSFUnitTestCase extends ServletTestCase {

    @Override
    protected void setUp() throws Exception {
        final Unitils unitils = Unitils.getInstance();
        unitils.getTestListener().beforeTestSetUp(this, getCurrentTestMethod());
        final Properties configuration = unitils.getConfiguration();
        boolean generateDtd = PropertyUtils.getBoolean(DBMaintainer.PROPKEY_GENERATE_DATA_SET_STRUCTURE_ENABLED, configuration);
        if (generateDtd) {
            DataSource dataSource = unitils.getModulesRepository().getModuleOfType(DatabaseModule.class)
                .getDataSourceAndActivateTransactionIfNeeded();
            getConfiguredDatabaseTaskInstance(DataSetStructureGenerator.class,
                configuration, new DefaultSQLHandler(dataSource)).generateDataSetStructure();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        Unitils.getInstance().getTestListener().afterTestTearDown(this, getCurrentTestMethod());
    }

    /**
     * Gets the method that has the same name as the current test.
     *
     * @return the method, not null
     * @throws org.unitils.core.UnitilsException
     *          if the method could not be found
     */
    protected Method getCurrentTestMethod() {
        String testName = getName();
        if (StringUtils.isEmpty(testName)) {
            throw new UnitilsException("Unable to find current test method. No test name provided (null) for test. Test class: " + getClass());
        }

        try {
            return getClass().getMethod(getName());

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Unable to find current test method. Test name: " + getName() + " , test class: " + getClass(), e);
        }
    }

    public static void run(TestComponentRunnable runnable) throws Throwable {
        try {
            runnable.run();
        } catch (Throwable e) {
            while (e.getCause() != null) {
                e = e.getCause();
            }
            throw e;
        }
    }

    public static interface TestComponentRunnable {
        void run() throws Throwable;
    }
}
