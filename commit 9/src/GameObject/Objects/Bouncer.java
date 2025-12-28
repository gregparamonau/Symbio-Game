package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Player.Player;
import Player.PlayerMovement;
import Rendering.Camera;

public class Bouncer extends GameObject{
	public Vector2 dir;
	Vector2 bounce_dir;
	public static int bounce_strength_h = 40;
	public static int bounce_strength_v = 25;
	public static int height_default = 1, width_default = 3;
	public static String sprite_name_default = "/object_textures/bounce.png";
	
	public static Color fill_save = new Color(127, 63, 0);
	
	public int variant;
	
	public boolean just_created = true;
	
	public Bouncer() {}
	
	public Bouncer(double a, double b, double c, double d, String sprite, int id) {
		this.pos = new Vector2(a, b);
		this.variant = (int)c;
		this.object_handle = (int)d;
		
		this.id = id;
		
		this.sprite_name = sprite;
		
		this.start_bouncer();
		
		this.solid = true;
		this.vis_solid = false;
		
		this.sliceable = false;
		
		this.fill = fill_save;
	}
	public boolean collide_with(Rectangle in, boolean col_action) {
		if (col_action) return Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y, this.width + 2, this.height + 2), in);
		return Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y, this.width - 2, this.height - 4), in);
	}
	public void collision_action() {
		//TODO: make it so that when you bounce higher when you jump..
		//find personal connection + give academic source to support
		Game.player.movement.dash_num = Game.player.movement.dash_keep;		
		if (Game.player.dashing) Game.player.movement.end_dash();
		this.transfer_player_momentum();
	}
	
	public void transfer_player_momentum() {
		//Game.player.momentum = new Vector2(this.bounce_dir);
		if (this.variant == 0 || this.variant == 3) Game.player.vel = new Vector2(this.bounce_dir);
		if (this.variant == 1 || this.variant == 2) {
			Game.player.momentum = new Vector2(this.bounce_dir);
			Game.player.vel = new Vector2(0, 0);
		}
		
		Game.player.movement.gravity = PlayerMovement.gravity_normal;
	}
	public void start_bouncer() {
		this.nodes = new Vector2[] {new Vector2(this.pos), new Vector2()};
		//  0
		//1   2
		//  3
		//TODO: rework these numbers a bit just in case
		if (this.variant == 0) {
			this.width = width_default;
			this.height = height_default;
			this.bounce_dir = Vector2.mult(Vector2.up, bounce_strength_v);
			this.nodes[1] = new Vector2(this.nodes[0].x, this.nodes[0].y + 2 * LevelEditor.grid_size);
		}
		if (this.variant == 1) {
			this.width = height_default;
			this.height = width_default;
			this.bounce_dir = Vector2.mult(new Vector2(-0.92, 0.4), bounce_strength_h);
			this.nodes[1] = new Vector2(this.nodes[0].x - 2 * LevelEditor.grid_size, this.nodes[0].y);
		}
		if (this.variant == 2) {
			this.width = height_default;
			this.height = width_default;
			this.bounce_dir = Vector2.mult(new Vector2(0.92, 0.4), bounce_strength_h);
			this.nodes[1] = new Vector2(this.nodes[0].x + 2 * LevelEditor.grid_size, this.nodes[0].y);
		}
		if (this.variant == 3) {
			this.width = width_default;
			this.height = height_default;
			this.bounce_dir = Vector2.mult(Vector2.down, bounce_strength_v);
			this.nodes[1] = new Vector2(this.nodes[0].x, this.nodes[0].y - 2 * LevelEditor.grid_size);
		}
		
		this.just_created = false;
	}
	public void generate_sprite(GameObject[] objects, String in) {
		try {
			if (this.variant == 0) this.sprite = ImageIO.read(getClass().getResource(in)).getSubimage(Camera.tile_size, 0, width_default * Camera.tile_size, height_default * Camera.tile_size);
			if (this.variant == 1) this.sprite = ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, height_default * Camera.tile_size, width_default * Camera.tile_size);
			if (this.variant == 2) this.sprite = Utility.flip(ImageIO.read(getClass().getResource(in)).getSubimage(0, 0, height_default * Camera.tile_size, width_default * Camera.tile_size), false, true);
			if (this.variant == 3) this.sprite = Utility.flip(ImageIO.read(getClass().getResource(in)).getSubimage(Camera.tile_size, 0, width_default * Camera.tile_size, height_default * Camera.tile_size), true, false);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("game")) {
			//this.draw(g, pane, xin, yin, location);
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
				this.draw_border(g, pane, xin, yin, location);
			}
		}
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
}
