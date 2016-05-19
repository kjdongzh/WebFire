package rcptext;

public class FireDrawCoordinator {
	
	private int semaphore;
	private boolean requestPending;
	
	private FireDrawCoordinator(boolean pendState){
		this.requestPending = pendState;
	}

	private static FireDrawCoordinator coordinator = new FireDrawCoordinator(false);
	public static FireDrawCoordinator getCoordinator(){
		return coordinator;
	}
	
	public synchronized void getResources(){
		
		try{
			requestPending = true;
			while (semaphore > 0) 
				wait();
			semaphore = 1;
			requestPending = false;
			return;
		}
		catch (InterruptedException w){
			return;
		}
	}

	public synchronized void freeResources(){
		semaphore = 0;
		notifyAll();
	} 
	
	
}
