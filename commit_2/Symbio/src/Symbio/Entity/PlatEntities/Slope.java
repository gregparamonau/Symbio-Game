package Symbio.Entity.PlatEntities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import LevelEdit.LevelCreator;
import Symbio.Game;
import Symbio.Player;
import Symbio.Entity.PlatEntity;
import Symbio.Logic.Line;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class Slope extends PlatEntity{
	Vector2 a, b;
	
	public Slope() {}
	
	public Slope(double a, double b, double c, double d, int variant, int id) {
		this.a = new Vector2(a, b);
		this.b = new Vector2(c, d);
		
		this.pos = new Vector2((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2);
		this.width = Math.abs(this.a.x - this.b.x);
		this.height = Math.abs(this.a.y - this.b.y);
		
		this.id = id;
		
		this.sprite_source = "/platform_textures/plat_0.png";
		
		this.variant = variant;
		
		this.start_nodes();
		
		this.sliceable = false;
	}
	//whether you are physically colliding with the platform
	public boolean collide_with(Player in) {
		return Rectangle.intersect_line(new Rectangle(in), this.a, this.b);
	}
	//whether the game should use your collision or ignore it for you to walk on stuff
	public boolean collide_with(Rectangle in, boolean player) {
		return Rectangle.intersect_line(in, this.a, this.b);
	}
	
	//rendering
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {
		if (location.equals("edit")) {
			new Line(this.a, this.b).draw_line(g, Color.magenta, pane, xin, yin, location, 1);
			this.draw_nodes(g, pane, xin, yin, location);
		}
		if (location.equals("game")) {
			new Rectangle((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2, 0, 0).draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) new Line(this.a, this.b).draw_line(g, Color.magenta, pane, xin, yin, location, 1);
		}
	}
	public boolean on_screen(Vector2 cam_pos) {
		return Rectangle.intersect(new Rectangle(cam_pos.x, cam_pos.y, Game.pane.getWidth() / Camera.pixel_size, Game.pane.getHeight() / Camera.pixel_size), new Rectangle((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2, Math.abs(this.a.x - this.b.x), Math.abs(this.a.y - this.b.y)));
	}
	
	public void start_sprites(String in) {
		BufferedImage source = new BufferedImage(1, 1, 1);
		try {
			source = ImageIO.read(getClass().getResource(in));
		} catch (IOException e) {e.printStackTrace();}
		
		int[] cols = {source.getRGB(1, 0),
				source.getRGB(2, 0),
				source.getRGB(3, 2),
				source.getRGB(5, 3),
				source.getRGB(25, 1),
				source.getRGB(25, 6)};
		
		int width = (int)Math.abs(this.a.x - this.b.x);
		int height = (int)Math.abs(this.a.y - this.b.y);
		
		Line line = new Line(new Vector2(0, height - 1), new Vector2(width - 1, 0));
		
		this.sprite = new BufferedImage(Math.max(1, width), Math.max(1, height), BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0; y<height; y++) {
			for (int x = 0; x<width - (double) width / height * y; x++) {
				Vector2 temp = Line.find_node_on_line(line, new Vector2(x, y));
				
				double dist = Utility.dist(x, y, temp.x, temp.y);
				double rand = Math.random();
				
				if (dist < 1) {
					if (rand <= 0.9) this.sprite.setRGB(x, y, cols[0]);
					if (rand > 0.9) this.sprite.setRGB(x, y, cols[1]);
				}
				else if (dist < 2) {
					if (rand <= 0.25) this.sprite.setRGB(x, y, cols[0]);
					if (rand > 0.25) this.sprite.setRGB(x, y, cols[1]);
				}
				else if (dist < 3) {
					if (rand <= 0.25) this.sprite.setRGB(x, y, cols[1]);
					if (rand > 0.25 && rand <= 0.75) this.sprite.setRGB(x, y, cols[2]);
					if (rand > 0.75) this.sprite.setRGB(x, y, cols[3]);
				}
				else if (dist >= 3) {
					if (rand <= 0.8) this.sprite.setRGB(x, y, cols[3]);
					if (rand > 0.8 && rand <= 0.9) this.sprite.setRGB(x, y, cols[4]);
					if (rand > 0.9) this.sprite.setRGB(x, y, cols[5]);
				}
			}
		}
		//variant 0 ==
		//1 1 1 1 1
		//1 1 1
		//1
		
		//variant 1 ==
		//1 1 1 1 1
		//    1 1 1
		//        1
		
		//variant 2 ==
		//1
		//1 1 1
		//1 1 1 1 1
		
		//variant 3 == 
		//        1
		//    1 1 1
		//1 1 1 1 1
		
		if (this.variant == 1) this.sprite = Utility.flip(this.sprite, false, true);
		if (this.variant == 2) this.sprite = Utility.flip(this.sprite, true, false);
		if (this.variant == 3) this.sprite = Utility.flip(this.sprite, true, true);
	}
	
	//basic
	public String toString() {
		return "slope " + this.a.x + " " + this.a.y + " " + this.b.x + " " + this.b.y + " " + this.variant + " ";
	}
	public String give_class() {
		return "slope";
	}
	
	//level editor stuff
	public void update_nodes(Vector2 in, int place) {
		this.nodes[place] = in;
		clip_nodes();
		
		this.a = this.nodes[0];
		this.b = this.nodes[1];
	}
	public void clip_nodes() {
		for (int x = 0; x<this.nodes.length; x++) {
			this.nodes[x].clip_node(LevelCreator.grid_size);
		}
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {this.a, this.b};
		this.nodes = temp;
		start = false;
	}
	
	public void scale(double in) {
		this.a = Vector2.mult(this.a, in);
		this.b = Vector2.mult(this.b, in);
		
		this.nodes[0] = Vector2.mult(this.nodes[0], in);
		this.nodes[1] = Vector2.mult(this.nodes[1], in);
		
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
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
