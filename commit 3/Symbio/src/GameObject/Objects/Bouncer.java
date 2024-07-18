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
import Rendering.Camera;

public class Bouncer extends GameObject{
	public Vector2 dir;
	Vector2 bounce_dir;
	public static int bounce_strength_h = 45;
	public static int bounce_strength_v = 35;
	
	public int variant;
	
	public boolean just_created = true;
	
	public Bouncer() {}
	public Bouncer(Vector2 pos) {
		this.pos = new Vector2(pos);
		
		this.width = 4 * LevelEditor.grid_size;
		this.height = 1 * LevelEditor.grid_size;
		
		this.start_nodes();
		
		this.fill = new Color(127, 63, 0);
	}
	
	public Bouncer(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.variant = (int)c;
		this.id = (int)d;
		this.fill = new Color(127, 63, 0);
		this.sprite_source = "/plat_ent_textures/bounce.png";
		
		this.start_bouncer();
		
		this.solid = false;
		
		this.sliceable = false;
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
		this.nodes = new Vector2[] {new Vector2(this.pos), new Vector2()};
		//  0
		//1   2
		//  3
		//TODO: rework these numbers a bit just in case
		if (this.variant == 0) {
			this.width = 4;
			this.height = 1;
			this.bounce_dir = Vector2.mult(Vector2.up, bounce_strength_v);
			this.nodes[1] = new Vector2(this.nodes[0].x, this.nodes[0].y + 2 * LevelEditor.grid_size);
		}
		if (this.variant == 1) {
			this.width = 1;
			this.height = 4;
			this.bounce_dir = Vector2.mult(new Vector2(-0.94, 0.33), bounce_strength_h);
			this.nodes[1] = new Vector2(this.nodes[0].x - 2 * LevelEditor.grid_size, this.nodes[0].y);
		}
		if (this.variant == 2) {
			this.width = 1;
			this.height = 4;
			this.bounce_dir = Vector2.mult(new Vector2(0.94, 0.33), bounce_strength_h);
			this.nodes[1] = new Vector2(this.nodes[0].x + 2 * LevelEditor.grid_size, this.nodes[0].y);
		}
		if (this.variant == 3) {
			this.width = 4;
			this.height = 1;
			this.bounce_dir = Vector2.mult(Vector2.down, bounce_strength_v);
			this.nodes[1] = new Vector2(this.nodes[0].x, this.nodes[0].y - 2 * LevelEditor.grid_size);
		}
		
		this.just_created = false;
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
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("game")) {
			this.draw(g, pane, xin, yin, location);
			/*this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
				this.draw_border(g, pane, xin, yin, location);
			}*/
		}
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
	
	public void start_nodes() {
		if (!just_created) return;
		
		this.nodes = new Vector2[] {new Vector2(this.pos), new Vector2(this.pos.x, this.pos.y + 2 * LevelEditor.grid_size)};
		this.clip_nodes(LevelEditor.grid_size);
		
		just_created = false;
	}
	
	public static Bouncer default_bouncer(Vector2 loc) {
		return new Bouncer(loc);
	}
	public void update_dimensions() {
		int var = this.give_variant();
		
		if (var == 0 || var == 3) {
			this.width = 4 * LevelEditor.grid_size;
			this.height = LevelEditor.grid_size;
		}
		if (var == 1 || var == 2) {
			this.width = LevelEditor.grid_size;
			this.height = 4 * LevelEditor.grid_size;
		}
	}
	public void update_nodes(Vector2 in, int place, int grid_size) {
		System.out.println("PLACEEEEE: " + place);
		if (place == 0) {
			this.update_dimensions();
			Vector2 temp = new Vector2(in.x - this.width / 2, in.y - (double)this.height / 2);
			temp.clip_node(grid_size);
			
			this.pos = new Vector2(temp.x + this.width / 2, temp.y + (double)this.height / 2);
			
			temp = Vector2.add(this.nodes[1], Vector2.neg(this.nodes[0]));
			this.nodes[0] = new Vector2(this.pos);
			this.nodes[1] = Vector2.add(this.nodes[0], temp);
		}
		
		if (place == 1) {
			this.nodes[1] = new Vector2(in);
			this.nodes[1].clip_node(grid_size);
			this.update_dimensions();
		}
	}
	public void move(int grid_size) {
		this.update_nodes(this.pos, 0, grid_size);
	}
	public int give_variant() {
		Vector2 temp = Vector2.add(this.nodes[1], Vector2.neg(this.nodes[0]));
		
		if (Math.abs(temp.y) >= Math.abs(temp.x)) {
			if (temp.y >= 0) return 0;
			if (temp.y < 0) return 3;
		}
		
		if (Math.abs(temp.y) < Math.abs(temp.x)) {
			if (temp.x >= 0) return 2;
			if (temp.x < 0) return 1;
		}
		
		return 0;
	}
	public String toString() {
		return "bouncer " + this.pos.x + " " + this.pos.y + " " + this.give_variant() + " ";
	}
	public String give_class() {
		return "bouncer";
	}
}
