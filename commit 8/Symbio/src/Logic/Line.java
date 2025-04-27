package Logic;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Rendering.Camera;

public class Line {
	boolean vertical = false, horizontal = false;
	public Vector2 a, b;
	
	public Line() {}
	public Line(Vector2 a, Vector2 b) {
		this.a = a;
		this.b = b;
		
		this.vertical = (this.a.x == this.b.x);
	}
	
	public String toString() {
		return (this.a.x + " " + this.a.y + " " + this.b.x + " " + this.b.y);
	}
	
	public static Vector2 line_intersect(Line a, Line b, int q) {
		boolean vertical = (b.a.x == b.b.x);
		double m1 = (a.a.y - a.b.y) / (a.a.x - a.b.x);
		double b1 = (a.a.y - m1 * a.a.x);
		
		double m2 = (b.a.y - b.b.y) / (b.a.x - b.b.x);
		double b2 = (b.a.y - m2 * b.a.x);
		
		double x_node = (b2 - b1) / (m1 - m2);
		double y_node = (m1 * x_node) + b1;
		
		return new Vector2(x_node, y_node);
		
	}
	
	public static boolean line_intersect(Line a, Line b) {
		boolean vertical = (b.a.x == b.b.x);
		double m1 = (a.a.y - a.b.y) / (a.a.x - a.b.x);
		double b1 = (a.a.y - m1 * a.a.x);
		
		if (vertical) {
			//double x_node = b.a.x;
			double y_node = m1 * b.a.x + b1;
			return (Math.min(b.a.y, b.b.y) <= y_node && y_node <= Math.max(b.a.y, b.b.y) && Math.min(a.a.y, a.b.y) <= y_node && y_node <= Math.max(a.a.y, a.b.y));
		}
		double m2 = (b.a.y - b.b.y) / (b.a.x - b.b.x);
		double b2 = (b.a.y - m2 * b.a.x);
		
		double x_node = (b2 - b1) / (m1 - m2);
		//double y_node = (m1 * x_node) + b1;
		
		return (Math.min(a.a.x, a.b.x) <= x_node && x_node <= Math.max(a.a.x, a.b.x) && Math.min(b.a.x, b.b.x) <= x_node && x_node <= Math.max(b.a.x, b.b.x));
	}
	
	public static void disp_rect(Line ln, Rectangle in, int direction) {
		
		//direction: 0 = any, 1 = vertical, 2 = horizontal
		
		Vector2 temp = Line.find_node_on_line(ln, in.pos);
		
		Vector2[] nodes = {
				new Vector2(in.pos.x + in.width / 2, in.pos.y + in.height / 2),
				new Vector2(in.pos.x - in.width / 2, in.pos.y + in.height / 2),
				new Vector2(in.pos.x - in.width / 2, in.pos.y - in.height / 2),
				new Vector2(in.pos.x + in.width / 2, in.pos.y - in.height / 2),
		};
		int place = 0;
		for (int x = 1; x<nodes.length; x++) {
			if (Vector2.dist(nodes[place], temp) <= Vector2.dist(nodes[x], temp)) continue;
			else place = x;
		}
		Vector2 offset = Vector2.sub(in.pos, nodes[place]);
		
		double x = (nodes[place].y - new Line(ln.a, ln.b).b()) / new Line(ln.a, ln.b).m();
		double y = new Line(ln.a, ln.b).m() * nodes[place].x + new Line(ln.a, ln.b).b();
		
		if (direction == 0) {
			if (Math.abs(x - nodes[place].x) >= Math.abs(y - nodes[place].y)) in.pos.y += y - nodes[place].y;
			if (Math.abs(x - nodes[place].x) < Math.abs(y - nodes[place].y)) in.pos.x += x - nodes[place].x;
		}
		if (direction == 1) {
			in.pos = Vector2.add(new Vector2(nodes[place].x, y), offset);
		}
		if (direction == 2) {
			in.pos = Vector2.add(new Vector2(x, nodes[place].y), offset);
		}
		
	}
	
	
	//this is assuming line b is vertical, which allows this to be infinitely easier!
	//we can express a vertical line as an x-coordinate, b
	public static boolean line_intersect(Line a, double b) {
		return (Math.min(a.a.x, a.b.x) <= b && Math.max(a.a.x, a.b.x) >= b);
	}
	public static double find_intersect(Line a, double b) {
		double l = Math.abs(b - a.a.x) / Math.abs(a.b.x - a.a.x);
		
		return a.a.y + l * (a.b.y - a.a.y);
	}
	public static Vector2 find_node_on_line(Line a, Vector2 in) {
		//horizontal
		if (a.a.y == a.b.y) {
			return new Vector2(clamp(in.x, a.a.x, a.b.x), a.a.y);
		}
		//vertical
		if (a.a.x == a.b.x) {
			return new Vector2(a.a.x, clamp(in.y, a.a.y, a.b.y));
		}
		double m = a.m();
		double b = a.b();
		
		double m1 = -1 / m;
		double b1 = in.y - m1 * in.x;
		
		double xnode = clamp((b1 - b) / (m - m1), a.a.x, a.b.x);
		double ynode = clamp(m * xnode + b, a.a.y, a.b.y);
		
		return new Vector2(xnode, ynode);
	}
	public double m() {
		return (this.a.y - this.b.y) / (this.a.x - this.b.x);
	}
	public double b() {
		return this.a.y - this.m() * this.a.x;
	}
	public static double clamp(double in, double min, double max) {
		return Math.max(Math.min(min, max), Math.min(Math.max(min, max), in));
	}
	public void extend_line(double margin) {
		Vector2 temp = Vector2.sub(b, a);
		a.add(Vector2.scale_to_length(temp, -margin));
		b.add(Vector2.scale_to_length(temp, margin));
	}
	public void draw_line(Graphics g, Color a, JPanel pane, double xpos, double ypos, String location) {
		g.setColor(a);
		
		Vector2 temp_a = Vector2.converted_pos(this.a, pane, xpos, ypos, location);
		Vector2 temp_b = Vector2.converted_pos(this.b, pane, xpos, ypos, location);
		
		g.drawLine((int)temp_a.x, (int)temp_a.y, (int)temp_b.x, (int)temp_b.y);
		
		g.setColor(Color.blue);
		
		g.fillOval((int)temp_a.x - 2, (int)temp_a.y - 2, 4, 4);
		g.fillOval((int)temp_b.x - 2, (int)temp_b.y - 2, 4, 4);
	}
	
	public Line offset_line(double y) {
		return new Line(new Vector2(this.a.x,this.a.y + y), new Vector2(this.b.x, this.b.y + y));
	}
}