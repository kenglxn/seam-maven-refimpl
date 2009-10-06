package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.action.home.SettingsHome;
import net.glxn.webcommerce.model.Settings;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("settingsList")
public class SettingsList extends EntityQuery<Settings> {
    private static final long serialVersionUID = -3406620263605736125L;

    @Logger
    private Log log;

    @In(required = false, create = true)
    @Out(required = false)
    SettingsHome settingsHome;

    public SettingsList() {
        setEjbql("select settings from Settings settings");
    }

    public void initSettingsHome() {
        log.info("runnning initSettingsHome");
        Settings settings = getSingleResult();
        if (settings == null) {
            log.warn("no settings found");
            return;
        }
        settingsHome.setInstance(settings);
    }
}