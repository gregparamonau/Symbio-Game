package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import DataManager.DataManager;
import DataManager.Room;
import Logic.Vector2;
import Rendering.Camera;

public class Game {
	static int screen_width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static int screen_height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static JFrame frame = null;
	public static JPanel pane = null;
	public static Graphics2D g = null;
	
	static BufferedImage[] imgs = new BufferedImage[2];
	public static Graphics2D[] gs = new Graphics2D[2];
	
	public static Player player = new Player();
	public static Camera cam = new Camera(player.pos.x, player.pos.y);
	public static Room current_room = new Room();
	public static double speed_mult = 0.8;
	public static int frame_rate = 60;
	
	public static boolean up, down, left, right;
	public static Vector2 player_respawn = new Vector2(0, 0);
	//public static int player_xpos = 0, player_ypos = 0;//player respawn position
	public static int level_num = 1;
	public static int count = 0;//used in main game loop
		
	//debug bs
	public static boolean pause = false;
	public static boolean slow = false;
	public static boolean debug_mode = false;
	public static boolean debug_text = false;
	public static boolean debug_borders = false;
	
	public static void main(String[] args) {
		
		current_room = DataManager.load_room(0, Camera.tile_size);
		Room.bounds = DataManager.load_rects(Camera.tile_size, 1);
		current_room.start();
		
		player.pos = new Vector2(current_room.pos);
		player_respawn = new Vector2(current_room.pos);
		
		init();

		
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 38) {
					up = true;
				}
				if (e.getKeyCode() == 40) {
					down = true;
				}
				
				//left/right, WASD/arrows
				if (e.getKeyCode() == 37) {
					left = true;
					player.last_dir = -1;
				}
				if (e.getKeyCode() == 39) {
					right = true;
					player.last_dir = 1;
				}
				//'c' to jump
				if (e.getKeyCode() == 67) {
					if ((player.wall_slide || player.on_hair) && !player.jumped) {
						if (player.on_hair) {
							if (player.on_hair_vertical) player.wall_jump(0, true, false);
							else if (!player.on_hair_vertical) player.wall_jump(0, true, down);
						}
						else {
							if (player.col_left) player.wall_jump(1, true, false);
							if (player.col_right) player.wall_jump(-1, true, false);
						}
					}
					else if (player.jump_num > 0 && !player.jumped) {
						player.jump(false);
					}
					else if (!player.jumped) player.jump_buffer = 6;
					player.jumped = true;
				}
				
				//'x' to dash
				if (e.getKeyCode() == 88) {
					if (player.dash_num > 0 && !player.dashed && player.dash_cooldown == 0) {
						player.dash();
						player.dash_direction_buffer = 0;
						player.gravity = 1;
						player.dashed = true;
					}
					else if (!player.dashed) player.dash_buffer = 4;
				}
				//'z' to attack
				if (e.getKeyCode() == 90) {
					if (player.swipe_cooldown == 0 && !player.on_hair && !player.swiped) {
						player.swipe();
						player.swiped = true;
					}
				}
				//shift to grab
				if (e.getKeyCode() == 16) {
					player.grab = true;
				}
				
				
				if (e.getKeyCode() == 32) pause = !pause;//space to pause it 
				if (e.getKeyCode() == 77) slow = !slow;//m to slow down game
				if (e.getKeyCode() == 84) debug_text = !debug_text;//display or not display info
				if (e.getKeyCode() == 86) debug_mode = !debug_mode;//v for debug mode
				if (e.getKeyCode() == 66) debug_borders = !debug_borders;//b for hitboxes
				
				//'p' to exit game instantly
				if (e.getKeyCode() == 80) System.exit(0);
				
				//'r' to respawn player
				if (e.getKeyCode() == 82) player.set_position((int)current_room.pos.x, (int)current_room.pos.y);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//front/back, WASD/arrows
				if (e.getKeyCode() == 38) {
					up = false;
				}
				if (e.getKeyCode() == 40) {
					down = false;
				}
				
				//left/right, WASD/arrows
				if (e.getKeyCode() == 37) {
					left = false;
					player.update_last_dir();
				}
				if (e.getKeyCode() == 39) {
					right = false;
					player.update_last_dir();
				}
				
				if (e.getKeyCode() == 67) {
					player.jumped = false;
					if (player.vel.y > 0 && ! player.swipe_gravity) player.gravity = 3;
				}
				
				if (e.getKeyCode() == 88) {
					player.dashed = false;
				}
				if (e.getKeyCode() == 90) {
					player.swiped = false;
				}
				if (e.getKeyCode() == 16) {
					player.grab = false;
				}
			}
		});
		int delta_time = 0;
		do {
			//pause logic
			while(pause) try {Thread.sleep(25);}catch(Exception e) {e.printStackTrace();}
			
			//timing and frame rate
			long a = System.currentTimeMillis();
			
			//draws previously rendered image to screen
			g.drawImage(((Image)(imgs[(count+1)%2])).getScaledInstance(-1, pane.getHeight(), Image.SCALE_FAST), 0, 0, Game.pane);
			//g.drawImage(imgs[(count+1)%2], 0, 0, Game.pane);
			
			//updates plat_ents & player
			for (int x = 0; x<current_room.objects.length; x++) current_room.objects[x].update();
			player.update();
			
			Room.update();
			
			//moves Camera
			cam.move();
			
			//draw next frame
			cam.draw_view(gs[count%2], delta_time);
			
			count++;
			
			long b = System.currentTimeMillis();
			
			delta_time = (int)(b - a);
			
			System.out.println("dt: " + delta_time);
			//System.out.println("dt: " + (double)(d - c) / 1000000);
			if (slow) try {Thread.sleep(500);}catch(Exception e) {e.printStackTrace();}
			else try {Thread.sleep(Math.max(1000/frame_rate - (b -     a), 0)); }catch(Exception e) {e.printStackTrace();}
		}while(true);
	}
	
	public static void init() {

		//DataManager.load("game", 0, Camera.tile_size);
		
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
		//System.exit(0);
		
		try {Thread.sleep(2000);}catch(Exception e) {e.printStackTrace();}
		g = (Graphics2D) pane.getGraphics();
		
		imgs[0] = new BufferedImage(pane.getWidth() / Camera.pixel_size, pane.getHeight() / Camera.pixel_size, 1);
		imgs[1] = new BufferedImage(pane.getWidth() / Camera.pixel_size, pane.getHeight() / Camera.pixel_size, 1);
		
		gs[0] = (Graphics2D) imgs[0].getGraphics();
		gs[1] = (Graphics2D) imgs[1].getGraphics();
		
		Camera.pixel_size = screen_height / Camera.pixel_height;
		Camera.res_width = pane.getWidth() / Camera.pixel_size;
		Camera.res_height = pane.getHeight() / Camera.pixel_size;
		
		cam.recalculate_bounds();
	}
}

//TODO: maybe add zoom fucntunality to camera
//TODO: create sound manager class?