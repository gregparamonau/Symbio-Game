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
	public boolean dash_side_unlock = true, dash_diagonal_unlock = true, wall_jump_unlock = true, swipe_unlock = true, dash_bounce_unlock = true;
	
	//physics
	public boolean col_left, col_right, col_up, col_down, col;
	public Vector2 vel = new Vector2(0, 0);
	public double acceleration = 1.63, gravity = 1;
	public Vector2 momentum = new Vector2(0, 0);
	public Vector2 face_dir;
	public static final int squash_death_threshold = 4;
	
	//gravity
	public static double gravity_normal = 1, gravity_fall = 3, gravity_float = 0.75;
	public static int fall_speed_slow = 15, fall_speed_normal = 25, fall_speed_fast = 35;
	
	//jump
	public int jump_keep = 1, jump_num = jump_keep;
	public static int jump_force = 25;
	public int max_fall_speed = 25;
	public static double speed = 12.5, vel_mult_const = 1.25;
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
	public double dash_dist, dash_length = 175;
	
	//extra bs
	public int object_intersect_id = -1;
	public int momentum_timer = 0;
	
	//health system
	public Vector2 respawn_point = new Vector2(0, 0);
	public int health_full = 3;
	public int health = health_full;
	
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
		//System.out.println("OII: " + this.object_intersect_id);
		//System.out.println(this.pos);
		
		this.move_player();
		this.face_dir = find_face_dir();
		
		//determine fall speed for next frame
		this.max_fall_speed = fall_speed_normal;
		if (Game.down) this.max_fall_speed = fall_speed_fast;
		else if (Game.up) this.max_fall_speed = fall_speed_slow;
				
		//collision handling & finding the platform that we intersect with
		this.object_intersect_id = -1;
		
		this.col_left = this.collide(new Vector2(-0.2, 0), true);
		this.col_right = this.collide(new Vector2(0.2, 0), true);
		
		this.col_up = this.collide(new Vector2(0, 0.2), true);
		this.col_down = this.collide(new Vector2(0, -0.2), true);
				
		this.wall_slide = this.momentum_timer == 0 && (this.col_left || this.col_right) && ((!this.col_down && !this.col_up));
		this.col = this.col_left || this.col_right || this.col_up || this.col_down;
		
				
		if (this.col_down) {
			this.jump_num = this.jump_keep;
			this.coyote_buffer = 4;
			if (this.dash_cooldown == 0) this.dash_num = this.dash_keep;
			this.gravity = gravity_normal;
			if (this.momentum_timer == 0) this.vel.y = 0;
			this.frames_grounded = Math.min(this.frames_grounded + 1, 10);
			if (!Room.respawn_point_set) {
				this.respawn_point = new Vector2(this.pos);
				Room.respawn_point_set = true;
			}
		}
		else this.frames_grounded = 0;
		
		if (this.wall_slide) this.coyote_buffer = 4;
		if (!this.col_down && this.jump_num == this.jump_keep && (this.coyote_buffer == 0 || this.dashing)) this.jump_num--;
		if (this.wall_slide && this.dash_cooldown == 0 && this.wall_jump_unlock) this.dash_num = this.dash_keep;
		
		//gravity & friction
		//momentum subtracting
		if (this.momentum.length() != 0 && ! this.dashing) {
			this.momentum = Vector2.scale_to_length(this.momentum, this.momentum.length() - Game.speed_mult);
			if (this.momentum_timer == 0 && (this.momentum.length() < 2 || (this.col && !this.jumped)) && (this.frames_grounded > 2 || !this.jumped || this.col_left || this.col_right)) {
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
			if (Math.abs(this.vel.y) < 3) this.gravity = gravity_float;
			
			//'y'
			if (this.vel.y > -max_fall_speed && this.momentum_timer == 0) this.vel.y -= acceleration * this.gravity * Game.speed_mult;
			this.vel.y = Utility.clamp_above(this.vel.y, -max_fall_speed);
			
			//'x'
			if ((Game.left || Game.right)) {
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
		}
		
		//counters to ensure smoother and more responsive gameplay
		if (this.jump_buffer > 0) this.jump_buffer--;
		if (this.coyote_buffer > 0) this.coyote_buffer--;
		if (this.dash_buffer > 0) this.dash_buffer--;
		if (this.dash_cooldown > 0) this.dash_cooldown--;
		if (this.momentum_timer > 0) this.momentum_timer--;
		
		this.last_pos = new Vector2(this.pos);
	}
	public void jump(boolean hop) {
		if (this.momentum_timer > 0) return;
		this.vel.y = jump_force;
		this.jump_num--;
		this.gravity = gravity_normal;
		this.jumped = true;
		if (this.dashing) this.end_dash("jump");
	}
	public void wall_jump(int in) {
		if (!this.wall_jump_unlock) return;
		if (!this.wall_jumped) {
			if (this.dashing) this.end_dash("wall");
			if (in == 1) {
				this.vel = new Vector2(wall_jump);
				this.momentum.x *= -1;
			}
			if (in == -1) {
				this.vel = Vector2.invert_x(wall_jump);
				this.momentum.x *= -1;
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
		if (!this.dash_side_unlock) return;
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
			double xplus = (this.dash_dir.x * speed * 3.5f) * Game.speed_mult;
			double yplus = (this.dash_dir.y * speed * 3.5f) * Game.speed_mult;
			this.move(new Vector2(xplus, yplus));
			this.dash_dist += speed * 1.5 * Game.speed_mult;
			
			//check if intersect with any plat_ents
			object_action();
			
			if (this.dash_dist >= dash_length) this.end_dash("dash");
		}
	}
	public void end_dash(String in) {
		
		this.dashing = false;
		this.dash_dist = 0;
		
		this.momentum = Vector2.mult(this.dash_dir, speed * (double)this.dash_length/100);
		
		if (in.equals("wall")) {
			this.momentum = new Vector2(0, 0);
		}
		
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
