package actions;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import rcptext.DownloadThread;
import rcptext.ImageConstant;
import rcptext.MapView;

public class WMSAction extends Action implements IWorkbenchAction{

	private  IWorkbenchWindow workbenchWindow;
	private String prefix;
	private String address;
	private String layerName;
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	
	public WMSAction(IWorkbenchWindow window) {
		super("服务器参数");
		this.setId("actions.WMSAction");
		this.workbenchWindow = window;
	}
	
	@Override
	public void run() {
		DataDialog dataDialog = null;
		//读取配置文件
		String currentPath = System.getProperty("user.dir",".");
		File file0 = new File(currentPath);
		File readfile = new File(file0, "imageconfig.txt");
		if(readfile.exists()&&readfile.isFile()){
			BufferedReader reader = null;
			String addressConfig = null;
			String layerConfig = null;
			try {
				reader = new BufferedReader(new FileReader(readfile));
				addressConfig = reader.readLine();
				layerConfig = reader.readLine();
				dataDialog = new DataDialog(workbenchWindow.getShell(),addressConfig,layerConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(reader != null)
						reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			dataDialog = new DataDialog(workbenchWindow.getShell());
		}
		
		int r = dataDialog.open();
		if(r == Window.OK){
			double[] lonlat = new double[4];
			try {
				address = dataDialog.getAddress();
				layerName = dataDialog.getLayerName();
				vertifyArguments(address,layerName);
				lonlat = parseCapabilitiesXMLtoLonLat(address,layerName);
			} catch (Exception e) {
				MessageDialog.openError(workbenchWindow.getShell(), "参数错误", e.getMessage());
				return;
			}
			
			minX = lonlat[0];
			minY = lonlat[1];
			maxX = lonlat[2];
			maxY = lonlat[3];
			if(minX == maxX || minY == maxY){
				return;
			}
			//保存配置文件
			File writeFile = new File(file0, "imageconfig.txt");
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(writeFile));
				writer.write(address);
				writer.newLine();
				writer.write(layerName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}finally{
				try {
					if(writer != null)
						writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//根据WMS地址生成GWC访问前缀
			String server = address.substring(address.indexOf("http://")+7).split("/")[0];
			String workspace = layerName.split(":")[0];
			String layername2 = layerName.split(":")[1];
			
			prefix = "http://" +server + "/geoserver/gwc/service/wms?LAYERS=" + workspace + "%3A" + layername2 
					+ "&FORMAT=image%2Fjpeg&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A4326&BBOX=";
			MapView.setPrefix(prefix);
			
			//生成当前地图服务的缓存文件夹
			String cacheservername = server.split(":")[0].replace(".", "")+server.split(":")[1];
			String imageCacheName = workspace+layername2+cacheservername;
			ImageConstant.saveImageCacheName(imageCacheName);
			//根据输入影像经纬范围生成最小外接瓦片的经纬范围和像素大小
			ImageConstant.setMinX(minX);
			ImageConstant.setMinY(minY);
			ImageConstant.setMaxX(maxX);
			ImageConstant.setMaxY(maxY);
			//根据影像经纬范围计算缩放级
			int zoom = calculatezoomlevel(maxX-minX);
			ImageConstant.setZoom(zoom);
			double diff = 180/Math.pow(2, zoom);
			double[] extent = {minX,minY,maxX,maxY};
			double[] imageargs = initialization(extent,zoom);
			ImageConstant.setMinLon(imageargs[0]);
			ImageConstant.setMinLat(imageargs[1]);
			ImageConstant.setMaxLon(imageargs[2]);
			ImageConstant.setMaxLat(imageargs[3]);
			ImageConstant.setWidth((int) imageargs[4]);
			ImageConstant.setHight((int) imageargs[5]);
			
		    String sufix = "&WIDTH=256&HEIGHT=256";
		    //计算第一幅瓦片的经纬度范围（包含126.6  45）
		    double firstminLon = ((int)(ImageConstant.minLon/diff))*diff;
		    double firstminLat = 90 - ((int)((90-ImageConstant.maxLat)/diff) + 1)*diff;
		    //计算第一幅瓦片的行列号
		    int firstrow = (int) (Math.pow(2, zoom) - (90-firstminLat)*Math.pow(2, zoom)/180);
		    int firstcol = (int) (Math.pow(2, zoom) + firstminLon*Math.pow(2, zoom)/180);
		    //计算最后一幅瓦片的经纬度范围（包含128  44）
		    double lastminLon = ((int)(ImageConstant.maxLon/diff))*diff;
		    double lastminLat = 90 - ((int)((90-ImageConstant.minLat)/diff) + 1)*diff;
		    //计算第一幅瓦片的行列号
		    int lastrow = (int) (Math.pow(2, zoom) - (90-lastminLat)*Math.pow(2, zoom)/180);
		    int lastcol = (int) (Math.pow(2, zoom) + lastminLon*Math.pow(2, zoom)/180);
		    
		    final Map<String, String> tileSet = new HashMap<String, String>();
		    for (int i = lastrow; i <= firstrow; i++) {
				for (int j = firstcol; j <= lastcol; j++) {
					//根据瓦片行列号计算瓦片范围
					double tileminlon = diff * (j - Math.pow(2, zoom));
					double tilemaxlon = diff * (j - Math.pow(2, zoom) + 1);
					double tileminlat = 90 - diff * (Math.pow(2, zoom) - i);
					double tilemaxlat = 90 - diff * (Math.pow(2, zoom) - i - 1);
					String tileUrl = prefix + tileminlon + "," + tileminlat + "," + tilemaxlon + "," + tilemaxlat + sufix;
					tileSet.put(j + "-" + i, tileUrl);
				}
			}
		    
		    final Set<String> keys = tileSet.keySet();
		    final int sum = keys.size();
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("影像数据初始化...", sum);
					
					 for(String key:keys){
	                    if(monitor.isCanceled()){
	                    	return;
	                    }
	                    monitor.worked(1);
	                    File f = new File(ImageConstant.cachePath + "/" + key + ".jpeg");
	                    if(!f.exists()){
							try {
								URL url = new URL(tileSet.get(key));
								BufferedImage image = ImageIO.read(url);
								ImageIO.write(image, "jpeg", f);
							} catch (Exception e) {
								e.printStackTrace();
							}
	                    }
					 }
	                monitor.done();
				}
			};
			try {
				new ProgressMonitorDialog(workbenchWindow.getShell()).run(true, true, runnable);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
			
			
			//mapview重新绘制
			double displayminlon = imageargs[0] + diff*((int)((imageargs[2]-imageargs[0])/(2*diff)));
			double displayminlat = imageargs[1] + diff*((int)((imageargs[3]-imageargs[1])/(2*diff)));//44.296875;
		    double displaymaxlon = displayminlon + diff;
		    double displaymaxlat = displayminlat + diff;
		    MapView.setZoom(zoom);
		    MapView.setDifference(diff);
		    MapView.setMinLon(displayminlon);
		    MapView.setMinLat(displayminlat);
		    MapView.setMaxLon(displaymaxlon);
		    MapView.setMaxLat(displaymaxlat);
		    MapView.getCanvas().redraw();
			
			downLoadTile(zoom+1);
		}
		
		
	}
	
	private int calculatezoomlevel(double d) {
		for (int i = 2; i < 16; i++) {
			double zoomlevel = 180/Math.pow(2, i);
			double tilenum = d/zoomlevel;
			if(tilenum>=10&&tilenum<=20){
				return i;
			}
		}
		return 16;
	}

	//根据wms地址，解析capabilities文档获取图层经纬度范围
	private double[] parseCapabilitiesXMLtoLonLat(String wmsAddress, String layername) throws Exception {
		double[] extent = {0,0,0,0};
		String capabilitiesurl = wmsAddress + "?service=WMS&version=1.1.0&request=GetCapabilities";
		String layername2 = layername.split(":")[1];
		SAXReader reader = new SAXReader();
		URL url = new URL(capabilitiesurl);
		Document document = reader.read(url);
		Element root = document.getRootElement();
		Element capabilityNode = root.element("Capability");
		Element layerNode = capabilityNode.element("Layer");
		List<Element> layerlist = layerNode.elements("Layer");
		Iterator<Element> layeriterator = layerlist.iterator();
		while(layeriterator.hasNext()){
			Element element = (Element) layeriterator.next();
			Element namenode = element.element("Name");
			String name = namenode.getText();
			if(name.equals(layername2)){
				Element bound = element.element("LatLonBoundingBox");
				List<Attribute> attributes = bound.attributes();
				for (int i = 0; i < attributes.size(); i++) {
					extent[i] = Double.parseDouble(attributes.get(i).getText());
				}
				break;
			}
		}
		//xml文档获取的经纬度与实际计算的经纬度范围有所差别（实际要小），影响了显示范围和计算矩阵和tomcat中属性大小
//		double dvalue = 180/Math.pow(2, 10);
//		extent[0] = 126.6;//extent[0] + dvalue;
//		extent[1] = 44;//extent[1] + dvalue/2;
//		extent[2] = 128;//extent[2] - dvalue;
//		extent[3] = 45;//extent[3] - dvalue;
		return extent;
	}

	private double[] initialization(double[] extent, int zoom){
		double[] result = new double[6];
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
	    double width = (lastcol-firstcol+1)*256;
	    double hight = (firstrow-lastrow+1)*256;
	    double tileMaxLat = 90 - diff*(Math.pow(2, zoom)-firstrow-1);
		double tileMinLon = diff*(firstcol - Math.pow(2, zoom));
		double tileMinLat = 90 - diff*(Math.pow(2, zoom)-lastrow);
		double tileMaxLon = diff*(lastcol - Math.pow(2, zoom)+1);
		result[0] = tileMinLon;
		result[1] = tileMinLat;
		result[2] = tileMaxLon;
		result[3] = tileMaxLat;
		result[4] = width;
		result[5] = hight;
		return result;
	}
	
	private void vertifyArguments(String address, String layerName) throws Exception{
		if(address == null || address.isEmpty()){
			throw new Exception("错误的服务地址");
		}else{
			//url地址正则表达式
			String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            if (!address.matches(regex)) 
            	throw new Exception("错误的服务地址");
        }
		
		if(layerName == null || layerName.isEmpty()){
			throw new Exception("错误的图层名");
		}else{
			if(!layerName.contains(":"))
				throw new Exception("错误的图层名");
		}
		
	}

	private void downLoadTile(int zoom) {

		double diff = 180/Math.pow(2, zoom);
	    //计算第一幅瓦片的经纬度范围（包含126.6  45）
	    double firstminLon = ((int)(ImageConstant.minX/diff))*diff;
	    double firstminLat = 90 - ((int)((90-ImageConstant.maxY)/diff) + 1)*diff;
	    //计算第一幅瓦片的行列号
	    int firstrow = (int) (Math.pow(2, zoom) - (90-firstminLat)*Math.pow(2, zoom)/180);
	    int firstcol = (int) (Math.pow(2, zoom) + firstminLon*Math.pow(2, zoom)/180);
	    //计算最后一幅瓦片的经纬度范围（包含128  44）
	    double lastminLon = ((int)(ImageConstant.maxX/diff))*diff;
	    double lastminLat = 90 - ((int)((90-ImageConstant.minY)/diff) + 1)*diff;
	    //计算最后一幅瓦片的行列号
	    int lastrow = (int) (Math.pow(2, zoom) - (90-lastminLat)*Math.pow(2, zoom)/180);
	    int lastcol = (int) (Math.pow(2, zoom) + lastminLon*Math.pow(2, zoom)/180);
	    
	    List<String> tileSet = new CopyOnWriteArrayList<String>();
	    int num = 1;
	    for (int i = lastrow; i <= firstrow; i++) {
			for (int j = firstcol; j <= lastcol; j++) {
				//根据瓦片行列号计算瓦片范围
				double tileminlon = diff * (j - Math.pow(2, zoom));
				double tilemaxlon = diff * (j - Math.pow(2, zoom) + 1);
				double tileminlat = 90 - diff * (Math.pow(2, zoom) - i);
				double tilemaxlat = 90 - diff * (Math.pow(2, zoom) - i - 1);
				String tileUrl = prefix + tileminlon + "," + tileminlat + "," + tilemaxlon + "," + tilemaxlat + "&WIDTH=256&HEIGHT=256";
				tileSet.add(tileUrl);
				num++;
				if(num%100 == 0){
					new DownloadThread (tileSet,zoom).start();
					tileSet.clear();
				}
			}
		}
	    new DownloadThread (tileSet,zoom).start();	
	}

	@Override
	public void dispose() {
	}

}
