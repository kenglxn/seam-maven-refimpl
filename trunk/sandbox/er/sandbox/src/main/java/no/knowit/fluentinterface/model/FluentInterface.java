package no.knowit.fluentinterface.model;

import java.beans.Statement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class FluentInterface<T> implements InvocationHandler {
	  final Object obj;

	  private FluentInterface(final Object obj) {
	    this.obj = obj;
	  }

	  public static <T> T create(Object object, Class<T> fluentInterfaceClass) {
	    FluentInterface<T> handler = new FluentInterface<T>(object);
	    @SuppressWarnings("unchecked")
	    T fluentInterface = (T) Proxy.newProxyInstance(
	       fluentInterfaceClass.getClassLoader(),
	        new Class[]{fluentInterfaceClass},
	        handler);
	    return fluentInterface;
	  }

	  public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
	    try {
	      String name = m.getName();
	      if ("create".equals(name)) {
	        return obj;
	      } else {
	        String setter = String.format("set%s%s", name.substring(0, 1).toUpperCase(),name.substring(1));
	        Statement stmt = new Statement(this.obj, setter, args);
	        stmt.execute();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	    }
	    return proxy;
	  }
	}

