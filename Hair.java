package Symbio.Entity.PlatEntities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Entity.PlatEntity;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class Hair extends PlatEntity{
	public static double flex = 2;
	
	public boolean vertical;
	public int swing_count = 0;
	public static int max_swing_count = 10;
	public int sign = 1;
	
	public BufferedImage[] sprites = new BufferedImage[3];
	
	Rectangle[] rects = new Rectangle[1];
	Vector2 anchor;
	
	public boolean player_just_collide;
	public int player_collide_with_section = -1;
	
	public Hair() {}
	public Hair(int a, int b, int c, int d, int e, Platform[] in) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.vertical = (this.width < this.height);
		
		this.start_nodes();
		this.fill = Color.orange;
		this.sprite_source = "/plat_ent_textures/hair.png";
		
		this.start_rects(Math.max(this.width/grid_size, this.height/grid_size));
		this.start_anchor(in);
		
		this.sliceable = true;
		this.slice_strength = 40;
		
		this.id = e;
	}
	public void update() {
		//System.out.println(this.id + " " + this.xvel + " " + this.yvel);
		if (this.swing_count == 0) return;
		if (this.vertical) {
			for (int x = 0; x<this.rects.length; x++) {
				this.rects[x].pos.x = this.pos.x + (int)(Math.round(Math.pow(flex, Utility.dy(this.rects[x], anchor)/250) * this.xvel / 5) * 5);
			}
			if (Math.abs(this.xvel) == 0) this.swing_count--;
			if (Math.abs(this.xvel) == this.swing_count) this.sign *= -1;
			this.xvel += this.sign;
		}
		if (!this.vertical) {
			for (int x = 0; x<this.rects.length; x++) {
				this.rects[x].pos.y = this.pos.y + (int)(Math.round(Math.pow(flex, Utility.dx(this.rects[x], anchor)/250) * this.yvel / 5) * 5);
			}
			if (Math.abs(this.yvel) == 0) this.swing_count--;
			if (Math.abs(this.yvel) == this.swing_count) this.sign *= -1;
			this.yvel += this.sign;
		}
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
				if (this.vel.x != 0) this.xvel = (int)Utility.clamp(this.vel.x, -max_swing_count, max_swing_count);
				this.swing_count = Math.abs(this.xvel);
				this.sign = Utility.sign(this.xvel);
				this.xvel = this.sign;
			}
			else if (!this.vertical) {
				if (this.vel.y != 0) this.yvel = (int)Utility.clamp(this.vel.y, -max_swing_count, max_swing_count);
				this.swing_count = Math.abs(this.yvel);
				this.sign = Utility.sign(this.yvel);
				this.yvel = this.sign;
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
			Game.player.pos.x = this.rects[this.player_collide_with_section].pos.x;
						
			Game.player.jump_num = Game.player.jump_keep;
			if (Game.up || Game.down) Game.player.move(new Vector2(0, Utility.sign(Game.player.face_dir.y) * Player.speed * Game.speed_mult));
			Game.player.pos.y = Utility.clamp(Game.player.pos.y, this.pos.y - this.height/2, this.pos.y + this.height/2);
			
		}
		if (!this.vertical) {
			Game.player.pos.y = this.rects[this.player_collide_with_section].pos.y;
			
			Game.player.jump_num = Game.player.jump_keep;
			
			Game.player.pos.x = Utility.clamp(Game.player.pos.x, this.pos.x - this.width/2, this.pos.x + this.width/2);
		}
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
			}
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
			}
		}
		
	}
	public boolean collision(Rectangle in) {
		this.player_collide_with_section = -1;
		for (int x = 0; x<this.rects.length; x++) {
			if (Rectangle.intersect(in, this.rects[x])) {
				this.player_collide_with_section = x;
				return true;
			}
		}
		return false;
	}
	public Vector2 snap_player_to_plat_ent() {
		Vector2 temp = new Vector2(Game.player.pos);
		if (this.vertical) temp.x = this.rects[this.player_collide_with_section].pos.x;
		else if (!this.vertical) temp.y = this.rects[this.player_collide_with_section].pos.y;
		
		Vector2 out = new Vector2(temp.x - Game.player.pos.x, temp.y - Game.player.pos.y);
		return out;
	}
	public void transfer_player_momentum() {
		if (this.vertical) Game.player.momentum = Vector2.add(Game.player.momentum, new Vector2(Math.abs(this.xvel) * this.sign * 2.5, 0));
		else if (!this.vertical) Game.player.momentum = Vector2.add(Game.player.momentum, new Vector2(0, Math.abs(this.yvel) * this.sign * 1.75));
	}
	public void start_sprites(String in) {
		try {
			if (!this.vertical) {
				//horizontal
				this.sprites[0] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
				this.sprites[1] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
				this.sprites[2] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(2 * tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			}
			else if (this.vertical) {
				//vertical
				this.sprites[0] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
				this.sprites[1] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
				this.sprites[2] = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(2 * tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void start_rects(int num) {
		this.rects = new Rectangle[num];
		for (int x = 0; x<num; x++) {
			if (this.vertical) this.rects[x] = new Rectangle(this.pos.x, this.pos.y + this.height/2 - grid_size * (x + 0.5), grid_size, grid_size);
			else if (!this.vertical) this.rects[x] = new Rectangle(this.pos.x - this.width/2 + grid_size * (x + 0.5), this.pos.y, grid_size, grid_size);
		}
	}
	public void start_anchor(Platform[] in) {
		Rectangle temp = new Rectangle(this.pos.x, this.pos.y - this.height/2 - grid_size, 1, 1);
		if (!Utility.platforms_intersect(temp, in)) temp = new Rectangle(this.pos.x, this.pos.y + this.height/2 + grid_size, 1, 1);
		if (!Utility.platforms_intersect(temp, in)) temp = new Rectangle(this.pos.x - this.width/2 - grid_size, this.pos.y, 1, 1);
		if (!Utility.platforms_intersect(temp, in)) temp = new Rectangle(this.pos.x + this.width/2 + grid_size, this.pos.y, 1, 1);
		this.anchor = temp.pos;
	}
	public String toString() {
		return "hair " + (int)this.pos.x + " " + (int)this.pos.y + " " + this.width + " " + this.height + " ";
	}
	public String give_class() {
		return "hair";
	}
}
