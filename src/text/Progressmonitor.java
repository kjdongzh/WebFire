package text;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;


public class Progressmonitor {
	static ProgressMonitorDialog dialog;
	public static void main(String[] args) {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
//				monitor.beginTask("begin" + "...... ",10);
				monitor.beginTask("begin" + "...... ",IProgressMonitor.UNKNOWN);
				monitor.setTaskName("Running cmd XXXX.");
				int i = 0;
				while(i++ < 10) {
					if(monitor.isCanceled()) {
						monitor.setTaskName("Canceled cmd XXXX.");
						break;
					}
					try {
						Thread.sleep(1000);
						monitor.setTaskName("Running cmd XXXX.");
						monitor.subTask("Running step " +i + " .");
						monitor.worked(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				monitor.done();
			}
		};
		try {
			dialog = new ProgressMonitorDialog(null);
			dialog.run(true, true, runnable);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}




