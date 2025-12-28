package GameObject.EditorObjects;

import java.awt.Color;

import GameObject.EditObject;
import GameObject.Objects.Slope;
import LevelEdit.LevelEditor;
import Logic.Vector2;

public class SlopeEdit extends Slope{
	
	public SlopeEdit() {}
	
	public SlopeEdit(Vector2 a, Vector2 b) {
		super(a, b);
		this.displaceable = false;
	}
	
	public SlopeEdit(double a, double b, double c, double d, double e, String sprite, int id) {
		super(a, b, c, d, e, sprite, id);
		
		System.out.println("A: " + this.a + "B: " + this.b);
	}
	
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		clip_nodes(grid_size);
		
		this.a = this.nodes[0];
		this.b = this.nodes[1];
		
		this.update_dimensions();
		
	}
	public void start_nodes() {
		Vector2[] temp = {this.a, this.b};
		this.nodes = temp;
	}
	
	public void update_dimensions() {		
		this.pos = Vector2.mult(Vector2.add(this.a, this.b), 0.5);
		
		this.width = Math.abs(this.a.x - this.b.x);
		this.height = Math.abs(this.a.y - this.b.y);
	}
	
	public static SlopeEdit default_slope(Vector2 loc) {
		
		SlopeEdit out = new SlopeEdit(new Vector2(loc.x - LevelEditor.grid_size, loc.y - LevelEditor.grid_size), new Vector2(loc.x + LevelEditor.grid_size, loc.y + LevelEditor.grid_size));
		
		out.pos = new Vector2(loc);
		
		out.update_dimensions();
		
		return out;
	}
	
	public void move(int grid_size) {		
		Vector2 temp = Vector2.mult(Vector2.add(this.a, this.b), 0.5);
		
		Vector2 off_a = Vector2.sub(this.a, temp);
		Vector2 off_b = Vector2.sub(this.b, temp);
		
		
		this.a = Vector2.add(this.pos, off_a);
		this.a.clip_node(LevelEditor.grid_size);
		
		this.pos = Vector2.sub(this.a, off_a);
		
		this.b = Vector2.add(this.pos, off_b);
		
		this.nodes[0] = new Vector2(this.a);
		this.nodes[1] = new Vector2(this.b);
		
		this.update_dimensions();
	}
	public void move(Vector2 in) {
		this.a.add(in);
		this.b.add(in);
	}
	
	public String toString() {
		return "slope " + this.a.x + " " + this.a.y + " " + this.b.x + " " + this.b.y + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "slope";
	}
}
