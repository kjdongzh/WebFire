package rcptext;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

public class FirstView extends ViewPart {

	public static final String ID = "rcptext.FirstView"; //$NON-NLS-1$
	private Text wind;
	private static Label east;
	private static Label west;
	private static Label north;
	private static Label south;
	private static Scale scale_1;
	private static Label firelength;
	private static Label firearea;
	private static Label firetime;
	private double arrow[][] = {
			{
				1.0D, 0
			}, {
				-0.875D, 0.25D
			}, {
				-0.75D, 0
			}, {
				-0.875D, -0.25D
			}
		};
    private double heading = 0;
	private int[] drawArrow = new int[8];
	private int xCurrent;
	private int yCurrent;
	private static double probabilityNorth;
	private static double probabilityEast;
	private static double probabilitySouth;
	private static double probabilityWest;
	private static int wet;
	private static int temperature;
	private static int weathermodel;
    private FireDrawThread thread;
	private FireDrawCoordinator coordinator;
	private Text text;
	private static String timeratio = "5";
    
	public FirstView() {
		coordinator = FireDrawCoordinator.getCoordinator();
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLocation(-64, -361);
		container.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		container.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		final Label label = new Label(container, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label.setBounds(10, 10, 119, 25);
		label.setText("火场周长(千米)：");
		
		firelength = new Label(container, SWT.CENTER);
		firelength.setText("0");
		firelength.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		firelength.setBounds(135, 10, 88, 25);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_2.setBounds(10, 147, 80, 25);
		label_2.setText("天气模式：");
		
		final Combo combo = new Combo(container, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		combo.setItems(new String[] {"    无雨", "    小雨", "    中雨", "    大雨"});
		combo.setBounds(123, 144, 88, 25);
		combo.select(0);
		combo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				weathermodel = combo.getSelectionIndex();
				wind.setFocus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setText("温度（℃）：");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_3.setBounds(10, 178, 80, 25);
		
		final Label label_4 = new Label(container, SWT.CENTER);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_4.setText("20℃");
		label_4.setBounds(123, 178, 61, 25);
		
		final Scale scale = new Scale(container, SWT.NONE);
		scale.setTouchEnabled(true);
		scale.setMaximum(40);
		scale.setMinimum(-40);
		scale.setSelection(20);
		scale.setBounds(10, 209, 170, 42);
		scale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				label_4.setText(scale.getSelection()+"℃");
				temperature = scale.getSelection();
				wind.setFocus();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label label_5 = new Label(container, SWT.NONE);
		label_5.setText("相对湿度（%）：");
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_5.setBounds(10, 257, 102, 25);
		
		final Label label_6 = new Label(container, SWT.CENTER);
		label_6.setText("10%");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_6.setBounds(123, 257, 61, 25);
		
		scale_1 = new Scale(container, SWT.NONE);
		scale_1.setSelection(10);
		scale_1.setBounds(10, 288, 170, 42);
		scale_1.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				label_6.setText(scale_1.getSelection()+"%");
				wet = scale_1.getSelection();
				wind.setFocus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label lblms = new Label(container, SWT.NONE);
		lblms.setText("相对风速(m/s)");
		lblms.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		lblms.setBounds(10, 336, 102, 25);
		
		wind = new Text(container, SWT.BORDER);
		wind.setText("0");
		wind.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		wind.setBounds(123, 333, 73, 25);
		wind.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				adjustWind(heading);
			}
		});
		final Canvas canvas = new Canvas(container, SWT.NONE);
		canvas.setBounds(58, 390, 120, 120);
		
		Label label_7 = new Label(container, SWT.NONE);
		label_7.setBounds(103, 367, 24, 17);
		label_7.setText("北：");
		
		north = new Label(container, SWT.NONE);
		north.setBounds(135, 367, 35, 17);
		
		Label label_9 = new Label(container, SWT.NONE);
		label_9.setText("西：");
		label_9.setBounds(28, 421, 24, 17);
		
		Label label_10 = new Label(container, SWT.NONE);
		label_10.setText("东：");
		label_10.setBounds(187, 421, 24, 17);
		
		east = new Label(container, SWT.NONE);
		east.setBounds(184, 444, 38, 17);
		
		west = new Label(container, SWT.NONE);
		west.setBounds(17, 444, 35, 17);
		
		Label label_13 = new Label(container, SWT.NONE);
		label_13.setText("南：");
		label_13.setBounds(103, 516, 24, 17);
		
		south = new Label(container, SWT.NONE);
		south.setBounds(133, 516, 35, 17);
		
		final Button startButton = new Button(container, SWT.NONE);
		startButton.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		startButton.setBounds(10, 552, 67, 30);
		startButton.setText("开始");
		
		final Button stopButton = new Button(container, SWT.NONE);
		stopButton.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		stopButton.setText("停止");
		stopButton.setBounds(103, 552, 61, 30);
		
		final Button clearButton = new Button(container, SWT.NONE);
		clearButton.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		clearButton.setBounds(64, 599, 61, 28);
		clearButton.setText("清空");
		
		Label label_8 = new Label(container, SWT.NONE);
		label_8.setText("过火面积(公顷)：");
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_8.setBounds(10, 43, 117, 25);
		
		firearea = new Label(container, SWT.CENTER);
		firearea.setText("0");
		firearea.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		firearea.setBounds(135, 43, 88, 25);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("模拟时间(小时)：");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_1.setBounds(10, 116, 117, 25);
		
		firetime = new Label(container, SWT.CENTER);
		firetime.setText("0");
		firetime.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		firetime.setBounds(135, 113, 88, 25);
		
		Label label_11 = new Label(container, SWT.NONE);
		label_11.setText("时间模式(分钟)：");
		label_11.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		label_11.setBounds(10, 85, 119, 25);
		
		text = new Text(container, SWT.BORDER);
		text.setText("5");
		text.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		text.setBounds(135, 82, 73, 25);
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				String timetext  = text.getText().trim();
				Pattern pattern = Pattern.compile("^([0-9]*)+(\\.[0-9]{1,5})?$");
				if(pattern.matcher(timetext).matches()){
					timeratio = timetext;
				}
			}
		});
		clearButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
				if(thread != null){
					thread.pause();
					thread = null;
				}
				FireModel.getFireModel().clear();
				MapView.setFirelat(0);
				MapView.setFirelon(0);
				MapView.setDoubleClicked(false);
				firelength.setText("0");
				firearea.setText("0");
				firetime.setText("0");
				
				File firecache = new File(ImageConstant.fireCache);
				if(firecache.exists()){
					File[] temps = firecache.listFiles();
					for (int i = 0; i < temps.length; i++) {
						temps[i].delete();
					}
				}
				MapView.getCanvas().redraw();
				
				text.setEditable(true);
				startButton.setEnabled(true);
				stopButton.setEnabled(true);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		startButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			    if(MapView.getFirelon() > ImageConstant.minLon && MapView.getFirelon() < ImageConstant.maxLon 
			    		&& MapView.getFirelat() > ImageConstant.minLat && MapView.getFirelat() < ImageConstant.maxLat){
			    	int[] pixel = fromGeoToPixel(MapView.getFirelon(), MapView.getFirelat());
			    	int i = pixel[0];//对应行号，Y轴
			    	int j = pixel[1];//对应列号，x轴
			    	FireModel.getFireModel().startFire(i, j);
			    	MapView.setDoubleClicked(true);
			    	
			    	text.setEditable(false);
			    	FireModel.getFireModel().setCaltimeratio(Double.parseDouble(timeratio));
			    	startButton.setEnabled(false);
			    	stopButton.setEnabled(true);
			    	clearButton.setEnabled(false);
			    	adjustWind(heading);
			    	thread = new FireDrawThread(FireModel.getFireModel());
			    	thread.start();
			    }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		
		stopButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				if(thread != null){
					thread.pause();
				}
				clearButton.setEnabled(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				e.gc.drawOval(10, 10, 100, 100);
				e.gc.drawLine(60, 10, 60, 110);
				e.gc.drawLine(10, 60, 110, 60);
				e.gc.drawLine(25, 25, 95, 95);
				e.gc.drawLine(25, 95, 95, 25);
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_GRAY));
				e.gc.fillOval(15, 15, 90, 90);
				
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				xCurrent = 110;
				yCurrent = 60;
				heading = Math.atan2(0, 1);
				double d = Math.cos(heading);
				double d1 = Math.sin(heading);
				for (int k = 0; k < 4; k++){
					 int index = 2*k;
					 drawArrow[index] = 60 + (int)(50 * (d * arrow[k][0] + d1 * arrow[k][1]));
					 drawArrow[index+1] = 60 + (int)(50 * (d * arrow[k][1] - d1 * arrow[k][0]));
				}
				e.gc.fillPolygon(drawArrow);
			}


		});
		
		canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				GC gc = new GC(canvas);			
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
				drawArr(gc, xCurrent, yCurrent);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawLine(60, 10, 60, 110);
				gc.drawLine(10, 60, 110, 60);
				gc.drawLine(25, 25, 95, 95);
				gc.drawLine(25, 95, 95, 25);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_GRAY));
				gc.fillOval(15, 15, 90, 90);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				drawArr(gc, e.x, e.y);
				gc.dispose();
				adjustWind(heading);
			}
			

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				GC gc = new GC(canvas);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
				drawArr(gc, xCurrent, yCurrent);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawLine(60, 10, 60, 110);
				gc.drawLine(10, 60, 110, 60);
				gc.drawLine(25, 25, 95, 95);
				gc.drawLine(25, 95, 95, 25);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_GRAY));
				gc.fillOval(15, 15, 90, 90);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				drawArr(gc, e.x, e.y);
				gc.dispose();
				adjustWind(heading);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		canvas.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				// TODO Auto-generated method stub
				GC gc = new GC(canvas);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
				drawArr(gc, xCurrent, yCurrent);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawLine(60, 10, 60, 110);
				gc.drawLine(10, 60, 110, 60);
				gc.drawLine(25, 25, 95, 95);
				gc.drawLine(25, 95, 95, 25);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_GRAY));
				gc.fillOval(15, 15, 90, 90);
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				drawArr(gc, e.x, e.y);
				gc.dispose();
				adjustWind(heading);
			}
		});
		
		canvas.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseExit(MouseEvent arg0) {
				// TODO Auto-generated method stub
				coordinator.freeResources();
			}
			
			@Override
			public void mouseEnter(MouseEvent arg0) {
				// TODO Auto-generated method stub
				coordinator.getResources();
			}
		});
		
		
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	protected void adjustWind(double d) {
		String windString = wind.getText().trim();
		Pattern pattern = Pattern.compile("^([0-9]*)+(\\.[0-9]{1,5})?$");
		if(windString.length() == 0||(!pattern.matcher(windString).matches())){
			probabilitySouth=0;
			probabilityNorth=0;
			probabilityEast=0;
			probabilityWest=0;
			east.setText("");
			west.setText("");
			north.setText("");
			south.setText("");
			return;
		}
		double d2 = Double.parseDouble(wind.getText());
		if(d<0){
			//如果在第四象限
			if(d>-1.5707963){
				probabilitySouth=d2*Math.sin(-d);
				probabilityNorth=-probabilitySouth;
				probabilityEast=d2*Math.cos(-d);
				probabilityWest=-probabilityEast;
			}else{
				d=3.1415926+d;
				probabilitySouth=d2*Math.sin(d);
				probabilityNorth=-probabilitySouth;
				
				probabilityWest=d2*Math.cos(d);
				probabilityEast=-probabilityWest;
			}
			
		}else{
			//如果在第一象限
			if(d<1.5707963){
				probabilityNorth=d2*Math.sin(d);
				probabilitySouth=-probabilityNorth;
				probabilityEast=d2*Math.cos(d);
				probabilityWest=-probabilityEast;
				
			}else{
				d=3.1415926-d;
				probabilityNorth=d2*Math.sin(d);
				probabilitySouth=-probabilityNorth;
				probabilityWest=d2*Math.cos(d);
				probabilityEast=-probabilityWest;
				
			}
		}
		
		east.setText(pString(probabilityEast));
		west.setText(pString(probabilityWest));
		north.setText(pString(probabilityNorth));
		south.setText(pString(probabilitySouth));
	}

	private String pString(double d){
		DecimalFormat   format=new DecimalFormat( "##.### ");
		return format.format(d);
	}
	
	private void drawArr(GC gc, int i, int j) {
		xCurrent = i;
		yCurrent = j;
		heading = Math.atan2(60-j, i-60);
		double d = Math.cos(heading);
		double d1 = Math.sin(heading);
		for (int k = 0; k < 4; k++){
			int index = 2*k;
			drawArrow[index] = 60 + (int)(50 * (d * arrow[k][0] + d1 * arrow[k][1]));
			drawArrow[index+1] = 60 + (int)(50 * (d * arrow[k][1] - d1 * arrow[k][0]));
		}
		gc.fillPolygon(drawArrow);
	}

	private int[] fromGeoToPixel(double lon, double lat) {
		
	    int[] pixel = new int[2];
    	//x对应于x轴，图像横轴，数组的列
    	int x = (int) ((lon - ImageConstant.minLon)*(ImageConstant.width - 1)/(ImageConstant.maxLon - ImageConstant.minLon));
    	int y = (int) ((lat - ImageConstant.minLat)*(ImageConstant.hight - 1)/(ImageConstant.maxLat - ImageConstant.minLat));
    	pixel[0] = y;
    	pixel[1] = x;
		return pixel;
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
	public static double getEastWind(){
		return probabilityEast;
	}
	public static double getWestWind(){
		return probabilityWest;
	}
	public static double getNorthWind(){
		return probabilityNorth;
	}
	public static double getSouthWind(){
		return probabilitySouth;
	}
	public static int getHumdity(){
		return wet;
	}
	public static int getTemperature() {
		return temperature;
	}

	public static void setTemperature(int temperature) {
		FirstView.temperature = temperature;
	}

	public static int getWeathermodel() {
		return weathermodel;
	}

	public static void setWeathermodel(int weathermodel) {
		FirstView.weathermodel = weathermodel;
	}

	public static void setFireattr(final String len, final String area){
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				firelength.setText(len);
				firearea.setText(area);
			}
		});
	}
	public static void setFiretime(final String time){
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				firetime.setText(time);
			}
		});
	}

	public static String getTimeratio() {
		return timeratio;
	}

	public static void setTimeratio(String timeratio) {
		FirstView.timeratio = timeratio;
	}
	
	
	
}
