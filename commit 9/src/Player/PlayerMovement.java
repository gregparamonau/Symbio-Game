package Player;

import DataManager.Room;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;

public class PlayerMovement {
	//Player player;
	Player player;
	//player position, velocity, and momentum are shared between PlayerMovement and Player
	
	public int momentum_keep_buffer, vel_keep_buffer;
	public double acceleration = 1.63, gravity = 1;
	
	//gravity
	public static double gravity_normal = 1, gravity_fall = 3, gravity_float = 0.75;
	public static int fall_speed_slow = 7, fall_speed_normal = 25;
	
	//jump
	public static int jump_force = 25;
	public int jump_keep = 1, jump_num = jump_keep;
	public int max_fall_speed = 25;
	public static double speed = 12.5, vel_mult_const = 1.25, bunny_hop_mult = 1.2, dash_end_const = 1.5;
	public int jump_buffer, coyote_buffer, frames_grounded;
	
	//wall jump
	static Vector2 wall_jump = new Vector2(25, 30);
	static double wall_slide_speed = 5;
	
	//dash
	public int dash_keep = 1, dash_num = dash_keep; 
	public int dash_buffer, dash_direction_buffer, dash_cooldown;
	public double dash_dist;
	public static double dash_length = 125;
	
	public PlayerMovement(Player player) {
		//TODO: make sure to NEVER set
		//player.pos directly, since this
		//tampers with player_movement pos
		//this.player = player;
		this.player = player;
		
		//nothing else...
	}
	
	void update() {
		this.max_fall_speed = fall_speed_normal;
		
		if (this.player.col_down) {
			this.jump_num = this.jump_keep;
			this.coyote_buffer = 4;
			if (this.dash_cooldown == 0) this.dash_num = this.dash_keep;
			this.gravity = gravity_normal;
			this.player.vel.y = 0;
			this.frames_grounded = Math.min(this.frames_grounded + 1, 10);
			if (!Room.respawn_point_set) {
				this.player.combat.respawn_point = new Vector2(this.player.pos);
				Room.respawn_point_set = true;
			}
		}
		else this.frames_grounded = 0;
		
		if (this.player.wall_slide) {
			this.coyote_buffer = 4;
		}
		if (!this.player.col_down && this.jump_num == this.jump_keep && (this.coyote_buffer == 0 || this.player.dashing)) this.jump_num--;
		if (this.player.wall_slide && this.dash_cooldown == 0 && this.player.wall_jump_unlock) this.dash_num = this.dash_keep;
		
		if (this.player.momentum.l() != 0 && ! this.player.dashing && this.momentum_keep_buffer == 0) {
			this.player.momentum = Vector2.scale_to_length(this.player.momentum, this.player.momentum.l() - Game.speed_mult);
			if ((this.player.momentum.l() < 2 || (this.player.col && !this.player.jumped)) && (this.frames_grounded > 2 || !this.player.jumped || this.player.col_left || this.player.col_right)) {
				this.player.momentum = new Vector2(0, 0);
			}
		}
		
		if (!this.player.dashing) {
			if (this.player.vel.y <= 0) {
				this.gravity = 3;
			}
			//if (this.col_up && this.vel.y > 0) this.gravity = 3;
			if (Math.abs(this.player.vel.y) < 3 && !this.player.slashing) this.gravity = gravity_float;
			
			//'y'
			if (this.player.vel.y > -max_fall_speed) this.player.vel.y -= acceleration * this.gravity * Game.speed_mult;
			this.player.vel.y = Utility.clamp_above(this.player.vel.y, -max_fall_speed * (this.vel_keep_buffer > 0 ? 0.5 : 1));
			
			//'x'
			if ((this.player.control.left || this.player.control.right) && !this.player.slashing) {
				if (Utility.sign(this.player.vel.x) == this.player.last_dir) this.player.vel.x = Utility.move_toward(this.player.vel.x, Utility.sign(this.player.face_dir.x) * speed, Game.speed_mult);
				else this.player.vel.x = Utility.move_toward(this.player.vel.x, Utility.sign(this.player.face_dir.x) * speed, 5 * Game.speed_mult);
			}
			else if (!this.player.control.left && !this.player.control.right) this.player.vel.x = Utility.move_toward(this.player.vel.x, 0, 5 * Game.speed_mult);
		}
		
		
		//~4 frame buffer to let player be more lenient when jumping & dashing (jump even if in midair)
		if (this.dash_buffer > 0 && (this.player.col_down || (this.player.wall_slide && this.player.wall_jump_unlock))) {
			this.player.movement.dash();
			this.dash_buffer = 0;
		}
		if (this.jump_buffer > 0 && (this.player.col_down || this.player.col_left || this.player.col_right || this.player.collider.collide(new Vector2(0, 0), false))) {
			this.player.movement.jump(this.frames_grounded < 3);
			this.jump_buffer = 0;
			this.momentum_keep_buffer = 10;
		}
		
		
		
		
		
		if (this.jump_buffer > 0) this.jump_buffer--;
		if (this.coyote_buffer > 0) this.coyote_buffer--;
		if (this.momentum_keep_buffer > 0) this.momentum_keep_buffer--;
		if (this.vel_keep_buffer > 0) this.vel_keep_buffer--;
		if (this.dash_buffer > 0) this.dash_buffer--;
		if (this.dash_cooldown > 0) this.dash_cooldown--;
		
	}
	
	
	public void jump(boolean hop) {
		this.player.vel.y = jump_force;
		this.jump_num--;
		System.out.println("JUMP");
		this.gravity = gravity_normal;
		this.player.jumped = true;
		if (this.player.dashing) this.end_dash();
		Game.current_room.objects[this.player.object_intersect_id].give_jump_momentum();
		if (hop) this.player.momentum.x *= bunny_hop_mult;
	}
	public void dash() {
		if (!this.player.dash_unlock) return;
		//~4 frame grace period to input dash direction
		if (!this.player.dashing || this.dash_direction_buffer < 4) {
			if (!this.player.dashing) this.dash_cooldown = 10;
			this.player.wall_jumped = false;
			this.player.vel.y = 0;
			this.player.dash_dir = this.player.face_dir;
			if (!this.player.dashing) this.dash_num--;
			this.player.dashing = true;
			Game.cam.screen_shake(this.player.dash_dir);
		}
		else {
			double xplus = (this.player.dash_dir.x * speed * 3.5f) * Game.speed_mult + this.player.momentum.x;
			double yplus = (this.player.dash_dir.y * speed * 3.5f) * Game.speed_mult + this.player.momentum.y;
			
			//MOVE AND COLLIDE METHODS STAY PROPRIETARY OF TEH PLAYER
			this.player.collider.move(new Vector2(xplus, yplus));
			this.dash_dist += speed * 1.5 * Game.speed_mult;
			
			//check if intersect with any plat_ents
			this.player.collider.object_action();
			
			if (this.dash_dist >= dash_length) this.end_dash();
		}
		
	}
	
	public void wall_jump(int in) {
		if (!this.player.wall_jump_unlock) return;
		if (!this.player.wall_jumped) {
			if (this.player.dashing) this.end_dash();
			if (in == 1) {
				this.player.vel = new Vector2(wall_jump);
				this.player.momentum.x *= -0.75;
				this.player.momentum.y = Math.abs(this.player.momentum.y) + 0.25 * Math.abs(this.player.momentum.x);
			}
			if (in == -1) {
				this.player.vel = Vector2.invert_x(wall_jump);
				this.player.momentum.x *= -0.75;
				this.player.momentum.y = Math.abs(this.player.momentum.y) + 0.25 * Math.abs(this.player.momentum.x);

			}
			this.gravity = gravity_normal;
			this.player.wall_jumped = true;
		}
		else {
			Vector2 move = new Vector2((this.player.vel.x + this.player.last_dir * speed + this.player.momentum.x) * Game.speed_mult, (this.player.vel.y + this.player.momentum.y) * Game.speed_mult);
			this.player.collider.move(move);
			//check if intersect with any plat_ents
			player.collider.object_action();
			if (this.player.col) this.player.wall_jumped = false;
		}
	}
	
	public void end_dash() {
		
		this.player.dashing = false;
		this.dash_dist = 0;
		
		this.player.momentum = Vector2.mult(this.player.dash_dir, speed * dash_end_const * (double)dash_length/100);
		
		this.player.dash_dir = new Vector2();
	}
		
	
	
	
}
