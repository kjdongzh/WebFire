package text;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ParseXML {

	private static String layername = "data";
	
	
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		URL url = new URL("http://localhost:8787/geoserver/fire/wms?service=WMS&version=1.1.0&request=GetCapabilities&layers=fire:data");
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
			if(name.equals(layername)){
				Element bound = element.element("LatLonBoundingBox");
				List<Attribute> attributes = bound.attributes();
				for (int i = 0; i < attributes.size(); i++) {
					System.out.println(attributes.get(i).getText());
				}
//				 for(Iterator it=bound.attributeIterator();it.hasNext();){
//	                Attribute attribute = (Attribute) it.next();
//	                String text=attribute.getText();
//	                System.out.println(text);
//	            }
				 break;
			}
		}
	
		
	}
	
	private double[] parseCapabilitiesXMLtoLonLat(String wmsAddress, String layername) throws Exception {
		double[] extent = {0,0,0,0};
		String capabilitiesurl = wmsAddress + "?service=WMS&version=1.1.0&request=GetCapabilities";
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
			if(name.equals(layername)){
				Element bound = element.element("LatLonBoundingBox");
				List<Attribute> attributes = bound.attributes();
				for (int i = 0; i < attributes.size(); i++) {
					extent[i] = Double.parseDouble(attributes.get(i).getText());
					System.out.println(extent[i]);
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
	
}
