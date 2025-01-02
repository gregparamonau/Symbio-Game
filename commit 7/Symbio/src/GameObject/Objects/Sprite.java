package GameObject.Objects;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Vector2;

public class Sprite extends GameObject{
	static String sprite_default = "/extra_textures/random_back.png"; //TODO: make this
	String sprite_name;
	double mode; //0 == normal single sprite attached, 1 == animation attached
	double anim_size;
	public Sprite(double a, double b, double vis_solid, double mode, double anim_size, String sprite_name) {
		this.pos = new Vector2(a, b);
		this.width = 0;
		this.height = 0;
		
		this.sprite_name = sprite_name;
		
		this.solid = false;
		
		this.vis_solid = vis_solid == 1;
		
		this.mode = mode;
		
		this.anim_size = anim_size;
		
		this.start_nodes();
	}
	public void generate_sprite(GameObject[] temp, String in) {
		//4 variations 
		if (this.mode == 0) this.load_sprite();
		if (this.mode == 1) this.load_animation();
	}
	//option 0
	public void load_sprite() {
		try {
			this.sprite = ImageIO.read(getClass().getResource(this.sprite_name));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	//option 1
	public void load_animation() {
		//TODO
	}
	
	public String toString() {
		return "sprite " + this.pos.x + " " + this.pos.y + " " + (this.vis_solid? 1 : 0) + " " + this.mode + " " + this.anim_size + " " + this.sprite_name + " ";
	}
	
	public String give_class() {
		return "sprite";
	}
	
	//update sprite for animations?
	
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
	
	public static Sprite default_sprite(Vector2 loc) {
		return new Sprite(loc.x, loc.y, 0, 0, 0, sprite_default);
		//not vis_solid by default
	}
	
	public boolean collide_with(Rectangle in, boolean col_action) {
		return false;
	}
	public void collision_action() {
		//write code here
	}
}
