package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Settings;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("settingsList")
@Scope(ScopeType.CONVERSATION)
public class SettingsList extends EntityQuery<Settings> {
    private static final long serialVersionUID = -3406620263605736125L;

    @Logger
    private Log log;
    
    public SettingsList() {
        setEjbql("select settings from Settings settings");
    }
}