package rcptext;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

public class DrawFireResult extends Thread {

	private ArrayList<String> fireresult = new ArrayList<String>();
	private int itera ;
	public DrawFireResult(ArrayList<String> fireresult, int iteration){
		this.fireresult = fireresult;
		this.itera = iteration;
	}
	
	@Override
	public void run() {
		BufferedImage fireimage = new BufferedImage(ImageConstant.width, ImageConstant.hight, BufferedImage.TYPE_INT_RGB);
		Graphics graphic = fireimage.getGraphics();
        graphic.setColor(Color.WHITE);
        graphic.fillRect(0, 0, ImageConstant.width, ImageConstant.hight);
        graphic.setColor(Color.BLACK);
		Iterator<String> it= fireresult.iterator();
		while(it.hasNext()){
			String location = it.next();
			int row = Integer.parseInt(location.split("-")[0]);
			int col = Integer.parseInt(location.split("-")[1]);
			graphic.fillRect(col, row, 1, 1);
		}
	 
		String firename = ImageConstant.fireCache +"/" + itera + ".jpg";
		try {
			ImageIO.write(fireimage, "jpg", new File(firename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
