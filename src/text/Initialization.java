package text;

public class Initialization {

	private static int zoom = 11;
	public static void main(String[] args) {
		//double[] extent = {126.6,44,128,45};
		double[] extent = {126.43728678149054,43.874483284331205,128.18909003870743,45.12649877725868};
		//initialization(extent);
		
		double diff = extent[2]-extent[0];
		for (int i = 2; i < 16; i++) {
			double zoomlevel = 180/Math.pow(2, i);
			double tilenum = diff/zoomlevel;
			if(tilenum>=10&&tilenum<=20){
				System.out.println(i);
				break;
			}
		}
		//saveFirecache(126.6,45);
	}

	//根据输入的经纬度范围确定最小外接瓦片的范围以及像素矩阵的大小
	public static void initialization(double[] extent){
		double diff = 180/Math.pow(2, zoom);
	    //计算第一幅瓦片(左上角)的经纬度范围（最小经度extent[0]和最大纬度[3]）
	    double firstminLon = ((int)(extent[0]/diff))*diff;
	    double firstminLat = 90 - ((int)((90-extent[3])/diff) + 1)*diff;
	    //计算第一幅瓦片的行列号
	    int firstrow = (int) (Math.pow(2, zoom) - (90-firstminLat)*Math.pow(2, zoom)/180);
	    int firstcol = (int) (Math.pow(2, zoom) + firstminLon*Math.pow(2, zoom)/180);
	    //计算最后一幅瓦片（右下角）的经纬度范围（最大经度extent[2]和最小纬度[1]）
	    double lastminLon = ((int)(extent[2]/diff))*diff;
	    double lastminLat = 90 - ((int)((90-extent[1])/diff) + 1)*diff;
	    //计算最后一幅瓦片的行列号
	    int lastrow = (int) (Math.pow(2, zoom) - (90-lastminLat)*Math.pow(2, zoom)/180);
	    int lastcol = (int) (Math.pow(2, zoom) + lastminLon*Math.pow(2, zoom)/180);
	    System.out.println("共有"+(lastcol-firstcol+1)+"列瓦片"+(firstrow-lastrow+1)+"行瓦片");
	    System.out.println("从第"+firstcol+"列到第"+lastcol+"列");
	    System.out.println("从第"+lastrow+"行到第"+firstrow+"行");
	    System.out.println("属性矩阵："+(lastcol-firstcol+1)*256+"×"+(firstrow-lastrow+1)*256);
	    
	    double tileMaxLat = 90 - diff*(Math.pow(2, zoom)-firstrow-1);
		double tileMinLon = diff*(firstcol - Math.pow(2, zoom));
		double tileMinLat = 90 - diff*(Math.pow(2, zoom)-lastrow);
		double tileMaxLon = diff*(lastcol - Math.pow(2, zoom)+1);
		System.out.println("最小外接经纬度范围："+tileMinLon+","+tileMinLat+","+tileMaxLon+","+tileMaxLat);
	}
	//根据火域点的经纬度计算7-12级别下对应的瓦片行列号以及瓦片中位置
	public static void saveFirecache(double lon, double lat){
		for (int i = 0; i < 6; i++) {
			int zoom = 7+i;
			double sum = Math.pow(2, zoom);
			double diff = 180/sum;
			double minLon = ((int)(lon/diff))*diff;
		    double minLat = 90 - ((int)((90-lat)/diff) + 1)*diff;
		    //计算当前zoom下包含火域点的瓦片行列号
		    int row = (int) (sum - (90-minLat)*sum/180);
		    int col = (int) (sum + minLon*sum/180);
		    System.out.println(zoom + "-" + col + "-" + row);
		    //计算火域点在当前瓦片中的位置
			int px = (int) (((lon/180)*sum-col+sum)*256);
			int py = (int) ((((90-lat)/180)*sum-sum+row+1)*256);
			System.out.println(px + "-" + py);
		}
	}
}
