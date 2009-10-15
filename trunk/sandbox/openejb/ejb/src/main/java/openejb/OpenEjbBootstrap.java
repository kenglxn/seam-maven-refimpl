package openejb2;

import org.apache.openejb.OpenEJB;
import org.apache.log4j.Logger;

import java.util.Properties;

public class OpenEjbBootstrap {

    private Logger logger = Logger.getLogger(this.getClass());

    public void startAndDeployResources()
            throws Exception {

        startAndDeployResources(null);
    }

    public void startAndDeployResources(final Properties pProperties)
            throws Exception {

        try {
            String base = "target/test-classes";
            System.setProperty("openejb.home", base);
            System.setProperty("openejb.base", base);

            Properties props = new Properties();
            props.put("openejb.jndiname.format", "{deploymentId}/{interfaceType.annotationName}");
            props.put("openejb.configuration", base + "/openejb.xml");

            if (pProperties != null) {
                props.putAll(pProperties);
            }

            // DEBUG - print classpath
            /*
            ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader ();
            URL[] urls = ((URLClassLoader) sysClassLoader).getURLs ();
            LoggingStaticService.info ("############################");
            LoggingStaticService.info ("CLASSPATH:");
            for (URL url : urls) {
                String prefix;
                String s = url.getFile ();
                if (s.endsWith (".jar")) {
                    prefix = "JAR: ";
                } else {
                    prefix = "DIR: ";
                }
                LoggingStaticService.info (prefix + s);
            }
            LoggingStaticService.info ("############################");
            */
            // DEBUG

            OpenEJB.init(props);
        }
        catch (Exception ex) {
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    private boolean resourceExists(final String pName) {

        return Thread.currentThread().getContextClassLoader().getResource(pName) != null;
    }

}
