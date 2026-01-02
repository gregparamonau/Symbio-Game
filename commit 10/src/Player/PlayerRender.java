package Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Logic.Line;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Animation;

public class PlayerRender {
	public Player player;
	public Animation anim;
	public boolean interrupt = false;
	public String current_state = "idle", last_state = "idle";
	
	public static final String idle = "/player_textures/player_idle.png";
	public static final String run = "/player_textures/player_run.png";
	
	//antenna logic
	public static final int antenna_length = 15;
	public static final double antenna_t = 0.5, max_theta_s = 0.262, theta_s_vel = 0.02, segment_length = 1.0; //pi / 12 ~ 15º
	public static final Vector2 left_offset = new Vector2(- PlayerCollision.width_save / 2, PlayerCollision.height_save / 2), right_offset = new Vector2(PlayerCollision.width_save, 0);
	
	public Vector2[] antenna;
	public double[] theta_s;
	
	public Vector2[] poss = new Vector2[50];
	
	public PlayerRender(Player in) {
		this.player = in;
		
		this.start_animations();
		this.start_poss();
		
		this.start_antenna();
	}
	
	public void draw_player(Graphics g, JPanel pane, double xin, double yin, String location) {
		this.update_poss();
		this.update_antenna();
		this.last_state = this.current_state;
		this.current_state = this.current_state();
		
		if (!this.current_state.equals(this.last_state)) {
			this.interrupt = true;
		}
		
		if (this.interrupt && this.anim.interrupt()) {
			
			this.anim = new Animation(anim_name(), this.player.pos, 10, 6, true, true);
			
			this.interrupt = false;
		}
		//TODO: don't forget this!
		this.anim.play(false, Vector2.zero, (this.player.input.last_dir == -1/* && !this.player.wall_slide*/) || (/*this.player.wall_slide && */this.player.collider.col_right), g, pane, xin, yin, location);
		
		this.draw_antenna(g, pane, xin, yin, location, null);
		
		if (this.player.slashing) {
			new Rectangle(this.player.pos.x + this.player.combat.slash_dir.x, this.player.pos.y + this.player.combat.slash_dir.y, (this.player.combat.slash_dir.x != 0 ? 1 : 0) * this.player.combat.slash_bounds.x + (this.player.combat.slash_dir.x == 0 ? 1: 0) * this.player.combat.slash_bounds.y, (this.player.combat.slash_dir.y != 0 ? 1.5 : 0) * this.player.combat.slash_bounds.x + (this.player.combat.slash_dir.y == 0 ? 1: 0) * this.player.combat.slash_bounds.y).draw(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		}
		
		if (Game.debug_mode) {
			for (int x = 0; x<poss.length; x++) {
				this.poss[x].draw_node(g, pane, xin, yin, location, Color.magenta);
			}
		}
		
		//if (this.player.movement.dash_num == 1) new Vector2(this.player.pos.x, this.player.pos.y + 10).draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.green);
		//if (this.player.movement.dash_num == 0) new Vector2(this.player.pos.x, this.player.pos.y + 10).draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.red);
	}
	public String anim_name() {
		if (current_state.equals("run")) {
			return run;
		}
		return idle;
	}
	
	public BufferedImage sprite_to_play(Player in) {
		return new BufferedImage(PlayerCollision.width_save, PlayerCollision.height_save, BufferedImage.TYPE_INT_RGB);
	}
	public String current_state() {
		//if (this.player.dashing) return "dash";
		if (this.player.collider.col_down && this.player.vel.x == 0) return "idle";
		if (this.player.collider.col_down && this.player.vel.x != 0) return "run";
		//if (this.player.wall_slide) return "wall";
		//return "air";
		return "idle";
	}
	public void start_animations() {
		this.anim = new Animation(this.anim_name(), this.player.pos, 10, 6, true, true);
	}
	
	public void start_poss() {
		for (int x = 0; x<this.poss.length; x++) {
			this.poss[x] = new Vector2(this.player.pos);
		}
	}
	public void start_antenna() {
		//start theta_s
		this.theta_s = new double[antenna_length];
		
		for (int x = 0; x < antenna_length; x++) {
			if (x == 0) {
				this.theta_s[x] = 0;
				continue;
			}
			
			this.theta_s[x] = max_theta_s / 2 + (Math.random() > 0.33 ? Math.random() > 0.67 ? 1 : 0  : -1) * theta_s_vel;
		}
		
		this.antenna = new Vector2[antenna_length];
		
		double sum = 0;
		
		for (int x = 0; x < antenna_length; x++) {
			 if (x == 0) {
				 this.antenna[x] = Vector2.add(Vector2.add(this.player.pos, left_offset), Vector2.up);//new Vector2(Vector2.up);
				 continue;
			 }
			 
			 sum += this.theta_s[x];
			 
			 
			 this.antenna[x] = Vector2.add(this.antenna[x - 1], Vector2.up.rotate(sum));
			 
		}
	}
	public void update_antenna() {
		//update theta_s arrays
		for (int x = antenna_length - 1; x > 1; x--) {
			this.theta_s[x] = this.theta_s[x - 1];
		}
		
		this.theta_s[1] = Math.random() * max_theta_s * this.player.input.last_dir;//Utility.clamp(this.theta_s[2] + (Math.random() > 0.25 ? (Math.random() > 0.75 ? 1 : 0 ) : -1) * theta_s_vel * this.player.input.last_dir, 0, max_theta_s * this.player.input.last_dir);
		
		
		double sum = 0;
		
		for (int x = 0; x < antenna_length; x++) {
			 if (x == 0) {
				 this.antenna[x] = Vector2.add(Vector2.add(this.player.pos, left_offset), Vector2.up);//new Vector2(Vector2.up);
				 continue;
			 }
			 
			 //find angles
			 
			 sum += this.theta_s[x];
			 
			 Vector2 now = Vector2.sub(Vector2.sub(this.antenna[x], this.antenna[x - 1]), this.player.last_move).norm();
			 
			 double angle = Vector2.angle(now, Vector2.up.rotate(sum));
			 
			 this.antenna[x] = Vector2.add(this.antenna[x - 1], now.rotate(angle * antenna_t));
			 
			// this.left[x] = Vector2.add(this.left[x - 1], Vector2.up.rotate(sum_left));
			// this.right[x] = Vector2.add(this.right[x - 1], Vector2.up.rotate(sum_right));
			 
		}
		
		
		//update antenna according to this array
	}
	
	public void draw_antenna(Graphics g, JPanel in, double xin, double yin, String location, BufferedImage target) {
		
		//Vector2 start = Vector2.add(this.player.pos, left_offset);
		
		for (int x = 1; x<antenna_length; x++) {
			//Vector2 past = new Vector2(start);
			
			//start.add(this.antenna[x]);
			
			new Line(this.antenna[x], this.antenna[x - 1]).draw_line(g, Color.black, in, xin, yin, location, false);
			new Line(Vector2.add(this.antenna[x], right_offset), Vector2.add(this.antenna[x - 1], right_offset)).draw_line(g, Color.black, in, xin, yin, location, false);;
			
			//g.setColor(null);
			//left_start.draw_node(g, in, xin, yin, location, Color.magenta);
			//right_start.draw_node(g, in, xin, yin, location, Color.magenta);
		}
	}
	public void update_poss() {
		for (int x = this.poss.length - 1; x > 0; x--) {
			this.poss[x] = new Vector2(this.poss[x - 1]);
		}
		this.poss[0] = new Vector2(this.player.pos);
	}
}
