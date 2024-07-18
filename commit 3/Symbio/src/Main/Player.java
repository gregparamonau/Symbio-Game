package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import GameObject.GameObject;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Rendering.Animation;

public class Player extends Rectangle{
	public int dash_trail_length = 5;
	public static final int width_save = 4, height_save = 12;
	public Color fill = Color.DARK_GRAY;
	public static Color dash_color = Color.cyan;
	public Rectangle[] dash_trail = new Rectangle[5];
	public Rectangle[] trail = new Rectangle[200];
	
	//unlocked abilities
	public boolean jump_unlock = true, dash_side_unlock = true, dash_diagonal_unlock = true, wall_jump_unlock = true, swipe_unlock = true, dash_bounce_unlock = false;
	
	//physics
	public boolean col_left, col_right, col_up, col_down, col;
	public Vector2 vel = new Vector2(0, 0);
	public double acceleration = 1.63, gravity = 1;
	public double dash_mult = 1.5;
	public Vector2 momentum = Vector2.zero;
	public Vector2 last_move = new Vector2(0, 0);
	public Vector2 face_dir;
	public String current_state = "idle", last_state = "idle";
	public static final int squash_death_threshold = 4;
	
	//jump
	public int jump_keep = 1, jump_num = jump_keep;
	public static int jump_force = 25;
	public int max_fall_speed = 25;
	public static double speed = 12.5;
	public int jump_buffer, coyote_buffer, frames_grounded;
	public boolean jumped;
	
	//wall jump
	static Vector2 wall_jump = new Vector2(25, 30);
	public boolean wall_jumped;
	public boolean wall_slide;
	static double wall_slide_speed = 5; //6.6
	
	//dash
	public int dash_keep = 1, dash_num = dash_keep; 
	public int last_dir = 1;
	public int dash_buffer, dash_direction_buffer, dash_cooldown;
	public boolean dashing, dashed;
	Vector2 dash_dir = Vector2.zero;
	public double dash_dist, dash_length = 200;
	
	//grab
	public boolean grab = false;
	
	//attack
	public int swipe_cooldown, swipe_buffer;
	public Vector2 swipe_horizontal = new Vector2(20, 16);
	public Vector2 swipe_vertical = new Vector2(16, 20);
	public boolean swiped, swipe_gravity;
	public Vector2 swipe_dir;
	
	//plat ent bs
	public int plat_ent_intersect_id = -1;
	public boolean on_hair = false;
	public boolean on_hair_last_frame;
	public boolean on_hair_vertical = true;
	public int plat_ent_momentum_timer = 0;
	//public int bounce_cooldown = 0;
	
	//RENDERING
	public static final String[] sprite_names = {"/player_textures/player_right.png", "/player_textures/player_left.png"};
	public Animation[] idle = new Animation[2];
	public Animation[] run = new Animation[2];
	public Animation[] swipe_side = new Animation[2];
	public Animation[] swipe_up = new Animation[2];
	public Animation[] swipe_down = new Animation[2];
	public Animation current_animation;
	public Animation current_swipe_animation;
	public boolean interrupt = false;
	int frame_count = 0;
	
	//RENDERING FILES
	public static final String idle_name = "/player_textures/player_idle.png";
	public static final String run_name = "/player_textures/player_run.png";
	public static final String swipe_horizontal_name = "/player_textures/swipe_horizontal.png";
	public static final String swipe_vertical_name = "/player_textures/swipe_vertical.png";
	
	public Vector2 last_pos = new Vector2(0, 0);
	
	//CONSTRUCTORS
	public Player() {
		this.start_trail(0);
		this.start_animations();
		this.start_debug_trail();
		
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
		//System.out.println(this.pos);
		
		Vector2 temp = new Vector2(this.pos);
		this.move_player();
		this.last_move = Vector2.sub(this.pos, temp);
		
		this.face_dir = find_face_dir();
		
		this.max_fall_speed = 25;
		if (Game.down && this.swipe_cooldown == 0) this.max_fall_speed = 35;
		else if (Game.up) this.max_fall_speed = 15;
				
		//collision handling
		this.col_left = this.collide(new Vector2(-0.2, 0));
		this.col_right = this.collide(new Vector2(0.2, 0));
		
		this.col_up = this.collide(new Vector2(0, 0.2));
		this.col_down = this.collide(new Vector2(0, -0.2));
		
		//if (this.col_left && this.col_right && this.col_up && this.col_down) this.respawn();
		
		this.wall_slide = (this.col_left || this.col_right) && ((!this.col_down && !this.col_up) || !this.jump_unlock);
		this.col = this.col_left || this.col_right || this.col_up || this.col_down;
		
		if (this.col && !this.col_up) {
			this.swipe_gravity = false;
		}
		
		if (this.col_down) {
			this.jump_num = this.jump_keep;
			this.coyote_buffer = 4;
			if (this.dash_cooldown == 0) this.dash_num = this.dash_keep;
			this.gravity = 1;
			this.vel.y = 0;
			this.frames_grounded = Math.min(this.frames_grounded + 1, 10);
		}
		else this.frames_grounded = 0;
		
		if (this.wall_slide) this.coyote_buffer = 4;
		if (!this.col_down && this.jump_num == this.jump_keep && (this.coyote_buffer == 0 || this.dashing)) this.jump_num--;
		if (this.wall_slide && this.dash_cooldown == 0 && this.wall_jump_unlock) this.dash_num = this.dash_keep;
		
		//gravity & friction
		//momentum subtracting
		if (this.momentum.length() != 0) {
			this.momentum = Vector2.scale_to_length(this.momentum, this.momentum.length() - Game.speed_mult);
			if (this.plat_ent_momentum_timer == 0 && (this.momentum.length() < 2 || (this.col && !this.jumped && !this.swipe_gravity)) && (this.frames_grounded > 2 || !this.jumped || this.col_left || this.col_right)) {
				this.momentum = new Vector2(0, 0);
			}
		}
		
		//velocity acceleration/deceleration
		//'y'
		if (!this.dashing) {
			if (!this.swipe_gravity) {
				if (this.vel.y <= 0) {
					this.gravity = 3;
				}
				if (this.col_up && this.vel.y > 0) this.gravity = 3;
			}
			if (Math.abs(this.vel.y) < 3) this.gravity = 0.75f;
			
			//'y'
			if (this.vel.y > -max_fall_speed) this.vel.y -= acceleration * this.gravity * Game.speed_mult;
			this.vel.y = Utility.clamp_above(this.vel.y, -max_fall_speed);
			
			//'x'
			if (Game.left || Game.right) {
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
		if (this.jump_buffer > 0 && (this.col_down || this.col_left || this.col_right || this.on_hair || this.collide(new Vector2(0, 0)))) {
			this.jump(this.frames_grounded < 3);
			this.jump_buffer = 0;
		}	
		if (this.swipe_buffer > 0) {
			this.swipe();
		}
		
		//counters to ensure smoother and more responsive gameplay
		if (this.jump_buffer > 0) this.jump_buffer--;
		if (this.coyote_buffer > 0) this.coyote_buffer--;
		if (this.dash_buffer > 0) this.dash_buffer--;
		if (this.dash_cooldown > 0) this.dash_cooldown--;
		if (this.swipe_cooldown > 0) this.swipe_cooldown--;
		if (this.swipe_buffer > 0) this.swipe_buffer--;
		if (this.plat_ent_momentum_timer > 0) this.plat_ent_momentum_timer--;
			if (this.plat_ent_momentum_timer == 1) this.vel.y = this.momentum.y * 0.5;
		
		this.last_pos = new Vector2(this.pos);
		this.last_state = this.current_state;
		this.current_state = this.return_current_state();
	}
	public void jump(boolean hop) {
		if (!this.jump_unlock) return;
		this.vel.y = jump_force;
		this.jump_num--;
		this.gravity = 1;
		this.jumped = true;
		if (this.dashing && !this.face_dir.equals(Vector2.up) && this.col_down && this.dash_bounce_unlock) {
			this.end_dash("jump");
			this.momentum = Vector2.mult(this.momentum, this.dash_mult);
		}
		if (hop && this.momentum.x != 0) this.momentum = Vector2.add(this.momentum, new Vector2(this.face_dir.x * speed * 1.5, this.face_dir.y));
		else if (!this.dashing && (Game.left || Game.right)) this.momentum = Vector2.add(this.momentum, new Vector2(this.face_dir.x * 0.5 * speed, this.face_dir.y));
	}
	public void wall_jump(int in, boolean plat_ent_intersect, boolean jump_down) {
		if (!this.wall_jump_unlock) return;
		if (!this.wall_jumped) {
			if (this.dashing) this.end_dash("wall");
			if (in == 1) {
				this.vel = new Vector2(wall_jump);
				if (jump_down) this.vel.y *= -1;
				this.momentum.x *= -1;
			}
			if (in == -1) {
				this.vel = Vector2.invert_x(wall_jump);
				if (jump_down) this.vel.y *= -1;
				this.momentum.x *= -1;
			}
			if (in == 0) {
				this.vel = new Vector2(0, wall_jump.y);
				if (jump_down) this.vel.y *= -1;
			}
			this.gravity = 1;
			this.wall_jumped = true;
		}
		else {
			Vector2 move = new Vector2((this.vel.x + this.last_dir * speed + this.momentum.x) * Game.speed_mult, (this.vel.y + this.momentum.y) * Game.speed_mult);
			this.move(move);
			//check if intersect with any plat_ents
			if (plat_ent_intersect) object_action();
			else this.on_hair = false;
			if (this.col) this.wall_jumped = false;
		}
	}
	public void dash() {
		if (!this.dash_side_unlock) return;
		//~4 frame grace period to input dash direction
		if (!this.dashing || this.dash_direction_buffer < 4) {
			this.start_trail(1);
			if (!this.dashing) this.dash_cooldown = 10;
			this.wall_jumped = false;
			this.vel.y = 0;
			this.dash_dir = this.face_dir;
			if (!this.dashing) this.dash_num--;
			this.swipe_gravity = false;
			this.dashing = true;
			Game.cam.screen_shake(this.dash_dir);
		}
		else {
			double xplus = ((this.dash_dir.x * speed * 3.5f) + this.momentum.x) * Game.speed_mult;
			double yplus = ((this.dash_dir.y * speed * 3.5f) + this.momentum.y) * Game.speed_mult;
			this.move(new Vector2(xplus, yplus));
			this.dash_dist += speed * 1.5 * Game.speed_mult;
			
			if (this.dash_dist > dash_length * this.dash_trail.length / this.dash_trail_length) this.add_to_dash_trail();
			//check if intersect with any plat_ents
			object_action();
			
			if (this.dash_dist >= dash_length) this.end_dash("dash");
		}
	}
	public void move_player() {
		if (this.dashing) {
			this.dash();
			this.dash_direction_buffer++;
			return;
		}
		if (this.wall_jumped) {
			this.wall_jump(0, !this.on_hair, false);
			return;
		}
		Vector2 move = Vector2.mult(new Vector2(this.momentum.x + this.vel.x, (this.vel.y * (float)jump_force/18) + this.momentum.y), Game.speed_mult);
		if (this.on_hair) move.x = 0;
		if (this.wall_slide && !this.jumped && this.wall_jump_unlock && this.plat_ent_momentum_timer == 0) {
			move.y = - (float)wall_slide_speed * Game.speed_mult;
			this.dash_num = this.dash_keep;
		}
		if (this.on_hair) move.y = 0;
		if (this.plat_ent_momentum_timer > 0) move = new Vector2(this.momentum.x + this.vel.x, this.momentum.y);
		this.move(move);
		
		//check if intersect with any plat_ents
		object_action();
	}
	
	public void move(Vector2 in) {
		long a = System.nanoTime();
		
		in = Vector2.mult(in, 0.2);
		if (this.wall_jumped) System.out.println("move: " + in);
		
		if (this.inside_object()) {
			System.out.println("inside platform ERROR");
			this.respawn();
			return;
		}
		else this.displace();
		//to allign everything with edges, to make sure player able to move everywhere
		//for 'y' axis
		if (in.y != 0) {
			for (int y = 0; y<Math.abs(in.y) * 5; y++) {
				this.pos.y += Utility.sign(in.y) * 0.2;
				if (this.collide(new Vector2(0, 0))) {
					this.pos.y -= Utility.sign(in.y) * 0.2;
					break;
				}
			}
		}
		//for 'x' axis
		if (in.x != 0) {
			for (int x = 0; x<Math.abs(in.x) * 5; x++) {
				this.pos.x += Utility.sign(in.x) * 0.2;
				if (this.collide(new Vector2(0, 0))) {
					if (!this.collide(new Vector2(0, 0.2))) {
						this.pos.y += 0.2;
						continue;
					}
					this.pos.x -= Utility.sign(in.x) * 0.2;
					break;
				}
			}
		}
		
		long b = System.nanoTime();
		
		//System.out.println("	mt: " + (double)(b-a) / 1_000_000);
	}
	
	public void displace() {
		for (GameObject obj: Game.current_room.objects) {
			if (!obj.solid || !Rectangle.intersect(obj, this) || !obj.displaceable) continue;
			
			Rectangle temp = Rectangle.intersect_area(obj, this);
			if (temp.height <= temp.width) {
				this.pos.y += Utility.sign(this.pos.y - obj.pos.y) * (temp.height + 1);
				break;
			}
			else if (temp.height > temp.width) {
				this.pos.x += Utility.sign(this.pos.x - obj.pos.x) * (temp.width + 1);
				break;
			}
		}
	}
	//ATTACK
	public void swipe() {
		if (!this.swipe_unlock) return;
		if (this.swipe_buffer == 0) {
			this.swipe_cooldown = 18;
			this.swipe_buffer = 6;
		}
		this.swipe_dir = this.return_swipe_dir(this.face_dir);
		this.swipe_sprite_to_play(0);
	/*
		for (int x = 0; x<Game.current_room.plat_ents.length; x++) {
			if (Game.current_room.plat_ents[x].sliceable && Game.current_room.plat_ents[x].collide_with(this.return_swipe(), false)) {
				Game.current_room.plat_ents[x].give_slice_momentum(this);
				this.dash_num = this.dash_keep;
				this.swipe_gravity = true;
				this.swipe_buffer = 0;
			}
		}
	*/
	}
	public Vector2 return_swipe_dir(Vector2 in) {
		if (in.y != 0) return new Vector2(0, Utility.sign(in.y));
		else return new Vector2(Utility.sign(in.x), 0);
	}
	public Rectangle return_swipe() {
		if (Math.abs(this.swipe_dir.y) == 1) 
			return new Rectangle(this.pos.x, this.pos.y + this.swipe_dir.y * this.swipe_vertical.y / 2, (int)this.swipe_vertical.x, (int)this.swipe_vertical.y);
		return new Rectangle(this.pos.x + this.swipe_dir.x * this.swipe_horizontal.x / 2, this.pos.y, (int)this.swipe_horizontal.x, (int)this.swipe_horizontal.y);
	}
	
	//COLLISION
	
	public boolean collide(Vector2 move) {
		for (GameObject obj: Game.current_room.objects) {
			
			if (obj.collide_with(new Rectangle(this.pos.x + move.x, this.pos.y + move.y, this.width, this.height), true)) return true;
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
		
		for (GameObject obj: Game.current_room.objects) {
			if (obj.collide_with(this)) {
				obj.collision_action();
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
	public void end_dash(String in) {
		
		this.dashing = false;
		this.dash_dist = 0;
		
		if (in.equals("jump")) {
			System.out.println("jump");
			this.momentum = Vector2.mult(this.dash_dir, speed * (double)this.dash_length/100);
			if (this.dash_dir.y < 0 && this.jumped && !this.dash_dir.equals(Vector2.down) && this.col_down) {
				this.momentum = Vector2.mult(new Vector2(this.dash_dir.x, 0), 2 * speed * (float)this.dash_length/100);
			}
		}
		if (in.equals("wall") || in.equals("dash") || in.equals("hair")) {
			this.momentum = Vector2.mult(this.dash_dir, speed * (double)this.dash_length/100);
		}
		this.dash_dir = new Vector2();
	}
	public void start_debug_trail() {
		for (int x = 0; x<this.trail.length; x++) this.trail[x] = this;
	}
	public void update_trail() {
		for (int x = this.trail.length - 1; x > 0; x--) {
			this.trail[x] = this.trail[x - 1];
		}
		this.trail[0] = this;
		if (this.dashing) this.trail[0].fill = Color.blue;
		else if (this.wall_jumped) this.trail[0].fill = Color.yellow;
		else if (this.jumped) this.trail[0].fill = Color.red;
		else if (this.swipe_gravity) this.trail[0].fill = Color.green;
	}
	public void draw_trail(Graphics g) {
		for (Rectangle temp: this.trail) {
			temp.draw(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
			temp.draw_border(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		}
	}
	public void start_trail(int q) {
		this.dash_trail = new Rectangle[q];
		for (int x = 0; x<this.dash_trail.length; x++) {
			this.dash_trail[x] = this;
			this.dash_trail[x].sprite = Utility.transformed_instance(this.sprite_to_play(), 1, dash_color, (double)(this.dash_trail.length + 1) / (this.dash_trail_length + 1));

		}
	}
	public void add_to_dash_trail() {
		Rectangle[] temp = new Rectangle[this.dash_trail.length + 1];
		
		for (int x = 0; x<this.dash_trail.length; x++) {
			temp[x] = this.dash_trail[x];
		}
		
		temp[this.dash_trail.length] = this;
		temp[this.dash_trail.length].sprite = Utility.transformed_instance(this.sprite_to_play(), 1, dash_color, (double)(this.dash_trail.length + 1) / (this.dash_trail_length + 1));
		
		this.dash_trail = temp;
	}
	public void shorten_trail() {
		Rectangle[] temp = new Rectangle[this.dash_trail.length-1];
		
		for (int x = 0; x<temp.length; x++) {
			temp[x] = this.dash_trail[x + 1];
		}
		
		this.dash_trail = temp;
	}
	public void player_add(double xplus, double yplus) {
		this.pos.x += xplus;
		this.pos.y += yplus;
	}
	public void set_position(double x, double y) {
		this.pos.x = x;
		this.pos.y = y;
	}
	public void update_last_dir() {
		if (Game.left) this.last_dir = -1;
		else if (Game.right) this.last_dir = 1;
	}
	public void respawn() {
		//TODO: finish this
		this.set_position(Game.player_respawn.x, Game.player_respawn.y);
		this.vel = new Vector2(0, 0);
		this.momentum = new Vector2(0, 0);
	}
	
	//LOGIC RANDOME OTHER BS
	
	//RENDER
	public void draw_player(Graphics g) {
		if (Game.debug_mode) {
			this.update_trail();
			this.draw_trail(g);
		}
		if (this.dash_trail.length > 0) {
			for (int x = 0; x<this.dash_trail.length; x++) {
				try {
					this.dash_trail[x].draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, this.dash_trail[x].sprite, "game");
				}catch(Exception e) {e.printStackTrace();}
			}
		}
		else {
			this.start_trail(0);
		}
		if (this.frame_count % 5 == 0 && this.dash_trail.length > 0 && !this.dashing) this.shorten_trail();
		this.draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, this.sprite_to_play(), "game");
		
		if (this.swipe_cooldown > 0) {
			this.return_swipe().draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, this.current_swipe_animation.return_sprite(), "game");
		}
		this.frame_count++;
		if (this.frame_count == 60) this.frame_count = 0;
		
		if (Game.debug_mode) {
			//this.draw_border(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
			if (this.swipe_cooldown > 0) this.return_swipe().draw_border(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		}

	}
	public BufferedImage sprite_to_play() {
		if (!this.current_state.equals(this.last_state) || this.interrupt) {
			this.interrupt = true;
			if(this.current_animation.interrupt()) {
				this.interrupt = false;
				this.current_animation = this.determine_new_animation();
				this.current_animation.play();
			}
		}
		return this.current_animation.return_sprite();
	}
	public BufferedImage swipe_sprite_to_play(int in) {
		if (in == 0) {
			this.current_swipe_animation = this.determine_new_swipe_animation();
			this.current_swipe_animation.play();
		}
		return this.current_swipe_animation.return_sprite();
	}
	public Animation determine_new_swipe_animation() {
		if (Game.up) return this.swipe_up[(int)Utility.clamp(this.last_dir, 0, 1)];
		if (Game.down) return this.swipe_down[(int)Utility.clamp(this.last_dir, 0, 1)];
		else return this.swipe_side[(int)Utility.clamp(this.last_dir, 0, 1)];
	}
	public Animation determine_new_animation() {
		if (this.current_state.equals("run_left") || this.current_state.equals("run_right")) return this.run[(int)Utility.clamp(this.last_dir, 0, 1)];
		return this.idle[(int)Utility.clamp(this.last_dir, 0, 1)]; 
	}
	public void start_animations() {
		this.idle[0] = new Animation(idle_name, 20, 20, false, true);
		this.idle[1] = new Animation(idle_name, 20, 20, false, false);
		
		this.run[0] = new Animation(run_name, 20, 20, false, true);
		this.run[1] = new Animation(run_name, 20, 20, false, false);
		
		this.swipe_side[0] = new Animation(swipe_horizontal_name, 20, 16, false, true);
		this.swipe_side[1] = new Animation(swipe_horizontal_name, 20, 16, false, false);
		
		this.swipe_up[0] = new Animation(swipe_vertical_name, 16, 20, true, true);
		this.swipe_up[1] = new Animation(swipe_vertical_name, 16, 20, true, false);
		
		this.swipe_down[0] = new Animation(swipe_vertical_name, 16, 20, false, true);
		this.swipe_down[1] = new Animation(swipe_vertical_name, 16, 20, false, false);
		
		this.current_animation = this.idle[0];
	}
	public String return_current_state() {
		String[] runs = {"run_left", "run_right"};
		String[] idles = {"idle_left", "idle_right"};
		if ((Game.left || Game.right) && this.col_down) {
			return runs[(int)Utility.clamp(this.last_dir, 0, 1)];
		}
		return idles[(int)Utility.clamp(this.last_dir, 0, 1)];
	}
	
	
	public void print_everything() {
		System.out.println("everything");
		System.out.println("   position: " + this.pos);
		System.out.println("   velocity: " + this.vel);
		System.out.println("   momentum: " + this.momentum);
		System.out.println("   booleans:");
		System.out.println("      up: " + this.col_up);
		System.out.println("      down: " + this.col_down);
		System.out.println("      left: " + this.col_left);
		System.out.println("      right: " + this.col_right);
		System.out.println("   booleans game: ");
		System.out.println("      up: " + Game.up);
		System.out.println("      down: " + Game.down);
		System.out.println("      left: " + Game.left);
		System.out.println("      right: " + Game.right);
		
		System.out.println("\n\n\n");
	}
}
