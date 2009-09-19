package net.glxn.webcommerce.action.util;

import org.ajax4jsf.model.DataComponentState;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.UITree;
import org.richfaces.model.TreeRowKey;

import javax.faces.context.FacesContext;
import java.io.Serializable;

@Scope(ScopeType.SESSION)
@Name("treeNodeUtil")
public class TreeNodeUtil implements Serializable {
    private static final long serialVersionUID = -7565079478326332663L;

    protected DataComponentState componentState;

    public Boolean adviseNodeOpened(UITree tree) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (!ctx.getRenderKit()
                .getResponseStateManager().isPostback(ctx)) {
            TreeRowKey treeRowKey = (TreeRowKey)
                    tree.getRowKey();
            if (treeRowKey == null || treeRowKey.depth() <= 2) {
                return Boolean.TRUE;
            }
        }
        return null;
    }

    public DataComponentState getComponentState() {
        return componentState;
    }

    public void setComponentState(DataComponentState componentState) {
        this.componentState = componentState;
    }

}
