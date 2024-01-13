package Symbio.Entity.PlatEntities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Symbio.Game;
import Symbio.Player;
import Symbio.Entity.PlatEntity;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class Bouncer extends PlatEntity{
	Vector2 bounce_dir;
	public static int bounce_strength_h = 45;
	public static int bounce_strength_v = 35;
	public Bouncer() {}
	public Bouncer(int a, int b, int c, int d) {
		this.pos = new Vector2(a, b);
		this.variant = c;
		this.id = d;
		this.fill = new Color(127, 63, 0);
		this.sprite_source = "/plat_ent_textures/bounce.png";
		
		this.start_bouncer();
		this.start_nodes();
	}
	public boolean collide_with(Player in) {
		return Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y, this.width, this.height), new Rectangle(in));
	}
	public boolean collide_with(Rectangle in, boolean player) {
		return false;
	}
	public void collision_action() {
		//TODO: make it so that when you bounce higher when you jump..
		//find personal connection + give academic source to support
		Game.player.dash_num = Game.player.dash_keep;
		
		if (Game.player.dashing) Game.player.end_dash("");
		if (Game.player.swipe_cooldown > 0) {
			this.give_slice_momentum(Game.player);
			return;
		}
		this.transfer_player_momentum();
	}
	public void transfer_player_momentum() {
		if (Game.player.plat_ent_momentum_timer > 0) return;
		Game.player.momentum = new Vector2(this.bounce_dir);
		Game.player.gravity = 1;
		if (this.variant == 0 || this.variant == 3) Game.player.plat_ent_momentum_timer = 10;
		if (this.variant == 1 || this.variant == 2) Game.player.plat_ent_momentum_timer = 5;
	}
	public void start_bouncer() {
		System.out.println("variant " + this.variant);
		//  0
		//1   2
		//  3
		if (this.variant == 0) {
			this.width = 100;
			this.height = 25;
			this.bounce_dir = Vector2.mult(Vector2.up, bounce_strength_v);
			System.out.println("0v: " + this.bounce_dir);
		}
		if (this.variant == 1) {
			this.width = 25;
			this.height = 100;
			this.bounce_dir = Vector2.mult(new Vector2(-0.94, 0.33), bounce_strength_h);
			System.out.println("1v: " + this.bounce_dir);
		}
		if (this.variant == 2) {
			this.width = 25;
			this.height = 100;
			this.bounce_dir = Vector2.mult(new Vector2(0.94, 0.33), bounce_strength_h);
			System.out.println("2v: " + this.bounce_dir);
		}
		if (this.variant == 3) {
			this.width = 100;
			this.height = 25;
			this.bounce_dir = Vector2.mult(Vector2.down, bounce_strength_v);
			System.out.println("3v: " + this.bounce_dir);
		}
	}
	public void start_sprites(String in) {
		try {
			if (this.variant == 0) this.sprite = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(tile_size, 0, 4 * tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			if (this.variant == 1) this.sprite = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, tile_size, 4 * tile_size), Camera.pixel_size, Color.black, 1);
			if (this.variant == 2) this.sprite = Utility.flip(Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, tile_size, 4 * tile_size), Camera.pixel_size, Color.black, 1), false, true);
			if (this.variant == 3) this.sprite = Utility.flip(Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(tile_size, 0, 4 * tile_size, tile_size), Camera.pixel_size, Color.black, 1), true, false);
		}catch(Exception e) {}
	}
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {
		if (location.equals("game")) {
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
				new Rectangle(this.pos.x, this.pos.y, this.width + 4, this.height + 4).draw_border(g, pane, xin, yin, location);
			}
		}
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
	public String toString() {
		return "bouncer " + (int)this.pos.x + " " + (int)this.pos.y + " " + this.variant + " ";
	}
	public String give_class() {
		return "bouncer";
	}
}
