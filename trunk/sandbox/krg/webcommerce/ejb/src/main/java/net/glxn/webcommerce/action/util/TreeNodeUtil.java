package net.glxn.webcommerce.action.util;

import org.ajax4jsf.model.DataComponentState;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.component.state.TreeStateAdvisor;

import javax.faces.context.FacesContext;
import java.io.Serializable;

@Scope(ScopeType.SESSION)
@Name("treeNodeUtil")
public class TreeNodeUtil implements TreeStateAdvisor, Serializable {
    private static final long serialVersionUID = -7565079478326332663L;

    @RequestParameter
    private String catId;

    @RequestParameter
    private String isChild;

    @Logger
    private Log log;

    protected DataComponentState componentState;

    @Override
    public Boolean adviseNodeOpened(UITree tree) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (!ctx.getRenderKit()
                .getResponseStateManager().isPostback(ctx)) {
            if(catId == null || isChild != null) {
                return null;
            }
            UITreeNode nodeFacet = tree.getNodeFacet();
            Long data = (Long) nodeFacet.getData();
            if(catId.equals(data.toString())) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    @Override
    public Boolean adviseNodeSelected(UITree tree) {
        return null;
    }

    public DataComponentState getComponentState() {
        return componentState;
    }

    public void setComponentState(DataComponentState componentState) {
        this.componentState = componentState;
    }

}
