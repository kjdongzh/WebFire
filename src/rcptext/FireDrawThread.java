package rcptext;


public class FireDrawThread extends Thread{

	private FireModel model;
	private boolean pause = false;
	private FireDrawCoordinator coordinator;
	
	public FireDrawThread(FireModel model){
		this.model = model;
		coordinator = FireDrawCoordinator.getCoordinator();
	}
	
	public void pause(){
		pause = true;
	}
	
	public void run(){
		coordinator.getResources();
		while (!pause) {
			try {
				model.setProbabilityEast(FirstView.getEastWind());
				model.setProbabilityNorth(FirstView.getNorthWind());
				model.setProbabilitySouth(FirstView.getSouthWind());
				model.setProbabilityWest(FirstView.getWestWind());
				model.setHumidity(FirstView.getHumdity());
				model.setWeathermodel(FirstView.getWeathermodel());
				
				int weathmodel = FirstView.getWeathermodel();
				int temper = FirstView.getTemperature();
				int wett = FirstView.getHumdity();
				int timeinterval = 100+weathmodel*5+wett-temper;
				if(timeinterval<0){
					timeinterval = 0;
				}
				model.setTimeinterval(timeinterval);
				
				model.iterateFire();
				if (pause)
					break;
				System.gc();
				coordinator.freeResources();
				Thread.sleep(timeinterval);//90
				coordinator.getResources();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				coordinator.freeResources();
			}
		}
	}
	
	
	
	
	
	
}
