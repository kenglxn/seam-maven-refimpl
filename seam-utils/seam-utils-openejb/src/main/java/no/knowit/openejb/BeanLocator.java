/**
 * This file is part of javaee-patterns.
 * 
 * javaee-patterns is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * javaee-patterns is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.opensource.org/licenses/gpl-2.0.php>.
 * 
 * Copyright (c) 04. August 2009 Adam Bien, blog.adam-bien.com
 * http://press.adam-bien.com
 */
package no.knowit.openejb;

import java.util.Properties;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/**
 * Hides and simplifies the access to objects stored in the JNDI-tree.
 * 
 * @author Adam Bien, www.adam-bien.com
 */
public final class BeanLocator {

  private BeanLocator() {
  }

  /**
   * @param clazz
   *          the type (Business Interface or Bean Class)
   * @param jndiName
   *          the global JNDI name with the pattern:
   *          java:global[/<app-name>]/<module-name>/<bean-name>#<fully-qualified-
   *          interface-name>
   * @return The local or remote reference to the bean.
   */
  public static <T> T lookup(final Class<T> clazz, final String jndiName) {
    final Object bean = lookup(jndiName);
    return clazz.cast(PortableRemoteObject.narrow(bean, clazz));
  }

  public static Object lookup(final String jndiName) {
    Context context = null;
    try {
      context = new InitialContext();
      return context.lookup(jndiName);
    }
    catch (final NamingException ex) {
      throw new IllegalStateException(
        "Cannot connect to bean: " + jndiName + " Reason: " + ex, ex.getCause());
    }
    finally {
      try {
        context.close();
      }
      catch (final NamingException ex) {
        throw new IllegalStateException(
          "Cannot close InitialContext. Reason: " + ex, ex.getCause());
      }
    }
  }

  public static class GlobalJNDIName {

    private static final String PREFIX = "java:global";
    private static final String SEPARATOR = "/";
    private static final String PROPERTY_FILE = "/jndi.properties";
    private static final String MODULE_NAME_KEY = "module.name";
    private static final String APP_NAME_KEY = "application.name";
    private final StringBuilder builder;
    private final Properties globalConfiguration;
    private String appName;
    private String moduleName;
    private String beanName;
    private String businessInterfaceName;

    public GlobalJNDIName() {
      builder = new StringBuilder();
      builder.append(PREFIX).append(SEPARATOR);
      globalConfiguration = new Properties();
      try {
        globalConfiguration.load(this.getClass().getResourceAsStream(PROPERTY_FILE));
      }
      catch (final Exception ex) {
        System.out.println("Cannot load configuration: " + ex);
      }
      appName = globalConfiguration.getProperty(APP_NAME_KEY);
      moduleName = globalConfiguration.getProperty(MODULE_NAME_KEY);
    }

    public GlobalJNDIName withAppName(final String appName) {
      this.appName = appName;
      return this;
    }

    public GlobalJNDIName withModuleName(final String moduleName) {
      this.moduleName = moduleName;
      return this;
    }

    public GlobalJNDIName withBeanName(final String beanName) {
      this.beanName = beanName;
      return this;
    }

    public GlobalJNDIName withBeanName(final Class<?> beanClass) {
      return withBeanName(computeBeanName(beanClass));
    }

    public GlobalJNDIName withBusinessInterface(final Class<?> interfaze) {
      businessInterfaceName = interfaze.getName();
      return this;
    }

    String computeBeanName(final Class<?> beanClass) {
      final Stateless stateless = beanClass.getAnnotation(Stateless.class);
      if (stateless != null && isNotEmpty(stateless.name())) {
        return stateless.name();
      }
      final Stateful stateful = beanClass.getAnnotation(Stateful.class);
      if (stateful != null && isNotEmpty(stateful.name())) {
        return stateful.name();
      }
      final Singleton singleton = beanClass.getAnnotation(Singleton.class);
      if (singleton != null && isNotEmpty(singleton.name())) {
        return singleton.name();
      }
      return beanClass.getSimpleName();
    }

    private boolean isNotEmpty(final String name) {
      return name != null && !name.isEmpty();
    }

    public String asString() {
      if (appName != null) {
        builder.append(appName).append(SEPARATOR);
      }
      builder.append(moduleName).append(SEPARATOR);
      builder.append(beanName);
      if (businessInterfaceName != null) {
        builder.append("#").append(businessInterfaceName);
      }
      return builder.toString();
    }

    public <T> T locate(final Class<T> clazz) {
      return BeanLocator.lookup(clazz, asString());
    }

    public Object locate() {
      return BeanLocator.lookup(asString());
    }
  } //~GlobalJNDIName
}
