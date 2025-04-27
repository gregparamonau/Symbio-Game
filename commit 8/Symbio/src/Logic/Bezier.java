package Logic;

import java.util.ArrayList;
import java.util.List;

//very useful class
public class Bezier {
	public Vector2[] control_points;
	public Rectangle bounding_box;
	int[] coeffs;
	int degree;
	
	//this is set at 19 since that is a much less round number than 20, which helps reduce the amount of issues with the rendering
	public static final int res = 19;
	
	public Rectangle test;
	
	public Bezier(double num, double[] pnts) {
		this.control_points = new Vector2[(int)num];
		
		for (int x = 0; x<this.control_points.length; x++) {
			this.control_points[x] = new Vector2(pnts[2 * x], pnts[2 * x + 1]);
		}
		
		this.degree = this.control_points.length - 1;
		
		this.coeffs = new int[this.degree + 1];
		for (int x = 0; x<this.degree + 1; x++) this.coeffs[x] = choose(this.degree, x);
		
		this.find_bounds();
	}
	
	//double y is the top of the rectangle, not the middle
	public double[] intersect_roots(double x, boolean find_t, boolean find_relative_pos, double y) {
		//if find relative pos, finds actual coordinates in relative form to y
		//if not in relative pos, finds t values that collide with x value.
		double[] y_out = new double[0];
		double[] t_out = new double[0];
		
		Vector2[] pnts = {pnt(0), new Vector2()};	
		
		for (int i = 0; i < res; i++) {
			
			pnts[(i + 1) % 2] = pnt((double)(i + 1) / res);
			
			Line ln = new Line(pnts[0], pnts[1]);
			
			if (Line.line_intersect(ln, x)) {

				double temp = (find_relative_pos ? y : 0) + (find_relative_pos ? -1 : 1) * Line.find_intersect(ln, x);
				double t = Math.abs((Line.find_intersect(ln, x) - pnts[0].x) / (pnts[1].x - pnts[0].x)) * 1.0 / res + (double)i / res;
				
				
				y_out = Utility.add_to_array(y_out, temp);
				t_out = Utility.add_to_array(t_out, t);
				
			}
		}
		if (find_t) {
			Utility.remove_duplicates(t_out, 0.01);
			return t_out;
		}
		if (y_out.length != 0)
			Utility.remove_duplicates(y_out, 0.02);
		return y_out;
	}
	
	public double newton(double t, double x, int count) {
		if (count > 200) return -1;
		
		System.out.println(t);
		
		if (Math.abs(pnt(t).x - x) < 0.01) return t;
		
		return newton(t - (pnt(t).x - x) / pnt_prime(t), x, count + 1);
	}
	
	public Line intersect_rect(Rectangle in) {
		
		Vector2[] pnts = {pnt(0), new Vector2()};
		
		for (int i = 0; i<res; i++) {
			pnts[(i + 1) % 2] = pnt((double)(i + 1) / res);
			
			if (Rectangle.intersect_line(in, pnts[0], pnts[1])) {
				return new Line(pnts[0], pnts[1]);
			}
		}
		
		return null;
	}
	
	public void find_bounds() {
		
		this.bounding_box = new Rectangle(pnt(0), pnt(1));
		
		for (int x = 1; x <res; x++) {
			this.bounding_box.expand(pnt((double)x / res));
		}
		this.bounding_box.height = Math.ceil(this.bounding_box.height);
		
		System.out.println(this.bounding_box);
		
		this.test = this.bounding_box;
		
	}
	
	//return the point of the Bezier at specified t value
	public Vector2 pnt(double t) {
		Vector2 out = new Vector2(0, 0);
		for (int x = 0; x<this.control_points.length; x++) {
			out.add(Vector2.mult(this.control_points[x], this.coeffs[x] * Math.pow(1 - t, this.degree - x) * Math.pow(t, x)));
		}
		
		return out;
	}
	
	public double pnt_prime(double t) {
	    double sum = 0;
	    for (int i = 0; i < degree; i++) {
	        Vector2 diff = Vector2.sub(control_points[i + 1], control_points[i]);
	        sum += degree * diff.x * choose(degree - 1, i) *
	               Math.pow(1 - t, degree - 1 - i) * Math.pow(t, i);
	    }
	    return sum;
	}
	
	public static int choose(int n, int r) {
		return factorial(n) / factorial (r) / factorial(n - r);
	}
	
	public static int factorial(int a) {
		int out = 1;
		for (int x = a; x > 0; x--) {
			out *= x;
		}
		return out;
	}
}
