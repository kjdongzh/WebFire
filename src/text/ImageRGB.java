package text;

import org.eclipse.swt.graphics.ImageData;

public class ImageRGB {

	public static void main(String[] args) {
		ImageData imageData = new ImageData("C:/D/111.jpg");
		//ImageData imageData = image.getImageData();
		int pixel = imageData.getPixel(200, 200);
		int r = (pixel & 0x000000ff);
	    int g = (pixel & 0x0000ff00) >> 8;
	    int b = (pixel & 0x00ff0000) >> 16;
	    System.out.println("R G B: "+ r+","+g+","+b);
	    
//	    int redMask = imageData.palette.redMask;
//	    int blueMask = imageData.palette.blueMask;
//	    int greenMask = imageData.palette.greenMask;
//	    int r1 = pixel & redMask;
//        int g1 = (pixel & greenMask) >> 8;
//        int b1 = (pixel & blueMask) >> 16; 
//        System.out.println("R G B: "+ r1+","+g1+","+b1);
	}
}
