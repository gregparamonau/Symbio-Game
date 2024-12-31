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
import Player.Player;
import Rendering.Camera;
import Rendering.Text;
import UI.Settings;
import UI.UI;

public class Game {	
	public static Player player = new Player();
	public static Camera cam = new Camera(0, 0);
	public static Room current_room = new Room();
	public static double speed_mult = 0.8;
	public static int frame_rate = 60;
	
	public static boolean up, down, left, right;
	public static int level_num = 1;
	public static int count = 0;
		
	//debug bs
	public static boolean pause = false;
	public static boolean slow = false;
	public static boolean debug_mode = false;
	
	public static void game() {
		
		init_game();
		
		add_key_listener();

		int delta_time = 0;
		
		do {
			//pause logic			
			if (UI.in_pause_menu) {
				cam.draw_view(Start.gs[Start.count % 2], 0);
				
				UI.draw_pause(Start.gs[Start.count%2]);
				
				Start.g.drawImage(Start.op.filter(Start.imgs[Start.count % 2], null), 0, 0, Start.pane);
				
				UI.update_pause_menu();
				
				count++;
				continue;
			}
			
			
			//timing and frame rate
			long a = System.currentTimeMillis();
			
			//draws previously rendered image to screen
			Start.g.drawImage(Start.op.filter(Start.imgs[(count+1)%2], null), 0, 0, Start.pane);
			
			//updates plat_ents & player
			for (int x = 0; x<current_room.objects.length; x++) current_room.objects[x].update();
			
			long c = System.nanoTime();
			for (int x = 0; x<current_room.enemies.length; x++) current_room.enemies[x].update();
			long d = System.nanoTime();
			System.out.println("update_enemies: " + (double)(d - c) / 1000000);

			player.update();
			
			Room.update();
			
			//moves Camera
			cam.move();
			
			//draw next frame
			cam.draw_view(Start.gs[count%2], delta_time);
			
			count++;
			
			long b = System.currentTimeMillis();
			
			delta_time = (int)(b - a);
			
			System.out.println("dt: " + delta_time);
			if (slow) try {Thread.sleep(500);}catch(Exception e) {e.printStackTrace();}
			else try {Thread.sleep(Math.max(1000/frame_rate - (b -     a), 0)); }catch(Exception e) {e.printStackTrace();}
		}while(true);
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
		Start.frame.addKeyListener(new KeyListener() {

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
				
				//'z' to slash 
				if (e.getKeyCode() == Settings.slash_key) {
					if (!player.slash_held && !player.slashing && player.slash_count == 0) {
						player.start_slash();
					}
					player.slash_held = true;
					player.slashing = true;
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
				if (e.getKeyCode() == Settings.slash_key) {
					player.slash_held = false;
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