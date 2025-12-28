package GameObject.EditorObjects;

import java.awt.Color;

import GameObject.EditObject;
import GameObject.Objects.OneWay;
import LevelEdit.LevelEditor;
import Logic.Vector2;

public class OneWayEdit extends OneWay{
	
	public OneWayEdit() {}
	public OneWayEdit(double a, double b, double c, double d, double e, double f, String sprite, int id) {
		super(a, b, c, d, e, f, sprite, id);
	}
	public static OneWayEdit default_oneway(Vector2 loc) {
		return new OneWayEdit(loc.x, loc.y, width_default * LevelEditor.grid_size, height_default * LevelEditor.grid_size, 0, -1, "", 0);
	}
	
	public void update_nodes(Vector2 in, int place, int grid_size) {
		if (place < 4) {
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
			this.clip_nodes(grid_size);
			this.update_dimensions();
			
			return;
		}
		this.nodes[place] = in;
		this.nodes[place].clip_node(grid_size);
		this.update_dimensions();
	}
	public void update_dimensions() {
		this.pos = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[2]), 0.5);
		Vector2 diag = Vector2.sub(this.nodes[0], this.nodes[2]);
		
		int var = this.give_variant();
		if (var == 0 || var == 3) {
			this.width = Math.max(Math.abs(diag.x), Math.abs(diag.y));
			this.height = LevelEditor.grid_size;
		}
		if (var == 1 || var == 2) {
			this.height = Math.max(Math.abs(diag.x), Math.abs(diag.y));
			this.width = LevelEditor.grid_size;
		}
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {
				new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2), 
				new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2),
				new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2),
				new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2),
				Vector2.add(this.pos, this.dir_vector())
		};
		this.nodes = temp;
		start = false;
	}
	public Vector2 dir_vector() {
		Vector2[] temp = {new Vector2(0, 2 * LevelEditor.grid_size), new Vector2(-2 * LevelEditor.grid_size, 0), new Vector2(2 * LevelEditor.grid_size, 0), new Vector2(0, -2 * LevelEditor.grid_size)};
		return temp[this.variant];
	}
	
	public int give_variant() {
		Vector2 temp = Vector2.sub(this.nodes[4], this.nodes[0]);
		
		if (Math.abs(temp.y) >= Math.abs(temp.x)) {
			if (temp.y >= 0) return 0;
			if (temp.y < 0) return 3;
		}
		
		if (Math.abs(temp.y) < Math.abs(temp.x)) {
			if (temp.x >= 0) return 2;
			if (temp.x < 0) return 1;
		}
		
		return 0;
	}
	public String toString() {
		return "oneway " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.give_variant() + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	
	public String give_class() {
		return "oneway";
	}
}
