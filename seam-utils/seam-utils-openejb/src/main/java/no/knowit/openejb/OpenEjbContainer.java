package no.knowit.openejb;

import java.util.Map;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.ejb.spi.EJBContainerProvider;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.openejb.NoSuchApplicationException;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.UndeployException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.loader.SystemInstance;

/**
 * OpenEJB 3.1.3 does not provide an implementation of the 
 * {@link javax.ejb.embeddable.EJBContainer#createEJBContainer()} api call. Using a standard api 
 * call to bootstrap the embedded container will make it easier to move integration tests to any
 * JEE6 compliant container. Until version 3.2 of OpenEJB is released this "naive" implementation of 
 * {@link javax.ejb.spiEJBContainerProvider}, which provides the 
 * {@link javax.ejb.embeddable.EJBContainer#createEJBContainer()} method, should be sufficient.
 * 
 * <p><b>Note:</b> Since I don't know how to create a factory in OpenEJB so I had to make a copy of
 * the {@link javax.ejb.embeddable.EJBContainer} class and modify the 
 * {@link javax.ejb.embeddable.EJBContainer#createEJBContainer()} method. Tried to put the  
 * file <code>javax.ejb.spiEJBContainerProvider</code> with content 
 * <code>no.knowit.openejb.OpenEjbContainer$Provider</code> into the
 * <code>src/main/resources/META-INF/services</code> directory as it is done in OpenEJB-3.2, 
 * but it had no effect - most likely OpenEJB-3.2 spesific.
 * </p>
 * 
 * @author LeifOO
 */

public class OpenEjbContainer extends EJBContainer {

  private static final Logger log = Logger.getLogger(OpenEjbContainer.class);

  private static final String CONTEXT_CLOSE = "openejb.embedded.initialcontext.close";
  private static final String DESTROY = "DESTROY";
  private static final String LOCAL_INITIAL_CONTEXT_FACTORY = "org.apache.openejb.client.LocalInitialContextFactory";
  
  private static EJBContainer container = null;
  private static boolean contextCloseDestroy = false;
  
  private final Context context;
  
  static {
    try {
      Class.forName("org.apache.openejb.OpenEJB");
    } 
    catch (ClassNotFoundException e) {
      throw new IllegalStateException("OpenEJB is not available. Check your classpath!");
    }
  }
  
  private OpenEjbContainer(Context context) {
    this.context = context;
  }
  
  @Override
  public synchronized void close() {
    
    // If the container was bootstrapped with the property 
    // <code>openejb.embedded.initialcontext.close=DESTROY</code> then 
    // OpenEJB destroys the embedded container when the initial context is closed.
    // Note: Bootstrap the OpenEJB container with the 
    // <code>openejb.embedded.initialcontext.close=DESTROY</code> property if you want to restart 
    // the server between test scenarios. 
    // see: http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
    // see: http://openejb.apache.org/faq.html
    
    if (OpenEJB.isInitialized()) {
      
      // Perform a clean shutdown of this embedded instance
      Assembler assembler = SystemInstance.get().getComponent(Assembler.class);
      for (AppInfo appInfo : assembler.getDeployedApplications()) {
        try {
          assembler.destroyApplication(appInfo.jarPath);
        } 
        catch (UndeployException e) {
          throw new IllegalStateException(e);
        } 
        catch (NoSuchApplicationException e) {
          throw new IllegalStateException(e);
        }
      }
      
      try {
        context.close();
      } 
      catch (NamingException e) {
        throw new IllegalStateException(e);
      }

      if(!contextCloseDestroy) {
        OpenEJB.destroy();
      }
    }
    container = null;
  }

  @Override
  public Context getContext() {
    if (!OpenEJB.isInitialized()) {
      throw new IllegalStateException(
          "Could not obtain initial context: OpenEJB is not initialized.");
    }
    return context;
  }

  public static class Provider implements EJBContainerProvider {

    @Override
    public synchronized EJBContainer createEJBContainer(Map<?, ?> properties) {
      if(container == null) {
        container = createContainer(properties);
      }
      return container;
    }

    private EJBContainer createContainer(Map<?, ?> properties) {
      Properties p = new Properties();
      
      // Set the initial context factory
      p.put(Context.INITIAL_CONTEXT_FACTORY, LOCAL_INITIAL_CONTEXT_FACTORY);
      
      // Overrides default properties in p if key match
      p.putAll(properties);
      
      // See the close() method
      final String s = (String)p.get(CONTEXT_CLOSE);
      contextCloseDestroy = s == null ? false : s.equals(DESTROY);
      
      if(s != null) {
        log.debug(String.format(
          "Bootstrapping OpenEJB Embedded Container with property \"%s=%s\".", CONTEXT_CLOSE, s));
      }
      
      try {
        return new OpenEjbContainer(new InitialContext(p));
      } 
      catch (NamingException e) {
        throw new IllegalStateException("Could not Bootstrap OpenEJB Embedded Container.", e);
      }
    }
  } //~Provider
}
