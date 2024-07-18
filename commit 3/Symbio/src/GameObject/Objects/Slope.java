package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Line;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Player;
import Rendering.Camera;

public class Slope extends GameObject{
	Vector2 a, b;
	Vector2 a_offset, b_offset; //level editor
	
	public Slope() {}
	
	public Slope(Vector2 a, Vector2 b) {
		this.a = a;
		this.b = b;
		
		this.start_nodes();
		this.displaceable = false;
	}
	
	public Slope(double a, double b, double c, double d, int id) {
		this.a = new Vector2(a, b);
		this.b = new Vector2(c, d);
		
		this.pos = new Vector2((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2);
		this.width = Math.abs(this.a.x - this.b.x);
		this.height = Math.abs(this.a.y - this.b.y);
		
		this.id = id;
		
		this.sprite_source = "/platform_textures/mouth_sprite_sheet.png";
				
		this.start_nodes();
		
		this.sliceable = false;
		
		this.solid = false;
		this.displaceable = false;
	}
	//whether you are physically colliding with the platform
	public boolean collide_with(Player in) {
		return Rectangle.intersect_line(in, this.a, this.b);
	}
	//whether the game should use your collision or ignore it for you to walk on stuff
	public boolean collide_with(Rectangle in, boolean player) {
		return Rectangle.intersect_line(in, this.a, this.b);
	}
	
	//rendering
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		//draw_object(Graphics g, JPanel pane, double xin, double yin, String location)
		if (location.equals("edit")) {
			this.draw(g, pane, xin, yin, location);
			new Line(this.a, this.b).draw_line(g, Color.magenta, pane, xin, yin, location);
			this.a.draw_node(g, pane, xin, yin, location, Color.green);
			this.b.draw_node(g, pane, xin, yin, location, Color.red);
		}
		if (location.equals("game")) {
			new Rectangle((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2, 0, 0).draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) 
				new Line(this.a, this.b).draw_line(g, Color.magenta, pane, xin, yin, location);
		}
	}
	public boolean on_screen(Vector2 cam_pos) {
		return Rectangle.intersect(new Rectangle(cam_pos.x, cam_pos.y, Game.pane.getWidth() / Camera.pixel_size, Game.pane.getHeight() / Camera.pixel_size), new Rectangle((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2, Math.abs(this.a.x - this.b.x), Math.abs(this.a.y - this.b.y)));
	}
	
	public void generate_sprite(GameObject[] objects, String in) {
		//TODO:refactor code to take into account the two ends of the line and to generate sprite accordingly
		BufferedImage source = new BufferedImage(1, 1, 1);
		try {
			source = ImageIO.read(getClass().getResource(in));
		} catch (IOException e) {e.printStackTrace();}
		
		int[] cols = {source.getRGB(source.getWidth() - 1, source.getHeight() - 1),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 2),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 3),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 4),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 5)};
		
		int width = (int)Math.abs(this.a.x - this.b.x);
		int height = (int)Math.abs(this.a.y - this.b.y);
		
		Line line = new Line(new Vector2(0, height - 1), new Vector2(width - 1, 0));
		
		this.sprite = new BufferedImage(Math.max(1, width), Math.max(1, height), BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0; y<height; y++) {
			for (int x = 0; x<width - (double) width / height * y; x++) {
				Vector2 temp = Line.find_node_on_line(line, new Vector2(x, y));
				
				double dist = Utility.dist(x, y, temp.x, temp.y);
				double rand = Math.random();
				
				if (dist < 2) {
					if (rand <= 0.8) this.sprite.setRGB(x, y, cols[0]);
					if (rand > 0.8) this.sprite.setRGB(x, y, cols[1]);
				}
				else if (dist < 4) {
					if (rand <= 0.75) this.sprite.setRGB(x, y, cols[1]);
					if (rand > 0.75) this.sprite.setRGB(x, y, cols[2]);
				}
				else if (dist < 5) {
					if (rand <= 0.1) this.sprite.setRGB(x, y, cols[1]);
					if (rand > 0.1 && rand <= 0.8) this.sprite.setRGB(x, y, cols[2]);
					if (rand > 0.8) this.sprite.setRGB(x, y, cols[3]);
				}
				else if (dist < 6) {
					if (rand <= 0.6) this.sprite.setRGB(x, y, cols[3]);
					if (rand > 0.6) this.sprite.setRGB(x, y, cols[4]);
				}
				else if (dist >= 6) {
					this.sprite.setRGB(x, y, cols[4]);
				}
			}
		}
		//variant 0 ==
		//1 1 1 1 A
		//1 1 1
		//B
		
		//variant 1 ==
		//B 1 1 1 1
		//    1 1 1
		//        A
		
		//variant 2 ==
		//A
		//1 1 1
		//1 1 1 1 B
		
		//variant 3 == 
		//        B
		//    1 1 1
		//A 1 1 1 1
		
		if (this.a.x > this.b.x && this.a.y < this.b.y) this.sprite = Utility.flip(this.sprite, false, true);
		if (this.a.x < this.b.x && this.a.y > this.b.y) this.sprite = Utility.flip(this.sprite, true, false);
		if (this.a.x < this.b.x && this.a.y < this.b.y) this.sprite = Utility.flip(this.sprite, true, true);
	}
	
	//basic
	public String toString() {
		return "slope " + this.a.x + " " + this.a.y + " " + this.b.x + " " + this.b.y + " ";
	}
	public String give_class() {
		return "slope";
	}
	
	//level editor stuff
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		clip_nodes(grid_size);
		
		this.a = this.nodes[0];
		this.b = this.nodes[1];
		
		this.update_dimensions();
		
	}
	public void start_nodes() {
		Vector2[] temp = {this.a, this.b};
		this.nodes = temp;
	}
	
	public void update_dimensions() {		
		this.pos = Vector2.mult(Vector2.add(this.a, this.b), 0.5);
		
		this.width = Math.abs(this.a.x - this.b.x);
		this.height = Math.abs(this.a.y - this.b.y);
	}
	
	public static Slope default_slope(Vector2 loc) {
		
		Slope out = new Slope(new Vector2(loc.x - LevelEditor.grid_size, loc.y - LevelEditor.grid_size), new Vector2(loc.x + LevelEditor.grid_size, loc.y + LevelEditor.grid_size));
		
		out.pos = new Vector2(loc);
		
		out.update_dimensions();
		
		return out;
	}
	
	public void move(int grid_size) {		
		Vector2 temp = Vector2.mult(Vector2.add(this.a, this.b), 0.5);
		
		Vector2 off_a = Vector2.add(this.a, Vector2.neg(temp));
		Vector2 off_b = Vector2.add(this.b, Vector2.neg(temp));
		
		
		this.a = Vector2.add(this.pos, off_a);
		this.a.clip_node(LevelEditor.grid_size);
		
		this.pos = Vector2.add(this.a, Vector2.neg(off_a));
		
		this.b = Vector2.add(this.pos, off_b);
		
		this.nodes[0] = new Vector2(this.a);
		this.nodes[1] = new Vector2(this.b);
		
		this.update_dimensions();
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
}
