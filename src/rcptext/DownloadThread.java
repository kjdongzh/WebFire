package rcptext;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

public class DownloadThread extends Thread {

	private List<String> tileSet = null;
	private int zoom;
	public DownloadThread(List<String> tileSet, int zoom){
		this.tileSet = tileSet;
		this.zoom = zoom;
	}
	@Override
	public void run() {
		for (String key:tileSet) {
			try {
				double tileMinLon = Double.parseDouble(getExtent(key)[0]);
				double tileMinLat = Double.parseDouble(getExtent(key)[1]);
				int row = (int) (Math.pow(2, zoom) - (90-tileMinLat)*Math.pow(2, zoom)/180);
				int col = (int) (Math.pow(2, zoom) + tileMinLon*Math.pow(2, zoom)/180);
				String filename = col + "-" + row;
				DrawfirecacheCoordinator.getCoor().getImagefile(filename);
				File f = new File(ImageConstant.cachePath + "/" + filename + ".jpeg");
				if(!f.exists()){
					URL url = new URL(key);
					BufferedImage image = ImageIO.read(url);
					ImageIO.write(image, "jpeg", f);
				}
				DrawfirecacheCoordinator.getCoor().freeImagefile(filename);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private String[] getExtent(String tileUrl){
		int bboxindex = tileUrl.lastIndexOf("BBOX=");
		String locationUrl = tileUrl.substring(bboxindex+5);
		String locationString = locationUrl.substring(0, locationUrl.length()-21);
		return locationString.split(",");
	}
	
}
