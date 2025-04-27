package Logic;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import Rendering.Camera;

public class Vector2 {
	public double x, y;
	public static int radius_edit = 16, radius_game = 3;
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
	public double mag_sq() {
		return this.x * this.x + this.y * this.y;
	}
	public static Vector2 mult(Vector2 in, double a) {
		return new Vector2(in.x * a, in.y * a);
	}
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x + b.x, a.y + b.y);
	}
	public void add(Vector2 in) {
		this.x += in.x;
		this.y += in.y;
	}
	public void mult(double in) {
		this.x *= in;
		this.y *= in;
	}
	public static Vector2 sub(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	public static Vector2 normalize(Vector2 in) {
		if (in.equals(Vector2.zero)) return in;
		return new Vector2(in.x / in.length(), in.y / in.length());
	}
	public static double dot(Vector2 a, Vector2 b) {
		Vector2 temp_a = normalize(a), temp_b = normalize(b);
		return (temp_a.x * temp_b.x + temp_a.y * temp_b.y);
	}
	public static Vector2 invert_x(Vector2 in) {
		return new Vector2(-in.x, in.y);
	}
	public static Vector2 invert_y(Vector2 in) {
		return new Vector2(in.x, -in.y);
	}
	public static double dist(Vector2 a, Vector2 b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	public static Vector2 perp(Vector2 in, int dir) {
		return new Vector2(-in.y * -dir, in.x * -dir);
	}
	public static Vector2 scale_to_length(Vector2 in, double length) {
		double mult = length/in.length();
		return new Vector2(in.x * mult, in.y * mult);
	}
	public static Vector2[] move_arr(Vector2[] in, Vector2 add) {
		Vector2[] out = new Vector2[in.length];
		for (int x = 0; x<out.length; x++) out[x] = Vector2.add(in[x], add);
		return out;
	}
	public static Vector2 converted_pos(Vector2 pos, JPanel in, double xin, double yin, String location) {
		JPanel pane = in;
		
		if (location.equals("edit")) pane = rescale(in, 1);
		else if (location.equals("game")) pane = rescale(in, (double)1 / Camera.pixel_size);
		
		return new Vector2((int)(pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight()/2 - pos.y + yin));
	}
	public void draw_node(Graphics g, JPanel in, double xin, double yin, String location, Color fill) {
		g.setColor(fill);
		
		JPanel pane = in;
		if (location.equals("edit")) pane = rescale(in, 1);
		else if (location.equals("game")) pane = rescale(in, (double)1 / Camera.pixel_size);
		
		//(int)(this.pos.y + yin - pane.getHeight() / 2 - this.height / 2)
		if (location.equals("edit")) g.drawOval((int)(this.x + pane.getWidth()/2 - xin - radius_edit), (int)(pane.getHeight() / 2 - this.y + yin - radius_edit), radius_edit * 2, radius_edit * 2);
		else if (location.equals("game")) g.drawOval((int)(this.x + pane.getWidth()/2 - xin - radius_game), (int)(pane.getHeight() / 2 - this.y + yin - radius_game), radius_game * 2, radius_game * 2);
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
	//void versions
	public static void add_to_vec_arr(Vector2[] in, Vector2 add) {		
		Vector2[] out = new Vector2[in.length + 1];
		
		for (int x = 0; x<in.length; x++) {
			out[x] = in[x];
		}
		
		out[out.length - 1] = add;
		
		in = out;		
	}
	public static void remove_from_vec_arr(Vector2[] in, int index) {
		Vector2[] out = new Vector2[in.length - 1];
		
		for (int x = 0; x<out.length; x++) {
			out[x] = in[x < index ? x : x + 1];
		}
		
		in = out;
	}
	
	//vector[] versions
	public static Vector2[] add_to_arr(Vector2[] in, Vector2 add) {		
		Vector2[] out = new Vector2[in.length + 1];
		
		for (int x = 0; x<in.length; x++) {
			out[x] = in[x];
		}
		
		out[out.length - 1] = add;
		
		return out;
	}
	public static Vector2[] merge_arr(Vector2[] a, Vector2[] b) {
		Vector2[] out = new Vector2[a.length + b.length];
		
		for (int x = 0; x< out.length; x++) {
			out[x] = (x < a.length ? a[x] : b[x - a.length]);
		}
		return out;
	}
	public static Vector2[] remove_from_arr(Vector2[] in, int index) {
		Vector2[] out = new Vector2[in.length - 1];
		
		for (int x = 0; x<out.length; x++) {
			out[x] = in[x < index ? x : x + 1];
		}
		
		return out;
	}
	
	public static double[] to_double_arr(Vector2[] in) {
		double[] out = new double[in.length * 2];
		
		for (int x =0; x<in.length; x++) {
			out[2 * x] = in[x].x;
			out[2 * x + 1] = in[x].y;
		}
		return out;
	}
	
	public static JPanel rescale(JPanel in, double scale) {
		JPanel out = new JPanel();
		out.setSize((int)(in.getWidth() * scale), (int)(in.getHeight() * scale));
		return out;
	}
	public String toString() {
		return "x: " + this.x + " y: " + this.y;
	}
}
