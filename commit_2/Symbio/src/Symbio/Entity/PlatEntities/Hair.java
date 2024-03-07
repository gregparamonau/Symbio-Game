package Symbio.Entity.PlatEntities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import LevelEdit.LevelCreator;
import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Entity.PlatEntity;
import Symbio.Logic.Line;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class Hair extends PlatEntity{
	public double flex = 0;
	public double length = 5;
	
	public double max_flex_dist = 20;
	
	public boolean vertical;
	public int swing_count = 0;
	public static int max_swing_count = 10;
	public int sign = 1;
	
	public BufferedImage[] sprites = new BufferedImage[3];
	
	Rectangle[] rects = new Rectangle[1];
	public int anchor;
	
	public boolean player_just_collide;
	public int player_collide_with_section = -1;
	
	public Hair() {}
	public Hair(double a, double b, double c, double d, int e, Platform[] in) {
		this.pos = new Vector2(a, b);
		this.width = (int)c;
		this.height = (int)d;
		this.vertical = (this.width < this.height);
		
		this.start_nodes();
		this.fill = Color.orange;
		this.sprite_source = "/plat_ent_textures/hair.png";
		
		this.sliceable = true;
		this.slice_strength = 40;
		
		this.id = e;
	}
	public void update() {
		if (this.swing_count == 0) return;
		
		if (this.anchor == 0) {
			for (int x = 0; x<this.rects.length; x++) {
				Vector2 add = new Vector2(this.length * sum_sin(flex * this.vel_extra, Math.abs(this.anchor - x)), this.length * sum_cos(flex * this.vel_extra, Math.abs(this.anchor - x)));
				if (this.vertical) this.rects[x].pos = Vector2.add(this.rects[this.anchor].pos, add);
				if (!this.vertical) this.rects[x].pos = Vector2.add(this.rects[this.anchor].pos, Vector2.swap_axis(add));
			}
		}
		if (this.anchor == this.rects.length - 1) {
			for (int x = this.anchor; x>=0; x--) {
				Vector2 add = new Vector2(this.length * sum_sin(flex * this.vel_extra, Math.abs(this.anchor - x)), this.length * sum_cos(flex * this.vel_extra, Math.abs(this.anchor - x)));
				if (this.vertical) this.rects[x].pos = Vector2.add(this.rects[this.anchor].pos, add);
				if (!this.vertical) this.rects[x].pos = Vector2.add(this.rects[this.anchor].pos, Vector2.swap_axis(add));
			}
		}
		
		if (Math.abs(this.vel_extra) == 0) this.swing_count--;
		if (Math.abs(this.vel_extra) == this.swing_count) this.sign *= -1;
		this.vel_extra += this.sign;
	}
	public boolean collide_with(Player in) {
		boolean temp = this.collision(new Rectangle(in)) && Game.player.grab && !Game.player.jumped;
		if (temp) {
			Game.player.on_hair = true;
			Game.player.on_hair_vertical = this.vertical;
			Game.player.wall_jumped = false;
			Game.player.swipe_gravity = false;
		}
		if (Game.player.on_hair && !Game.player.on_hair_last_frame) {
			if (Game.player.dashing) Game.player.end_dash("hair");
			this.vel = new Vector2(Game.player.vel);
			//this.vel = Vector2.add(Game.player.vel, Game.player.momentum);
			if (this.vertical) {
				if (this.vel.x != 0) this.vel_extra = (int)Utility.clamp(this.vel.x, -max_swing_count, max_swing_count);
				this.swing_count = Math.max(Math.abs(this.vel_extra), this.swing_count);
				this.sign = Utility.sign(this.vel_extra);
				this.vel_extra = this.sign;
			}
			else if (!this.vertical) {
				if (this.vel.y != 0) this.vel_extra = (int)Utility.clamp(this.vel.y, -max_swing_count, max_swing_count);
				this.swing_count = Math.max(Math.abs(this.vel_extra), this.swing_count);
				this.sign = Utility.sign(this.vel_extra);
				this.vel_extra = this.sign;
			}
		}

		return temp;
	}
	public boolean collide_with(Rectangle in, boolean player) {
		if (player) return false;
		for (int x = 0; x<this.rects.length; x++) {
			if (Rectangle.intersect(in, this.rects[x])) return true;
		}
		return false;
	}
	public void collision_action() {
		Game.player.plat_ent_intersect_id = this.id;
		if (this.vertical) {
			if (Game.up) this.player_collide_with_section--;
			if (Game.down) this.player_collide_with_section++;
			//if (Game.up || Game.down) Game.player.move(new Vector2(0, Utility.sign(Game.player.face_dir.y) * Player.speed * Game.speed_mult));
			//Game.player.pos.y = Utility.clamp(Game.player.pos.y, this.pos.y - this.height/2, this.pos.y + this.height/2);
			
		}
		if (!this.vertical) {
			if (Game.left) this.player_collide_with_section--;
			if (Game.right) this.player_collide_with_section++;
			//Game.player.pos.y = this.rects[this.player_collide_with_section].pos.y;
			
			//Game.player.jump_num = Game.player.jump_keep;
			
			//Game.player.pos.x = Utility.clamp(Game.player.pos.x, this.pos.x - this.width/2, this.pos.x + this.width/2);
		}
		
		this.player_collide_with_section = (int)Utility.clamp(this.player_collide_with_section, 0, this.rects.length - 1);
		
		Vector2 move = Vector2.sub(this.rects[this.player_collide_with_section].pos, Game.player.pos);
		if (move.length() > 1) Game.player.move(move);
		//Game.player.pos.x = this.rects[this.player_collide_with_section].pos.x;
					
		Game.player.jump_num = Game.player.jump_keep;
	}
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {	
		if (location.equals("edit")) {
			this.draw(g, pane, xin, yin, location);
		}
		if (location.equals("game")) {
			for (int x = 0; x<this.rects.length; x++) {
				BufferedImage temp = null;
				if (x == 0) temp = this.sprites[0];
				else if (x > 0 && x < this.rects.length - 1) temp = this.sprites[1];
				else if (x == this.rects.length - 1) temp = this.sprites[2];
				
				this.rects[x].draw_with_sprite(g, pane, xin, yin, temp, location);
				if (Game.debug_mode && x != 0) new Line(this.rects[x].pos, this.rects[x - 1].pos).draw_line(g, Color.magenta, pane, xin, yin, location, 1);;//this.rects[x].draw_border(g, pane, xin, yin, location);
			}
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
			}
		}
		
	}
	public boolean collision(Rectangle in) {
		if (!Game.player.grab) this.player_collide_with_section = -1;
		boolean out = false;
		
		for (int x = 0; x<this.rects.length; x++) {
			if (Rectangle.intersect(in, this.rects[x])) {
				if (this.player_collide_with_section == -1) this.player_collide_with_section = x;
				else if (Vector2.dist(Game.player.pos, this.rects[x].pos) < Vector2.dist(Game.player.pos, this.rects[this.player_collide_with_section].pos)) this.player_collide_with_section = x;
				out = true;
			}
		}
		return out;
	}
	public Vector2 snap_player_to_plat_ent() {
		Vector2 out = Vector2.sub(this.rects[this.player_collide_with_section].pos, Game.player.pos);
		return out;
	}
	public void transfer_player_momentum() {
		if (this.vertical) Game.player.momentum = Vector2.add(Game.player.momentum, new Vector2(Math.abs(this.vel_extra) * this.sign * 2.5, 0));
		else if (!this.vertical) Game.player.momentum = Vector2.add(Game.player.momentum, new Vector2(0, Math.abs(this.vel_extra) * this.sign * 1.75));
	}
	public void start_sprites(String in) {
		try {
			if (!this.vertical) {
				//horizontal
				this.sprites[0] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
				this.sprites[1] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
				this.sprites[2] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(2 * Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			}
			else if (this.vertical) {
				//vertical
				this.sprites[0] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
				this.sprites[1] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
				this.sprites[2] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(2 * Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void start_rects(int num) {
		this.rects = new Rectangle[num];
		for (int x = 0; x<num; x++) {
			if (this.vertical) this.rects[x] = new Rectangle(this.pos.x, this.pos.y + this.height/2 - Camera.tile_size * (x + 0.5), Camera.tile_size, Camera.tile_size);
			else if (!this.vertical) this.rects[x] = new Rectangle(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y, Camera.tile_size, Camera.tile_size);
		}
	}
	public void start_assets(Platform[] in) {
		this.start_rects((int)Math.max(this.width / Camera.tile_size, this.height / Camera.tile_size));
		this.start_anchor(in);
		this.flex = Math.atan(Math.abs(max_flex_dist / Math.max(this.height, this.width))) / this.rects.length;
	}
	public void start_anchor(Platform[] in) {
		this.anchor = 0;
		
		if (this.vertical) this.length = -5;
		if (!this.vertical) this.length = 5;
		
		if (Utility.platforms_intersect(new Rectangle(this.pos.x + this.width / 2 + Camera.tile_size, this.pos.y, 1, 1), in) || Utility.platforms_intersect(new Rectangle(this.pos.x, this.pos.y - this.height / 2 - Camera.tile_size, 1, 1), in)) {
			this.anchor = this.rects.length - 1;
			this.length *= -1;
		}
	}
	public static double sum_sin(double sin, int n) {
		double out = 0;
		for (int x = 0; x<n; x++) out += Math.sin(x * sin);
		return out;
	}
	public static double sum_cos(double cos, int n) {
		double out = 0;
		for (int x = 0; x<n; x++) out += Math.cos(x * cos);
		return out;
	}
	public String toString() {
		return "hair " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " ";
	}
	public String give_class() {
		return "hair";
	}
}
