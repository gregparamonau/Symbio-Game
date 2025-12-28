package Player;

import java.awt.event.KeyEvent;

import UI.Settings;
import UI.UI;
import Main.Game;

public class PlayerControls {
	Player player;
	
	public boolean up, down, left, right;
	
	public PlayerControls(Player in) {
		this.player = in;
	}
	
	
	public void key_pressed(KeyEvent e) {
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
				if (player.col_left) player.movement.wall_jump(1);
				if (player.col_right) player.movement.wall_jump(-1);
			}
			else if (player.movement.jump_num > 0 && !player.jumped) {
				player.movement.jump(false);
			}
			else if (!player.jumped) player.movement.jump_buffer = 6;
			player.jumped = true;
			
		}
		
		//'x' to dash
		if (e.getKeyCode() == Settings.dash_key) {
			
			if (player.movement.dash_num > 0 && !player.dashed && player.movement.dash_cooldown == 0) {
				player.movement.dash();
				player.movement.dash_direction_buffer = 0;
				player.movement.gravity = PlayerMovement.gravity_normal;
				player.dashed = true;
			}
			else if (!player.dashed) player.movement.dash_buffer = 4;
		}
		
		//'z' to slash 
		if (e.getKeyCode() == Settings.slash_key) {
			if (player.combat.slash_wait_count == 0 && !player.slash_held && !player.slashing && player.combat.slash_count == 0) {
				player.combat.start_slash();
			}
			else player.combat.slash_buffer = 6;
		}
		//shift to grab
		if (e.getKeyCode() == Settings.grab_key) {
			//player.grab = true;
		}
		
		
		if (e.getKeyCode() == Settings.pause_key) {
			if (!UI.in_pause_menu) UI.start_pause_menu();
			else if (UI.in_pause_menu) UI.end_pause_menu();
			
		}
		if (e.getKeyCode() == Settings.slow_key) Game.slow = !Game.slow;//m to slow down game
		if (e.getKeyCode() == Settings.debug_key) Game.debug_mode = !Game.debug_mode;//v for debug mode
		
		//'p' to exit game instantly
		if (e.getKeyCode() == Settings.exit_key) System.exit(0);
		
		//'r' to respawn player
		if (e.getKeyCode() == Settings.respawn_key) player.combat.hazard_respawn(false);
	}
	
	
	public void key_released(KeyEvent e) {
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
			if (player.vel.y > 0) player.movement.gravity = PlayerMovement.gravity_fall;
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
}
