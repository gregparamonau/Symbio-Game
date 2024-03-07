package Symbio.Logic;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Symbio.Rendering.Camera;

public class Vector2 {
	public double x, y;
	public static int radius_edit = 12, radius_game = 3;
	public Color fill;
	
	public static final Vector2 up = new Vector2(0, 1);
	public static final Vector2 down = new Vector2(0, -1);
	public static final Vector2 left = new Vector2(-1, 0);
	public static final Vector2 right = new Vector2(1, 0);
	
	public static final Vector2 up_left = new Vector2(-0.7, 0.7);
	public static final Vector2 up_right = new Vector2(0.7, 0.7);
	public static final Vector2 down_left = new Vector2(-0.7, -0.7);
	public static final Vector2 down_right = new Vector2(0.7, -0.7);
	
	public static final Vector2 zero = new Vector2(0, 0);
	
	public Vector2() {
		this.x = 0;
		this.y = 0;
	}
	public Vector2(double a, double b) {
		this.x = a;
		this.y = b;
	}
	public Vector2(Vector2 in) {
		this.x = in.x;
		this.y = in.y;
	}
	
	//utility methods
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	public static Vector2 mult(Vector2 in, double a) {
		return new Vector2(in.x * a, in.y * a);
	}
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x + b.x, a.y + b.y);
	}
	public static Vector2 sub(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	public static double dot(Vector2 a, Vector2 b) {
		return (a.x * b.x + a.y * b.y);
	}
	public static Vector2 neg(Vector2 in) {
		return new Vector2(-in.x, -in.y);
	}
	public static Vector2 invert_x(Vector2 in) {
		return new Vector2(-in.x, in.y);
	}
	public static Vector2 invert_y(Vector2 in) {
		return new Vector2(in.x, -in.y);
	}
	public static Vector2 mult_x(Vector2 in, double q) {
		return new Vector2(in.x * q, in.y);
	}
	public static Vector2 mult_y(Vector2 in, double q) {
		return new Vector2(in.x, in.y * q);
	}
	public static double dist(Vector2 a, Vector2 b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	public static double dist_x(Vector2 a, Vector2 b) {
		return Math.abs(a.x - b.x);
	}
	public static double dist_y(Vector2 a, Vector2 b) {
		return Math.abs(a.y - b.y);
	}
	public static Vector2 scale_to_length(Vector2 in, double length) {
		double mult = length/in.length();
		return new Vector2(in.x * mult, in.y * mult);
	}
	public void draw_node(Graphics g, JPanel pane, double xin, double yin, String location, Color fill) {
		g.setColor(fill);
		if (location.equals("edit")) g.drawOval((int)(this.x + pane.getWidth()/2 - xin - radius_edit), (int)(pane.getHeight()/2 - this.y - radius_edit - yin), radius_edit * 2, radius_edit * 2);
		else if (location.equals("game")) g.fillOval((int)(this.x - xin + pane.getWidth() / 2 / Camera.pixel_size - radius_game), pane.getHeight() / Camera.pixel_size - ((int)(this.y - yin + pane.getHeight() / 2 / Camera.pixel_size)) - radius_game, radius_game * 2, radius_game * 2);
	}
	public static double angle(Vector2 a) {
		if (a.x == 0) {
			if (a.y > 0) return Math.PI / 2;
			if (a.y < 0) return 3 * Math.PI / 2;
		}
		double out = Math.atan(a.y/a.x);
		
		if (a.x < 0 && a.y > 0) out = Math.PI - out;
		else if (a.x < 0 && a.y < 0) out = Math.PI + out;
		else if (a.x > 0 && a.y < 0) out = 2 * Math.PI - out;
		
		return out;
	}
	public static Vector2 swap_axis(Vector2 in) {
		return new Vector2(in.y, in.x);
	}
	public Vector2 round(int decimal_place) {
		return new Vector2(Utility.round(this.x, decimal_place), Utility.round(this.y, decimal_place));
	}
	public void clip_node(int grid_size) {
		this.x = (Math.round(this.x / grid_size)) * grid_size;
		this.y = (Math.round(this.y / grid_size)) * grid_size;
	}
	public String toString() {
		return "x: " + this.x + " y: " + this.y;
	}
}
