package GameObject.EditorObjects;

import GameObject.EditObject;
import GameObject.Objects.Bouncer;
import LevelEdit.LevelEditor;
import Logic.Vector2;

public class BouncerEdit extends Bouncer{
	public BouncerEdit(Vector2 pos) {
		this.pos = new Vector2(pos);
		
		this.width = width_default * LevelEditor.grid_size;
		this.height = height_default * LevelEditor.grid_size;
		
		this.start_nodes();
		this.vis_solid = false;
	}
	public BouncerEdit(double a, double b, double c, double d, String sprite, int id) {
		super(a, b, c, d, sprite, id);
	}
	
	public void start_nodes() {
		if (!just_created) return;
		
		this.nodes = new Vector2[] {new Vector2(this.pos), new Vector2(this.pos.x, this.pos.y + 2 * LevelEditor.grid_size)};
		this.clip_nodes(LevelEditor.grid_size);
		
		just_created = false;
	}
	
	public void start_bouncer() {
		this.nodes = new Vector2[] {new Vector2(this.pos), new Vector2()};
		//  0
		//1   2
		//  3
		//TODO: rework these numbers a bit just in case
		if (this.variant == 0) {
			this.width = width_default;
			this.height = height_default;
			this.nodes[1] = new Vector2(this.nodes[0].x, this.nodes[0].y + 2 * LevelEditor.grid_size);
		}
		if (this.variant == 1) {
			this.width = height_default;
			this.height = width_default;
			this.nodes[1] = new Vector2(this.nodes[0].x - 2 * LevelEditor.grid_size, this.nodes[0].y);
		}
		if (this.variant == 2) {
			this.width = height_default;
			this.height = width_default;
			this.nodes[1] = new Vector2(this.nodes[0].x + 2 * LevelEditor.grid_size, this.nodes[0].y);
		}
		if (this.variant == 3) {
			this.width = width_default;
			this.height = height_default;
			this.nodes[1] = new Vector2(this.nodes[0].x, this.nodes[0].y - 2 * LevelEditor.grid_size);
		}
		
		this.just_created = false;
	}
	
	public static BouncerEdit default_bouncer(Vector2 loc) {
		return new BouncerEdit(loc);
	}
	public void update_dimensions() {
		int var = this.give_variant();
		
		if (var == 0 || var == 3) {
			this.width = width_default * LevelEditor.grid_size;
			this.height = height_default * LevelEditor.grid_size;
		}
		if (var == 1 || var == 2) {
			this.width = height_default * LevelEditor.grid_size;
			this.height = width_default * LevelEditor.grid_size;
		}
	}
	public void update_nodes(Vector2 in, int place, int grid_size) {
		System.out.println("PLACEEEEE: " + place);
		if (place == 0) {
			this.update_dimensions();
			
			Vector2 temp = new Vector2(in.x - this.width / 2, in.y - this.height / 2);
			
			temp.clip_node(grid_size / 2);
			
			this.pos = new Vector2(temp.x + this.width / 2, temp.y + this.height / 2);
			
			temp = Vector2.sub(this.nodes[1], this.nodes[0]);
			
			
			this.nodes[0] = new Vector2(this.pos);
			this.nodes[1] = Vector2.add(this.nodes[0], temp);
		}
		
		if (place == 1) {
			this.nodes[1] = new Vector2(in);
			this.nodes[1].clip_node(grid_size / 2);
			this.update_dimensions();
		}
	}
	public void move(int grid_size) {
		if (!LevelEditor.mouse_pressed) return;
		
		this.nodes = Vector2.move_arr(this.save_nodes, Vector2.sub(LevelEditor.mouse_pos, LevelEditor.click));
		this.clip_nodes(grid_size / 2);
	}
	public void clip_nodes(int grid_size) {
		for (int x = 0; x<this.nodes.length; x++) this.nodes[x].clip_node(grid_size);
		this.pos = this.nodes[0];
	}
	public int give_variant() {
		Vector2 temp = Vector2.sub(this.nodes[1], this.nodes[0]);
		
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
		return "bouncer " + this.pos.x + " " + this.pos.y + " " + this.give_variant() + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "bouncer";
	}
}
