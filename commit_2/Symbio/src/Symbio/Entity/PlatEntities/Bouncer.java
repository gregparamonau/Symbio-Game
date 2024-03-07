package Symbio.Entity.PlatEntities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import LevelEdit.LevelCreator;
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
	public Bouncer(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.variant = (int)c;
		this.id = (int)d;
		this.fill = new Color(127, 63, 0);
		this.sprite_source = "/plat_ent_textures/bounce.png";
		
		this.start_bouncer();
		this.start_nodes();
		
		this.sliceable = false;
	}
	public boolean collide_with(Rectangle in, boolean player) {
		return false;
	}
	public void collision_action() {
		//TODO: make it so that when you bounce higher when you jump..
		//find personal connection + give academic source to support
		Game.player.dash_num = Game.player.dash_keep;
		
		if (Game.player.dashing) Game.player.end_dash("");
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
		//  0
		//1   2
		//  3
		if (this.variant == 0) {
			this.width = 4;
			this.height = 1;
			this.bounce_dir = Vector2.mult(Vector2.up, bounce_strength_v);
		}
		if (this.variant == 1) {
			this.width = 1;
			this.height = 4;
			this.bounce_dir = Vector2.mult(new Vector2(-0.94, 0.33), bounce_strength_h);
		}
		if (this.variant == 2) {
			this.width = 1;
			this.height = 4;
			this.bounce_dir = Vector2.mult(new Vector2(0.94, 0.33), bounce_strength_h);
		}
		if (this.variant == 3) {
			this.width = 4;
			this.height = 1;
			this.bounce_dir = Vector2.mult(Vector2.down, bounce_strength_v);
		}
	}
	public void start_sprites(String in) {
		try {
			if (this.variant == 0) this.sprite = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(Camera.tile_size, 0, 4 * Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			if (this.variant == 1) this.sprite = Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, Camera.tile_size, 4 * Camera.tile_size), 1, Color.black, 1);
			if (this.variant == 2) this.sprite = Utility.flip(Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, Camera.tile_size, 4 * Camera.tile_size), 1, Color.black, 1), false, true);
			if (this.variant == 3) this.sprite = Utility.flip(Utility.transformed_instance(ImageIO.read(getClass().getResource(in)).getSubimage(Camera.tile_size, 0, 4 * Camera.tile_size, Camera.tile_size), 1, Color.black, 1), true, false);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {
		if (location.equals("game")) {
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
				this.draw_border(g, pane, xin, yin, location);
			}
		}
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
	public String toString() {
		return "bouncer " + this.pos.x + " " + this.pos.y + " " + this.variant + " ";
	}
	public String give_class() {
		return "bouncer";
	}
	public static void create_props(JFrame temp, JTextField[] fields, JLabel[] labels) {
		fields[0] = new JTextField(LevelCreator.plat_ent_type + "");
		fields[0].setBounds(50, 50, 200, 25);
		fields[0].setVisible(true);
		temp.add(fields[0]);
		
		labels[0] = new JLabel("type");
		labels[0].setBounds(300, 50, 100, 50);
		labels[0].setVisible(true);
		temp.add(labels[0]);
		
		fields[1] = new JTextField(LevelCreator.plat_ent_variant + "");
		fields[1].setBounds(50, 100, 200, 25);
		fields[1].setVisible(true);
		temp.add(fields[1]);
		
		labels[1] = new JLabel("variant");
		labels[1].setBounds(300, 100, 100, 50);
		labels[1].setVisible(true);
		temp.add(labels[1]);
	}
}
