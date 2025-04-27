package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import GameObject.Objects.BezierTerrain;
import Logic.Bezier;
import Logic.Line;
import Logic.Rectangle;
import Logic.Vector2;
import Rendering.Camera;
import Rendering.Text;
import UI.OptionPane;
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
	
	public static int count = 0, choice = 0;
	public static String location = "MENU";
	
	public static OptionPane o_pane;
	public static boolean typing = false;
	public static boolean pause = false;
	
	public static void main(String[] args) {
		
		Text.start_font(Text.font_name);
		
		init();
		
		UI.main_menu();
	}
	
	public static void init() {
		init_vis_start();
		
		imgs[0] = new BufferedImage(pane.getWidth() / Camera.pixel_size, pane.getHeight() / Camera.pixel_size + 2, 1);
		imgs[1] = new BufferedImage(pane.getWidth() / Camera.pixel_size, pane.getHeight() / Camera.pixel_size + 2, 1);
		
		gs[0] = (Graphics2D) imgs[0].getGraphics();
		gs[1] = (Graphics2D) imgs[1].getGraphics();
		
		Camera.res_width = pane.getWidth() / Camera.pixel_size;
		Camera.res_height = pane.getHeight() / Camera.pixel_size;
	}
	public static void init_vis_start() {
		Start.frame = new JFrame();
	     Start.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     Start.frame.setUndecorated(true); // no window borders

	     Start.pane = new JPanel() {
	         @Override
	         protected void paintComponent(Graphics g) {
	        	 super.paintComponent(g);
	        	 //Start.g.drawImage(Start.op.filter(Start.imgs[(count+1)%2], null), 0, 0, Start.pane);
	        	 
	        	 g.drawImage(Start.op.filter(Start.imgs[(Start.count + 1)%2], null), 0, 0, this);
	         }
	     };
	     Start.pane.setSize(Start.frame.getWidth(), Start.frame.getHeight());
	     
	     Start.pane.setOpaque(true);
	     Start.pane.setBackground(Color.WHITE);
	     
	     GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	     gd.setFullScreenWindow(Start.frame); // go fullscreen

	     Start.frame.getContentPane().add(Start.pane); // ✅ now it's in the correct content pane
	     Start.frame.setVisible(true); // show it

	     Start.pane.revalidate(); // force layout
	     Start.pane.repaint();    // initial draw
	}
}
