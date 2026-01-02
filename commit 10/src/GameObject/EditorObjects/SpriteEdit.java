package GameObject.EditorObjects;

import GameObject.EditObject;
import GameObject.Objects.Sprite;
import Logic.Vector2;

public class SpriteEdit extends Sprite{
	public SpriteEdit(double a, double b, double vis_solid, double mode, double anim_size, String sprite_name) {
		super(a, b, vis_solid, mode, anim_size, sprite_name);
	}
	
	public static SpriteEdit default_sprite(Vector2 loc) {
		return new SpriteEdit(loc.x, loc.y, 0, 0, 0, sprite_default);
		//not vis_solid by default
	}
	
	public void start_nodes() {
		this.nodes = new Vector2[] {new Vector2(this.pos.x, this.pos.y)};
	}
	
	public void scale(double in) {
		this.pos.mult(in);
		this.nodes[0].mult(in);
	}
	
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		
		this.clip_nodes(grid_size);
		
		this.pos = this.nodes[0];
	}
	
	public String toString() {
		return "sprite " + this.pos.x + " " + this.pos.y + " " + (this.vis_solid? 1 : 0) + " " + this.mode + " " + this.anim_size + " " + this.sprite_name + " ";
	}
	
	public String give_class() {
		return "sprite";
	}
}
