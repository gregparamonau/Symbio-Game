package GameObject.EditorObjects;

import java.awt.Color;

import GameObject.EditObject;
import GameObject.Objects.Circle;
import LevelEdit.LevelEditor;
import Logic.Vector2;

public class CircleEdit extends Circle{

	public CircleEdit(Vector2 pos, double radius) {
		this.pos = new Vector2(pos);
		
		this.radius = radius;
		
		this.width = 2 * radius;
		this.height = 2 * radius;
		
		this.displaceable = false;
		
		this.start_nodes();
	}
	
	public CircleEdit(double a, double b, double c, double d, String sprite, int id) {
		super(a, b, c, d, sprite, id);
	}
	
	public static CircleEdit default_circle(Vector2 loc) {
		return new CircleEdit(loc, default_radius * LevelEditor.grid_size);
	}
	
	public void update_nodes(Vector2 in, int place, int grid_size) {
		if (place == 0) {
			Vector2 offset = Vector2.sub(this.nodes[1], this.nodes[0]);
			this.nodes[0] = in;
			this.nodes[1] = Vector2.add(this.nodes[0], offset);
		}
		if (place == 1) {
			this.nodes[1] = in;
		}
		this.clip_nodes(LevelEditor.grid_size);
		
		this.radius = Vector2.dist(this.nodes[0], this.nodes[1]);
		
		this.pos = this.nodes[0];
	}
	public void move(int grid_size) {
		this.update_nodes(this.pos, 0, grid_size);
	}
	public void update_dimensions() {
		this.radius = Vector2.dist(this.nodes[0], this.nodes[1]);
		this.width = this.radius * 2;
		this.height = this.radius * 2;
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {this.pos, new Vector2(this.pos.x + this.radius, this.pos.y)};
		this.nodes = temp;
		start = false;
	}
	
	public String toString() {
		return "circle " + this.pos.x + " " + this.pos.y + " " + this.radius + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "circle";
	}
}
