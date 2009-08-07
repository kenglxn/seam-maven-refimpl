package org.jboss.seam.example.action;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.StopWatch;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

@Name("jpqlSampler")
public class JPQLSamplerAction implements Serializable {


    private static final long serialVersionUID = -4345141619125985848L;

    @In
    private EntityManager em;

    @Logger
    private Log log;

    @In
    FacesMessages facesMessages;

    @In(create = true, required = false)
    @Out(required = false)
    private String jpqlStatement;

    @In(create = true, required = false)
    @Out(required = false)
    private String iterations = "1";

    @In(create = true, required = false)
    @Out(required = false)
    private String firstResult = "0";

    @In(create = true, required = false)
    @Out(required = false)
    private String maxResults = "10";
    @In(create = true, required = false)
    @Out(required = false)
    private Boolean printResults = false;

    private static String printResult(Object result) throws Exception {

        String s = "";

        if (result == null) {
            s = "NULL";
        } else if (result instanceof Object[]) {
            Object[] row = (Object[]) result;
            s += "[";
            for (int i = 0; i < row.length; i++) {
                s += printResult(row[i]);
            }
            s += "] ";
        } else if (result instanceof Long ||
                result instanceof Double ||
                result instanceof String) {
            return (result.getClass().getName() + ": " + result + ", ");
        } else {
            return ReflectionToStringBuilder.toString(result,
                    ToStringStyle.SHORT_PREFIX_STYLE);
        }

        return s;
    }

    @SuppressWarnings("unchecked")
    private void executeQueryNTimes(final String q, int n, int firstResult, int maxResults) {

        log.info("executeQueryNTimes: query:\n[" + q + "]\n# of times: " + n + "\n");

        // Create a stop watch to measure execution time
        StopWatch stopWatch = new StopWatch();
        long iterationTime = 0;

        // Execute query n times
        stopWatch.start();
        stopWatch.suspend();
        List<Object> result = null;

        for (int i = 1; i < n + 1; i++) {

            Query query;
            //long createQueryTime = stopWatch.getTime();

            stopWatch.resume();

            try {
                query = em.createQuery(q);
            } catch (IllegalArgumentException iaEx) {
                facesMessages.add("Could not create query, check your syntax, your JPQL is not correct");
                log.warn("Could not create query, check your syntax, your JPQL is not correct");
                return;
            }

            if (maxResults > 0) {
                query.setFirstResult(firstResult).setMaxResults(maxResults);
            }

            //stopWatch.suspend();
            //log.info("createQuery took: " + (stopWatch.getTime()-createQueryTime) + " ms. " );
            //stopWatch.resume();

            // Retrieve objects
            result = query.getResultList();

            stopWatch.suspend();

            log.info("Query iteration #: " + i + " took: " +
                    (stopWatch.getTime() - iterationTime) + " ms. " +
                    "result.size(): " + result.size());

            iterationTime = stopWatch.getTime();

            if (i != n) {
                result = null;
            }

        } //~for

        stopWatch.stop();
        log.info("Total time to execute query " + n + " times: " + stopWatch.toString());
        facesMessages.add("Total time to execute query " + n + " times: " + stopWatch.toString());

        // List results
        if (printResults.booleanValue() == true) {
            for (Object o : result) {
                try {
                    log.info(printResult(o));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        result = null;
    }


    public void executeJPQL() {
        int i = 0, f = 0, m = 0;

        try {
            i = Integer.parseInt(iterations);
        } catch (NumberFormatException nfEx) {
            facesMessages.add("Not able to parse number, check iterations input");
            log.warn("Not able to parse number, check iterations input");
            return;
        }

        try {
            f = Integer.parseInt(firstResult);
            m = Integer.parseInt(maxResults);
        } catch (NumberFormatException nfEx) {
            f = 0;
            m = 0;
        }


        if (jpqlStatement == null || jpqlStatement.trim().length() == 0) {
            facesMessages.add("No jpql found, enter a jpql statement");
            log.warn("No jpql found, enter a jpql statement");
            return;
        }

        // Execute JPQL n times
        executeQueryNTimes(jpqlStatement, i, f, m);

    }


    public String getInitValues() {
        if (iterations == null) {
            iterations = "1";
        }
        if (firstResult == null) {
            firstResult = "0";
        }
        if (maxResults == null) {
            maxResults = "10";
        }
        return "";
    }

}