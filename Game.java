package Symbio;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import DataManager.DataManager;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class Game {
	static int screen_width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static int screen_height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60;
	
	public static JFrame frame = null;
	public static JPanel pane = null;
	static Graphics g = null;
	
	static BufferedImage[] imgs = {new BufferedImage(screen_width, screen_height, 1), new BufferedImage(screen_width, screen_height, 1)};
	static Graphics2D[] gs = {(Graphics2D) imgs[0].getGraphics(), (Graphics2D) imgs[1].getGraphics()};
	
	public static Player player = new Player();
	public static Camera cam = new Camera(player.pos.x, player.pos.y);
	public static Room[] rooms = new Room[0];
	public static Room current_room = new Room();
	public static double speed_mult = 0.8;
	public static int frame_rate = 60;
	
	public static boolean up, down, left, right;
	public static int player_xpos = 0, player_ypos = 0;//player respawn position
	public static int level_num = 1;
	public static int count = 0;//used in main game loop
	
	//debug bs
	public static boolean pause = false;
	public static boolean slow = false;
	public static boolean draw_hitboxes = false;
	public static boolean debug_mode = false;
	
	public static void main(String[] args) {
		
		DataManager.load_game(DataManager.DATA_FILE);
		current_room = rooms[0];
		cam.recalculate_bounds();
		//System.exit(0);
		
		frame = new JFrame();
		frame.setSize(screen_width, screen_height);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pane = new JPanel();
		pane.setSize(screen_width, screen_height - 50);
		frame.add(pane);
		
		try {Thread.sleep(2000);}catch(Exception e) {System.out.println("ERROR with wait function: 56");}
		g = pane.getGraphics();
		
		if (rooms.length != 1) {
			int room_num = Integer.parseInt(JOptionPane.showInputDialog("chose room (less than " + rooms.length + ")"));
			current_room = rooms[room_num];
			cam.recalculate_bounds();
		}

		
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
							if (player.on_hair_vertical) player.wall_jump(player.last_dir, true, false);
							else if (!player.on_hair_vertical) player.wall_jump(0, true, down);
						}
						else {
							if (player.col_left) player.wall_jump(1, true, false);
							if (player.col_right) player.wall_jump(-1, true, false);
						}
					}
					else if (player.jump_num > 0 && !player.jumped) {
						player.jump();
					}
					else if (!player.jumped) player.jump_buffer = 4;
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
				if (e.getKeyCode() == 86) {//v for debug mode
					draw_hitboxes = !draw_hitboxes;
					debug_mode = !debug_mode;
				}
				
				//'p' to exit game instantly
				if (e.getKeyCode() == 80) System.exit(0);
				
				//'r' to respawn player
				if (e.getKeyCode() == 82) player.set_position(player_xpos, player_ypos);
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
			while(pause) try {Thread.sleep(25);}catch(Exception e) {System.out.println("ERROR with wait function: 134");}
			long a = System.currentTimeMillis();
			g.drawImage(imgs[(count+1)%2], 0, 0, null);
			player.update();
			for (int x = 0; x<current_room.plat_ents.length; x++) current_room.plat_ents[x].update();
			cam.move();
			
			cam.draw_view(gs[count%2], delta_time);
			
			count++;
			
			if (count % frame_rate == 0) System.out.println("A SECOND PAASSED");
			long b = System.currentTimeMillis();
			delta_time = (int)(b-a);
			System.out.println(b-a);
			if (slow) try {Thread.sleep(500);}catch(Exception e) {}
			else try {Thread.sleep(Math.max(1000/frame_rate - (b-a), 0)); }catch(Exception e) {e.printStackTrace();}
		}while(true);
	}
}

//TODO: maybe add zoom fucntunality to camera
//TODO: create sound manager class?