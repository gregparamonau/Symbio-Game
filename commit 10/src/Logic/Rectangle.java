package Logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import LevelEdit.LevelEditor;
import Main.Game;
import Main.Start;
import Rendering.Animation;
import Rendering.Camera;

public class Rectangle {
	public Vector2 pos = new Vector2(0, 0); 
	public double width, height;
	public Color fill = new Color(0, 0, 0, 128);
	public BufferedImage sprite;
	public Animation anim;
	
	boolean rotated = false;
	
	public boolean start = true;
	
	public Vector2[] nodes = new Vector2[4];
	public Vector2[] save_nodes = new Vector2[4];	
	
	public Rectangle() {}
	public Rectangle(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.sprite = new BufferedImage(1, 1, 1);
		this.start_nodes();
	}
	public Rectangle(Rectangle in) {
		this.pos = new Vector2(in.pos);
		this.width = in.width;
		this.height = in.height;
		this.sprite = new BufferedImage(1, 1, 1);
		this.start_nodes();
	}
	/*public Rectangle(Vector2 node, Vector2 add) {
		this.pos = new Vector2(node.x + add.x, node.y + add.y);
		this.width = 1;
		this.height = 1;
		this.sprite = new BufferedImage(1, 1, 1);
		this.start_nodes();
	}*/
	public Rectangle(Vector2 a, Vector2 b) {
		this.pos = Vector2.mult(Vector2.add(a, b), 0.5);
		
		this.width = Math.abs(a.x - b.x);
		this.height = Math.abs(a.y - b.y);
	}
	//rendering
	public void draw(Graphics g, JPanel in, double xin, double yin, String location) {		
		this.start_nodes();
		
		g.setColor(this.fill);
		
		Vector2 loc = Vector2.converted_pos(this.pos, in, xin, yin, location);
		g.fillRect((int)(loc.x - this.width / 2), (int)(loc.y - this.height / 2), (int)this.width, (int)this.height);
		
		/*if (location.equals("edit") || Game.debug_mode) {
			this.draw_border(g, in, xin, yin, location);
			if (location.equals("edit")) this.draw_nodes(g, in, xin, yin, location);
		}*/

	}
	public void draw_with_sprite(Graphics g, JPanel in, double xin, double yin, BufferedImage draw, String location) {
		
		Vector2 loc = Vector2.converted_pos(this.pos, in, xin, yin, location);
		
		g.drawImage(draw, (int)Math.floor(loc.x - draw.getWidth() / 2), (int)Math.floor(loc.y - draw.getHeight() / 2), null);
		
		//if (Game.debug_mode) this.draw_border(g, in, xin, yin, location);
	}
	public void draw_border(Graphics g, JPanel in, double xin, double yin, String location) {
		g.setColor(Color.magenta);
		
		Vector2 loc = Vector2.converted_pos(this.pos, in, xin, yin, location);
		g.drawRect((int)(loc.x - this.width / 2), (int)(loc.y - this.height / 2), (int)this.width, (int)this.height);
	}
	public void draw_nodes(Graphics g, JPanel pane, double xpos, double ypos, String location) {
		for (Vector2 temp: this.nodes) temp.draw_node(g, pane, xpos, ypos, location, Color.magenta);
	}
	public boolean on_screen(Vector2 cam_pos) {
		return Rectangle.intersect(this, new Rectangle(cam_pos.x, cam_pos.y, Start.pane.getWidth() / Camera.pixel_size, Start.pane.getHeight() / Camera.pixel_size));
	}
	
	//logic *important*
	public static boolean intersect(Rectangle a, Rectangle b) {
		return (a.pos.x - a.width/2 < b.pos.x + b.width/2 && a.pos.x + a.width/2 > b.pos.x - b.width/2 && a.pos.y - a.height/2 < b.pos.y + b.height/2 && a.pos.y + a.height/2 > b.pos.y - b.height/2);
	}
	
	public static boolean inside_of(Rectangle a, Rectangle b) {
		//a is inside b
		return (a.pos.x - a.width / 2 > b.pos.x - b.width / 2 && a.pos.x + a.width / 2 < b.pos.x + b.width / 2 && a.pos.y - a.height / 2 > b.pos.y - b.height / 2 && a.pos.y + a.height / 2 < b.pos.y + b.height / 2);
	}
	public static Rectangle intersect_area(Rectangle a, Rectangle b) {
		return new Rectangle((Math.min(a.pos.x + a.width/2, b.pos.x + b.width/2) - Math.max(a.pos.x - a.width/2, b.pos.x - b.width/2))/2, (Math.min(a.pos.y + a.height/2, b.pos.y + b.height/2) - Math.max(a.pos.y - a.height/2, b.pos.y - b.height/2))/2, (int)(Math.min(a.pos.x + a.width/2, b.pos.x + b.width/2) - Math.max(a.pos.x - a.width/2, b.pos.x - b.width/2)), (int)(Math.min(a.pos.y + a.height/2, b.pos.y + b.height/2) - Math.max(a.pos.y - a.height/2, b.pos.y - b.height/2)));
	}
	public static boolean intersect_line(Rectangle in, Vector2 a, Vector2 b) {
		//preliminary checks
		Rectangle temp = new Rectangle((a.x + b.x) / 2, (a.y + b.y) / 2, Math.abs(a.x - b.x), Math.abs(a.y - b.y));
		if (!intersect(in, temp)) return false;
		if (a.x == b.x || a.y == b.y) return intersect(in, temp);
		
		//if (Line.line_intersect(new Line(a, b), new Line(new Vector2(in.pos.x - in.width / 2, in.pos.y + in.height / 2), new Vector2(in.pos.x + in.width / 2, in.pos.y - in.height / 2)))) return true;
		//if (Line.line_intersect(new Line(a, b), new Line(new Vector2(in.pos.x - in.width / 2, in.pos.y - in.height / 2), new Vector2(in.pos.x + in.width / 2, in.pos.y + in.height / 2)))) return true;


		//full on slope detection
		//if (Line.line_intersect(new Line(a, b), new Line(new Vector2(in.pos.x + in.width / 2, in.pos.y - in.height / 2), new Vector2(in.pos.x - in.width / 2, in.pos.y - in.height / 2)))) return true;
		//if (Line.line_intersect(new Line(a, b), new Line(new Vector2(in.pos.x - in.width / 2, in.pos.y + in.height / 2), new Vector2(in.pos.x + in.width / 2, in.pos.y + in.height / 2)))) return true;
		if (Line.line_intersect(new Line(a, b), new Line(new Vector2(in.pos.x - in.width / 2, in.pos.y - in.height / 2), new Vector2(in.pos.x - in.width / 2, in.pos.y + in.height / 2)))) return true;
		if (Line.line_intersect(new Line(a, b), new Line(new Vector2(in.pos.x + in.width / 2, in.pos.y + in.height / 2), new Vector2(in.pos.x + in.width / 2, in.pos.y - in.height / 2)))) return true;
		
		return false;
	}
	public static boolean intersect_circle(Rectangle in, Vector2 center, double radius) {
		Vector2 temp = in.clamp_within(center);
		return Vector2.dist(temp, center) < radius;
	}
	
	public Vector2 clamp_within(Vector2 in) {
		return new Vector2(Utility.clamp(in.x, this.pos.x - this.width / 2, this.pos.x + this.width / 2), Utility.clamp(in.y, this.pos.y - this.height / 2, this.pos.y + this.height / 2));
	}
	
	public boolean within(Vector2 in) {
		return (this.pos.x - this.width / 2 < in.x && this.pos.x + this.width / 2 > in.x && this.pos.y - this.height / 2 < in.y && this.pos.y + this.height / 2 > in.y);
	}
	public void expand(Rectangle in) {
		double max_x = Math.max(this.pos.x + this.width / 2, in.pos.x + in.width / 2);
		double min_x = Math.min(this.pos.x - this.width / 2, in.pos.x - in.width / 2);
		
		double max_y = Math.max(this.pos.y + this.height / 2, in.pos.y + in.height / 2);
		double min_y = Math.min(this.pos.y - this.height / 2, in.pos.y - in.height / 2);
		
		this.pos.x = (max_x + min_x) / 2;
		this.pos.y = (max_y + min_y) / 2;
		this.width = max_x - min_x;
		this.height = max_y - min_y;
	}
	
	public void expand(Vector2 in) {
		if (this.within(in)) return;
		
		double x_min = Math.min(in.x, this.pos.x - this.width / 2);
		double x_max = Math.max(in.x, this.pos.x + this.width / 2);
		
		double y_min = Math.min(in.y, this.pos.y - this.height / 2);
		double y_max = Math.max(in.y, this.pos.y + this.height / 2);
		
		this.pos = new Vector2((x_max + x_min) / 2, (y_max + y_min) / 2);
		this.width = x_max - x_min;
		this.height = y_max - y_min;
		
		System.out.println("VEC: " + in + " RECT: " + this);
	}
	
	public double area() {
		return this.width * this.height;
	}
	
	//leveleditor stuff
	//public void update_nodes(Vector2 in, int place, int grid_size)
	//public void move(int grid_size)
	//public void update_dimensions()
	//public void start_nodes()
	//public void scale(double in)
	
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		switch(place) {
		case 0:
			this.nodes[3].x = in.x;
			this.nodes[1].y = in.y;
			break;
		case 1:
			this.nodes[0].y = in.y;
			this.nodes[2].x = in.x;
			break;
		case 2:
			this.nodes[1].x = in.x;
			this.nodes[3].y = in.y;
			break;
		case 3:
			this.nodes[2].y = in.y;
			this.nodes[0].x = in.x;
			break;
		}
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}
	public void move(int grid_size) {
		if (!LevelEditor.mouse_pressed) return;
		
		this.nodes = Vector2.move_arr(this.save_nodes, Vector2.sub(LevelEditor.mouse_pos, LevelEditor.click));
		this.clip_nodes(grid_size);
		
		this.pos = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[2]), 0.5);
	
	}
	public void clip_nodes(int grid_size) {
		for (int x = 0; x<this.nodes.length; x++) {
			this.nodes[x].clip_node(grid_size);
		}
	}
	public void update_dimensions() {
		
		
		this.pos.x = (this.nodes[0].x + this.nodes[2].x) / 2;
		this.pos.y = (this.nodes[0].y + this.nodes[2].y) / 2;
		this.width = Math.abs(this.nodes[2].x - this.nodes[0].x);
		this.height = Math.abs(this.nodes[2].y - this.nodes[0].y);
		
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {
				new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2), 
				new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2),
				new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2),
				new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2)
		};
		//System.out.println("rect " + this.nodes.length);
		this.nodes = temp;
		start = false;
	}
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
		if (this.nodes[0] != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	public static JPanel rescale(JPanel in, double scale) {
		JPanel out = new JPanel();
		out.setSize((int)(in.getWidth() * scale), (int)(in.getHeight() * scale));
		return out;
	}
	public String toString() {
		return (int)this.pos.x + " " + (int)this.pos.y + " " + this.width + " " + this.height + " ";
	}
}
