package GameObject.Objects;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Vector2;

public class Sprite extends GameObject{
	protected static String sprite_default = "/extra_textures/random_back.png"; //TODO: make this
	protected String sprite_name;
	protected double mode; //0 == normal single sprite attached, 1 == animation attached
	protected double anim_size;
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
	
	public void start_nodes() {
		this.nodes = new Vector2[] {new Vector2(this.pos.x, this.pos.y)};
	}
	
	//update sprite for animations?
	
	public boolean collide_with(Rectangle in, boolean col_action) {
		return false;
	}
	public void collision_action() {
		//write code here
	}
}
