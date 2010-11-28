package no.knowit.m2m.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
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
    
    Set<Entry<Object, Object>> result = readManifestAttributes();
    if(result == null) {
      statusMessages.add(Severity.ERROR, "Could not read war/META-INF/MANIFEST.MF");
      log.error("Could not read war/META-INF/MANIFEST.MF");
    }
    return new ArrayList<Object>(result);
  }
    
    
    /*
     * This is not production code. Normally we should put code like this into application startup
     */
  
  private static Manifest getManifest() {
    try {
      ServletContext sc = ServletLifecycle.getServletContext();
      return new Manifest( sc.getResourceAsStream("META-INF/MANIFEST.MF") );
    }
    catch (IOException e) {
      return null;
    }
  }
  
  public static Set<Map.Entry<Object,Object>> readManifestAttributes() {
    Manifest manifest = getManifest();
    if(manifest == null) {
      return null;
    }
      
    Attributes attributes = manifest.getMainAttributes();
    Set<Map.Entry<Object,Object>> orderedAttributes = new TreeSet<Map.Entry<Object,Object>>(
      new Comparator<Map.Entry<Object,Object>>() {
        public int compare(Map.Entry<Object,Object> lhs, Map.Entry<Object,Object> rhs) {
          return  lhs.getKey().toString().compareTo(rhs.getKey().toString());
        }
      }
    );
    
    for(Map.Entry<Object,  Object> entry : attributes.entrySet()) {
      orderedAttributes.add(entry);
    }
    
    return orderedAttributes;
  }
  
}
