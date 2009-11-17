package no.knowit.example.jpqlconsole;

import java.io.Serializable;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang.time.StopWatch;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

@Name("jpqlConsoleAction")
public class JpqlConsoleAction implements Serializable {

	private static final long serialVersionUID = -4345141619125985848L;
	
	private static final List<String> ALLOWED_TYPES = Arrays.asList(
		"java.lang.Boolean", "java.lang.Byte", "java.lang.Character",	"java.lang.Double", "java.lang.Float", 
		"java.lang.Integer", "java.lang.Long", "java.lang.Number"   , "java.lang.Short" ,	"java.lang.String",
		"java.util.Date" );
		
	@In
	private EntityManager entityManager;

	@Logger
	private Log log;

	@In
	private FacesMessages facesMessages;
	
	private Integer iterations = 1;
	private Integer firstResult = 0;
	private Integer maxResults = 10;
	private Boolean printResults = false;
	private String jpql;
	private Long jpqlResultCount = 0L;
	private List<Object> jpqlResults = new ArrayList<Object>();
	private List<ColumnData> jpqlColumns = new ArrayList<ColumnData>();
	private Boolean isSingleSubject = false; // Becomes true when jpql like [select e.property from Entity e]
	
	//@Factory("jpqlResults")
	public List<Object> getJpqlResults() {
		log.debug("Accessing getJpqlResults, size: " + jpqlResults.size());
		return jpqlResults;
	}
	
	@Factory("jpqlColumns")  // rich:columns need this to be a factory
	public List<ColumnData> getJpqlColumns() {
		log.debug("Accessing getJpqlColumns, size: " + jpqlColumns.size());
		return jpqlColumns;
	}
	
	
	public void executeJPQL() {
		jpqlResults.clear();
		jpqlColumns.clear();
		
		if (jpql == null || jpql.trim().length() == 0) {
			facesMessages.add(Severity.ERROR, "No jpql found, enter a jpql statement");
			log.error("No jpql found, enter a jpql statement");
			return;
		}
		
		try {
			jpqlResults = executeQueryNTimes();
		}
		catch (Exception e) {
			facesMessages.add(Severity.ERROR, "Could not create query, check your syntax, your JPQL is not correct");
			log.error("Could not create query, check your syntax, your JPQL is not correct: ", e);
			return;
		}
		
		try {
			jpqlResultCount = executeCountQuery();
		}
		catch (Exception e) {
			facesMessages.add(Severity.WARN, "Was not able to execute count query: [" + getCountJpql(jpql) + "]");
			log.warn("Was not able to execute count query: [" + getCountJpql(jpql) + "]", e);
			return;
		}
		
		initColumnData();
		
		if (printResults.booleanValue() == true) {
			logResults();
		}
	}

	
	
	private String getCountJpql(final String q) {
		String s = q.toLowerCase();
		int i = s.indexOf("from");
		int j = s.indexOf("order ");
		return i < 0 ? null : "select count(*) " + q.substring(i, (j>0?j:q.length()));
	}

	private Long executeCountQuery() {
		String countJpql = getCountJpql(jpql);
		return (Long) entityManager.createQuery(countJpql).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> executeQuery() {
		Query query =  entityManager.createQuery(jpql);
		if (maxResults > 0) {
			query.setFirstResult(firstResult).setMaxResults(maxResults);
		}
		return query.getResultList();
	}
	
	private List<Object> executeQueryNTimes() {

		log.debug("executeQueryNTimes:\nQuery: [" + jpql + "]\n# of times: " + iterations);
		
		List<Object> result = new ArrayList<Object>();
		long iterationTime = 0;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		stopWatch.suspend();
		try {
			for (int i = 1; i < iterations + 1; i++) {
				stopWatch.resume();
				result = executeQuery();
				stopWatch.suspend();
				log.debug("Query iteration #: " + i + " took: " + (stopWatch.getTime() - iterationTime) + " ms.");
				iterationTime = stopWatch.getTime();
			}
		}
		finally {
			stopWatch.stop();
		}
		
		facesMessages.add("Total time to execute query " + iterations + 
				" time" + (iterations == 0 || iterations > 1 ? "s: " : ": ") + stopWatch.toString());
		log.debug("Total time to execute query " + iterations + 
				" time" + (iterations == 0 || iterations > 1 ? "s: " : ": ") + stopWatch.toString());

		return result;
	}

	
	private void initColumnData() {
		
		if(jpqlResults != null && !jpqlResults.isEmpty()) {
			
			Object data = jpqlResults.get(0);
			Class type = data.getClass();
			
			if (data == null) {
				//TODO:
			} 
			else if (data instanceof Object[] || (type != null && (type.isPrimitive() || ALLOWED_TYPES.indexOf(type.getName()) > -1)) ) {
				// e.g. [select e.property_1, e.property_n from Entity e]
				// n.b. [select e.property from Entiity e] is a special case, see xhtml
				
    		String s = jpql.toLowerCase();
    		int i = s.indexOf("select");
    		if(i >= 0) {
      		StringTokenizer st = new StringTokenizer(s.substring(i+6, s.indexOf("from")), ",");
      		isSingleSubject = st.countTokens() < 2;
      		Long n = 0L;
      		while (st.hasMoreTokens()) {
      			jpqlColumns.add(new ColumnData(st.nextToken().trim(), n++, null));
      		}
    		}
			} 
			else { // if (data instanceof Object) {
				// e.g. select e from Entity e
				
				BeanMap beanMap = new BeanMap(data);
				Set keys = beanMap.keySet();
				Iterator i = keys.iterator();
				
				while (i.hasNext()) {
					String name = (String) i.next();
					type = beanMap.getType(name);
					if(type != null) {
						if(type.isPrimitive() || ALLOWED_TYPES.indexOf(type.getName()) > -1) {	
							jpqlColumns.add(new ColumnData(name, name, type));
						}
						else {
							//TODO: 
						}
					}
				}			
			}
		}
	}
	
	private void logResults() {
		for (Object o : jpqlResults) {
			try {
				log.info(printRow(o, 0));
			} catch (Exception e) {
				log.info("Logging failed:", e);
				break;
			}
		}
	}

	
	private static String getBeanTypeName(Class beanType) {
		String result = "";
		
		// TODO: Create enum
		if(beanType == null) {
			result = "** NULL beanType";
		}
		else {
			if(beanType.isAnnotation()) {
				result = "annotation ";
			}
			else if(beanType.isArray()) {
				result = "array ";
			}
			else if(beanType.isEnum()) {
				result = "enum ";
			}
			else if(beanType.isInterface()) {
				result = "interface ";
			}
			else if(beanType.isPrimitive()) {
				result = "";
			}
			else {
				result = "class ";
			}
			result += beanType.getName();
		}
		return result;
	}
	
	private static String printRow(Object data, int indent) throws Exception {

		String s = ""; 
		
		if (data == null) {
			s = "NULL";
		} 
		else if (data instanceof Object[]) {
			Object[] row = (Object[]) data;
			s += "[";
			for (int i = 0; i < row.length; i++) {
				s += printRow(row[i], 0);
			}
			s += "] ";
		}
		else {
			Class type = data.getClass(); // Had to move this here due to strange exception from logger
			
			if (type != null && (type.isPrimitive() || ALLOWED_TYPES.indexOf(type.getName()) > -1)) {
				return (data.getClass().getName() + ": " + data + ", ");
			} 
			else if (data instanceof Object) {
				BeanMap beanMap = new BeanMap( data );
				Set keys = beanMap.keySet( );
				Iterator i = keys.iterator( );
				
				s = "\n" +data.getClass().getName() + ": [\n";
				while (i.hasNext()) {
					String propertyName = (String) i.next( );
					Object value = beanMap.get(propertyName);
					Class beanType = beanMap.getType(propertyName);
					String beanTypeName = getBeanTypeName(beanType);

					if(indent > 0) {
						s += String.format("%"+indent+"s", " ");
					}
					s += "{Property: " + propertyName;
					if (beanType != null) {
						if (beanType.isPrimitive() || ALLOWED_TYPES.indexOf(beanType.getName()) > -1) {
							s +=", Value: " + value;
						}
						else {
							//TODO:
							if(beanTypeName.indexOf("class") > -1 && beanTypeName.indexOf("java.lang.Class") < 0) {
								s += ", Value: [";
								s += printRow(value, indent+2);
								s += "]";
							}
						}
					}
	      	s += ", Type: " + getBeanTypeName(beanType);
	      	s += "}" + (i.hasNext() == true ? ",\n" : "" );
				}
				s += "]";
				
//				s = "[";
//				Map<?, ?> map = PropertyUtils.describe(result);
//				for(Map.Entry<?, ?> entry : map.entrySet()) {
//					Object v = entry.getValue();
//					s += "{";
//					if (v instanceof Long || v instanceof Double	|| v instanceof String || v instanceof java.util.Date) {
//						s += 
//							"Property: " + entry.getKey() + 
//			      	", Value: " + v +
//			      	", Type: " + v.getClass();
//					}
//					else {
//						s += "Type: " + v.getClass(); //->nullpointer
//					}
//	      	s += "}, ";
//				}
//				return s + "]";
				
				
//				return ReflectionToStringBuilder.toString(result,	ToStringStyle.SHORT_PREFIX_STYLE);
			}			
		}
		
		return s;
	}

	
	public void setJpql(String jpql) {
		this.jpql = jpql.trim();
	}

	public String getJpql() {
		return jpql;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations == null || iterations < 1 ? 1 : iterations;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult == null || firstResult < 0 ? 0 : firstResult;
	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults == null ? 0 : maxResults;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setPrintResults(Boolean printResults) {
		this.printResults = printResults;
	}

	public Boolean getPrintResults() {
		return printResults;
	}

	public void setIsSingleSubject(Boolean isSingeSubject) {
		this.isSingleSubject = isSingeSubject;
	}

	public Boolean getIsSingleSubject() {
		return isSingleSubject;
	}

	public void setJpqlResultCount(Long jpqlResultCount) {
		this.jpqlResultCount = jpqlResultCount;
	}

	public Long getJpqlResultCount() {
		return jpqlResultCount;
	}

}