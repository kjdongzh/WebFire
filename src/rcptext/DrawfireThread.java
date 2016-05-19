package rcptext;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

public class DrawfireThread extends Thread {

	private ArrayList<String> firecache = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> fireImage = new HashMap<String, ArrayList<String>>();

	public DrawfireThread(ArrayList<String> firecache){
		this.firecache = firecache;
	}
	@Override
	/**
	 * 1、firecache由hashmap改为arraylist,提高遍历速度
	 * 2、由计算每个点不同zoom下的位置改成计算不同zoom下的所有点的位置
	 * 3、减少影像IO操作，不同点对应同一幅瓦片时，只读取一次
	 */
	public void run() {
		
	  for (int i = 0; i < 6; i++) {
		int zoom = 7+i;
		double sum = Math.pow(2, zoom);
		double diff = 180/sum;
		
		Iterator<String> it = firecache.iterator();
		while(it.hasNext()){
			String rowcol = it.next();
			int x = Integer.parseInt(rowcol.split("-")[0]);
			int y = Integer.parseInt(rowcol.split("-")[1]);
			double lat = (x*(ImageConstant.maxLat-ImageConstant.minLat)/(ImageConstant.hight-1))+ImageConstant.minLat;
    		double lon = (y*(ImageConstant.maxLon-ImageConstant.minLon)/(ImageConstant.width-1))+ImageConstant.minLon;
			double minLon = ((int)(lon/diff))*diff;
			double minLat = 90 - ((int)((90-lat)/diff) + 1)*diff;
			//计算当前zoom下包含火域点的瓦片行列号
			int row = (int) (sum - (90-minLat)*sum/180);
			int col = (int) (sum + minLon*sum/180);
			//计算火域点在当前瓦片中的位置
			int px = (int) (((lon/180)*sum-col+sum)*256);
			int py = (int) ((((90-lat)/180)*sum-sum+row+1)*256);
			
			String index = col+"-"+row;
			String imagePosition = px+"-" +py;
			if(fireImage.containsKey(index)){
				fireImage.get(index).add(imagePosition);
			}else{
				fireImage.put(index, new ArrayList<String>());
				fireImage.get(index).add(imagePosition);
			}
			
		}
		
		Iterator<Entry<String, ArrayList<String>>> iterator = fireImage.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, ArrayList<String>> next = iterator.next();
			String imagename = next.getKey();
			DrawfirecacheCoordinator.getCoor().getImagefile(imagename);
			ArrayList<String> value = next.getValue();
			File output = new File(ImageConstant.fireCache + "/" +imagename+".jpeg");
			File input = new File(ImageConstant.cachePath + "/" +imagename+".jpeg");
			BufferedImage image = null;
			Graphics graphics = null;
			try {
				if(output.exists()&&output.isFile()){
					image = ImageIO.read(output);
					output.delete();
				}else if(input.exists()){
					image = ImageIO.read(input);
				}else{
					int tilecol = Integer.parseInt(imagename.split("-")[0]); 
					int tilerow = Integer.parseInt(imagename.split("-")[1]);
					//由列号计算zoom
					int tilezoom = (int) Math.floor(Math.log(tilecol)/Math.log(2));
					double tilediff = 180/Math.pow(2, tilezoom);
					double tileMinLon = tilediff*(tilecol - Math.pow(2, tilezoom));
					double tileMinLat = 90 - tilediff*(Math.pow(2, tilezoom)-tilerow);
					double tileMaxLon = tilediff*(tilecol - Math.pow(2, tilezoom)+1);
					double tileMaxLat = 90 - tilediff*(Math.pow(2, tilezoom)-tilerow-1);
					String tileurl = MapView.getPrefix() 
							+ tileMinLon + "," 
							+ tileMinLat + ","
							+ tileMaxLon + ","
							+ tileMaxLat + "&WIDTH=256&HEIGHT=256";
					URL tile = new URL(tileurl);
					image = ImageIO.read(tile);
					ImageIO.write(image, "jpeg", input);
				}
				graphics = image.createGraphics();
				graphics.setColor(java.awt.Color.black);
				Iterator<String> valueit = value.iterator();
				while(valueit.hasNext()){
					String imageposition = valueit.next();
					int imagex = Integer.parseInt(imageposition.split("-")[0]);
					int imagey = Integer.parseInt(imageposition.split("-")[1]);
					graphics.fillRect(imagex-2, imagey-2, 5, 5);
				}
				ImageIO.write(image, "jpeg", output);
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(100);
					ImageIO.write(image, "jpeg", output);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}finally{
				DrawfirecacheCoordinator.getCoor().freeImagefile(imagename);
			}
		
		}
		
	}
	
		
		
//	  while(iterator.hasNext()){
//		  Entry<String, ArrayList<String>> next = iterator.next();
//		  String imagename = next.getKey();
//		  DrawfirecacheCoordinator.getCoor().getImagefile(imagename);
//		  ArrayList<String> value = next.getValue();
//		  String imagepath = ImageConstant.fireCache + "/" +imagename+".jpeg";
//		  File f = new File(imagepath);
//		  if(!f.exists()){
//			  imagepath = ImageConstant.cachePath + "/" +imagename+".jpeg";
//			  f = new File(imagepath);
//		  }
//		  BufferedImage image = null;
//		  Graphics graphics = null;
//		  try {
//			  image = ImageIO.read(f);
////				if(imagepath == (ImageConstant.fireCache + "/" +imagename+".jpeg")){
////					f.delete();
////				}
//			  graphics = image.createGraphics();
//			  graphics.setColor(java.awt.Color.black);
//			  Iterator<String> valueit = value.iterator();
//			  while(valueit.hasNext()){
//				  String imageposition = valueit.next();
//				  int imagex = Integer.parseInt(imageposition.split("-")[0]);
//				  int imagey = Integer.parseInt(imageposition.split("-")[1]);
//				  graphics.fillRect(imagex-2, imagey-2, 5, 5);
//			  }
//			  if(imagepath == (ImageConstant.fireCache + "/" +imagename+".jpeg")){
//				  ImageIO.write(image, "jpeg", f);
//			  }else{
//				  ImageIO.write(image, "jpeg", new File(ImageConstant.fireCache + "/" +imagename+".jpeg"));
//			  }
//		  } catch (IOException e) {
//			  e.printStackTrace();
//		  }finally{
//			  DrawfirecacheCoordinator.getCoor().freeImagefile(imagename);
//		  }
//		  
//	  }
		
		
	}
	
	
	
	
}
