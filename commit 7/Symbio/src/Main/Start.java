package Main;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Rendering.Camera;
import Rendering.Text;
import UI.UI;

public class Start {
	public static int screen_width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static int screen_height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static JFrame frame = null;
	public static JPanel pane = null;
	public static Graphics2D g = null;
	
	public static BufferedImage[] imgs = new BufferedImage[2];
	public static Graphics2D[] gs = new Graphics2D[2];
	static AffineTransform transform = AffineTransform.getScaleInstance(Camera.pixel_size, Camera.pixel_size);
	public static AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	
	public static Camera cam = new Camera(0, 0);
	
	public static int count = 0;
	
	public static int choice = 0;
	
	public static void main(String[] args) {
		
		Text.start_font(Text.font_name);
		
		init();
		
		UI.main_menu();
	}
	
	public static void init() {
		frame = new JFrame();
		frame.setSize(screen_width, screen_height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		pane = new JPanel();
		pane.setSize(frame.getWidth(), frame.getHeight() - frame.getInsets().top);
		frame.add(pane);
		
		System.out.println("f: " + frame.getWidth() + " " + frame.getHeight());
		System.out.println("p: " + pane.getWidth() + " " + pane.getHeight());
		
		try {Thread.sleep(2000);}catch(Exception e) {e.printStackTrace();}
		g = (Graphics2D) pane.getGraphics();
		
		imgs[0] = new BufferedImage(pane.getWidth() / Camera.pixel_size, pane.getHeight() / Camera.pixel_size + 2, 1);
		imgs[1] = new BufferedImage(pane.getWidth() / Camera.pixel_size, pane.getHeight() / Camera.pixel_size + 2, 1);
		
		gs[0] = (Graphics2D) imgs[0].getGraphics();
		gs[1] = (Graphics2D) imgs[1].getGraphics();
		
		//Camera.pixel_size = screen_height / Camera.pixel_height;
		Camera.res_width = pane.getWidth() / Camera.pixel_size;
		Camera.res_height = pane.getHeight() / Camera.pixel_size;
		
	}
}
