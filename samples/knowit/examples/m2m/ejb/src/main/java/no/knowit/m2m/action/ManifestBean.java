package no.knowit.m2m.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

@Name("manifest")
public class ManifestBean {
	
    @Logger private Log log;
    @In StatusMessages statusMessages;

    @Factory("manifest.attributes")
    public List<Object> getAttributes() {
    	log.debug("Reading war/META-INF/MANIFEST.MF");
    	
    	List<Object> result = new ArrayList<Object>(readManifestAttributes());
    	if(result == null) {
            statusMessages.add(Severity.ERROR, "Could not read war/META-INF/MANIFEST.MF");
        	log.error("Could not read war/META-INF/MANIFEST.MF");
    	}
    	return result;
    }
    
    
    /*
     * This is not production code. Normally we'll put code like this into application startup
     */
	@SuppressWarnings("unchecked")
	public static Set<Map.Entry<Object,Object>> readManifestAttributes() {
		Manifest manifest;
	    try {
			ServletContext sc = ServletLifecycle.getServletContext();
			manifest = new Manifest( sc.getResourceAsStream("META-INF/MANIFEST.MF") );
	    }
	    catch (IOException e) {
			return null;
	    }
	    
		Attributes attributes = manifest.getMainAttributes();
		//return attributes.entrySet();
		
		Set<Map.Entry<Object,Object>> orderedAttributes = new TreeSet<Map.Entry<Object,Object>>(
			new Comparator<Object>() {
				public int compare(Object lhs, Object rhs) {
					return  ((Map.Entry)lhs).getKey().toString().compareTo( 
							((Map.Entry)rhs).getKey().toString());
				}
			}
		);
		
		Iterator<?> i = attributes.entrySet().iterator();
		while(i.hasNext()) {
			Map.Entry entry = (Map.Entry)i.next();
			orderedAttributes.add(entry);
		}
		
		return orderedAttributes;
	}
    
}
