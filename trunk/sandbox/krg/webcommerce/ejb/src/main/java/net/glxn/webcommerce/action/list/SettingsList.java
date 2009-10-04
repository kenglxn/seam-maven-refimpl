package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Settings;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("settingsList")
public class SettingsList extends EntityQuery<Settings> {
    private static final long serialVersionUID = -3406620263605736125L;

    public SettingsList() {
        setEjbql("select settings from Settings settings");
    }
}