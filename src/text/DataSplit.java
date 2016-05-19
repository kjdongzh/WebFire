package text;

import java.nio.charset.Charset;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class DataSplit {
	
	static String[] record = new String[256];

	public static void main(String[] args) throws Exception {
		//getRowCol(11);
		//getPosition(128, 44, 3504, 1524, 11);
		
		//getExtent(6976, 3071, 12);
//		String filepath = "C:/D/ndvidata/";
//		int i = 1;
		
		//第一列的切法
//		    int col = 3488;
//			int row = 1535;
//			String filename = filepath + col +"_" + row + ".csv";
//			CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//			
//			
//			while(in.readRecord()){
//				for (int j = 0; j < 256; j++) {
//					if(j < 109){
//						record[j] = "0";
//					}else{
//						record[j] = in.get(j-109);
//					}
//				}
//				write.writeRecord(record);
//				
//				if(i%256 == 0){
//					write.close();
//					row = row-1;
//					if(row < 1524)
//						return;
//					filename = filepath + col +"_" + row + ".csv";
//					write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//				}
//				
//				i++;
//			}
//			
//			write.close();
			
			//第2-16列的切法
//			for(int col = 3489; col < 3504; col++){
//				CsvReader in = new CsvReader("C:/D/NDVI.csv", ',');
//				int row = 1535;
//				i = 1;
//				String filename = filepath + col +"_" + row + ".csv";
//				CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//				
//				
//				while(in.readRecord()){
//					for (int j = 0; j < 256; j++) {
//						int index = j+147+(col-3489)*256;
//						if(index > 3634){
//							record[j] = "0.5";
//						}else{
//							record[j] = in.get(index);
//						}
//					}
//					write.writeRecord(record);
//					
//					if(i%256 == 0){
//						write.close();
//						row = row-1;
//						if(row < 1524)
//							break;
//						filename = filepath + col +"_" + row + ".csv";
//						write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//					}
//					i++;
//				}
//				
//				in.close();
//			}
			
			//第17列的切法
//			    int col = 3504;
//				CsvReader in = new CsvReader("C:/D/NDVI.csv", ',');
//				int row = 1535;
//				String filename = filepath + col +"_" + row + ".csv";
//				CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//				
//				
//				while(in.readRecord()){
//					for (int j = 0; j < 256; j++) {
//						if(j > 91){
//							record[j] = "0";
//						}else{
//							record[j] = in.get(3543+j);
//						}
//					}
//					write.writeRecord(record);
//					
//					if(i%256 == 0){
//						write.close();
//						row = row-1;
//						if(row < 1524)
//							break;
//						filename = filepath + col +"_" + row + ".csv";
//						write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//					}
//					i++;
//				}
//				
//				in.close();
//				write.close();
		
		
		//把DEM的行宽变为6倍
//		String filename = "C:/D/DEMrow6.csv";
//	    CsvReader in = new CsvReader("C:/D/DEM.csv", ',');
//	    String record[] = new String[774];
//		CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//		while(in.readRecord()){
//			int colnum = in.getColumnCount();
//			for (int i = 0; i < colnum; i++) {
//				record[i] = in.get(i);
//			}
//			for (int i = 0; i < 6; i++) {
//				write.writeRecord(record);
//			}
//		}
//		in.close();
//		write.close();
		
		String filepath = "C:/D/demdata/";
		int i = 1;
		//第一列的切法
//	    int col = 3488;
//	    CsvReader in = new CsvReader("C:/D/DEMrow6.csv", ',');
//		int row = 1535;
//		String filename = filepath + col +"_" + row + ".csv";
//		CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//		
//		
//		while(in.readRecord()){
//			for (int j = 0; j < 256; j++) {
//				if(j < 109){
//					record[j] = "0";
//				}else{
//					record[j] = in.get((j-109)/6);
//				}
//			}
//			write.writeRecord(record);
//			if(i%256 == 0){
//				write.close();
//				row = row-1;
//				if(row < 1524)
//					return;
//				filename = filepath + col +"_" + row + ".csv";
//				write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//			}
//			
//			i++;
//		}
		

		//第2-16列的切法
//		for(int col = 3489; col < 3504; col++){
//			CsvReader in = new CsvReader("C:/D/DEMrow6.csv", ',');
//			int row = 1535;
//			i = 1;
//			String filename = filepath + col +"_" + row + ".csv";
//			CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//			while(in.readRecord()){
//				for (int j = 0; j < 256; j++) {
//					int index = j+147+(col-3489)*256;
//					record[j] = in.get(index/6);
//				}
//				write.writeRecord(record);
//				
//				if(i%256 == 0){
//					write.close();
//					row = row-1;
//					if(row < 1524)
//						break;
//					filename = filepath + col +"_" + row + ".csv";
//					write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
//				}
//				i++;
//			}
//			
//			in.close();
//		}
		
		//第17列的切法
	    int col = 3504;
	    i = 1;
		CsvReader in = new CsvReader("C:/D/DEMrow6.csv", ',');
		int row = 1535;
		String filename = filepath + col +"_" + row + ".csv";
		CsvWriter write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
		
		
		while(in.readRecord()){
			for (int j = 0; j < 256; j++) {
				if(j > 91){
					record[j] = "0";
				}else{
					int index = j+147+(col-3489)*256;
					record[j] = in.get(index/6);
				}
			}
			write.writeRecord(record);
			
			if(i%256 == 0){
				write.close();
				row = row-1;
				if(row < 1524)
					break;
				filename = filepath + col +"_" + row + ".csv";
				write = new CsvWriter(filename, ',', Charset.forName("utf-8"));
			}
			i++;
		}
		
		in.close();
	}
	
	/**
		3488--1535
		3504--1524
		共204个瓦片
	 */
	private static void getRowCol(int zoom){
		double diff = 180/Math.pow(2, zoom);
	    //计算第一幅瓦片的经纬度范围（包含126.6  45）
	    double firstminLon = ((int)(126.6/diff))*diff;
	    double firstminLat = 90 - ((int)((90-45)/diff) + 1)*diff;
	    //计算第一幅瓦片的行列号
	    int firstrow = (int) (Math.pow(2, zoom) - (90-firstminLat)*Math.pow(2, zoom)/180);
	    int firstcol = (int) (Math.pow(2, zoom) + firstminLon*Math.pow(2, zoom)/180);
	    System.out.println(firstcol + "--" + firstrow);
	    //计算最后一幅瓦片的经纬度范围（包含128  44）
	    double lastminLon = ((int)(128/diff))*diff;
	    double lastminLat = 90 - ((int)((90-44)/diff) + 1)*diff;
	    //计算最后一幅瓦片的行列号
	    int lastrow = (int) (Math.pow(2, zoom) - (90-lastminLat)*Math.pow(2, zoom)/180);
	    int lastcol = (int) (Math.pow(2, zoom) + lastminLon*Math.pow(2, zoom)/180);
	    System.out.println(lastcol + "--" + lastrow);
	    System.out.println("共"+(firstrow-lastrow+1)*(lastcol-firstcol+1)+"个瓦片");
	}
	private static void getPosition(double lon, double lat, int col, int row, int zoom){
		double sum = Math.pow(2, zoom);
		int px = (int) (((lon/180)*sum-col+sum)*256);
		int py = (int) ((((90-lat)/180)*sum-sum+row+1)*256);
		System.out.println("当前坐标所在位置("+ px+","+py+")");
	}

	//由行列号计算经纬范围
	private static void getExtent(int col, int row, int zoom){
		double diff = 180/Math.pow(2, zoom);
		double tileMinLat = 90 - diff*(Math.pow(2, zoom)-row);
		double tileMaxLat = tileMinLat + diff;
		double tileMinLon = diff*(col - Math.pow(2, zoom));
		double tileMaxLon = diff + tileMinLon;
		System.out.println(tileMinLon + " " + tileMinLat  +" " + tileMaxLon + " " +tileMaxLat);
	}
}
