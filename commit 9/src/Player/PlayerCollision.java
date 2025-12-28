package Player;

import GameObject.GameObject;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;

public class PlayerCollision {
	Player player;
	public static final int squash_death_threshold = 4; //collision
	
	public static final int width_save = 4, height_save = 12;
	
	
	public PlayerCollision(Player in) {
		this.player = in;
	}
	
	public void update() {
		this.move_player();
		
		this.player.col_left = this.collide(new Vector2(-0.2, 0), true);
		this.player.col_right = this.collide(new Vector2(0.2, 0), true);
		
		this.player.col_up = this.collide(new Vector2(0, 0.2), true);
		this.player.col_down = this.collide(new Vector2(0, -0.2), true);
		
		this.player.wall_slide = (this.player.col_left || this.player.col_right) && ((!this.player.col_down && !this.player.col_up));
		this.player.col = this.player.col_left || this.player.col_right || this.player.col_up || this.player.col_down;
	}
	
	public void move_player() {
		if (this.player.dashing) {
			this.player.movement.dash();
			this.player.movement.dash_direction_buffer++;
			return;
		}
		if (this.player.wall_jumped) {
			this.player.movement.wall_jump(this.player.last_dir);
			return;
		}
		Vector2 move = Vector2.mult(new Vector2(this.player.momentum.x + this.player.vel.x, (this.player.vel.y * PlayerMovement.vel_mult_const) + this.player.momentum.y), Game.speed_mult);
		if (this.player.wall_slide && !this.player.jumped && this.player.wall_jump_unlock) {
			move.y = - (float)PlayerMovement.wall_slide_speed * Game.speed_mult;
			this.player.movement.dash_num = this.player.movement.dash_keep;
		}
		//if (this.object_momentum_timer > 0) move = new Vector2(this.momentum.x + this.vel.x, this.momentum.y);
		this.move(move);
		
		//check if intersect with any plat_ents
		this.object_action();
		this.enemy_action();
	}

	public void move(Vector2 in) {
		long a = System.nanoTime();
		
		in = Vector2.mult(in, 0.2);
		
		if (this.inside_object()) {
			this.player.combat.hazard_respawn(true);
			return;
		}
		else this.displace(0);
		//to allign everything with edges, to make sure player able to move everywhere
		//for 'y' axis
		if (in.y != 0) {
			for (int y = 0; y<Math.abs(in.y) * 5; y++) {
				this.player.pos.y += Utility.sign(in.y) * 0.2;
				if (this.collide(new Vector2(0, 0), false)) {
					if (!this.collide(new Vector2(0.2, 0), false) && Utility.sign(in.x) != 1) {
						this.player.pos.x += 0.2;
						continue;
					}
					if (!this.collide(new Vector2(-0.2, 0), false) && Utility.sign(in.x) != -1) {
						this.player.pos.x += -0.2;
						continue;
					}
					this.player.pos.y -= Utility.sign(in.y) * 0.2;
					break;
				}
			}
		}
		//for 'x' axis
		if (in.x != 0) {
			for (int x = 0; x<Math.abs(in.x) * 5; x++) {
				this.player.pos.x += Utility.sign(in.x) * 0.2;
				if (this.collide(new Vector2(0, 0), false)) {
					if (!this.collide(new Vector2(0, 0.2), false)) {
						this.player.pos.y += 0.2;
						continue;
					}
					this.player.pos.x -= Utility.sign(in.x) * 0.2;
					break;
				}
				if (this.collide(new Vector2(0, -0.6), false) && !this.player.dashing) {
					for (int y = 0; y<3; y++) {
						if (!this.collide(new Vector2(0, -0.2), false)) {
							this.player.pos.y += -0.2;
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
			Game.current_room.objects[x].displace_entity(this.player, direction);
		}
	}
	
	//COLLISION
	public boolean collide(Vector2 move, boolean find_collide) {
		//System.out.println("START");
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			if (!Game.current_room.objects[x].solid) continue;
			//System.out.println("	COLLIDE_CHECK_393: " + Game.current_room.objects[x].toString());
			if (Game.current_room.objects[x].collide_with(new Rectangle(this.player.pos.x + move.x, this.player.pos.y + move.y, this.player.width, this.player.height), false)) {
				if (find_collide) this.player.object_intersect_id = x;
				return true;
			}
		}
		return false;
	}
	
	public boolean inside_object() {
		for (GameObject obj: Game.current_room.objects) {
			if (!obj.solid || !Rectangle.intersect(obj, this.player)) continue;
			Rectangle temp = Rectangle.intersect_area(obj, this.player);
			if (Math.min(temp.width, temp.height) > squash_death_threshold) return true;
		}
		return false;
	}
	public boolean object_action() {
		
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			if (Game.current_room.objects[x].collide_with(this.player, true)) {
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
}
