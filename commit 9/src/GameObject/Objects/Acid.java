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
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;
import Rendering.Camera;

public class Acid extends GameObject{
	public static String sprite_name_default = "/object_textures/acid.png";
	public static String bubble = "/particle_textures/bubble.png", bubble_spawn = "/particle_textures/bubble_spawn.png", bubble_float = "/particle_textures/bubble_float.png", bubble_pop = "/particle_textures/bubble_pop.png";
	public static Color fill_save = new Color(6, 214, 160);
	
	public Vector2[] bubbles = new Vector2[0];
	public Animation[] bubble_anims;
	static double bubble_mult = 0.75;
	
	Vector2 rise_vel = new Vector2(0, 0.5);
	
	public Acid() {}
	public Acid(double a, double b, double c, double d, double e, String sprite, int id) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		
		this.object_handle = (int)e;
		
		this.fill = fill_save;
		
		this.sprite_name = sprite;
		this.id = id;
		
		this.vis_solid = false;
		this.start_nodes();
	}
	
	public void update() {		
		
		if (this.object_handle != -1)
			this.move(Vector2.sub(Game.current_room.objects[this.object_handle].pos, Game.current_room.objects[this.object_handle].last_pos));

		
		if (!this.on_screen(Game.cam.pos)) return;
		this.update_bubbles();
	}
	public boolean collide_with(Rectangle in, boolean col_action) {
		return Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y - Camera.tile_size/4, this.width, this.height - Camera.tile_size/2), in) && col_action;
	}
	public void collision_action() {
		Game.player.combat.hazard_respawn(true);
	}
	public void displace_entity(Rectangle in, int direction) {
		//nothing to see here :)
	}
	
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	//particle logic
	void start_bubbles() {
		this.bubbles = new Vector2[(int)(this.width * this.height * bubble_mult / Camera.tile_size / Camera.tile_size)];
		this.bubble_anims = new Animation[this.bubbles.length];
		
		for (int x = 0; x<this.bubbles.length; x++) {
			this.bubbles[x] = new Vector2(- this.width / 2 + (x % (this.width / Camera.tile_size) + 0.5) * Camera.tile_size, this.height * (Math.random()  - 0.5));
			this.bubble_anims[x] = new Animation(bubble, this.bubbles[x], 15, 3, false, false);
		}
	}
	void update_bubbles() {
		long a = System.nanoTime();
		for (int x = 0; x<this.bubbles.length; x++) {
			
			if (this.bubble_anims[x].ended) {
				this.respawn_bubble(x);
				this.bubble_anims[x] = new Animation(bubble, this.bubbles[x], 15, 3, false, false);
				continue;
			}
			
			if (this.bubble_anims[x].frame < 5) continue;
			
			if (this.bubbles[x].y < this.height * 0.5 - Camera.tile_size * 0.5 && this.bubble_anims[x].frame > 9) {
				this.bubble_anims[x].frame = 5;
				this.bubble_anims[x].counter = 15;
			}
			
			this.bubbles[x].y += rise_vel.y;
			this.bubbles[x].x += (Math.random() - 0.5);
			
			if (Utility.clamp(this.bubbles[x].x, - this.width/2, this.width/2) != this.bubbles[x].x) {
				this.bubbles[x].x = - Utility.sign(this.bubbles[x].x) * this.width * 0.5;
			}
		}
		long b = System.nanoTime();
		System.out.println("	UB: " + (double)(b - a) / 1000000);
	}
	void draw_bubbles(Graphics g, JPanel pane, double xin, double yin, String location) {
		for (int x = 0; x<this.bubbles.length; x++) {
			this.bubble_anims[x].play(true, Vector2.add(this.pos, this.bubbles[x]), false, g, pane, xin, yin, location);
		}
	}
	public void respawn_bubble(int id) {
		this.bubbles[id] = new Vector2(this.bubbles[id].x, this.height * (Math.random()  - 0.5));
	}
	
	//saving
	public String toString() {
		return "acid " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "acid";
	}
}
