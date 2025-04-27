package Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import DataManager.Room;
import GameObject.GameObject;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;
import Rendering.Camera;

public class Player extends Rectangle{
	public static final int width_save = 4, height_save = 12;
	
	//unlocked abilities
	public boolean dash_unlock = true, wall_jump_unlock = true, slash_unlock = true, grab_unlock = true;
	
	//physics
	public boolean col_left, col_right, col_up, col_down, col;
	public Vector2 vel = new Vector2(0, 0);
	public double acceleration = 1.63, gravity = 1;
	public Vector2 momentum = new Vector2(0, 0);
	public int momentum_keep_buffer, vel_keep_buffer;
	public Vector2 face_dir;
	public static final int squash_death_threshold = 4;
	
	//gravity
	public static double gravity_normal = 1, gravity_fall = 3, gravity_float = 0.75;
	public static int fall_speed_slow = 7, fall_speed_normal = 25;
	
	//jump
	public int jump_keep = 1, jump_num = jump_keep;
	public static int jump_force = 25;
	public int max_fall_speed = 25;
	public static double speed = 12.5, vel_mult_const = 1.25, bunny_hop_mult = 1.2, dash_end_const = 1.5;
	public int jump_buffer, coyote_buffer, frames_grounded;
	public boolean jumped;
	
	//wall jump
	static Vector2 wall_jump = new Vector2(25, 30);
	public boolean wall_jumped;
	public boolean wall_slide;
	static double wall_slide_speed = 5;
	
	//dash
	public int dash_keep = 1, dash_num = dash_keep; 
	public int last_dir = 1;
	public int dash_buffer, dash_direction_buffer, dash_cooldown;
	public boolean dashing, dashed;
	Vector2 dash_dir = Vector2.zero;
	public double dash_dist, dash_length = 125;
	
	//extra bs
	public int object_intersect_id = -1;
	
	//Attack code
	public int slash_strength = 1;
	public Vector2 slash_dir;
	
	public Vector2 slash_bounds = new Vector2(35, 24);
	public Vector2 offset_slash = new Vector2(10, 12);
	
	public int slash_length = 10, slash_pause = 5;
	public int slash_count = 0, slash_wait_count = 0, slash_buffer;
	public boolean slashing = false, slash_held = false;
	
	//health system
	public Vector2 respawn_point = new Vector2(0, 0);
	public int health_full = 3;
	public int health = health_full;
	public int knockback = 40;
	
	public int invincibility_frames = 0;
	public int invincibility_frames_max = 60;
	
	public Vector2 last_pos = new Vector2(0, 0);
	
	//CONSTRUCTORS
	public Player() {
		
		this.pos = new Vector2(0, 0);
		this.width = width_save;
		this.height = height_save;
	}
	//HOOK of GAME: Rooms change shape/size/stretch/etc
	//Muscles: stretch/contract how muscles do
	//lungs: expand away from center of room
	//heart: pulse?
	//intestine: poo moves through the intestines (water too?)
	//stomach: compresses & acid levels rise
	//MOVEMENT 
	public void update() {
		if (this.slash_count > 0) {
			this.slashing = true;
			this.slash();
		}
		else this.slashing = false;
		//System.out.println("OII: " + this.object_intersect_id);
		//System.out.println(this.pos);
		
		this.move_player();
		this.face_dir = find_face_dir();
		
		//determine fall speed for next frame
		this.max_fall_speed = fall_speed_normal;
		//if (this.slashing) this.max_fall_speed = fall_speed_slow;
				
		//collision handling & finding the platform that we intersect with
		this.object_intersect_id = -1;
		
		this.col_left = this.collide(new Vector2(-0.2, 0), true);
		this.col_right = this.collide(new Vector2(0.2, 0), true);
		
		this.col_up = this.collide(new Vector2(0, 0.2), true);
		this.col_down = this.collide(new Vector2(0, -0.2), true);
				
		this.wall_slide = (this.col_left || this.col_right) && ((!this.col_down && !this.col_up));
		this.col = this.col_left || this.col_right || this.col_up || this.col_down;
		
				
		if (this.col_down) {
			this.jump_num = this.jump_keep;
			this.coyote_buffer = 4;
			if (this.dash_cooldown == 0) this.dash_num = this.dash_keep;
			this.gravity = gravity_normal;
			this.vel.y = 0;
			this.frames_grounded = Math.min(this.frames_grounded + 1, 10);
			if (!Room.respawn_point_set) {
				this.respawn_point = new Vector2(this.pos);
				Room.respawn_point_set = true;
			}
		}
		else this.frames_grounded = 0;
		
		/*if (this.col_up) {
			this.gravity = 0.75;
			this.vel.y = 0;
		}*/
		
		if (this.wall_slide) {
			this.coyote_buffer = 4;
		}
		if (!this.col_down && this.jump_num == this.jump_keep && (this.coyote_buffer == 0 || this.dashing)) this.jump_num--;
		if (this.wall_slide && this.dash_cooldown == 0 && this.wall_jump_unlock) this.dash_num = this.dash_keep;
		
		//gravity & friction
		//momentum subtracting
		if (this.momentum.length() != 0 && ! this.dashing && this.momentum_keep_buffer == 0) {
			this.momentum = Vector2.scale_to_length(this.momentum, this.momentum.length() - Game.speed_mult);
			if ((this.momentum.length() < 2 || (this.col && !this.jumped)) && (this.frames_grounded > 2 || !this.jumped || this.col_left || this.col_right)) {
				this.momentum = new Vector2(0, 0);
			}
		}
		
		//velocity acceleration/deceleration
		//'y'
		if (!this.dashing) {
			if (this.vel.y <= 0) {
				this.gravity = 3;
			}
			//if (this.col_up && this.vel.y > 0) this.gravity = 3;
			if (Math.abs(this.vel.y) < 3 && !this.slashing) this.gravity = gravity_float;
			
			//'y'
			if (this.vel.y > -max_fall_speed) this.vel.y -= acceleration * this.gravity * Game.speed_mult;
			this.vel.y = Utility.clamp_above(this.vel.y, -max_fall_speed * (this.vel_keep_buffer > 0 ? 0.5 : 1));
			
			//'x'
			if ((Game.left || Game.right) && !this.slashing) {
				if (Utility.sign(this.vel.x) == this.last_dir) this.vel.x = Utility.move_toward(this.vel.x, Utility.sign(this.face_dir.x) * speed, Game.speed_mult);
				else this.vel.x = Utility.move_toward(this.vel.x, Utility.sign(this.face_dir.x) * speed, 5 * Game.speed_mult);
			}
			else if (!Game.left && !Game.right) this.vel.x = Utility.move_toward(this.vel.x, 0, 5 * Game.speed_mult);
		}
		
		//~4 frame buffer to let player be more lenient when jumping & dashing (jump even if in midair)
		if (this.dash_buffer > 0 && (this.col_down || (this.wall_slide && this.wall_jump_unlock))) {
			this.dash();
			this.dash_buffer = 0;
		}
		if (this.jump_buffer > 0 && (this.col_down || this.col_left || this.col_right || this.collide(new Vector2(0, 0), false))) {
			this.jump(this.frames_grounded < 3);
			this.jump_buffer = 0;
			this.momentum_keep_buffer = 10;
		}
		if (this.slash_buffer > 0 && !this.slashing && this.slash_wait_count == 0) {
			this.start_slash();
		}
		
		//counters to ensure smoother and more responsive gameplay
		if (this.jump_buffer > 0) this.jump_buffer--;
		if (this.coyote_buffer > 0) this.coyote_buffer--;
		if (this.momentum_keep_buffer > 0) this.momentum_keep_buffer--;
		if (this.vel_keep_buffer > 0) this.vel_keep_buffer--;
		if (this.dash_buffer > 0) this.dash_buffer--;
		if (this.dash_cooldown > 0) this.dash_cooldown--;
		if (this.slash_wait_count > 0) this.slash_wait_count--;
		if (this.slash_buffer > 0) this.slash_buffer--;
		if (this.invincibility_frames > 0) this.invincibility_frames--;
		
		this.last_pos = new Vector2(this.pos);
	}
	public void jump(boolean hop) {
		this.vel.y = jump_force;
		this.jump_num--;
		System.out.println("JUMP");
		this.gravity = gravity_normal;
		this.jumped = true;
		if (this.dashing) this.end_dash();
		Game.current_room.objects[this.object_intersect_id].give_jump_momentum();
		if (hop) this.momentum.x *= bunny_hop_mult;
	}
	public void wall_jump(int in) {
		if (!this.wall_jump_unlock) return;
		if (!this.wall_jumped) {
			if (this.dashing) this.end_dash();
			if (in == 1) {
				this.vel = new Vector2(wall_jump);
				this.momentum.x *= -0.75;
				this.momentum.y = Math.abs(this.momentum.y) + 0.25 * Math.abs(this.momentum.x);
			}
			if (in == -1) {
				this.vel = Vector2.invert_x(wall_jump);
				this.momentum.x *= -0.75;
				this.momentum.y = Math.abs(this.momentum.y) + 0.25 * Math.abs(this.momentum.x);

			}
			this.gravity = gravity_normal;
			this.wall_jumped = true;
		}
		else {
			Vector2 move = new Vector2((this.vel.x + this.last_dir * speed + this.momentum.x) * Game.speed_mult, (this.vel.y + this.momentum.y) * Game.speed_mult);
			this.move(move);
			//check if intersect with any plat_ents
			object_action();
			if (this.col) this.wall_jumped = false;
		}
	}
	public void dash() {
		if (!this.dash_unlock) return;
		//~4 frame grace period to input dash direction
		if (!this.dashing || this.dash_direction_buffer < 4) {
			if (!this.dashing) this.dash_cooldown = 10;
			this.wall_jumped = false;
			this.vel.y = 0;
			this.dash_dir = this.face_dir;
			if (!this.dashing) this.dash_num--;
			this.dashing = true;
			Game.cam.screen_shake(this.dash_dir);
		}
		else {
			double xplus = (this.dash_dir.x * speed * 3.5f) * Game.speed_mult + this.momentum.x;
			double yplus = (this.dash_dir.y * speed * 3.5f) * Game.speed_mult + this.momentum.y;
			this.move(new Vector2(xplus, yplus));
			this.dash_dist += speed * 1.5 * Game.speed_mult;
			
			//check if intersect with any plat_ents
			object_action();
			
			if (this.dash_dist >= dash_length) this.end_dash();
		}
	}
	public void end_dash() {
		
		this.dashing = false;
		this.dash_dist = 0;
		
		this.momentum = Vector2.mult(this.dash_dir, speed * dash_end_const * (double)this.dash_length/100);
		
		this.dash_dir = new Vector2();
	}
	public void move_player() {
		if (this.dashing) {
			this.dash();
			this.dash_direction_buffer++;
			return;
		}
		if (this.wall_jumped) {
			this.wall_jump(this.last_dir);
			return;
		}
		Vector2 move = Vector2.mult(new Vector2(this.momentum.x + this.vel.x, (this.vel.y * vel_mult_const) + this.momentum.y), Game.speed_mult);
		if (this.wall_slide && !this.jumped && this.wall_jump_unlock) {
			move.y = - (float)wall_slide_speed * Game.speed_mult;
			this.dash_num = this.dash_keep;
		}
		//if (this.object_momentum_timer > 0) move = new Vector2(this.momentum.x + this.vel.x, this.momentum.y);
		this.move(move);
		
		//check if intersect with any plat_ents
		object_action();
		enemy_action();
	}
	
	public void move(Vector2 in) {
		long a = System.nanoTime();
		
		in = Vector2.mult(in, 0.2);
		
		if (this.inside_object()) {
			this.hazard_respawn(true);
			return;
		}
		else this.displace(0);
		//to allign everything with edges, to make sure player able to move everywhere
		//for 'y' axis
		if (in.y != 0) {
			for (int y = 0; y<Math.abs(in.y) * 5; y++) {
				this.pos.y += Utility.sign(in.y) * 0.2;
				if (this.collide(new Vector2(0, 0), false)) {
					if (!this.collide(new Vector2(0.2, 0), false) && Utility.sign(in.x) != 1) {
						this.pos.x += 0.2;
						continue;
					}
					if (!this.collide(new Vector2(-0.2, 0), false) && Utility.sign(in.x) != -1) {
						this.pos.x += -0.2;
						continue;
					}
					this.pos.y -= Utility.sign(in.y) * 0.2;
					break;
				}
			}
		}
		//for 'x' axis
		if (in.x != 0) {
			for (int x = 0; x<Math.abs(in.x) * 5; x++) {
				this.pos.x += Utility.sign(in.x) * 0.2;
				if (this.collide(new Vector2(0, 0), false)) {
					if (!this.collide(new Vector2(0, 0.2), false)) {
						this.pos.y += 0.2;
						continue;
					}
					this.pos.x -= Utility.sign(in.x) * 0.2;
					break;
				}
				if (this.collide(new Vector2(0, -0.6), false) && !this.dashing) {
					for (int y = 0; y<3; y++) {
						if (!this.collide(new Vector2(0, -0.2), false)) {
							this.pos.y += -0.2;
						}
					}
				}
			}
		}
		
		long b = System.nanoTime();
		
		//System.out.println("MOVE_TIME: " + (double)(b-a) / 1_000_000);
	}
	
	public void displace(int direction) {
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			Game.current_room.objects[x].displace_entity(this, direction);
		}
	}
	
	//COLLISION
	public boolean collide(Vector2 move, boolean find_collide) {
		//System.out.println("START");
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			if (!Game.current_room.objects[x].solid) continue;
			//System.out.println("	COLLIDE_CHECK_393: " + Game.current_room.objects[x].toString());
			if (Game.current_room.objects[x].collide_with(new Rectangle(this.pos.x + move.x, this.pos.y + move.y, this.width, this.height), false)) {
				if (find_collide) this.object_intersect_id = x;
				return true;
			}
		}
		return false;
	}
	
	public boolean inside_object() {
		for (GameObject obj: Game.current_room.objects) {
			if (!obj.solid || !Rectangle.intersect(obj, this)) continue;
			Rectangle temp = Rectangle.intersect_area(obj, this);
			if (Math.min(temp.width, temp.height) > squash_death_threshold) return true;
		}
		return false;
	}
	public boolean object_action() {
		
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			if (Game.current_room.objects[x].collide_with(this, true)) {
				Game.current_room.objects[x].collision_action();
				return true;
			}
		}
		return false;
	}
	
	public void enemy_action() {
		for (int x = 0; x<Game.current_room.enemies.length;x ++) {
			Game.current_room.enemies[x].collision_action();
		}
	}
	
	//ATTACK CODE
	public void start_slash() {
		if (!this.slash_unlock) return;
		
		this.slash_count = this.slash_length;
		this.slash_dir = this.find_slash_dir();
		
		this.slash_held = true;
		this.slashing = true;
		
		if (this.dashing) {
			this.end_dash();
		}
		this.gravity = 3;
		
		this.slash();
	}
	public void slash() {
		if (this.slash_count > this.slash_length - 5) {
			this.slash_dir = this.find_slash_dir();
			this.slash_count--;
			return;
		}
		if (!this.slash_unlock) return;
		for (int x = 0; x<Game.current_room.enemies.length; x++) {
			if (Rectangle.intersect(new Rectangle(this.pos.x + this.slash_dir.x, this.pos.y + this.slash_dir.y, (this.slash_dir.x != 0 ? 1 : 0) * this.slash_bounds.x + (this.slash_dir.x == 0 ? 1: 0) * this.slash_bounds.y, (this.slash_dir.y != 0 ? 1.25 : 0) * this.slash_bounds.x + (this.slash_dir.y == 0 ? 1: 0) * this.slash_bounds.y), Game.current_room.enemies[x])) {
				//TODO: transfer this over to enemy script, so not all enemiesa re affected the same
				Game.current_room.enemies[x].momentum = Vector2.scale_to_length(this.slash_dir, this.knockback * 0.1);
				
				if (Game.current_room.enemies[x].damage(this.slash_strength)) {
					this.vel = Vector2.scale_to_length(this.slash_dir, -this.knockback * 0.5 * (this.slash_dir.x == 0 && this.slash_dir.y < 0 ? 2 : 0));
					
					this.momentum.x *= (this.slash_dir.x == 0 ? 1: 0);
					this.momentum.y *= (this.slash_dir.y == 0 ? 1: 0);
					
					
					this.momentum_keep_buffer = 10;
					this.vel_keep_buffer = 25;
					
					if (Vector2.dot(this.slash_dir, Vector2.down) == 1) this.dash_num = this.dash_keep;
				}
			}
		}
		this.slash_count--;
		
		if (this.slash_count == 0) {
			this.slash_wait_count = this.slash_pause;
		}
	}
	
	
	public Vector2 find_slash_dir() {
		if (this.face_dir.y != 0 && !(this.face_dir.y < 0 && this.col_down)) {
			return new Vector2(0, this.offset_slash.y * Utility.sign(this.face_dir.y));
		}
		return new Vector2(this.offset_slash.x * this.last_dir * (this.wall_slide ? -1 : 1), 0);
	}
	
	
	//LOGIC METHODS (BASICS)
	public Vector2 find_face_dir() {
		Vector2 out = Vector2.zero;
		if (Game.up) {
			if (Game.left) out = Vector2.up_left;
			else if (Game.right) out = Vector2.up_right;
			else out = Vector2.up;
		}
		else if (Game.down) {
			if (Game.left) out = Vector2.down_left;
			else if (Game.right) out = Vector2.down_right;
			else out = Vector2.down;
		}
		else out = new Vector2(this.last_dir, 0);
		return out;
	}
	public void set_position(double x, double y) {
		this.pos.x = x;
		this.pos.y = y;
	}
	public void update_last_dir() {
		if (Game.left) this.last_dir = -1;
		else if (Game.right) this.last_dir = 1;
	}
	public void damage(int damage, Rectangle source) {
		if (this.invincibility_frames > 0) return;
		this.health -= damage;
		
		this.vel = Vector2.scale_to_length(Vector2.sub(this.pos, source.pos), this.knockback);
		if (this.dashing) {
			this.end_dash();
			this.momentum = new Vector2(0, 0);
		}
		else this.momentum = Vector2.scale_to_length(Vector2.sub(this.pos, source.pos), this.momentum.length());
		
		this.invincibility_frames = this.invincibility_frames_max;
		
		if (this.health <= 0) this.death_respawn();
	}
	public void death_respawn() {
		//TODO: make this method
		this.health = this.health_full;
		this.set_position(this.respawn_point.x, this.respawn_point.y);
		this.vel = new Vector2(0, 0);
		this.momentum = new Vector2(0, 0);
	}
	public void hazard_respawn(boolean damage) {
		//TODO: finish this
		if (damage) this.health--;
		if (this.health == 0) {
			this.death_respawn();
			return;
		}
		
		this.set_position(this.respawn_point.x, this.respawn_point.y);
		this.vel = new Vector2(0, 0);
		this.momentum = new Vector2(0, 0);
	}
}
