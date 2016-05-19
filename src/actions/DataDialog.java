package actions;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class DataDialog extends TitleAreaDialog{

	private Text serverAddress;
	private Text layer;
	private String address;
	private String layerName;
//	private String minX;
//	private String minY;
//	private String maxX;
//	private String maxY;
	
	
	public DataDialog(Shell parentShell) {
		super(parentShell);
	}
	public DataDialog(Shell parentShell, String address, String layername) {
		super(parentShell);
		this.address = address;
		this.layerName = layername;
	}

	@Override
	protected Control createContents(Composite parent) {
		super.createContents(parent);
		this.getShell().setText("参数对话框");
		this.setTitle("地图服务参数配置");
		this.setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
		return parent;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 60;
		layout.marginTop = 30;
		layout.marginBottom = 40;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		
		Label label1 = new Label(composite, SWT.NONE);
		label1.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
		label1.setText("服务地址 : ");
		
		serverAddress = new Text(composite, SWT.BORDER);
		GridData data = new GridData();
		data.widthHint = 250;
		serverAddress.setLayoutData(data);
		if(address != null&&address.length()>0){
			serverAddress.setText(address);
		}
		serverAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				address = serverAddress.getText().trim();
			}
		});
		serverAddress.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				if(address == null || address.isEmpty()){
					setMessage("请输入服务地址", IMessageProvider.WARNING);
				}else{
					//url地址正则表达式
					String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		            if (!address.matches(regex)) {
		            	setMessage("请输入合法的服务地址", IMessageProvider.ERROR);
		            } else {
		            	setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
		            }
		        }
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label kong1 = new Label(composite, SWT.NONE);
		Label addressExample = new Label(composite, SWT.NONE);
		addressExample.setFont(SWTResourceManager.getFont("微软雅黑", 8, SWT.NORMAL));
		//addressExample.setText("例如：http://localhost:8787/geoserver/gwc/service/wms");
		addressExample.setText("例如：http://localhost:8787/geoserver/<workspace>/wms");
		
		Label label2 = new Label(composite, SWT.NONE);
		label2.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
		label2.setText("图       层 : ");
		
		data = new GridData();
		data.widthHint = 100;
		layer = new Text(composite, SWT.BORDER);
		layer.setLayoutData(data);
		if(layerName != null&&layerName.length()>0){
			layer.setText(layerName);
		}
		layer.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				layerName = layer.getText().trim();
			}
		});
		layer.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				if(layerName == null || layerName.isEmpty()){
					setMessage("请输入图层名", IMessageProvider.WARNING);
				}else{
					if(!layerName.contains(":")){
						setMessage("请输入合法的图层名！", IMessageProvider.ERROR);
					}else{
						setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
					}
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		Label kong4 = new Label(composite, SWT.NONE);
		Label layerExample = new Label(composite, SWT.NONE);
		layerExample.setFont(SWTResourceManager.getFont("微软雅黑", 8, SWT.NORMAL));
		layerExample.setText("例如：fire:data/<workspace>:<layer>");
		
		
		
//		Label extent = new Label(composite, SWT.NONE);
//		extent.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
//		extent.setText("经纬范围 : ");
//		Label kong6 = new Label(composite, SWT.NONE);
//		
//		Label minLon = new Label(composite, SWT.NONE);
//		minLon.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
//		minLon.setText("最小经度");
//		final Text minLonText = new Text(composite, SWT.BORDER);
//		minLonText.setLayoutData(data);
//		minLonText.addModifyListener(new ModifyListener() {
//			
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				minX = minLonText.getText().trim();
//			}
//		});
//		minLonText.addFocusListener(new FocusListener() {
//			
//			@Override
//			public void focusLost(FocusEvent arg0) {
//				if(minX == null || minX.isEmpty()){
//					setMessage("请输入数字！", IMessageProvider.WARNING);
//				}else{
//					Pattern pattern = Pattern.compile("^[0-9]+.{0,1}[0-9]{0,10}$");
//					if(!pattern.matcher(minX).matches()){
//						setMessage("请输入合法数字！", IMessageProvider.ERROR);
//					}else{
//						setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
//					}
//				}
//			}
//			
//			@Override
//			public void focusGained(FocusEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
//		
//		Label minLat = new Label(composite, SWT.NONE);
//		minLat.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
//		minLat.setText("最小纬度");
//		final Text minLatText = new Text(composite, SWT.BORDER);
//		minLatText.setLayoutData(data);
//		minLatText.addModifyListener(new ModifyListener() {
//			
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				minY = minLatText.getText().trim();
//			}
//		});
//		minLatText.addFocusListener(new FocusListener() {
//			
//			@Override
//			public void focusLost(FocusEvent arg0) {
//				if(minY == null || minY.isEmpty()){
//					setMessage("请输入数字！", IMessageProvider.WARNING);
//				}else{
//					Pattern pattern = Pattern.compile("^[0-9]+.{0,1}[0-9]{0,10}$");
//					if(!pattern.matcher(minY).matches()){
//						setMessage("请输入合法数字！", IMessageProvider.ERROR);
//					}else{
//						setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
//					}
//				}
//			}
//			
//			@Override
//			public void focusGained(FocusEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
//		Label maxLon = new Label(composite, SWT.NONE);
//		maxLon.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
//		maxLon.setText("最大经度");
//		final Text maxLonText = new Text(composite, SWT.BORDER);
//		maxLonText.setLayoutData(data);
//		maxLonText.addModifyListener(new ModifyListener() {
//			
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				maxX = maxLonText.getText().trim();
//			}
//		});
//		maxLonText.addFocusListener(new FocusListener() {
//			
//			@Override
//			public void focusLost(FocusEvent arg0) {
//				if(maxX == null || maxX.isEmpty()){
//					setMessage("请输入数字！", IMessageProvider.WARNING);
//				}else{
//					Pattern pattern = Pattern.compile("^[0-9]+.{0,1}[0-9]{0,10}$");
//					if(!pattern.matcher(maxX).matches()){
//						setMessage("请输入合法数字！", IMessageProvider.ERROR);
//					}else{
//						setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
//					}
//				}
//			}
//			
//			@Override
//			public void focusGained(FocusEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
//		Label maxLat = new Label(composite, SWT.NONE);
//		maxLat.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
//		maxLat.setText("最大纬度");
//		final Text maxLatText = new Text(composite, SWT.BORDER);
//		maxLatText.setLayoutData(data);
//		maxLatText.addModifyListener(new ModifyListener() {
//			
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				maxY = maxLatText.getText().trim();
//			}
//		});
//		maxLatText.addFocusListener(new FocusListener() {
//			
//			@Override
//			public void focusLost(FocusEvent arg0) {
//				if(maxY == null || maxY.isEmpty()){
//					setMessage("请输入数字！", IMessageProvider.WARNING);
//				}else{
//					Pattern pattern = Pattern.compile("^[0-9]+.{0,1}[0-9]{0,10}$");
//					if(!pattern.matcher(maxY).matches()){
//						setMessage("请输入合法数字！", IMessageProvider.ERROR);
//					}else{
//						setMessage("请输入地图服务地址、图层名及经纬度范围", IMessageProvider.INFORMATION);
//					}
//				}
//			}
//			
//			@Override
//			public void focusGained(FocusEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		
		return parent;
	}

	public String getAddress() {
		return address;
	}


	public String getLayerName() {
		return layerName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public Text getServerAddress() {
		return serverAddress;
	}

	public Text getLayer() {
		return layer;
	}

//	public String getMinX() {
//		return minX;
//	}
//
//	public String getMinY() {
//		return minY;
//	}
//
//	public String getMaxX() {
//		return maxX;
//	}
//
//	public String getMaxY() {
//		return maxY;
//	}

}
