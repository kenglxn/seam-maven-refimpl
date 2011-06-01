package no.knowit.seam.example.test;

import junit.framework.TestCase;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import java.util.Iterator;

public class JSFUnitUtils {
    public static void logFacesMessages(JSFSession session) {
        JSFServerSession server = session.getJSFServerSession();
        Iterator<FacesMessage> iterator = server.getFacesContext().getMessages(null);
        System.out.println("Global messages:");
        while (iterator.hasNext()) {
            FacesMessage message = iterator.next();
            System.out.println(message.getSeverity() + ":" + message.getSummary() + " | " + message.getDetail());
        }
        Iterator<String> idsWithMessages = server.getFacesContext().getClientIdsWithMessages();
        while (idsWithMessages.hasNext()) {
            String id = idsWithMessages.next();
            if (id == null) {
                continue;
            }
            System.out.println("Messages for component " + id + ":");
            iterator = server.getFacesMessages(id);
            while (iterator.hasNext()) {
                FacesMessage message = iterator.next();
                System.out.println(id + ":" + message.getSeverity() + ":" + message.getSummary() + " | " + message.getDetail());
            }
        }
    }

    public static void assertGlobalMessage(JSFSession session, String message) {
        Element messagesElement = session.getJSFClientSession().getElement("messages");
        TestCase.assertNotNull(messagesElement);
        NodeList messagesItems = messagesElement.getElementsByTagName("li");
        TestCase.assertEquals(1, messagesItems.getLength());
        for (int i = 0; i < messagesItems.getLength(); i++) {
            TestCase.assertEquals(message, messagesItems.item(i).getTextContent().trim());
        }
    }

    public static String messageForKey(JSFSession session, String key) {
        final Object value = getELValue(session, "#{messages['" + key + "']}");
        return value == null ? null : value.toString();
    }

    public static Object getELValue(JSFSession session, String expression) {
        final FacesContext facesContext = session.getJSFServerSession().getFacesContext();
        return facesContext.getApplication().createValueBinding(expression).getValue(facesContext);
    }

    public static void assertMessageForControl(JSFSession session, String controlId, String messageKey, FacesMessage.Severity severity) {
        Iterator<FacesMessage> messageIterator = session.getJSFServerSession().getFacesMessages(controlId);
        TestCase.assertTrue(messageIterator.hasNext());
        final FacesMessage message = messageIterator.next();
        TestCase.assertEquals(severity, message.getSeverity());
        TestCase.assertEquals(messageForKey(session, messageKey), message.getSummary());
    }

    public static void assertMessageForControl(JSFSession session, String controlId, String messageKey) {
        assertMessageForControl(session, controlId, messageKey, FacesMessage.SEVERITY_ERROR);
    }

    public static EntityManager getEntityManager(JSFSession jsfSession) {
        return (EntityManager) JSFUnitUtils.getELValue(jsfSession, "#{entityManager}");
    }
}
