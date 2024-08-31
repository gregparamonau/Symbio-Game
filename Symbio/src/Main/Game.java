package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import DataManager.DataManager;
import DataManager.Room;
import Logic.Utility;
import Logic.Vector2;
import Rendering.Camera;
import Rendering.Text;
import UI.Settings;
import UI.UI;

public class Game {
	static int screen_width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static int screen_height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static JFrame frame = null;
	public static JPanel pane = null;
	public static Graphics2D g = null;
	
	public static BufferedImage[] imgs = new BufferedImage[2];
	public static Graphics2D[] gs = new Graphics2D[2];
	static AffineTransform transform = AffineTransform.getScaleInstance(Camera.pixel_size, Camera.pixel_size);
	public static AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	
	public static Player player = new Player();
	public static Camera cam = new Camera(0, 0);
	public static Room current_room = new Room();
	public static double speed_mult = 0.75;
	public static int frame_rate = 60;
	
	public static boolean up, down, left, right;
	public static int level_num = 1;
	public static int count = 0;
		
	//debug bs
	public static boolean pause = false;
	public static boolean slow = false;
	public static boolean debug_mode = false;
	
	public static void main(String[] args) {
		
		Text.start_font(Text.font_name);
		
		init_base();
		
		UI.main_menu();
		
		init_game();
		
		add_key_listener();

		int delta_time = 0;
		do {
			//pause logic			
			if (UI.in_pause_menu) {
				cam.draw_view(Game.gs[Game.count % 2], 0);
				
				UI.draw_pause(Game.gs[Game.count%2]);
				
				g.drawImage(Game.op.filter(Game.imgs[Game.count % 2], null), 0, 0, Game.pane);
				
				UI.update_pause_menu();
				
				count++;
				continue;
			}
			
			//timing and frame rate
			long a = System.currentTimeMillis();
			
			//draws previously rendered image to screen
			g.drawImage(op.filter(imgs[(count+1)%2], null), 0, 0, Game.pane);
			
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
			if (slow) try {Thread.sleep(500);}catch(Exception e) {e.printStackTrace();}
			else try {Thread.sleep(Math.max(1000/frame_rate - (b -     a), 0)); }catch(Exception e) {e.printStackTrace();}
		}while(true);
	}
	
	public static void init_base() {
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
		
		Camera.pixel_size = screen_height / Camera.pixel_height;
		Camera.res_width = pane.getWidth() / Camera.pixel_size;
		Camera.res_height = pane.getHeight() / Camera.pixel_size;
	}
	public static void init_game() {
		//start room
		current_room = DataManager.load_room(0, Camera.tile_size);
		Room.bounds = DataManager.load_rects(Camera.tile_size, 1);
		current_room.start();
		
		//start player
		player.pos = new Vector2(current_room.pos);
		player.respawn_point = new Vector2(player.pos);
		
		//start camera
		cam.set_position(player.pos);
		Camera.HUD_offset = new Vector2(-Camera.res_width / 2 + Camera.tile_size, Camera.res_height / 2 - Camera.tile_size);
		cam.recalculate_bounds();
	}
	public static void add_key_listener() {
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == Settings.up_key) {
					up = true;
				}
				if (e.getKeyCode() == Settings.down_key) {
					down = true;
				}
				
				//left/right, WASD/arrows
				if (e.getKeyCode() == Settings.left_key) {
					left = true;
					player.last_dir = -1;
				}
				if (e.getKeyCode() == Settings.right_key) {
					right = true;
					player.last_dir = 1;
				}
				//'c' to jump
				if (e.getKeyCode() == Settings.jump_key) {
					if (player.wall_slide && !player.jumped) {
						if (player.col_left) player.wall_jump(1);
						if (player.col_right) player.wall_jump(-1);
					}
					else if (player.jump_num > 0 && !player.jumped) {
						player.jump(false);
					}
					else if (!player.jumped) player.jump_buffer = 6;
					player.jumped = true;
				}
				
				//'x' to dash
				if (e.getKeyCode() == Settings.dash_key) {
					if (player.dash_num > 0 && !player.dashed && player.dash_cooldown == 0) {
						player.dash();
						player.dash_direction_buffer = 0;
						player.gravity = Player.gravity_normal;
						player.dashed = true;
					}
					else if (!player.dashed) player.dash_buffer = 4;
				}
				//shift to grab
				if (e.getKeyCode() == Settings.grab_key) {
					//player.grab = true;
				}
				
				
				if (e.getKeyCode() == Settings.pause_key) {
					if (!UI.in_pause_menu) UI.start_pause_menu();
					else if (UI.in_pause_menu) UI.end_pause_menu();
					
				}
				if (e.getKeyCode() == Settings.slow_key) slow = !slow;//m to slow down game
				if (e.getKeyCode() == Settings.debug_key) debug_mode = !debug_mode;//v for debug mode
				
				//'p' to exit game instantly
				if (e.getKeyCode() == Settings.exit_key) System.exit(0);
				
				//'r' to respawn player
				if (e.getKeyCode() == Settings.respawn_key) player.hazard_respawn(false);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//front/back, WASD/arrows
				if (e.getKeyCode() == Settings.up_key) {
					up = false;
				}
				if (e.getKeyCode() == Settings.down_key) {
					down = false;
				}
				
				//left/right, WASD/arrows
				if (e.getKeyCode() == Settings.left_key) {
					left = false;
					player.update_last_dir();
				}
				if (e.getKeyCode() == Settings.right_key) {
					right = false;
					player.update_last_dir();
				}
				
				if (e.getKeyCode() == Settings.jump_key) {
					player.jumped = false;
					if (player.vel.y > 0) player.gravity = Player.gravity_fall;
				}
				
				if (e.getKeyCode() == Settings.dash_key) {
					player.dashed = false;
				}
				if (e.getKeyCode() == Settings.grab_key) {
					//player.grab = false;
				}
			}
		});
	}
}

//TODO: maybe add zoom fucntunality to camera
//TODO: create sound manager class?