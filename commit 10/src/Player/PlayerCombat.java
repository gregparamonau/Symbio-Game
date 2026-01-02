package Player;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;

public class PlayerCombat {
	//attacks and health
	Player player;
	
	//attack
	public int slash_strength = 1;
	public Vector2 slash_dir;
	
	public Vector2 slash_bounds = new Vector2(35, 24);
	public Vector2 offset_slash = new Vector2(10, 12);
	
	public int slash_length = 10, slash_pause = 5;
	public int slash_count = 0, slash_wait_count = 0, slash_buffer;
	
	
	//health system
	public Vector2 respawn_point = new Vector2(0, 0);
	public int health_full = 3;
	public int health = health_full;
	public int knockback = 40;
		
	public int invincibility_frames = 0;
	public int invincibility_frames_max = 60;
	
	
	public PlayerCombat(Player in) {
		this.player = in;
	}

	public void update() {
		
		if (this.slash_buffer > 0 && !this.player.slashing && this.player.combat.slash_wait_count == 0) {
			this.start_slash();
		}
		
		if (this.slash_wait_count > 0) this.slash_wait_count--;
		if (this.slash_buffer > 0) this.slash_buffer--;
		if (this.invincibility_frames > 0) this.invincibility_frames--;
	}
	
	//ATTACK CODE
	public void start_slash() {
		if (!this.player.slash_unlock) return;
		
		this.slash_count = this.slash_length;
		this.slash_dir = this.find_slash_dir();
			
		this.player.slash_held = true;
		this.player.slashing = true;
			
		/*if (this.player.dashing) {
			this.player.movement.end_dash();
		}*/
			//this.gravity = 3;
			
		this.slash();
	}
	public void slash() {
		if (this.slash_count > this.slash_length - 5) {
			this.slash_dir = this.find_slash_dir();
			this.slash_count--;
			return;
		}
		if (!this.player.slash_unlock) return;
		for (int x = 0; x<Game.current_room.enemies.length; x++) {
			if (Rectangle.intersect(new Rectangle(this.player.pos.x + this.slash_dir.x, this.player.pos.y + this.slash_dir.y, (this.slash_dir.x != 0 ? 1 : 0) * this.slash_bounds.x + (this.slash_dir.x == 0 ? 1: 0) * this.slash_bounds.y, (this.slash_dir.y != 0 ? 1.25 : 0) * this.slash_bounds.x + (this.slash_dir.y == 0 ? 1: 0) * this.slash_bounds.y), Game.current_room.enemies[x])) {
				//TODO: transfer this over to enemy script, so not all enemiesa re affected the same
				Game.current_room.enemies[x].momentum = Vector2.scale_to_length(this.slash_dir, this.knockback * 0.1);
					
				if (Game.current_room.enemies[x].damage(this.slash_strength)) {
					this.player.vel = Vector2.scale_to_length(this.slash_dir, -this.knockback * 0.5 * (this.slash_dir.x == 0 && this.slash_dir.y < 0 ? 2 : 0));
						
					this.player.momentum.x *= (this.slash_dir.x == 0 ? 1: 0);
					this.player.momentum.y *= (this.slash_dir.y == 0 ? 1: 0);
						
						
					//this.player.movement.momentum_keep_buffer = 10;
					//this.player.movement.vel_keep_buffer = 25;
						
					//if (Vector2.dot(this.slash_dir, Vector2.down) == 1) this.player.movement.dash_num = this.player.movement.dash_keep;
				}
			}
		}
		this.slash_count--;
			
		if (this.slash_count == 0) {
			this.slash_wait_count = this.slash_pause;
		}
	}
	
	public Vector2 find_slash_dir() {
		if (this.player.input.face_dir.y != 0 && !(this.player.input.face_dir.y < 0 && this.player.collider.col_down)) {
			return new Vector2(0, this.offset_slash.y * Utility.sign(this.player.input.face_dir.y));
		}
		//TODO: don't forget this
		return new Vector2(this.offset_slash.x * this.player.input.last_dir * 1/*(this.player.wall_slide ? -1 : 1)*/, 0);
	}
	
	
	
	
	//health
	public void damage(int damage, Rectangle source) {
		if (this.invincibility_frames > 0) return;
		this.health -= damage;
		
		this.player.vel = Vector2.scale_to_length(Vector2.sub(this.player.pos, source.pos), this.knockback);
		/*if (this.player.dashing) {
			this.player.movement.end_dash();
			this.player.momentum = new Vector2(0, 0);
		}
		else */this.player.momentum = Vector2.scale_to_length(Vector2.sub(this.player.pos, source.pos), this.player.momentum.l());
		
		this.invincibility_frames = this.invincibility_frames_max;
		
		if (this.health <= 0) this.death_respawn();
	}
	public void death_respawn() {
		//TODO: make this method
		this.health = this.health_full;
		this.player.set_position(this.respawn_point.x, this.respawn_point.y);
		this.player.vel = new Vector2(0, 0);
		this.player.momentum = new Vector2(0, 0);
	}
	public void hazard_respawn(boolean damage) {
		//TODO: finish this
		if (damage) this.health--;
		if (this.health == 0) {
			this.death_respawn();
			return;
		}
		
		this.player.set_position(this.respawn_point.x, this.respawn_point.y);
		this.player.vel = new Vector2(0, 0);
		this.player.momentum = new Vector2(0, 0);
	}
}
