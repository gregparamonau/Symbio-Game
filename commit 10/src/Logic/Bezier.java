package Logic;

import java.util.ArrayList;
import java.util.List;

//very useful class
public class Bezier {
	public Vector2[] control_points;
	public Line[] lines;
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
		
		this.lines = new Line[this.control_points.length - 1];
		for (int x = 0; x< this.lines.length; x++) {
			this.lines[x] = new Line(this.control_points[x], this.control_points[x + 1]);
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
	
	public Vector2[] to_polygon(boolean clockwise) {
		
		//01
		//32
		Vector2[] corners = new Vector2[] {
				new Vector2(this.bounding_box.pos.x - this.bounding_box.width / 2, this.bounding_box.pos.y + this.bounding_box.height / 2),
				new Vector2(this.bounding_box.pos.x + this.bounding_box.width / 2, this.bounding_box.pos.y + this.bounding_box.height / 2),
				new Vector2(this.bounding_box.pos.x + this.bounding_box.width / 2, this.bounding_box.pos.y - this.bounding_box.height / 2),
				new Vector2(this.bounding_box.pos.x - this.bounding_box.width / 2, this.bounding_box.pos.y - this.bounding_box.height / 2),

		};
		Vector2[] out = new Vector2[res + 1];
		
		for (int x = 0; x<out.length; x++) out[x] = this.pnt((double)x / (res));
		
		double[] angles = new double[4];
		int[] ids = {0, 1, 2, 3};
		
		//if clockwise angle > 0, find smallest positive angle from all 4 corners
		for (int x = 0; x < 4; x++) {
			if (Vector2.dist(out[out.length - 1], corners[x]) < 1) {
				angles[x] = 0;
				continue;
			}
			
			angles[x] = Vector2.angle(Vector2.sub(corners[x], out[out.length - 1]), Vector2.sub(out[out.length - 2], out[out.length - 1]));
		}
		
		//sort according to direction
		boolean sorted = false;
		
		int count = 0;
		
		//if ccw, largest first VV
		//if cw smallest (negative too) first
		do {
			count++;
			sorted = true;
			for (int x = 1; x < 4; x++) {
				if ((clockwise ? -1 : 1) * angles[x] > (clockwise ? -1 : 1) * angles[x - 1]) {
					//change spots of angles
					double temp = angles[x];
					angles[x] = angles[x - 1];
					angles[x - 1] = temp;
					
					//change spots of ids
					int tempi = ids[x];
					ids[x] = ids[x - 1];
					ids[x - 1] = tempi;
					
					sorted = false;
				}
			}
		}while(!sorted && count < 200);
		
		if (count > 199) {
			System.out.println("ISSUE IN THE SORT METHOD");
		}
		
		count = 0;
		
		int target = ids[0];
		
		boolean on_side = this.on_side(out[0], out[out.length - 1], corners[target]);
		
		while(!on_side && count < 4) {
			
			count ++;
			out = Vector2.add_to_arr(out, corners[target]);
			
			target = (target + (clockwise ? 1 : -1) + 4) % 4;
			
			on_side = this.on_side(out[0], out[out.length - 1], corners[target]);
		}
		
		if (count > 4) System.out.println("ISSUE IN THE ADD CORNERS PART");
		
		for (int x = 0; x<out.length; x++) {
			System.out.println("out[" + x + "] = " + out[x]);
		}
		//System.exit(0);
		
		return out;
	}
	
	boolean on_side(Vector2 in, Vector2 a, Vector2 b) {
		
		return Vector2.dist(Line.find_node_on_line(new Line(a, b), in),in) < 1;
		//return (Math.abs(Vector2.angle(Vector2.sub(in, a), Vector2.sub(b, a))) < 1e-2);
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
