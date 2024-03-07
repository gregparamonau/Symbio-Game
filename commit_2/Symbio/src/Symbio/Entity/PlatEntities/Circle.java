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

public class Circle extends PlatEntity{
	double radius;
	
	public Circle() {}
	
	public Circle(double a, double b, double c, int id) {
		this.pos = new Vector2(a, b);
		
		this.radius = c;
		
		this.width = this.radius * 2;
		this.height = this.radius * 2;
		
		this.id = id;
		
		this.sliceable = false;
		
		this.sprite_source = "/platform_textures/plat_0.png";
		
		this.start_nodes();
	}
	
	//whether you are physically colliding with the platform
	public boolean collide_with(Player in) {
		return Rectangle.intersect_circle(new Rectangle(in), this.pos, this.radius);
	}
	//whether the game should use your collision or ignore it for you to walk on stuff
	public boolean collide_with(Rectangle in, boolean player) {
		return Rectangle.intersect_circle(in, this.pos, this.radius);
	}
	
	//graphics & rendering
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
		if (location.equals("game")) {
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) this.draw_border(g, pane, xin, yin, location);
		}
	}
	public boolean on_screen(Vector2 cam_pos) {
		return Rectangle.intersect_circle(new Rectangle(cam_pos.x, cam_pos.y, Game.pane.getWidth() / Camera.pixel_size, Game.pane.getHeight() / Camera.pixel_size), this.pos, this.radius);
	}
	
	public void draw(Graphics g, JPanel in, double xin, double yin, String location) {	
		g.setColor(new Color(119, 186, 153));
		g.fillOval((int)(converted_pos(this.pos, in, xin, yin, location).x - this.radius), (int)(converted_pos(this.pos, in, xin, yin, location).y - this.radius), (int)(this.radius * 2), (int)(this.radius * 2));
	}
	public void draw_border(Graphics g, JPanel in, double xin, double yin, String location) {
		g.setColor(Color.magenta);
		g.drawOval((int)(converted_pos(this.pos, in, xin, yin, location).x - this.radius), (int)(converted_pos(this.pos, in, xin, yin, location).y - this.radius), (int)(this.radius * 2), (int)(this.radius * 2));
	}
	
	
	public void start_sprites(String in) {
		BufferedImage source = new BufferedImage(1, 1, 1);
		try {
			source = ImageIO.read(getClass().getResource(in));
		} catch (IOException e) {e.printStackTrace();}
		
		this.sprite = new BufferedImage((int)(this.radius * 2 + 1), (int)(this.radius * 2 + 1), BufferedImage.TYPE_INT_ARGB);
		
		int[] cols = {source.getRGB(1, 0),
				source.getRGB(2, 0),
				source.getRGB(3, 2),
				source.getRGB(5, 3),
				source.getRGB(25, 1),
				source.getRGB(25, 6)};
		
		for (int y = 0; y<this.sprite.getHeight(); y++) {
			for (int x = 0; x<this.sprite.getWidth(); x++) {
				double dist = Utility.dist(x, y, this.sprite.getWidth() / 2, this.sprite.getHeight() / 2);
				double rand = Math.random();
				
				if (dist < this.radius + 1 && dist >= this.radius) {
					if (rand <= 0.9) this.sprite.setRGB(x, y, cols[0]);
					if (rand > 0.9) this.sprite.setRGB(x, y, cols[1]);
				}
				else if (dist < this.radius && dist >= this.radius - 1) {
					if (rand <= 0.25) this.sprite.setRGB(x, y, cols[0]);
					if (rand > 0.25) this.sprite.setRGB(x, y, cols[1]);
				}
				else if (dist < this.radius - 1 && dist >= this.radius - 3) {
					if (rand <= 0.25) this.sprite.setRGB(x, y, cols[1]);
					if (rand > 0.25 && rand <= 0.75) this.sprite.setRGB(x, y, cols[2]);
					if (rand > 0.75) this.sprite.setRGB(x, y, cols[3]);
				}
				else if (dist < this.radius - 3  && dist >= 0) {
					if (rand <= 0.8) this.sprite.setRGB(x, y, cols[3]);
					if (rand > 0.8 && rand <= 0.9) this.sprite.setRGB(x, y, cols[4]);
					if (rand > 0.9) this.sprite.setRGB(x, y, cols[5]);
				}
			}
		}
	}
	
	//basic
	public String toString() {
		return "circle " + this.pos.x + " " + this.pos.y + " " + this.radius + " ";
	}
	public String give_class() {
		return "circle";
	}
	
	//level editor stuff
	public void update_nodes(Vector2 in, int place) {
		this.nodes[place] = in;
		clip_nodes();
		
		this.pos = this.nodes[0];
	}
	public void clip_nodes() {
		for (int x = 0; x<this.nodes.length; x++) {
			this.nodes[x].clip_node(LevelCreator.grid_size);
		}
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {this.pos};
		this.nodes = temp;
		start = false;
	}
	
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.radius *= in;
		this.width *= in;
		this.height *= in;
		this.nodes[0] = Vector2.mult(this.nodes[0], in);
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
		
		fields[1] = new JTextField(LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]].radius / LevelCreator.grid_size + "");
		fields[1].setBounds(50, 100, 200, 25);
		fields[1].setVisible(true);
		temp.add(fields[1]);
		
		labels[1] = new JLabel("radius (tiles)");
		labels[1].setBounds(300, 100, 100, 50);
		labels[1].setVisible(true);
		temp.add(labels[1]);
	}
}
