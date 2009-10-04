package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.Settings;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("settingsHome")
public class SettingsHome extends EntityHome<Settings> {
    private static final long serialVersionUID = 1346891683026964563L;
}