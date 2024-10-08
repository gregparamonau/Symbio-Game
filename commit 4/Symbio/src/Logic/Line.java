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
	public void draw_line(Graphics g, Color a, JPanel pane, double xpos, double ypos, String location) {
		g.setColor(a);
		
		Vector2 temp_a = Vector2.converted_pos(this.a, pane, xpos, ypos, location);
		Vector2 temp_b = Vector2.converted_pos(this.b, pane, xpos, ypos, location);
		
		g.drawLine((int)temp_a.x, (int)temp_a.y, (int)temp_b.x, (int)temp_b.y);
		
		
		int[] xposs = new int[2], yposs = new int[2];
		if (location.equals("edit")) {
			int[] xposst = {(int)(this.a.x + pane.getWidth()/2 - xpos), (int)(this.b.x + pane.getWidth()/2 - xpos)};
			int[] yposst = {(int)(pane.getHeight()/2 - this.a.y - ypos), (int)(pane.getHeight()/2 - this.b.y - ypos)};
			xposs = xposst;
			yposs = yposst;
		}
		else if (location.equals("game")) {
			int[] xposst = {(int)this.a.x + pane.getWidth() / 2 / Camera.pixel_size - (int)xpos, (int)this.b.x + pane.getWidth() / 2 / Camera.pixel_size - (int)xpos};
			int[] yposst = {pane.getHeight() / Camera.pixel_size - ((int)this.a.y - (int)ypos + pane.getHeight() / 2 / Camera.pixel_size), pane.getHeight() / Camera.pixel_size - ((int)this.b.y - (int)ypos + pane.getHeight() / 2 / Camera.pixel_size)};
			xposs = xposst;
			yposs = yposst;
		}
		//Game.pane.getHeight() - (this.a.y - Game.cam.ypos + Game.pane.getHeight()/2)
		g.drawLine(xposs[0], yposs[0], xposs[1], yposs[1]);
	}
}