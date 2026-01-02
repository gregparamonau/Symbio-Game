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
	
	public boolean col_left, col_right, col_up, col_down, col;
	
	public int frames_grounded = 0, frames_on_wall = 0;
	public int ground_coyote = 0, wall_coyote = 0;
	public static final int coyote_max = 8;
	
	
	public PlayerCollision(Player in) {
		this.player = in;
	}
	
	public void update() {
		
		this.col_left = this.collide(new Vector2(-0.2, 0), true);
		this.col_right = this.collide(new Vector2(0.2, 0), true);
		
		this.col_up = this.collide(new Vector2(0, 0.2), true);
		this.col_down = this.collide(new Vector2(0, -0.2), true);
		
		//this.player.wall_slide = (this.player.col_left || this.player.col_right) && ((!this.player.col_down && !this.player.col_up));
		this.col = this.col_left || this.col_right || this.col_up || this.col_down;
		
		this.update_counters();
		
		System.out.println("FW: " + this.frames_on_wall);
	}
	
	public void update_counters() {
		//grounded coutners
		if (this.col_down) this.frames_grounded ++;
		else this.frames_grounded = 0;
		
		if (this.col_left || this.col_right) this.frames_on_wall ++;
		else this.frames_on_wall = 0;
		
		//coyotes
		if (this.col_down) this.ground_coyote = coyote_max;
		else if (this.ground_coyote > 0) this.ground_coyote--;
		
		if (this.col_left || this.col_right) this.wall_coyote = coyote_max;
		else if (this.wall_coyote > 0) this.wall_coyote--;
	}

	public void move(Vector2 in) {
		long a = System.nanoTime();
		
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
				//TODO: DONT FORGET TO PUT THIS BACK
				if (this.collide(new Vector2(0, -0.6), false) && !(this.player.movement.dash_counter > 0)) {
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
