package rcptext;

import java.io.File;

public class ImageConstant {

	public static double minLon ;//= 126.5625;//126.6
	public static double minLat ;//= 43.9453125;//44
	public static double maxLon ;//= 128.056640625;//128
	public static double maxLat ;//= 45;//45
	public static int width ;//= 4352;//3635
	public static int hight ;//= 3072;//3782
	//用户输入
	public static double minX ;//= 126.6;
	public static double minY ;//= 44;
	public static double maxX ;//= 128;
	public static double maxY ;//= 45;
	//开始显示的级别，也决定燃烧矩阵和属性矩阵大小
	public static int zoom ;//= 11;
	public static String cachePath ;//= "C:/D/cache";
	public static String tiandituCache ;//= "C:/D/cache/tianditu";
	public static String fireCache ;//= "C:/D/cache/fireimage";
	public static String remotedataPath = "http://202.114.114.28:8080/firedata/";
	
	
	
	
	public static String cachePathName ="cache";
	public static String tiandituCacheName = "tianditu";
	public static String fireCacheName = "fireimage";
	
	static{
//		File cach = new File(cachePath);
//		File tianditucach = new File(tiandituCache);
//		File firecach = new File(fireCache);
//		
//		if(!cach.exists()){
//			cach.mkdirs();
//		}
//		if(!tianditucach.exists()){
//			tianditucach.mkdirs();
//		}
//		if(!firecach.exists()){
//			firecach.mkdirs();
//		}
		
		
		
		
		String currentPath = System.getProperty("user.dir",".");
		File file0 = new File(currentPath);
		File cacheFile = new File(file0, cachePathName);
		if(cacheFile.exists()&&cacheFile.isDirectory()){
		}else{
			cacheFile.mkdirs();
		}
		
		File tiandituFile = new File(cacheFile, tiandituCacheName);
		if(tiandituFile.exists()&&tiandituFile.isDirectory()){
			tiandituCache = tiandituFile.getAbsolutePath().replaceAll("\\\\", "/");
		}else{
			tiandituFile.mkdirs();
			tiandituCache = tiandituFile.getAbsolutePath().replaceAll("\\\\", "/");
		}
		
		File fireimageFile = new File(cacheFile, fireCacheName);
		if(fireimageFile.exists()&&fireimageFile.isDirectory()){
			fireCache = fireimageFile.getAbsolutePath().replaceAll("\\\\", "/");
		}else{
			fireimageFile.mkdirs();
			fireCache = fireimageFile.getAbsolutePath().replaceAll("\\\\", "/");
		}
		
		
		
	}
	
	public static double getMinLon() {
		return minLon;
	}
	public static void setMinLon(double minLon) {
		ImageConstant.minLon = minLon;
	}
	public static double getMinLat() {
		return minLat;
	}
	public static void setMinLat(double minLat) {
		ImageConstant.minLat = minLat;
	}
	public static double getMaxLon() {
		return maxLon;
	}
	public static void setMaxLon(double maxLon) {
		ImageConstant.maxLon = maxLon;
	}
	public static double getMaxLat() {
		return maxLat;
	}
	public static void setMaxLat(double maxLat) {
		ImageConstant.maxLat = maxLat;
	}
	public static int getWidth() {
		return width;
	}
	public static void setWidth(int width) {
		ImageConstant.width = width;
	}
	public static int getHight() {
		return hight;
	}
	public static void setHight(int hight) {
		ImageConstant.hight = hight;
	}
	public static double getMinX() {
		return minX;
	}
	public static void setMinX(double minX) {
		ImageConstant.minX = minX;
	}
	public static double getMinY() {
		return minY;
	}
	public static void setMinY(double minY) {
		ImageConstant.minY = minY;
	}
	public static double getMaxX() {
		return maxX;
	}
	public static void setMaxX(double maxX) {
		ImageConstant.maxX = maxX;
	}
	public static double getMaxY() {
		return maxY;
	}
	public static void setMaxY(double maxY) {
		ImageConstant.maxY = maxY;
	}
	public static int getZoom() {
		return zoom;
	}
	public static void setZoom(int zoom) {
		ImageConstant.zoom = zoom;
	}
	public static void saveImageCacheName(String imageCacheName) {
		String currentPath = System.getProperty("user.dir",".");
		File file0 = new File(currentPath);
		File cacheFile = new File(file0, cachePathName);
		File imageCache = new File(cacheFile, imageCacheName);
		if(imageCache.exists()&&imageCache.isDirectory()){
			cachePath = imageCache.getAbsolutePath().replaceAll("\\\\", "/");
		}else{
			imageCache.mkdirs();
			cachePath = imageCache.getAbsolutePath().replaceAll("\\\\", "/");
		}
	}
	
	
}
