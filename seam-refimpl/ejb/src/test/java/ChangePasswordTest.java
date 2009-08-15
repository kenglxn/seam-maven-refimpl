
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.model.User;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class ChangePasswordTest extends SeamTest
{
   
   @Test
   public void testChangePassword() throws Exception
   {
      
      new FacesRequest() {
         
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getSessionContext().set("user", new User("Ken Gullaksen", "glxn", "secret"));
            setValue("#{identity.username}", "admin");
            setValue("#{identity.password}", "");
            invokeMethod("#{identity.login}");
         }
         
      }.run();
      
      new FacesRequest() {
         
         @Override
         protected void processValidations() throws Exception
         {
            validateValue("#{user.password}", "xxx");
            assert isValidationFailure();
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Ken Gullaksen");
            assert getValue("#{user.username}").equals("glxn");
            assert getValue("#{user.password}").equals("secret");
            assert !Manager.instance().isLongRunningConversation();
            assert getValue("#{identity.loggedIn}").equals(true);

         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.password}", "xxxyyy");
            setValue("#{changePassword.verify}", "xxyyyx");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeAction("#{changePassword.changePassword}")==null;
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Ken Gullaksen");
            assert getValue("#{user.username}").equals("glxn");
            assert getValue("#{user.password}").equals("secret");
            assert !Manager.instance().isLongRunningConversation();
            assert getValue("#{identity.loggedIn}").equals(true);
         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.password}", "xxxyyy");
            setValue("#{changePassword.verify}", "xxxyyy");
         }

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{changePassword.changePassword}");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Ken Gullaksen");
            assert getValue("#{user.username}").equals("glxn");
            assert getValue("#{user.password}").equals("xxxyyy");
            assert !Manager.instance().isLongRunningConversation();
            assert getValue("#{identity.loggedIn}").equals(true);

         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            assert getValue("#{user.password}").equals("xxxyyy");
            setValue("#{user.password}", "secret");
            setValue("#{changePassword.verify}", "secret");
         }

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{changePassword.changePassword}");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Ken Gullaksen");
            assert getValue("#{user.username}").equals("glxn");
            assert getValue("#{user.password}").equals("secret");
            assert !Manager.instance().isLongRunningConversation();
            assert getValue("#{identity.loggedIn}").equals(true);

         }
         
      }.run();
      
   }

}
