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
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Camera;

public class Circle extends GameObject{
	public static int default_radius = 3;
	protected double radius;
	public static Color fill_save = new Color(119, 186, 153);
	
	
	
	public Circle() {}
	
	public Circle(double a, double b, double c, double d, String sprite, int id) {
		this.pos = new Vector2(a, b);
		
		this.radius = c;
		
		this.object_handle = (int)d;
		
		this.width = this.radius * 2;
		this.height = this.radius * 2;
		
		this.id = id;
		
		this.sliceable = false;
		
		this.solid = true;
		
		this.sprite_name = sprite;
		
		this.start_nodes();
		this.displaceable = false;
		
		this.fill = fill_save;
	}
	
	//whether you are physically colliding with the platform
	public boolean collide_with(Rectangle in, boolean col_action) {
		return Rectangle.intersect_circle(in, this.pos, this.radius);
	}
	
	public void displace_entity(Rectangle in, int direction) {
		if (!Rectangle.intersect_circle(in, this.pos, this.radius)) return;
		
		Vector2 clamp = new Vector2(Utility.clamp(this.pos.x, in.pos.x - in.width / 2, in.pos.x + in.width / 2), Utility.clamp(this.pos.y, in.pos.y - in.height / 2, in.pos.y + in.height  / 2));
		
		Vector2 dist = Vector2.sub(clamp, this.pos);
		
		Vector2 offset = Vector2.sub(Vector2.scale_to_length(dist, this.radius), dist);
		
		in.pos.add(offset);
	}
	
	//graphics & rendering
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("edit")) {
			this.draw(g, pane, xin, yin, location);
			this.draw_border(g, pane, xin, yin, location);
			this.draw_nodes(g, pane, xin, yin, location);
			//this.nodes[0].draw_node(g, pane, xin, yin, location, Color.magenta);
			//this.nodes[1].draw_node(g, pane, xin, yin, location, Color.magenta);
		}
		if (location.equals("game")) {
			//this.draw(g, pane, xin, yin, location);
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			if (Game.debug_mode) this.draw_border(g, pane, xin, yin, location);
		}
	}
	public boolean on_screen(Vector2 cam_pos) {
		return Rectangle.intersect_circle(new Rectangle(cam_pos.x, cam_pos.y, Start.pane.getWidth() / Camera.pixel_size, Start.pane.getHeight() / Camera.pixel_size), this.pos, this.radius);
	}
	
	public void draw(Graphics g, JPanel in, double xin, double yin, String location) {	
		g.setColor(this.fill);
		g.fillOval((int)(Vector2.converted_pos(this.pos, in, xin, yin, location).x - this.radius), (int)(Vector2.converted_pos(this.pos, in, xin, yin, location).y - this.radius), (int)(this.radius * 2), (int)(this.radius * 2));
	}
	public void draw_border(Graphics g, JPanel in, double xin, double yin, String location) {
		g.setColor(Color.magenta);
		g.drawOval((int)(Vector2.converted_pos(this.pos, in, xin, yin, location).x - this.radius), (int)(Vector2.converted_pos(this.pos, in, xin, yin, location).y - this.radius), (int)(this.radius * 2), (int)(this.radius * 2));
	}
	
	
	public void generate_sprite(GameObject[] objects, String in) {
		BufferedImage source = new BufferedImage(1, 1, 1);
		try {
			source = ImageIO.read(getClass().getResource(in));
		} catch (IOException e) {e.printStackTrace();}
		
		this.sprite = new BufferedImage((int)(this.radius * 2 + 1), (int)(this.radius * 2 + 1), BufferedImage.TYPE_INT_ARGB);
		
		int[] cols = {source.getRGB(source.getWidth() - 1, source.getHeight() - 1),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 2),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 3),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 4),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 5)};
		
		for (int y = 0; y<this.sprite.getHeight(); y++) {
			for (int x = 0; x<this.sprite.getWidth(); x++) {
				double dist = Utility.dist(x, y, this.sprite.getWidth() / 2, this.sprite.getHeight() / 2);
				
				if (dist >= this.radius) continue;
				dist = this.radius - dist;
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
					if (rand > 0.7) this.sprite.setRGB(x, y, cols[3]);
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
	}
	
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.radius *= in;
		this.width *= in;
		this.height *= in;
		this.nodes[0] = Vector2.mult(this.nodes[0], in);
		this.nodes[1] = Vector2.mult(this.nodes[1], in);
	}
	
	//basic
	
	//level editor stuff
}
