package no.knowit.seam.statefulaction.test;

import java.util.GregorianCalendar;

import javax.faces.model.DataModel;

import no.knowit.openejb.mock.test.ContextPropertiesForTest;
import no.knowit.seam.model.Message;
import no.knowit.seam.openejb.mock.SeamOpenEjbTest;
import no.knowit.seam.statefulaction.MessageManager;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MessageManagerTest extends SeamOpenEjbTest {
	
	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
	 contextProperties = ContextPropertiesForTest.getDefaultContextProperties(contextProperties);
	 
	 contextProperties.put("log4j.category.no.knowit.seam.statefulaction", "DEBUG");
	 super.beforeSuite();
	}

	@Test(groups={ "seam", "unit-test" }, enabled=true)
	public void testStatefulActionBean() throws Exception {

		new ComponentTest() {
			@Override
			protected void testComponents() throws Exception {
				MessageManager messageManager = getComponentInstanceWithAsserts("messageManager", MessageManager.class);
				Assert.assertNotNull(messageManager, "Entity manager was null");
				
				messageManager.deleteAllMessages();
				
				messageManager.addMessage(new Message("Hello World",
						"This is an example of a message.", Boolean.FALSE,
						new GregorianCalendar(2006, 1, 1, 12, 0, 0).getTime()));
				
				messageManager.addMessage(new Message("Greetings Earthling",
						"This is another example of a message.", Boolean.FALSE,
						new GregorianCalendar(2006, 2, 4, 3, 4, 0).getTime()));
			}
		}.run();
		
    new NonFacesRequest() {
    	
			@Override
			protected void renderResponse() throws Exception {
				DataModel list = (DataModel) getInstance("messageList");
				Assert.assertEquals(list.getRowCount(), 2, "List.getRowCount()");
			}

		}.run();
		
    new FacesRequest() {
			@Override
			protected void updateModelValues() throws Exception {
				DataModel list = (DataModel) getInstance("messageList");
				assert list.getRowCount() == 2;
				list.setRowIndex(1);
			}

			@Override
			protected void invokeApplication() throws Exception {
				invokeMethod("#{messageManager.select}");
			}

			@Override
			protected void renderResponse() throws Exception {
				DataModel list = (DataModel) getInstance("messageList");
				assert list.getRowCount() == 2;
				assert getValue("#{message.title}").equals("Hello World");
				assert getValue("#{message.read}").equals(true);
			}
		}.run();

    new FacesRequest() {
			@Override
			protected void updateModelValues() throws Exception {
				DataModel list = (DataModel) getInstance("messageList");
				assert list.getRowCount() == 2;
				list.setRowIndex(0);
			}

			@Override
			protected void invokeApplication() throws Exception {
				invokeMethod("#{messageManager.delete}");
			}

			@Override
			protected void renderResponse() throws Exception {
				DataModel list = (DataModel) getInstance("messageList");
				assert list.getRowCount() == 1;
			}
		}.run();

		new NonFacesRequest() {
			@Override
			protected void renderResponse() throws Exception {
				DataModel list = (DataModel) getInstance("messageList");
				assert list.getRowCount() == 1;
			}
		}.run();

	}
}