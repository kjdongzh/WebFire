package rcptext;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import actions.WMSAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction wms;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	wms = new WMSAction(window);
    	register(wms);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	IMenuManager dataMenuManager = new MenuManager("配置");
    	dataMenuManager.add(wms);
    	menuBar.add(dataMenuManager);
    }
    
}
