package rcptext;

import java.util.LinkedList;

public class DrawfirecacheCoordinator {

	private LinkedList<String> images = new LinkedList<String>();
	private DrawfirecacheCoordinator(){
	}
	private static DrawfirecacheCoordinator coor = new DrawfirecacheCoordinator();
	public static DrawfirecacheCoordinator getCoor(){
		return coor;
	}
	
	public synchronized void getImagefile(String filename){
		try {
			while(images.contains(filename)){
				wait();
			}
			images.add(filename);
			return;
		} catch (InterruptedException e) {
			return;
		}
	}
	
	public synchronized void freeImagefile(String filename){
		images.remove(filename);
		notifyAll();
	}


}
