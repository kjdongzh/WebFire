package rcptext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.ImageData;

import com.csvreader.CsvReader;
import com.infomatiq.jsi.Rectangle;



public class FireModel {

	private FireLink firstFireLink;
	private byte fire[][] = new byte[ImageConstant.hight][ImageConstant.width];
	private double ndvi[][] = new double[ImageConstant.hight][ImageConstant.width];
	private double dem[][] = new double[ImageConstant.hight][ImageConstant.width];
	private double probabilityNorth;
	private double probabilitySouth;
	private double probabilityEast;
	private double probabilityWest;
	private int humidity;
	private int weathermodel;
	private ArrayList<String> Tilesets = new ArrayList<String>();
	private ArrayList<String> demTilesets = new ArrayList<String>();
	private ArrayList<String> fireresult = new ArrayList<String>();
	private int iteration = 0;
	private int calzoom = 0;
	private double caltimeratio = 0;
	private double timesum = 0;
	private double timeinterval = 0;
	private double threshold = 0.1;
    private HashMap<String, ImageData> imageDatas = new HashMap<String, ImageData>();

    private double startTime = 0;
    private double endTime = 0;

	//单例模式
	private FireModel(){
		this.calzoom = ImageConstant.getZoom();
		this.caltimeratio = Double.parseDouble(FirstView.getTimeratio());
	}
	private static FireModel fireModel = new FireModel();
	public static FireModel getFireModel(){
		return fireModel;
	}
	
	//set
	public void setProbabilityNorth(double probabilityNorth) {
		this.probabilityNorth = probabilityNorth;
	}
	public void setProbabilitySouth(double probabilitySouth) {
		this.probabilitySouth = probabilitySouth;
	}
	public void setProbabilityEast(double probabilityEast) {
		this.probabilityEast = probabilityEast;
	}
	public void setProbabilityWest(double probabilityWest) {
		this.probabilityWest = probabilityWest;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public int getWeathermodel() {
		return weathermodel;
	}

	public void setWeathermodel(int weathermodel) {
		this.weathermodel = weathermodel;
	}

	public void setCaltimeratio(double caltimeratio) {
		this.caltimeratio = caltimeratio;
	}

	public void setTimeinterval(double timeinterval) {
		this.timeinterval = timeinterval;
	}

	public void startFire(int i, int j) {

		if(i >= 0 && i < ImageConstant.hight && j >= 0 && j < ImageConstant.width && fire[i][j] == 0){
			FireLink firelink = new FireLink(i, j);//将当前点的值设置为燃烧，并传递下去
			firelink.next = firstFireLink;
			firstFireLink = firelink;
			fire[i][j] = 1;
		}
    }

	public void iterateFire() {

		double iteratestart = System.currentTimeMillis();
		ArrayList<String> firecache = new ArrayList<String>();

		if (firstFireLink == null){
			return;
		}
		FireLink firelink = null;
		
		for (FireLink firelink1 = firstFireLink; firelink1 != null;){
			
			byte byte0 = ++fire[firelink1.x][firelink1.y];
			
			MapView.setFire(firelink1.x,firelink1.y,byte0);
			//着火点
			if (byte0 == 2){
		         for(int i = -1; i < 2; i++){
		        	 for(int j = -1; j < 2; j++){
		        		 if(i != j && i!= -j){
		        			 if((firelink1.x+i) >= 0 && (firelink1.x+i) < ImageConstant.hight && 
		        					 (firelink1.y+j) >= 0 && (firelink1.y+j) < ImageConstant.width &&
		        					 fire[firelink1.x+i][firelink1.y+j]==0){
		        				//计算可燃值 参数（行 列）
//		        				double ndvi = calculateNDVI(ImageConstant.hight-firelink1.x-i-1, firelink1.y+j);
//		        				int color[] = getRGB(firelink1.x+i, firelink1.y+j);
		        				//double Q = CalQ(color,ndvi);
		        				double Q = 0.8;
		        				//坡度计算
//		        				double slop = calculateSlope(ImageConstant.hight-firelink1.x-1, firelink1.y, i, j);
		        				double slop = 0.5;
			        			double speed;
			        			//计算风速
			        			if(i == 0 && j == -1)
			        			    speed = probabilityWest;
			        			else if(i == 0 && j == 1)
			        				speed = probabilityEast;
			        			else if(j == 0 && i == -1)
			        				speed = probabilitySouth;
			        			else
			        				speed = probabilityNorth;
			        			double posibility=0;
			        			
			        			posibility = CalPosibilityofBurn(Q, humidity/100.0, slop, speed);
			        			//System.out.println("Q：" + Q +"  slope:"+ slop +"   posibility:" + posibility);
//			        			if(speed>0){
////			        				threshold = 0.09;
//			        				threshold = 0.1-speed/100;
//			        			}else if(speed<=0){
//			        				threshold = 0.1+Math.abs(speed)/20;
//			        			}
//			        			if(posibility > (0.4+Math.random()*threshold)){
//			        				startFire(firelink1.x+i, firelink1.y+j);
//			        			}
			        			if(speed>8){
			        				threshold = 0.1;
			        			}else if(speed<-8){
			        				threshold = 0.8;
			        			}else{
			        				threshold = 0.1+Math.random()*0.8;
			        			}
			        			threshold += weathermodel*0.1;
			        			if(posibility > (0.4+threshold)){
			        				startFire(firelink1.x+i, firelink1.y+j);
			        			}
		        			 }
		        			 
		        		 }
		        	 }	
			    }
			}
			if (byte0 >= 7)
			{
				firecache.add(firelink1.x+"-" +firelink1.y);
				if (firelink == null)
				{
					//第一个
					firstFireLink = firelink1.next;
					firelink1.next = null;
					firelink1 = firstFireLink;
				} else
				{
					//把byte[][]==7（烧成灰烬，迭代结束）对应的ij所在的firelink从Firstlink删除
					firelink.next = firelink1.next;
					firelink1.next = null;
					//遍历下一个
					firelink1 = firelink.next;
				}
			} else
			{  
				firelink = firelink1;
				//遍历firstfirelink的next
				firelink1 = firelink1.next;
			}
		}
	
		new DrawfireThread(firecache).start();
		
		
		if(iteration != 0 && iteration%10 == 0){
			String leng = "0";
			String area = "0";
			
			int number = 0;
			int areanum = 0;
			fireresult.clear();
			//数组下标越界问题，k和l从1开始， 边界-1终结
			for(int k = 1; k < ImageConstant.hight -1; k++){
				for(int l = 1; l < ImageConstant.width -1; l++){
					if(fire[k][l]!=0&&fire[k][l-1]*fire[k][l+1]==0){
						number++;
					}
					if(fire[k][l] > 1){
						areanum++;
						int row = ImageConstant.hight-1-k;
						fireresult.add(row+"-"+l);
					}
				}
			}
		    
			DecimalFormat df = new DecimalFormat("#.###");
			leng = df.format(number*3/1000.0);
			area = df.format(areanum*9/10000.0);
		    
			FirstView.setFireattr(leng,area);
			
			//保存燃烧结果
			if(iteration%50 == 0){
				new DrawFireResult(fireresult,iteration/100).start();
			}
		}
		
		double iterateend = System.currentTimeMillis();
		timesum = timesum + iterateend - iteratestart + timeinterval;
		double times = timesum*caltimeratio/1000;
		int hour = (int) (times/60);
		int minute = (int) (times%60);
		FirstView.setFiretime(hour+":"+minute);
		
		
		iteration++;
	}
	
	
	private double CalQ(int[] rgb, double ndvi) {
		int R = rgb[0];
		int G = rgb[1];
		int B = rgb[2];
		//System.out.println(R+"-"+G+"-"+B);
		if(R > 20 && R < 100){
			if(B>G&&(B-G)>20){
				return 0;
			}else{
				if(ndvi == 0)
					ndvi = ndvi + 0.1;
				return ndvi*0.1+0.7;
			}
		}
		return 0;
	}

	private double CalPosibilityofBurn(double q, double humidity, double slope, double speed) {

		double prob = 0;
		double B = 0.2945;//常数
		double K1 = 0.9;//树种可燃性权重
		double K2 = -0.2;//湿度的权重
		double K3 = 0.2;//坡度的权重
		double K4 = 0.1;
		humidity = 0.1;
		if(speed >= 0){
			speed = 0;
			prob = q*((K1*(B+K2*humidity+K3*slope+K4*speed))+0.8);
		}else{
			speed = 0;
			prob = q*((K1*(B+K2*humidity+K3*slope+K4*speed))+0.8);
		}
		return prob;
	}
	
	//计算火场周长
    private String calcuLen(ArrayList<FirePoint> point) {
    	double len = 0;
    	for (int i = 0; i < point.size()-1; i++) {
    		int col1 = point.get(i).getL_Lon();
    		int row1 = point.get(i).getK_Lat();
    		int col2 = point.get(i+1).getL_Lon();
    		int row2 = point.get(i+1).getL_Lon();
			len += getDistance(col1,row1,col2,row2);
		}
    	len = len/100000;
    	DecimalFormat format = new DecimalFormat("#.###");
		return format.format(len);
	}

    //像素分辨率为3m
	private double getDistance(int col1, int row1, int col2, int row2) {
		int coldiff = Math.abs(col1-col2);
		int rowdiff = Math.abs(row1-row2);
		double result = Math.sqrt(Math.pow(coldiff, 2)+Math.pow(rowdiff, 2));
		return result*3;
	}

	//DEM[x][y] x行y列
	private double calculateSlope(int x, int y, int i, int j) {
		double slope = 0;
		double tilenum = Math.pow(2, calzoom);
		int firstrow = (int) (tilenum - (90-ImageConstant.maxLat+180/tilenum)*tilenum/180);//1535
	    int firstcol = (int) (tilenum + ImageConstant.minLon*tilenum/180);//3488
		int col = firstcol + y/256;
		int row = firstrow - x/256;
		int col2 = firstcol + (y+6*j)/256;
		int row2 = firstrow - (x-6*i)/256;
		String index = col+"_"+row;
		if(!demTilesets.contains(index)){
			
			String remoteFilePath = ImageConstant.remotedataPath +"demdata/"+index+".csv";
			URL url = null;
	        BufferedReader reader = null;
	        try
	        {
	            url = new URL(remoteFilePath);
	            reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
	            String temp = null;
	            int x1 = 0;
	    		while((temp = reader.readLine()) != null){
	    			String[] data = temp.split(",");
	    			for (int k = 0; k < data.length; k++) {
						dem[(firstrow-row)*256+x1][(col-firstcol)*256+k] = Double.parseDouble(data[k]);
					}
	    			x1++;
	    		}
	            
	        }catch (Exception e){
	        	return 0;
	        }
	        finally{
	            try{
	            	if(reader != null){
	            		reader.close();
	            	}
	            }
	            catch (IOException e){
	                e.printStackTrace();
	            }
	        }
			demTilesets.add(index);
		}
		if(col!=col2||row!=row2){
			String index2 = col2+"_"+row2;
			if(!demTilesets.contains(index2)){
				String remoteFilePath = ImageConstant.remotedataPath +"demdata/"+index2+".csv";
				URL url = null;
		        BufferedReader reader = null;
		        try
		        {
		            url = new URL(remoteFilePath);
		            reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		            String temp = null;
		            int x1 = 0;
		    		while((temp = reader.readLine()) != null){
		    			String[] data = temp.split(",");
		    			for (int k = 0; k < data.length; k++) {
							dem[(firstrow-row2)*256+x1][(col2-firstcol)*256+k] = Double.parseDouble(data[k]);
						}
		    			x1++;
		    		}
		            
		        }catch (Exception e){
		        	return 0;
		        }
		        finally{
		            try{
		            	if(reader != null){
		            		reader.close();
		            	}
		            }
		            catch (IOException e){
		                e.printStackTrace();
		            }
		        }
				demTilesets.add(index2);
			}
		}
		//System.out.println(dem[x-6*i][y+6*j] +"!!" + dem[x][y]);
		slope = (dem[x-6*i][y+6*j]-dem[x][y])/6;
		return slope;
	}

	//NDVI[x][y] x行y列
	private double calculateNDVI(int x, int y){
		double result = 0;
		double tilenum = Math.pow(2, calzoom);
		int firstrow = (int) (tilenum - (90-ImageConstant.maxLat+180/tilenum)*tilenum/180);//1535
	    int firstcol = (int) (tilenum + ImageConstant.minLon*tilenum/180);//3488
		int col = firstcol + y/256;
		int row = firstrow - x/256;
		String index = col + "_" + row;
		if(Tilesets.contains(index)){
			result = ndvi[x][y];
		}else{
			String remoteFilePath = ImageConstant.remotedataPath +"ndvidata/"+index+".csv";
			URL url = null;
	        BufferedReader reader = null;
	        try
	        {
	            url = new URL(remoteFilePath);
	            reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
	            String temp = null;
	            int x1 = 0;
	    		while((temp = reader.readLine()) != null){
	    			String[] data = temp.split(",");
	    			for (int i = 0; i < data.length; i++) {
						ndvi[(firstrow-row)*256+x1][(col-firstcol)*256+i] = Double.parseDouble(data[i]);
					}
	    			x1++;
	    		}
	            
	        }catch (Exception e){
	            return 0;
	        }
	        finally{
	            try{
	            	if(reader != null){
	            		reader.close();
	            	}
	            }
	            catch (IOException e){
	                e.printStackTrace();
	            }
	        }
			Tilesets.add(index);
			result = ndvi[x][y];
		}
		
		return result;
	}

	public void clear(){
		firstFireLink = null;
		for(int k = 0; k < ImageConstant.hight; k++){
			for(int l = 0; l < ImageConstant.width; l++){
				fire[k][l] = 0;
			}
		}	
		iteration = 0;
		timesum = 0;
	}
	

	
	
	//y为燃烧点在影像中的高度（维度），x为燃烧点在影像中的宽度（经度）
		public  int[] getRGB(int y, int x){
			int rgbs[] = {0,0,0};
			//计算左下角瓦片（zoom=11）的行列号
			double tilenum = Math.pow(2, calzoom);
			int firstrow = (int) (tilenum - (90-ImageConstant.minLat)*tilenum/180);//1524
		    int firstcol = (int) (tilenum + ImageConstant.minLon*tilenum/180);//3488
		    //计算燃烧点所在瓦片及位置
			int row = firstrow + y/256;
			int col = firstcol + x/256;
		    int xpixel = x%256;
		    int ypixel = 255 - y%256;
		    String tile = col + "-" + row;
		    if(!imageDatas.containsKey(tile)){
		    	String filename = ImageConstant.cachePath + "/" + tile + ".jpeg";
		    	File f = new File(filename);
		    	if(!f.exists()){
		    		return rgbs;
		    	}else{
		    		DrawfirecacheCoordinator.getCoor().getImagefile(tile);
		    		ImageData imageData = new ImageData(filename);
		    		DrawfirecacheCoordinator.getCoor().freeImagefile(tile);
		    		imageDatas.put(tile, imageData);
		    	}
		    }
			int pixel = imageDatas.get(tile).getPixel(xpixel, ypixel);
			rgbs[0] = (pixel & 0x000000ff);
			rgbs[1] = (pixel & 0x0000ff00) >> 8;
			rgbs[2] = (pixel & 0x00ff0000) >> 16;
			return rgbs;
		}
	
	
}
