package rcptext;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;



public class MapView extends ViewPart {

	public static  Browser map;
	public static final String ID = "rcptext.MapView"; //$NON-NLS-1$

	private static double firelon;
    private static double firelat;
    private static String prefix = "http://***/geoserver/gwc/service/wms?LAYERS=fire%3Adata&FORMAT=image%2Fjpeg&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A4326&BBOX=";
    private static String sufix = "&WIDTH=256&HEIGHT=256";
    private static int zoom ;//= 11;
    private static double difference ;
    private static double minLon ;//= ImageConstant.minLon + difference*((int)((ImageConstant.maxLon-ImageConstant.minLon)/(2*difference)));
    private static double minLat ;//= ImageConstant.minLat + difference*((int)((ImageConstant.maxLat-ImageConstant.minLat)/(2*difference)));
    private static double maxLon ;//= minLon + difference;
    private static double maxLat ;//= minLat + difference;
    private static Set<Integer> zooms = new HashSet<Integer>();
    private static Canvas canvas = null;
	private moveListener listener;
    private int downX;
    private int downY;
    private int upX;
    private int upY;
    private int x_sum = 0;
    private int y_sum = 0;
    private int x = 0;
    private int y = 0;
    private int hoverx = 0;
    private int hovery = 0;
    private int wheelmovetime = 0;
    private String preUrl = "http://t5.tianditu.cn/img_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=c&";
    private static ArrayList<String> urls = new ArrayList<String>();
    private static ArrayList<String> tiandituUrls = new ArrayList<String>();
    private static ArrayList<TileLocation> locations = new ArrayList<TileLocation>();
    private static double leftX = minLon;
    private static double topY = maxLat;
    private static int canvasWidth = 0;
    private static int canvasHight = 0;
    private static boolean doubleClicked = false;
    private static int lastcolor;
    private FireDrawCoordinator coordinator;
    
    
    
	public MapView() throws Exception {
		listener = new moveListener();
		coordinator = FireDrawCoordinator.getCoordinator();
		
		if(ImageConstant.minLon!=ImageConstant.maxLon&&ImageConstant.minLat!=ImageConstant.maxLat){
			zoom = ImageConstant.getZoom();
			difference = 180/Math.pow(2, zoom);
		    minLon = ImageConstant.minLon + difference*((int)((ImageConstant.maxLon-ImageConstant.minLon)/(2*difference)));
		    minLat = ImageConstant.minLat + difference*((int)((ImageConstant.maxLat-ImageConstant.minLat)/(2*difference)));
		    maxLon = minLon + difference;
		    maxLat = minLat + difference;
		}else{
			zoom = 3;
			difference = 180/Math.pow(2, zoom);
			//暂定80为中国最西段经度
			minLon = (int)((80/difference))*difference;
			//暂定50为中国最北端纬度
			minLat = 90 - ((int)((90-50)/difference) + 1)*difference;
			maxLon = minLon + difference;
		    maxLat = minLat + difference;
		}
		
	}

	public static void setZoom(int zoom) {
		MapView.zoom = zoom;
		zooms.add(zoom);
	}

	public static void setDifference(double difference) {
		MapView.difference = difference;
	}

	public static double getFirelon() {
		return firelon;
	}

	public static double getFirelat() {
		return firelat;
	}

   public static String getPrefix() {
		return prefix;
	}

   public static void setFirelon(double firelon) {
		MapView.firelon = firelon;
	}

	public static void setPrefix(String prefix) {
		MapView.prefix = prefix;
	}

	public static void setFirelat(double firelat) {
		MapView.firelat = firelat;
	}
	public static void setMinLon(double minLon) {
		MapView.minLon = minLon;
	}
	
	public static void setMinLat(double minLat) {
		MapView.minLat = minLat;
	}
	
	public static void setMaxLon(double maxLon) {
		MapView.maxLon = maxLon;
	}
	
	public static void setMaxLat(double maxLat) {
		MapView.maxLat = maxLat;
	}
	
	public static Canvas getCanvas() {
		return canvas;
	}

	public static void setDoubleClicked(boolean b) {
		doubleClicked = b;
	}

	//鼠标按下时触发，拖动地图
	public class moveListener implements MouseMoveListener{
		@Override
		public void mouseMove(MouseEvent e) {
			x = e.x - downX;
			y = e.y - downY;
			canvas.redraw();
		}
		
	}
	//计算瓦片请求URL地址
	private ArrayList<String> calculateURL(int x, int y) {
		ArrayList<String> result = new ArrayList<String>();
		//屏幕左上角地理坐标
		leftX = minLon - (x/256.0)*difference;
		topY = maxLat + (y/256.0)*difference;
		//拖动后计算瓦片的偏移量
		int xoffset = 0;
		int yoffset = 0;
		if(x > 0){
			xoffset = -(x/256 + 1);
		}else{
			xoffset = -(x/256);
		}
		if(y > 0){
			yoffset = y/256 +1;
		}else{
			yoffset = y/256;
		}
		
		//左上角第一幅天地图瓦片的行列号(根据第一幅geoserver瓦片的经纬度计算)
//		double tileminlon = minLon +xoffset*difference;
//		double tilemaxlat = maxLat+ yoffset*difference;
//		int left = (int)Math.floor((tileminlon+ 180.00) * Math.pow(2, zoom+1) / 360.00);
//		int top = (int)Math.floor((90.00 - tilemaxlat) * Math.pow(2, (zoom)) / 180.00);
		int left = (int)Math.floor((leftX+ 180.00) * Math.pow(2, zoom+1) / 360.00);
		int top = (int)Math.floor((90.00 - topY) * Math.pow(2, (zoom)) / 180.00);
		
		int rowSum = 5;//行
		int colSum = 5;//列
		if(canvasHight !=0 && canvasWidth != 0){
			rowSum = canvasHight/256 + 2;
			colSum = canvasWidth/256 + 2;
		}
		for (int i = 0;  i < rowSum; i++) {
			for (int j = 0; j < colSum; j++) {
				String urlString = prefix + (minLon + (xoffset+j)*difference) 
						+ "," 
						+ (minLat+ (yoffset-i)*difference) 
						+ "," 
						+ (maxLon+ (xoffset+j)*difference) 
						+ "," 
						+ (maxLat+ (yoffset-i)*difference) 
						+ sufix;
				
				double tile_MinLat = minLat+ (yoffset-i)*difference;
				double tile_MinLon = minLon + (xoffset+j)*difference;
				double tile_MaxLat = maxLat+ (yoffset-i)*difference;
				double tile_MaxLon = maxLon+ (xoffset+j)*difference;
				if(zoom >= (ImageConstant.zoom+2) || tile_MinLon > ImageConstant.maxX || tile_MaxLon < ImageConstant.minX
						|| tile_MinLat > ImageConstant.maxY || tile_MaxLat < ImageConstant.minY ){
					
				}else{
					int row = (int) (Math.pow(2, zoom) - (90-tile_MinLat)*Math.pow(2, zoom)/180);
					int col = (int) (Math.pow(2, zoom) + tile_MinLon*Math.pow(2, zoom)/180);
					File f = new File(ImageConstant.cachePath + "/" + col + "-" + row + ".jpeg");
					if(!f.exists()&&!prefix.contains("**")){
						try {
							URL url = new URL(urlString);
							BufferedImage image = ImageIO.read(url);
							ImageIO.write(image, "jpeg", f);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				result.add(urlString);
			}
		}
		tiandituUrls.clear();
		for (int k = 0; k < rowSum; k++) {
			for (int l = 0; l < colSum; l++) {
				String tiandituUrl = preUrl + "&TILEMATRIX=" 
									+ Integer.toString(zoom+1) + "&TILEROW=" + (top+k) + "&TILECOL=" + (left+l) 
									+ "&style=default&format=tiles";
				tiandituUrls.add(tiandituUrl);
			}
		}
		
		return result;
	}
	//计算各瓦片屏幕坐标
	private ArrayList<TileLocation> calculateLoc(int x, int y){
		ArrayList<TileLocation> result = new ArrayList<TileLocation>();
		//第一幅瓦片左上角的屏幕坐标
		int xlocation = 0;
		int ylocation = 0;
		if(x > 0){
			xlocation = x-(x/256+1)*256;
		}else{
			xlocation = x%256;
		}
		if(y > 0){
			ylocation = y-(y/256+1)*256;
		}else{
			ylocation = y%256;
		}
		
		int rowSum = 5;//行
		int colSum = 5;//列
		if(canvasHight !=0 && canvasWidth != 0){
			rowSum = canvasHight/256 + 2;
			colSum = canvasWidth/256 + 2;
		}
		for (int i = 0; i < rowSum; i++) {
			for (int j = 0; j < colSum; j++) {
				result.add(new TileLocation(xlocation+j*256, ylocation+i*256, 256, 256));
			}
		}
		
		return result;
	}
	@Override
	public void createPartControl(Composite parent) {
		//canvas消除闪屏，存在拖动之后或火灾区不显示问题
		canvas = new Canvas(parent, SWT.NO_BACKGROUND|SWT.DOUBLE_BUFFERED);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				//根据canvas尺寸决定瓦片请求的数量
				Point size = canvas.getSize();
				canvasWidth = size.x;
				canvasHight = size.y;
				//根据左上角地理坐标获取瓦片地址及屏幕坐标
				urls = calculateURL(x+x_sum, y+y_sum);
				locations = calculateLoc(x+x_sum, y+y_sum);
				try {
					for (int i = 0; i < urls.size(); i++) {
						Image image =null;
						Image image2 = null;
						
						double tileMinLon = Double.parseDouble(getExtent(urls.get(i))[0]);
						double tileMinLat = Double.parseDouble(getExtent(urls.get(i))[1]);
						double tileMaxLon = Double.parseDouble(getExtent(urls.get(i))[2]);
						double tileMaxLat = Double.parseDouble(getExtent(urls.get(i))[3]);
						
						if(tileMinLon > ImageConstant.maxX || tileMaxLon < ImageConstant.minX || 
								tileMinLat > ImageConstant.maxY || tileMaxLat < ImageConstant.minY){
							String tiandituID = parseTianditu(tiandituUrls.get(i));
							String tiandituPath = ImageConstant.tiandituCache + "/" + tiandituID +".jpeg";
							if(new File(tiandituPath).exists()){
								image = new Image(Display.getDefault(), tiandituPath);
							}else{
								image = new Image(Display.getDefault(), new URL(tiandituUrls.get(i)).openStream());
								ImageIO.write(ImageIO.read(new URL(tiandituUrls.get(i))), "jpeg", new File(tiandituPath));
							}
							e.gc.drawImage(image, locations.get(i).getX(), locations.get(i).getY());
							image.dispose();
						}else{
							int row = (int) (Math.pow(2, zoom) - (90-tileMinLat)*Math.pow(2, zoom)/180);
							int col = (int) (Math.pow(2, zoom) + tileMinLon*Math.pow(2, zoom)/180);
							String filename = ImageConstant.fireCache + "/" + col + "-" + row + ".jpeg";
							
							DrawfirecacheCoordinator.getCoor().getImagefile(col + "-" + row);
							if(new File(filename).exists()){
								image2 = new Image(Display.getDefault(), filename);
							}else{
								filename = ImageConstant.cachePath + "/" + col + "-" + row + ".jpeg";
								if(new File(filename).exists()){
									image2 = new Image(Display.getDefault(), filename);
								}else{
									image2 = new Image(Display.getDefault(), this.getClass()
											.getClassLoader().getResourceAsStream("data/geoserver-dispatch.jpg"));
								}
							}
							DrawfirecacheCoordinator.getCoor().freeImagefile(col + "-" + row);
							
							if(tileMinLon>=ImageConstant.minX&&tileMaxLon<=ImageConstant.maxX
									&&tileMinLat>=ImageConstant.minY&&tileMaxLat<=ImageConstant.maxY){
								e.gc.drawImage(image2, locations.get(i).getX(), locations.get(i).getY());
								image2.dispose();
							}else{
								String tiandituID = parseTianditu(tiandituUrls.get(i));
								String tiandituPath = ImageConstant.tiandituCache + "/" + tiandituID +".jpeg";
								if(new File(tiandituPath).exists()){
									image = new Image(Display.getDefault(), tiandituPath);
								}else{
									image = new Image(Display.getDefault(), new URL(tiandituUrls.get(i)).openStream());
									ImageIO.write(ImageIO.read(new URL(tiandituUrls.get(i))), "jpeg", new File(tiandituPath));
								}
								e.gc.drawImage(image, locations.get(i).getX(), locations.get(i).getY());
								image.dispose();
								//原始影像中的横纵坐标、宽度、高度
								int imageX = 0;
								int imageY = 0;
								int imageWidth = 256;
								int imageHight = 256;
								if((tileMinLon-ImageConstant.minX)*(tileMaxLon-ImageConstant.minX)<=0&&tileMaxLon<ImageConstant.maxX){
									imageX = (int) ((ImageConstant.minX-tileMinLon)*256.0/difference);
									imageWidth = 256-imageX;
								}else if((tileMinLon-ImageConstant.maxX)*(tileMaxLon-ImageConstant.maxX)<=0&&tileMinLon>ImageConstant.minX){
									imageWidth = (int) ((ImageConstant.maxX-tileMinLon)*256.0/difference);
								}else if(tileMinLon < ImageConstant.minX && tileMaxLon > ImageConstant.maxX){
									imageX = (int) ((ImageConstant.minX-tileMinLon)*256.0/difference);
									imageWidth = (int) ((ImageConstant.maxX-ImageConstant.minX)*256.0/difference);
								}
								
								if((tileMinLat-ImageConstant.minY)*(tileMaxLat-ImageConstant.minY)<=0){
									imageHight = (int) ((tileMaxLat-ImageConstant.minY)*256.0/difference);
								}else if((tileMinLat-ImageConstant.maxY)*(tileMaxLat-ImageConstant.maxY)<=0){
									imageY = (int) ((tileMaxLat-ImageConstant.maxY)*256.0/difference);
									imageHight = 256 - imageY;
								}
								
								e.gc.drawImage(image2, imageX,imageY,imageWidth,imageHight,
										locations.get(i).getX()+imageX,locations.get(i).getY()+imageY,imageWidth,imageHight);
								image2.dispose();
							}
						}
						
//						if(tileMinLon > 128 || tileMaxLon < 126.6 || tileMinLat > 45 || tileMaxLat < 44){
////							image = new Image(Display.getDefault(), this.getClass()
////									.getClassLoader().getResourceAsStream("data/geoserver-dispatch.jpg"));
//							image = new Image(Display.getDefault(), new URL(tiandituUrls.get(i)).openStream());
//						}else{
//							int row = (int) (Math.pow(2, zoom) - (90-tileMinLat)*Math.pow(2, zoom)/180);
//							int col = (int) (Math.pow(2, zoom) + tileMinLon*Math.pow(2, zoom)/180);
//							String filename = ImageConstant.cachePath + "/" + col + "-" + row + ".jpeg";
//							if(new File(filename).exists()){
//								image = new Image(Display.getDefault(), filename);
//							}else{
//								image = new Image(Display.getDefault(), this.getClass()
//										.getClassLoader().getResourceAsStream("data/geoserver-dispatch.jpg"));
//							}
//						}
//						
//						e.gc.drawImage(image, locations.get(i).getX(), locations.get(i).getY());
//						image.dispose();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				//绘制着火点
				if(firelon != 0 && firelat != 0){
					double scale = (maxLon-minLon)/256;
					int x = (int) ((firelon - leftX)/scale);
					int y = (int) ((topY - firelat)/scale);
					e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
					e.gc.drawLine(x-5, y, x+5, y);
					e.gc.drawLine(x, y-5, x, y+5);
				}
				
 			}
		});
		canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseDown(MouseEvent e) {
					
				ImageData imageData = new ImageData(MapView.class.getClassLoader()
						.getResourceAsStream("icons/draw.png"));
				Cursor cursor = new Cursor(Display.getDefault(), imageData, 5, 5);
				canvas.setCursor(cursor);
				downX = e.x;
				downY = e.y;
				canvas.addMouseMoveListener(listener);
				coordinator.getResources();
			}
			
			@Override
			public void mouseUp(MouseEvent e) {
				upX = e.x;
				upY = e.y;
				x = 0;
				y = 0;
				x_sum += upX - downX;
				y_sum += upY - downY;
				//着火点符号移动（鼠标第一次up时重绘canvas）与火灾区移动冲突
//				if(e.count == 1){
//					coordinator.getResources();
//					FireModel.getFireModel().redrawFire();
//					coordinator.freeResources();
//				}
				canvas.setCursor(new Cursor(Display.getDefault(), SWT.CURSOR_ARROW));
				canvas.removeMouseMoveListener(listener);
				coordinator.freeResources();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(!doubleClicked){
					doubleClicked = true;
					GC gc = new GC(canvas);
					gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
					gc.drawLine(e.x-5, e.y, e.x+5, e.y);
					gc.drawLine(e.x, e.y-5, e.x, e.y+5);
					firelon = leftX + (e.x/256.0)*difference;
					firelat = topY - (e.y/256.0)*difference;
					gc.dispose();
				}
			}
			
		});

		//鼠标滑轮事件
		final Listener mouseWheel = new Listener() {
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
					case  SWT.MouseWheel:
						if(e.time != wheelmovetime &&Math.abs(e.time-wheelmovetime)>150){
							coordinator.getResources();
							wheelmovetime = e.time;
							//向下滑动，图片比例缩小，difference加倍，zoom变小
							if(e.count == -3 && zoom > 2){
								zoom = zoom -1;
								//下载zoom级下所有缓存
								if(!zooms.contains(zoom)&&zoom>=2&&zoom<(ImageConstant.zoom+2)&&!prefix.contains("**")){
									zooms.add(zoom);
									downLoadTile(zoom);
								}
								
								//根据鼠标处屏幕坐标计算对应瓦片的URL
								int xindex = hoverx - locations.get(0).getX();
								int yindex = hovery - locations.get(0).getY();
								double correctLon = leftX + (hoverx/256.0)*difference;
								double correctLat = topY - (hovery/256.0)*difference;
								//index (y-1)*5+x-1
								int colnum = canvasWidth/256+2;
								int index = (xindex/256)+(yindex/256)*colnum;
								//当前瓦片地址
								String tileUrl = urls.get(index);
								double present_minlon = Double.parseDouble(getExtent(tileUrl)[0]);
								double present_minlat = Double.parseDouble(getExtent(tileUrl)[1]);
								double present_maxlon = Double.parseDouble(getExtent(tileUrl)[2]);
								double present_maxlat = Double.parseDouble(getExtent(tileUrl)[3]);
								difference = difference*2;
								//计算包含当前瓦片的上一级瓦片的地理范围
								int xnum = (int) ((180-present_minlon+difference/2)/difference);
								present_minlon = 180 - xnum*difference;
								present_maxlon = present_minlon + difference;
								
								int ynum = (int) ((90 - present_maxlat)/difference);
								int sign = (ynum > 0) ? 1 : -1;
								present_maxlat = 90 - ynum*difference;
								present_minlat = present_maxlat - difference;
//								//计算第1幅瓦片的坐标范围
								minLon = present_minlon - (xindex/256)*difference;
								minLat = present_minlat + (yindex/256)*difference;
								maxLon = present_maxlon - (xindex/256)*difference;
								maxLat = present_maxlat + (yindex/256)*difference;
								double currentLon = minLon + (hoverx/256.0)*difference;
								double currentLat = maxLat - (hovery/256.0)*difference;
								x = 0;
								y = 0;
								x_sum = (int) ((currentLon - correctLon)*256/difference);
								y_sum = (int) ((correctLat - currentLat)*256/difference);
								canvas.redraw();
							}
							//向上滑动，图片比例放大，difference减半，zoom变大
							if(e.count == 3 && zoom < (ImageConstant.zoom+1)){
								zoom = zoom + 1;
								
								if(!zooms.contains(zoom)&&zoom>=2&&zoom<(ImageConstant.zoom+2)&&!prefix.contains("**")){
									zooms.add(zoom);
									downLoadTile(zoom);
								}
								
								//根据鼠标处屏幕坐标计算对应瓦片的URL
								int xindex = hoverx - locations.get(0).getX();
								int yindex = hovery - locations.get(0).getY();
								double correctLon = leftX + (hoverx/256.0)*difference;
								double correctLat = topY - (hovery/256.0)*difference;
								//index (y-1)*5+x-1
								int colnum = canvasWidth/256+2;
								int index = (xindex/256)+(yindex/256)*colnum;
								//当前瓦片地址
								String tileUrl = urls.get(index);
								double present_minlon = Double.parseDouble(getExtent(tileUrl)[0]);
								double present_minlat = Double.parseDouble(getExtent(tileUrl)[1]);
								double present_maxlon = Double.parseDouble(getExtent(tileUrl)[2]);
								double present_maxlat = Double.parseDouble(getExtent(tileUrl)[3]);
								difference = difference/2;
								//计算当前瓦片的下一级瓦片的地理范围
								present_minlon = present_maxlon - difference;
								present_minlat = present_maxlat - difference;
//								//计算第1幅瓦片的坐标范围
								minLon = present_minlon - (xindex/256)*difference;
								minLat = present_minlat + (yindex/256)*difference;
								maxLon = present_maxlon - (xindex/256)*difference;
								maxLat = present_maxlat + (yindex/256)*difference;
								double currentLon = minLon + (hoverx/256.0)*difference;
								double currentLat = maxLat - (hovery/256.0)*difference;
								x = 0;
								y = 0;
								x_sum = (int) ((currentLon - correctLon)*256/difference);
								y_sum = (int) ((correctLat - currentLat)*256/difference);
								canvas.redraw();
							}
							coordinator.freeResources();
						}
				}
			}

		};
		//鼠标移动事件，鼠标进入mapview视图时触发，获取鼠标的屏幕坐标
		final MouseMoveListener mouseHover = new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				hoverx = e.x;
				hovery = e.y;
			}
		};
		
		
		canvas.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseEnter(MouseEvent arg0) {
				canvas.getDisplay().addFilter(SWT.MouseWheel, mouseWheel);
				canvas.addMouseMoveListener(mouseHover);
			}
			
			@Override
			public void mouseExit(MouseEvent arg0) {
				canvas.getDisplay().removeFilter(SWT.MouseWheel, mouseWheel);
				canvas.removeMouseMoveListener(mouseHover);
			}
			
			@Override
			public void mouseHover(MouseEvent e) {
			}
		});
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
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
	    //计算第一幅瓦片的行列号
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
				String tileUrl = prefix + tileminlon + "," + tileminlat + "," + tilemaxlon + "," + tilemaxlat + sufix;
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


	//获取瓦片地址中的地理坐标范围
	private static String[] getExtent(String tileUrl){
		int bboxindex = tileUrl.lastIndexOf("BBOX=");
		String locationUrl = tileUrl.substring(bboxindex+5);
		String locationString = locationUrl.substring(0, locationUrl.length()-21);
		return locationString.split(",");
	}
	//解析天地图url
	private static String parseTianditu(String url){
		String uuid = null;
		int index = url.indexOf("TILEMATRIX=");
		String trimUrl = url.substring(index);
		String[] trimUrls = trimUrl.split("&");
		String zoom = trimUrls[0].substring(trimUrls[0].indexOf("=")+1);
		String row = trimUrls[1].substring(trimUrls[1].indexOf("=")+1);
		String col = trimUrls[2].substring(trimUrls[2].indexOf("=")+1);
		uuid = zoom + "_" + row+"_"+ col;
		return uuid;
	}
	
	public static void setFire(final int i, final int j, final byte firevalue) {
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	if(!canvas.isDisposed()){
		    		GC gc = new GC(canvas);
		    		switch(firevalue){
		    		case 1:
		    		case 2:	
		    		case 3:
		    		case 4:
		    			gc.setBackground(++lastcolor% 2 != 0 ?Display.getDefault().getSystemColor(SWT.COLOR_YELLOW):
		    				new Color(Display.getDefault(), 255, 200, 0));
		    			break;
		    		case 5:
		    		case 6: 
		    			gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));//迭代6次表示完全燃烧
		    			break;
		    		case 7: 
		    			gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		    			break;
		    		default:
		    			gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
		    			break;	
		    		}
		    		//地图大小改变（3635*3782   8605*8592），发生仿射变换，显示效果为菱形
		    		double firela = (i*(ImageConstant.maxLat-ImageConstant.minLat)/(ImageConstant.hight-1))+ImageConstant.minLat;
		    		double firelo = (j*(ImageConstant.maxLon-ImageConstant.minLon)/(ImageConstant.width-1))+ImageConstant.minLon;
		    		//firelo = firelon + (firelo-firelon)*0.71;
		    		int x = (int) ((firelo - leftX)*256.0/difference);
		    		int y = (int) ((topY - firela)*256.0/difference);
		    		
		    		if(firevalue < 5){
		    			gc.fillRectangle(x, y, 3, 3);
		    		}else{
		    			gc.fillRectangle(x, y, 3, 3);
		    		}
		    		gc.dispose();
		    	}
		    }
	    }); 
	}
	
	
}
