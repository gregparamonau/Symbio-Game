package LevelEdit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import DataManager.DataManager;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;

public class LevelEditor {
	static int screen_width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static int screen_height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static int frame_rate = 60;
	
	public static JFrame frame;
	public static JPanel pane;
	public static Graphics g;
	public static BufferedImage[] imgs = {new BufferedImage(screen_width, screen_height, 1), new BufferedImage(screen_width, screen_height, 1)};
	public static Graphics[] gs = {imgs[0].getGraphics(), imgs[1].getGraphics()};
	
	public static int grid_size = 24;
	public static int room_to_tile = 16;
	
	public static boolean in_room_editor = false;
	
	//overall editor stuff
	public static Vector2 pos = new Vector2(0, 0);
	public static Vector2 click = new Vector2(0, 0);
	public static Vector2 mouse_pos = new Vector2(0, 0);
	
	public static boolean mouse_pressed = false;
	
	public static void main(String[] args) {
		
		frame = new JFrame();
		frame.setSize(screen_width, screen_height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		pane = new JPanel();
		pane.setSize(frame.getWidth(), frame.getHeight() - frame.getInsets().top);
		frame.add(pane);
		try {Thread.sleep(2000);}catch(Exception e) {}
		g = pane.getGraphics();
		
		add_mouse_functions();
		add_key_functions();
		
		//init different aspects
		GameEditor.rooms_display = DataManager.load_rects(grid_size, room_to_tile);
		
		int count = 0;
		do {
			
			long a = System.currentTimeMillis();
			
			draw_view(gs[count%2]);
			
			count++;
			
			g.drawImage(imgs[count%2], 0, 0, pane);
			
			update();
			
			long b = System.currentTimeMillis();
			
			try {
				Thread.sleep(Math.max((int)(1000 / frame_rate - (b - a)), 0));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}while(true);
		
	}
	public static void update() {
		//System.out.println(select[0] + " " + select[1]);
		
		mouse_pos = convert_mouse_pos(current_mouse_pos());
		
		//System.out.println(mouse_pos);
		
		//in overall editor
		if (!in_room_editor) {
			GameEditor.update();
		}
		
		if (in_room_editor) RoomEditor.update();
		
		//TODO: room editor
	}
	public static void draw_view(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, screen_width, screen_height);
		
		draw_grid(g);
		
		//overall editor
		if (!in_room_editor) {
			GameEditor.draw_view(g);
		}
		
		//room_editor
		if (in_room_editor) {
			RoomEditor.draw_view(g);
		}
		
		g.setColor(Color.black);
		g.drawString("pos: " + LevelEditor.pos, 50, 50);
		
		click.draw_node(g, pane, pos.x, pos.y, "edit", Color.green);
		mouse_pos.draw_node(g, pane, pos.x, pos.y, "edit", Color.blue);
	}
	public static void draw_grid(Graphics g) {
		g.setColor(Color.black);
		Vector2 temp = new Vector2(pane.getWidth() / 2, pane.getHeight() / 2);
		for (int x = - pane.getWidth() / grid_size / 2; x<pane.getWidth() / grid_size / 2; x++) {
			for (int y = - pane.getHeight() / grid_size / 2; y<pane.getHeight() / grid_size / 2; y++) {
				g.drawRect((int)temp.x + x * grid_size, (int)temp.y + y * grid_size, grid_size, grid_size);
			}
		}
	}
	public static void add_mouse_functions() {
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				click = convert_mouse_pos(new Vector2(e.getX(), e.getY() - frame.getInsets().top));
				
				System.out.println("c: " + click);
				//overall game editor
				if (!in_room_editor) {
					GameEditor.mouse_pressed_function(e);	
				}
				
				if (in_room_editor) {
					RoomEditor.mouse_pressed_function(e);
				}
				
				//TODO: room editor
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//overall game editor
				if (!in_room_editor) {
					GameEditor.mouse_released_function(e);
				}
				
				if (in_room_editor) {
					RoomEditor.mouse_released_function(e);
				}
				
				//TODO: room editor
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
	}
	public static void add_key_functions() {
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 37) pos.x -= grid_size;
				if (e.getKeyCode() == 39) pos.x += grid_size;
				
				if (e.getKeyCode() == 38) pos.y += grid_size;
				if (e.getKeyCode() == 40) pos.y -= grid_size;
				
				//'p' to end
				if (e.getKeyCode() == 80) {
					
					if (in_room_editor) {
						DataManager.save(RoomEditor.room, RoomEditor.room_editing, grid_size, false);
					}
					System.exit(0);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
	public static Vector2 current_mouse_pos() {
		return new Vector2(MouseInfo.getPointerInfo().getLocation().getX() - frame.getLocationOnScreen().getX(), MouseInfo.getPointerInfo().getLocation().getY() - frame.getLocationOnScreen().getY() - frame.getInsets().top);
	}
	public static Vector2 convert_mouse_pos(Vector2 in) {
		return new Vector2((int)(pos.x + in.x - pane.getWidth()/2), (int)(pane.getHeight()/2 + pos.y - in.y));
	}
	
	public static double dist(Vector2 a, Vector2 b) {
		return Vector2.dist(a, b);
	}
}
