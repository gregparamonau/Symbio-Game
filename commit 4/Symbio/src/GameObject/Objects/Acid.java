package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;
import Rendering.Camera;

public class Acid extends GameObject{
	public static String sprite_name_default = "/object_textures/acid.png";
	public static String bubble_sprite_name = "/particle_textures/bubble.png";
	public static Color fill_save = new Color(6, 214, 160);
	
	public Vector2[] bubbles = new Vector2[0];
	public int[] count = new int[0];
	static double bubble_mult = 0.5;
	
	public Animation[] bubble_animations = new Animation[3];
	Vector2 rise_vel = new Vector2(0, 0.5);
	
	public Acid() {}
	public Acid(double a, double b, double c, double d, String sprite, int id) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		
		this.fill = fill_save;
		
		this.sprite_name = sprite;
		this.id = id;
		
		this.vis_solid = false;
		this.start_nodes();
	}
	public static Acid default_acid(Vector2 loc) {
		return new Acid(loc.x, loc.y, LevelEditor.grid_size, LevelEditor.grid_size, sprite_name_default, 0);
	}
	public void update() {
		if (!this.on_screen(Game.cam.pos)) return;
		this.update_bubbles();
	}
	public boolean collide_with(Rectangle in, boolean col_action) {
		return Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y - Camera.tile_size, this.width, this.height - 2 * Camera.tile_size), in) && col_action;
	}
	public void collision_action() {
		Game.player.hazard_respawn(true);
	}
	public void displace_player(int direction) {}
	
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("game")) {
			this.draw_bubbles(g, pane, xin, yin, location);
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);

		}
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
	
	public void generate_sprite(GameObject[] objects, String in) {
		BufferedImage[] sprites_temp = return_sprite_array(in);
		
		this.sprite = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.sprite.getGraphics();
		
		for (int y = 0; y < this.height / Camera.tile_size; y++) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				g.drawImage(sprites_temp[this.return_sprite_type(new Vector2(x, y), objects)], x * Camera.tile_size, y * Camera.tile_size, null);
			}
		}
		
		this.start_bubbles();
	}
	public int return_sprite_type(Vector2 in, GameObject[] objects) {
		return (in.y == 0 ? 0 : 3) + (in.x != 0 && in.x != this.width / Camera.tile_size - 1 ? 1: 0) + (in.x == this.width / Camera.tile_size - 1 ? 2 : 0);
	}
	public BufferedImage[] return_sprite_array(String in) {
		BufferedImage[] out = new BufferedImage[6];
		try {
			BufferedImage source = ImageIO.read(getClass().getResource(in));
			for (int x = 0; x<out.length; x++) {
				out[x] = source.getSubimage((x % 3) * Camera.tile_size, (x / 3) * Camera.tile_size, Camera.tile_size, Camera.tile_size);
			}
			
			this.bubble_animations[0] = new Animation(bubble_sprite_name, 8, 8, 0, false, false);
			this.bubble_animations[1] = new Animation(bubble_sprite_name, 8, 8, 8, false, false);
			this.bubble_animations[2] = new Animation(bubble_sprite_name, 8, 8, 16, false, false);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	//particle logic
	void start_bubbles() {
		this.bubbles = new Vector2[(int)(this.width * this.height * bubble_mult / Camera.tile_size / Camera.tile_size)];
		this.count = new int[this.bubbles.length];
		
		for (int x = 0; x<this.bubbles.length; x++) {
			this.bubbles[x] = new Vector2(this.pos.x - this.width / 2 + (x % (this.width / Camera.tile_size) + 0.5) * Camera.tile_size, this.pos.y + this.height * (Math.random() - 0.5));
			this.count[x] = (int)(5 + Math.random() * 5);
		}
	}
	void update_bubbles() {
		long a = System.nanoTime();
		for (int x = 0; x<this.bubbles.length; x++) {
			if(this.count[x] > 4 && this.count[x] < 10) this.bubbles[x].y += rise_vel.y;
			
			if (this.bubbles[x].y > this.pos.y + this.height / 2 - Camera.tile_size / 2 && this.count[x] == 14) {
				this.bubbles[x].y = this.pos.y - Math.random() * this.height / 2 + Camera.tile_size / 2;
				this.count[x] = 0;
				continue;
			}
			
			if (this.count[x] > 4 && this.bubbles[x].y < this.pos.y + this.height / 2 - Camera.tile_size / 2) this.count[x] = 5 + (this.count[x] + 1) % 5;
			else this.count[x] ++;
		}
		long b = System.nanoTime();
		//System.out.println("	UB: " + (double)(b - a) / 1000000);
	}
	void draw_bubbles(Graphics g, JPanel pane, double xin, double yin, String location) {
		for (int x = 0; x<this.bubbles.length; x++) {
			new Rectangle(this.bubbles[x].x, this.bubbles[x].y, 0, 0).draw_with_sprite(g, pane, xin, yin, this.return_sprite(this.count[x]), location);
		}
	}
	BufferedImage return_sprite(int place) {
		return this.bubble_animations[place / 5].sprites[place % 5];
	}
	
	//saving
	public String toString() {
		return "acid " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "acid";
	}
}
