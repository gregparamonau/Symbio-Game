package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import Player.PlayerMovement;
import Player.PlayerRender;
import Rendering.Camera;
import Rendering.Text;
import UI.Settings;
import UI.UI;

public class Game {	
	public static Player player = new Player();
	public static Camera cam = new Camera(0, 0);
	public static Room current_room = new Room();
	public static double speed_mult = 1;
	public static int frame_rate = 60;
	
	public static int level_num = 1;
	
	//hit stop so satisfyign
	public static int hit_stop;
		
	//debug bs
	public static boolean pause = false;
	public static boolean slow = false;
	public static boolean debug_mode = false;
	
	public static void game() {
		//init_vis_game();
		init_game();
		
		add_key_listener();

		double delta_time = 0;
		
		do {
			long start = System.nanoTime();
			//pause logic			
			if (UI.in_pause_menu) {
				
				long a = System.currentTimeMillis();
				cam.draw_view(Start.gs[Start.count % 2], 0);
				
				UI.draw_pause(Start.gs[Start.count%2]);
				
				UI.hovering();
				
				Start.pane.repaint();
				//Start.g.drawImage(Start.op.filter(Start.imgs[Start.count % 2], null), 0, 0, Start.pane);
				
				UI.update_pause_menu();
				
				Start.count++;
				
				long b = System.currentTimeMillis();
				
				try {Thread.sleep(Math.max(1000/frame_rate - (b -     a), 0)); }catch(Exception e) {e.printStackTrace();}

				continue;
			}
			
			
			//timing and frame rate
			long a = System.currentTimeMillis();
			
			if (hit_stop == 0) {
				//draws previously rendered image to screen
				Start.pane.repaint();

				//Start.g.drawImage(Start.op.filter(Start.imgs[(count+1)%2], null), 0, 0, Start.pane);
				
				//updates plat_ents & player
				for (int x = 0; x<current_room.objects.length; x++) current_room.objects[x].update();
				
				long c = System.nanoTime();
				for (int x = 0; x<current_room.enemies.length; x++) current_room.enemies[x].update();
				long d = System.nanoTime();
				//System.out.println("update_enemies: " + (double)(d - c) / 1000000);

				player.update();
				
				Room.update();
				
				//moves Camera
				cam.move();
			}
			
			//draw next frame
			cam.draw_view(Start.gs[Start.count%2], (int)delta_time);
			
			if (hit_stop > 0) hit_stop--;
			
			Start.count++;
			
			long b = System.currentTimeMillis();
			long end = System.nanoTime();
			
			delta_time = (double)(end - start) / 1000000;
			
			System.out.println("dt: " + delta_time);
			if (slow) try {Thread.sleep(500);}catch(Exception e) {e.printStackTrace();}
			else try {Thread.sleep(Math.max(1000/frame_rate - (b -     a), 0)); }catch(Exception e) {e.printStackTrace();}
		}while(true);
	}
	
	public static void init_game() {
		//start room
		current_room = DataManager.load_room(0, Camera.tile_size, false);
		Room.bounds = DataManager.load_rects(Camera.tile_size, 1);
		current_room.start();
		
		//start player
		player.pos = new Vector2(current_room.pos);
		player.combat.respawn_point = new Vector2(player.pos);
		
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
				player.input.key_pressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				player.input.key_released(e);
			}
		});
	}
}

//TODO: maybe add zoom fucntunality to camera
//TODO: create sound manager class?