package LevelEdit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import Main.Start;
import Rendering.Camera;
import Rendering.Text;

public class LevelEditor {
	public static int frame_rate = 60;
	
	public static int grid_size = 8;
	public static int room_to_tile = 16;
	
	public static boolean in_room_editor = false;
	
	//overall editor stuff
	public static Vector2 pos = new Vector2(0, 0);
	public static Vector2 click = new Vector2(0, 0), click_save;
	public static Vector2 mouse_pos = new Vector2(0, 0);
	
	public static boolean mouse_pressed = false;
	
	public static void level_editor() {
		
		/*Start.frame = new JFrame();
		Start.frame.setSize(Start.screen_width, Start.screen_height);
		Start.frame.setLocationRelativeTo(null);
		Start.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Start.frame.setVisible(true);
		
		Start.pane = new JPanel();
		Start.pane.setSize(Start.frame.getWidth(), Start.frame.getHeight() - Start.frame.getInsets().top);
		Start.frame.add(Start.pane);
		try {Thread.sleep(2000);}catch(Exception e) {}
		Start.g = (Graphics2D) Start.pane.getGraphics();*/
		
		/*Start.pane = new JPanel() {
	         @Override
	         protected void paintComponent(Graphics g) {
	        	 super.paintComponent(g);
	        	 
	        	 if (Start.o_pane!= null) Start.o_pane.update();
	        	 //Start.g.drawImage(Start.op.filter(Start.imgs[(count+1)%2], null), 0, 0, Start.pane);
	        	 
	        	 g.drawImage(Start.imgs[(Start.count + 1)%2], 0, 0, this);
	         }
	     };*/
	     Start.pane.setSize(Start.frame.getWidth(), Start.frame.getHeight());
	     
	     Start.pane.setOpaque(true);
	     Start.pane.setBackground(Color.WHITE);
	     
	     GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	     gd.setFullScreenWindow(Start.frame); // go fullscreen

	     Start.frame.getContentPane().add(Start.pane); // ✅ now it's in the correct content pane
	     Start.frame.setVisible(true); // show it

	     Start.pane.revalidate(); // force layout
	     //Start.pane.repaint();    // initial draw
		
		/*Start.imgs[0] = new BufferedImage(Start.pane.getWidth(), Start.pane.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Start.imgs[1] = new BufferedImage(Start.pane.getWidth(), Start.pane.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Start.gs[0] = (Graphics2D) Start.imgs[0].getGraphics();
		Start.gs[1] = (Graphics2D) Start.imgs[1].getGraphics();*/
		
		add_mouse_functions();
		add_key_functions();
		
		//init different aspects
		GameEditor.rooms_display = DataManager.load_rects(grid_size, room_to_tile);
		
		//int count = 0;
		do {
			
			long a = System.currentTimeMillis();
			
			if (Start.o_pane!= null) Start.o_pane.update();
			
			draw_view(Start.gs[Start.count%2]);
			
			Start.pane.repaint();		
			
			Start.count++;//Start.g.drawImage(Start.imgs[count%2], 0, 0, Start.pane);
			
			update();
			
			long b = System.currentTimeMillis();
			
			System.out.println("LEVEL EDITOR DT: " + (b - a));
			
			try {
				Thread.sleep(Math.max((int)(1000 / frame_rate - (b - a)), 0));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}while(true);
		
	}
	public static void update() {
		//System.out.println(select[0] + " " + select[1]);
		
		mouse_pos = convert_mouse_pos(current_mouse_pos(), LevelEditor.pos);
		
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
		g.fillRect(0, 0, Start.screen_width, Start.screen_height);
		
		draw_grid(g);
		
		//overall editor
		if (!in_room_editor) {
			GameEditor.draw_view(g);
		}
		
		//room_editor
		if (in_room_editor) {
			RoomEditor.draw_view(g);
		}
		
		if (Start.o_pane != null) Start.o_pane.draw(g, Start.pane, pos.x, pos.y, "edit");
		
		Text.draw_string(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit", "" + LevelEditor.pos, new Rectangle(LevelEditor.pos.x - 80, LevelEditor.pos.y + 70, 100, 10), 3);
		//g.setColor(Color.black);
		//g.drawString("pos: " + LevelEditor.pos, 50, 50);
		
		click.draw_node(g, Start.pane, pos.x, pos.y, "edit", Color.green);
		mouse_pos.draw_node(g, Start.pane, pos.x, pos.y, "edit", Color.blue);
	}
	public static void draw_grid(Graphics g) {
		g.setColor(Color.black);
		Vector2 temp = new Vector2(Start.pane.getWidth() / 2, Start.pane.getHeight() / 2);
		for (int x = - Start.pane.getWidth() / grid_size / 2; x<Start.pane.getWidth() / grid_size / 2; x++) {
			for (int y = - Start.pane.getHeight() / grid_size / 2; y<Start.pane.getHeight() / grid_size / 2; y++) {
				g.drawRect((int)temp.x + x * grid_size, (int)temp.y + y * grid_size, grid_size, grid_size);
			}
		}
	}
	public static void add_mouse_functions() {
		Start.frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				
		//if ()
				click = convert_mouse_pos(new Vector2(e.getX(), e.getY() - Start.frame.getInsets().top), LevelEditor.pos);
				
				if (Start.o_pane != null) {
					System.out.println(Start.o_pane);
					for (int x = 0; x<Start.o_pane.buttons.length; x++) {
						Start.o_pane.buttons[x].button_clicked(click);
					}
					return;
				}
				
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
		Start.frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
				if (!Start.typing) return;
				
				char c = e.getKeyChar();
				
				if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c) && "!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?".indexOf(c) == -1) return;
				
				Start.o_pane.input_text += c;
				
				Start.o_pane.buttons[0].text = Start.o_pane.input_text;
				
				System.out.println(Start.o_pane.buttons[0].text);
				
				System.out.println("TYPED: " + e.getKeyCode() + " " + e.getKeyChar());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 37) pos.x -= grid_size;
				if (e.getKeyCode() == 39) pos.x += grid_size;
				
				if (e.getKeyCode() == 38) pos.y += grid_size;
				if (e.getKeyCode() == 40) pos.y -= grid_size;
				
				//'p' to end
				if (e.getKeyCode() == 80) {
					
					if (in_room_editor) {
						DataManager.save(RoomEditor.room, RoomEditor.room_editing, grid_size, false, DataManager.DATA_FILE);
					}
					System.exit(0);
				}
				
				//s to save to backup
				if (e.getKeyCode() == 83) {
					DataManager.save(RoomEditor.room, RoomEditor.room_editing, grid_size, false, DataManager.BACKUP);

				}
				
				if (!Start.typing) return;
				
				System.out.println("PRESSED: " + e.getKeyCode());
				
				if (e.getKeyCode() == 8) {
					if (Start.o_pane.input_text.equals("")) return;
					Start.o_pane.input_text = Start.o_pane.input_text.substring(0, Start.o_pane.input_text.length() - 1);
					Start.o_pane.buttons[0].text = Start.o_pane.input_text;
					return;
				}
				
				if (e.getKeyCode() == 10) {
					Start.o_pane.run.run();
					
					Start.o_pane.done();
					
					Start.typing = false;
					return;
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
	public static Vector2 current_mouse_pos() {
		return new Vector2(MouseInfo.getPointerInfo().getLocation().getX() - Start.frame.getLocationOnScreen().getX(), MouseInfo.getPointerInfo().getLocation().getY() - Start.frame.getLocationOnScreen().getY() - Start.frame.getInsets().top);
	}
	
	public static Vector2 convert_mouse_pos(Vector2 in, Vector2 pos) {
		return new Vector2(pos.x + (in.x - Start.pane.getWidth() / 2) / Camera.pixel_size, pos.y + (Start.pane.getHeight() / 2 - in.y) / Camera.pixel_size);
		
		//return new Vector2(pos.x - (Start.pane.getWidth() / 2 - in.x) / Camera.pixel_size, pos.y - (in.y - Start.pane.getHeight() / 2) / Camera.pixel_size);
		//return new Vector2((int)(Game.cam.pos.x + in.x - Game.pane.getWidth()/2) / Camera.pixel_size, (int)(Game.pane.getHeight()/2 + Game.cam.pos.y - in.y) / Camera.pixel_size);
	}
	/*public static Vector2 convert_mouse_pos(Vector2 in) {
		return new Vector2((int)(pos.x + in.x - Start.pane.getWidth()/2) / Camera.pixel_size, (int)(Start.pane.getHeight()/2 + pos.y - in.y) / Camera.pixel_size);
	}*/
	
	public static double dist(Vector2 a, Vector2 b) {
		return Vector2.dist(a, b);
	}
}
