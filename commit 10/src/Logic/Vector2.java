package Logic;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import Rendering.Camera;

public class Vector2 {
	public double x, y;
	public static int radius = 3;
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
	
	//useful if no reassigning wanted
	public void set(Vector2 in) {
		this.x = in.x;
		this.y = in.y;
	}
	//set vector to zero
	public void zero() {
		this.x = 0;
		this.y = 0;
	}
	
	//utility methods
	public double l() {
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
	public void add(Vector2 in) {
		this.x += in.x;
		this.y += in.y;
	}
	public void mult(double in) {
		this.x *= in;
		this.y *= in;
	}
	public Vector2 norm() {
		double l = this.l();
		if (l == 0) return this;
		
		return new Vector2(this.x / l, this.y / l);
	}
	public static Vector2 normalize(Vector2 in) {
		if (in.equals(Vector2.zero)) return in;
		return new Vector2(in.x / in.l(), in.y / in.l());
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
	public Vector2 perp() {
		return new Vector2(-this.y, this.x);
	}
	public static Vector2 scale_to_length(Vector2 in, double length) {
		double mult = length/in.l();
		return new Vector2(in.x * mult, in.y * mult);
	}
	public void scale_to_length(double length) {
		double l = this.l();
		if (l == 0) return;
		double mult = length / l;
		
		this.x *= mult;
		this.y *= mult;
	}
	public static Vector2[] move_arr(Vector2[] in, Vector2 add) {
		Vector2[] out = new Vector2[in.length];
		for (int x = 0; x<out.length; x++) out[x] = Vector2.add(in[x], add);
		return out;
	}
	public static Vector2 converted_pos(Vector2 pos, JPanel in, double xin, double yin, String location) {
		JPanel pane = in;
		
		//if (location.equals("edit")) pane = rescale(in, 1);
		//else if (location.equals("game")) ;
		pane = rescale(in, (double)1 / Camera.pixel_size);
		
		return new Vector2((int)Math.round(pos.x + pane.getWidth()/2 - xin), (int)Math.round(pane.getHeight()/2 - pos.y + yin));
	}
	public void draw_node(Graphics g, JPanel in, double xin, double yin, String location, Color fill) {
		g.setColor(fill);
		
		JPanel pane = in;
		pane = rescale(in, (double)1 / Camera.pixel_size);
		
		//(int)(this.pos.y + yin - pane.getHeight() / 2 - this.height / 2)
		if (location.equals("edit")) g.drawOval((int)(this.x + pane.getWidth()/2 - xin - radius), (int)(pane.getHeight() / 2 - this.y + yin - radius), radius * 2, radius * 2);
		else if (location.equals("game")) g.drawOval((int)(this.x + pane.getWidth()/2 - xin - radius), (int)(pane.getHeight() / 2 - this.y + yin - radius), radius * 2, radius * 2);
	}
	public static double angle(Vector2 a, Vector2 b) {
		//return a.angle() - b.angle();
		
		double theta = Math.acos(Utility.clamp(Vector2.dot(a, b) / a.l() / b.l(), -1, 1));
		
		if (Math.acos(Utility.clamp(Vector2.dot(a.rotate(0.00000000001), b) / a.l() / b.l(), -1, 1)) > theta) {
			return -theta;
		}
		return theta;
		//return Math.atan2(b.y, b.x) - Math.atan2(a.y, a.x);
		//return Math.acos(clamp(Vector2.dot(a, b) / a.l() / b.l(), -1, 1));
	}
	
	public Vector2 rotate(double theta) {
	    double cos = Math.cos(theta);
	    double sin = Math.sin(theta);
	    return new Vector2(this.x * cos - this.y * sin,
	                       this.x * sin + this.y * cos);
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
	
	public static Vector2[] mult_vec_arr(Vector2[] in, double mult) {
		Vector2[] out = new Vector2[in.length];
		for (int x = 0; x<out.length; x++) out[x] = Vector2.mult(in[x], mult);
		
		return out;
	}
	
	public static int[][] to_double_arr_arr(Vector2[] in) {
		int[][] out = new int[2][in.length];
		
		for (int x = 0; x<in.length; x++) {
			out[0][x] = (int)in[x].x;
			out[1][x] = (int)in[x].y;
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
