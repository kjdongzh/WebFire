package rcptext;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
//		String editorArea = layout.getEditorArea();
//		layout.addView("rcptext.FirstView", IPageLayout.LEFT, 0.4f, editorArea);
//		layout.addView("rcptext.MapView", IPageLayout.RIGHT, 0.4f, editorArea);
		//layout.addStandaloneView("rcptext.MapView", true, IPageLayout.RIGHT, 0.4f, editorArea);
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addView("rcptext.FirstView", IPageLayout.LEFT, 0.2f, "rcptext.MapView");
		layout.addView("rcptext.MapView", IPageLayout.RIGHT, 0.28f, "rcptext.FirstView");
		layout.getViewLayout("rcptext.FirstView").setCloseable(false);
		layout.getViewLayout("rcptext.FirstView").setMoveable(false);
		layout.getViewLayout("rcptext.MapView").setCloseable(false);
		layout.getViewLayout("rcptext.MapView").setMoveable(false);
	}
}
