package Logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Main.Game;
import Rendering.Camera;

public class Rectangle {
	public Vector2 pos = new Vector2(0, 0); 
	public double width, height;
	public Color fill = new Color(0, 0, 0, 128);
	public BufferedImage sprite;
	
	public boolean start = true;
	
	public Vector2[] nodes = new Vector2[4];
	
	public Rectangle() {}
	public Rectangle(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.sprite = new BufferedImage(1, 1, 1);
	}
	public Rectangle(Vector2 node, Vector2 add) {
		this.pos = new Vector2(node.x + add.x, node.y + add.y);
		this.width = 1;
		this.height = 1;
		this.sprite = new BufferedImage(1, 1, 1);
	}
	//rendering
	public void draw(Graphics g, JPanel in, double xin, double yin, String location) {		
		this.start_nodes();
		
		g.setColor(this.fill);
		
		Vector2 loc = converted_pos(this.pos, in, xin, yin, location);
		g.fillRect((int)(loc.x - this.width / 2), (int)(loc.y - this.height / 2), (int)this.width, (int)this.height);
		
		if (location.equals("edit") || Game.debug_mode) {
			if (location.equals("edit") || Game.debug_borders) this.draw_border(g, in, xin, yin, location);
			if (location.equals("edit")) this.draw_nodes(g, in, xin, yin, location);
		}

	}
	public void draw_with_sprite(Graphics g, JPanel in, double xin, double yin, BufferedImage draw, String location) {
		
		Vector2 loc = converted_pos(this.pos, in, xin, yin, location);
		g.drawImage(draw, (int)(loc.x - draw.getWidth() / 2), (int)(loc.y - draw.getHeight() / 2), null);
	}
	public void draw_border(Graphics g, JPanel in, double xin, double yin, String location) {
		g.setColor(Color.magenta);
		
		Vector2 loc = converted_pos(this.pos, in, xin, yin, location);
		g.drawRect((int)(loc.x - this.width / 2), (int)(loc.y - this.height / 2), (int)this.width, (int)this.height);
	}
	public static Vector2 converted_pos(Vector2 pos, JPanel in, double xin, double yin, String location) {
		JPanel pane = in;
		
		if (location.equals("edit")) pane = rescale(in, 1);
		else if (location.equals("game")) pane = rescale(in, (double)1 / Camera.pixel_size);
		
		return new Vector2((int)(pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight()/2 - pos.y + yin));
	}
	public void draw_nodes(Graphics g, JPanel pane, double xpos, double ypos, String location) {
		for (Vector2 temp: this.nodes) temp.draw_node(g, pane, xpos, ypos, location, Color.magenta);
	}
	public boolean on_screen(Vector2 cam_pos) {
		return Rectangle.intersect(this, new Rectangle(cam_pos.x, cam_pos.y, Game.pane.getWidth() / Camera.pixel_size, Game.pane.getHeight() / Camera.pixel_size));
	}
	
	//logic *important*
	public static boolean intersect(Rectangle a, Rectangle b) {
		return (a.pos.x - a.width/2 < b.pos.x + b.width/2 && a.pos.x + a.width/2 > b.pos.x - b.width/2 && a.pos.y - a.height/2 < b.pos.y + b.height/2 && a.pos.y + a.height/2 > b.pos.y - b.height/2);
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
		Vector2 temp = new Vector2(Utility.clamp(center.x, in.pos.x - in.width / 2, in.pos.x + in.width / 2), Utility.clamp(center.y, in.pos.y - in.height / 2, in.pos.y + in.height  / 2));
		return Vector2.dist(temp, center) < radius;
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
		clip_nodes(grid_size);
		update_dimensions();
	}
	public void move(int grid_size) {
		
		this.nodes[1] = new Vector2(this.pos.x - this.width / 2, this.pos.y + this.width / 2);
		
		this.nodes[1].clip_node(grid_size);
		
		this.nodes[0] = new Vector2(this.nodes[1].x + this.width, this.nodes[1].y);
		this.nodes[2] = new Vector2(this.nodes[1].x, this.nodes[1].y - this.height);
		this.nodes[3] = new Vector2(this.nodes[1].x + this.width, this.nodes[1].y - this.height);
		
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
		Vector2[] temp = {new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2), 
				new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2),
				new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2),
				new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2)};
		//System.out.println("rect " + this.nodes.length);
		this.nodes = temp;
		start = false;
	}
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
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
