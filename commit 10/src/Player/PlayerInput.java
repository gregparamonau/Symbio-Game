package Player;

import java.awt.event.KeyEvent;

import Logic.Vector2;
import UI.Settings;
import UI.UI;
import Main.Game;

public class PlayerInput {
	Player player;
	
	public boolean up, down, left, right;
	public boolean jump, dash, slash;
	
	//counters for how long each key has been pressed
	public int count_left = 0, count_right = 0;
	
	public int last_dir = 1;
	public Vector2 face_dir = new Vector2(Vector2.zero);
	
	//consts
	public static final int max_buffer = 4, max_direction_buffer = 2;
	public static final int max_key_buffer = 2;
	
	//list of counters --> input buffers
	public int jump_buffer = 0, dash_buffer = 0, slash_buffer = 0;
	public int dash_direction_buffer = 0, wall_jump_direction_buffer = 0;
	
	public PlayerInput(Player in) {
		this.player = in;
	}
	
	
	public void key_pressed(KeyEvent e) {
		
		int input = e.getKeyCode();
		//DO NOT remove this line, in fullscreen its impossible to get out without it
		if (input == Settings.exit_key) System.exit(0);
		
		if (input == Settings.up_key) up = true;
		if (input == Settings.down_key) down = true;
		if (input == Settings.left_key) left = true;
		if (input == Settings.right_key) right = true;
		
		if (input == Settings.jump_key) jump = true;
		if (input == Settings.dash_key) dash = true;
		if (input == Settings.slash_key) slash = true;
		
		//debug
		if (input == Settings.debug_key) Game.debug_mode = !Game.debug_mode;
		if (input == Settings.pause_key) {
			if (!UI.in_pause_menu) UI.start_pause_menu();
			else if (UI.in_pause_menu) UI.end_pause_menu();
		}
		if (input == Settings.slow_key) Game.slow = !Game.slow;
	}
	
	
	public void key_released(KeyEvent e) {
		int input = e.getKeyCode();
		
		if (input == Settings.up_key) up = false;
		if (input == Settings.down_key) down = false;
		if (input == Settings.left_key) left = false;
		if (input == Settings.right_key) right = false;
		
		if (input == Settings.jump_key) jump = false;
		if (input == Settings.dash_key) dash = false;
		if (input == Settings.slash_key) slash = false;
	}
	
	public void update() {
		this.face_dir.set(this.find_face_dir());
		this.update_last_dir();
		
		this.update_counters();
		
		if (!this.jump) player.movement.jumped = false;
	}
	
	public void update_counters() {
		//count the buffers
		if (this.jump_buffer > 0) this.jump_buffer--;
		if (this.dash_buffer > 0) this.dash_buffer--;
		if (this.slash_buffer > 0) this.slash_buffer--;
		
		System.out.println(this.count_left + " " + this.count_right);
		
		//key counters
		if (this.left) this.count_left ++; else this.count_left = 0;
		if (this.right) this.count_right ++; else this.count_right = 0;
		
		
		//initialize the buffers
		if (this.jump && !this.player.movement.jumped) this.jump_buffer = max_buffer;
		if (this.dash && !this.player.movement.dashed) this.dash_buffer = max_buffer;
		//repeat same fix here
		if (this.slash) this.slash_buffer = max_buffer;
	}
	
	public Vector2 find_face_dir() {
		if (this.up) {
			if (this.left) return Vector2.up_left;
			else if (this.right) return Vector2.up_right;
			else return Vector2.up;
		}
		else if (this.down) {
			if (this.left) return Vector2.down_left;
			else if (this.right) return Vector2.down_right;
			else return Vector2.down;
		}
		else return new Vector2(this.last_dir, 0);
	}
	public void update_last_dir() {
		if (this.left) this.last_dir = -1;
		else if (this.right) this.last_dir = 1;
	}
}
