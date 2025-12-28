package DataManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import Enemies.Enemy;
import GameObject.EditObject;
import GameObject.GameObject;
import GameObject.Objects.BezierTerrain;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;
import Rendering.Camera;

public class Room extends Rectangle{
	
	public GameObject[] objects;
	
	public Enemy[] enemies = new Enemy[0];
	public static int room_num = 0;
	public static Rectangle[] bounds;
	public static boolean respawn_point_set = false;
	
	public String area = "mouth";
	
	public Room() {}
	
	public Room(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
	}
	
	public void set_params(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
	}
	
	public void start() {
		long a = System.nanoTime();
		
		//this.sort_b();
		System.out.println(this.objects.length);
		
		for (int x = this.objects.length - 1; x >= 0; x--) {
			System.out.println("	GC" + this.objects[x].give_class());
			this.objects[x].generate_sprite(this.objects, this.objects[x].sprite_name);
		}
		//this.return_sort();
		
		System.out.println(this.objects.length);
		//System.exit(0);
		
		//for (int x = 0; x<this.objects.length; x++) this.objects[x].update();
		
		//TODO: start textures
		long b = System.nanoTime();
		System.out.println("	room load time: " + (double)(b-a) / 1000000);
		
		
		this.generate_terrain_sprites("/object_textures/mouth.png");
		//System.exit(0);
	}
	
	public void generate_terrain_sprites(String in) {
		BufferedImage terr = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = terr.getGraphics();
		
		long a = System.nanoTime();
		
		
		BufferedImage source = new BufferedImage(1, 1, 1);
		try {
			source = ImageIO.read(getClass().getResource(in));
		} catch (IOException e) {e.printStackTrace();}
		
		int[] cols = {
				0,
				source.getRGB(source.getWidth() - 1, source.getHeight() - 1),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 2),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 3),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 4),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 5)};
		
		g.setColor(new Color(cols[5]));
		
		for (int x = 0; x<this.objects.length; x++) {
			if (!this.objects[x].terrain || this.objects[x].object_handle != -1) continue;
			this.objects[x].draw_blank_object(g, this);
		}
		
		int[] pixels = ((DataBufferInt) terr.getRaster().getDataBuffer()).getData();
		
		System.out.println("PL: " + pixels.length);
		
		//BezierTerrain.write_image("/Users/gregoryparamonau/Downloads/BEZIER_IMAGE.png", terr);
		
		//System.exit(0);
		
		int w = (int)this.width, l = pixels.length;
		
		int background_col = cols[5], band_width = 3;
		double chance_sub = 0.5, chance_sub_empty = 0.9;
		
		
		for (int col = 1; col < 5; col++) {
			int color_to_paint = cols[col], painter_color = cols[col - 1], rare_color = cols[clamp(col + 1, 0, 4)];
			for (int x = 0; x<pixels.length; x++) {
				if (painter_color == 0 && pixels[x] == background_col && Math.random() < 0 / 100) {
					pixels[x] = cols[1];
					continue;
				}
				else if (pixels[x] != painter_color) continue;
				
				for (int i = 0; i < band_width; i++) {
					double rand = Math.random();
					
					if (x - (w + 1) * i  >= 0 && pixels[x - (w + 1) * i ] == background_col) {
						if (rand > chance_sub) pixels[x - (w + 1) * i ] = rare_color;
						else pixels[x - (w + 1) * i ] = color_to_paint;
					}
					if (x - w * i  >= 0 && pixels[x - w * i ] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x - w * i ] = rare_color;
						else pixels[x - w * i ] = color_to_paint;
					}
					if (x - (w - 1) * i  >= 0 && pixels[x - (w - 1) * i ] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x - (w - 1) * i ] = rare_color;
						else pixels[x - (w - 1) * i ] = color_to_paint;
					}
					if (x - i >= 0 && pixels[x - i] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x - i] = rare_color;
						else pixels[x - i] = color_to_paint;
					}
					if (x + i < l && pixels[x + i] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x + i] = rare_color;
						else pixels[x + i] = color_to_paint;
					}
					if (x + (w - 1) * i  < l && pixels[x + (w - 1) * i ] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x + (w - 1) * i ] = rare_color;
						else pixels[x + (w - 1) * i ] = color_to_paint;
					}
					if (x + w * i  < l && pixels[x + w * i ] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x + w * i ] = rare_color;
						else pixels[x + w * i ] = color_to_paint;
					}
					if (x + (w + 1) * i  < l && pixels[x + (w + 1) * i ] == background_col) {
						rand = Math.random();
						if (rand > chance_sub) pixels[x + (w + 1) * i ] = rare_color;
						else pixels[x + (w + 1) * i ] = color_to_paint;
					}
				}
			}
		}
		
		for (int x = 0; x<pixels.length; x++) if (pixels[x] == cols[1] && Math.random() > chance_sub_empty) pixels[x] = 0;
		
		//THIS FOR LOOP HERE CRASHES THE GAME AND FORCES ME TO RESTART MY COMPUTER
		
		try {
		System.out.println(this);
		for (int x = 0; x < this.objects.length; x++) {
		    GameObject o = this.objects[x];
		    if (!o.terrain) continue;

		    // world(center) -> image(top-left) for the object center
		    double cx = o.pos.x - this.pos.x + this.width  / 2.0;
		    double cy = this.height / 2.0 - o.pos.y + this.pos.y;

		    // build an integer rect with safe rounding
		    int sx = (int)Math.floor(cx - o.width  / 2.0);
		    int sy = (int)Math.floor(cy - o.height / 2.0);
		    int sw = (int)Math.ceil(o.width);
		    int sh = (int)Math.ceil(o.height);

		    // clip to terr bounds
		    int ix0 = Math.max(0, sx);
		    int iy0 = Math.max(0, sy);
		    int ix1 = Math.min(terr.getWidth(),  sx + sw);
		    int iy1 = Math.min(terr.getHeight(), sy + sh);

		    int cw = ix1 - ix0;
		    int ch = iy1 - iy0;

		    if (cw <= 0 || ch <= 0) {
		        // Nothing visible inside the room; skip to avoid RasterFormatException.
		        o.sprite = null;
		        continue;
		    }

		    this.objects[x].sprite = terr.getSubimage(ix0, iy0, cw, ch);
		}
		/*for (int x = 0; x<this.objects.length; x++) {
			if (!this.objects[x].terrain) continue;

			
			//System.out.println("INTERSECT: " + intersect);
			
			int xc = (int)(this.objects[x].pos.x - this.pos.x + this.width / 2);
			int yc = (int)(this.height / 2 - this.objects[x].pos.y + this.pos.y);

			Rectangle intersect = Rectangle.intersect_area(new Rectangle(this.width / 2, this.height / 2, this.width, this.height), new Rectangle(xc, yc, this.objects[x].width, this.objects[x].height));
			
			System.out.println("X: " + xc + " Y: " + yc);
			
			this.objects[x].sprite = terr.getSubimage((int)(intersect.pos.x - intersect.width / 2), (int)(intersect.pos.y - intersect.height / 2), (int)intersect.width, (int)intersect.height);
			
			//this.objects[x].sprite = terr.getSubimage(x, x, w, band_width)
		}*/
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		long b = System.nanoTime();
		
		System.out.println("TIME TO GENERATE ROOM SPRITE: " + (double)(b - a) / 1000000);
		
		BezierTerrain.write_image("/Users/gregoryparamonau/Downloads/BEZIER_IMAGE.png", terr);
	}
	
	int clamp(int in, int min, int max) {
		return Math.max(Math.min(max, in), min);
	}
	
	public static void update() {
		for (int x = 0; x<bounds.length; x++) {
			if (Rectangle.intersect(Game.player, bounds[x]) && x == room_num) return;
			if (Rectangle.intersect(Game.player, bounds[x]) && x != room_num) {
				//for (Rectangle temp: bounds) System.out.println(temp);
				//System.out.println("CHANGE ROOM");
				room_num = x;
				respawn_point_set = false;
				Animation.anims = new Animation[0];
				Game.current_room = DataManager.load_room(room_num, Camera.tile_size, false);
				Game.cam.recalculate_bounds();
				return;
			}
		}
		Game.player.combat.hazard_respawn(true);
	}
	
	public void add_enemy(Enemy in) {
		
		System.out.println("spawning enemy " + this.enemies.length);
		Enemy[] out = new Enemy[this.enemies.length + 1];
		
		for (int x = 0; x<this.enemies.length; x++) {
			out[x] = this.enemies[x];
		}
		
		System.out.println("enemy spawning: " + in + " " + (out.length - 1));
		
		out[out.length - 1] = in;
		
		this.enemies = out;
		
		System.out.println("enemy length after spawn " + this.enemies.length);
	}
	public void remove_enemy(int id) {
		Enemy[] out = new Enemy[this.enemies.length - 1];
		
		for (int x =0; x<out.length; x++) {
			out[x] = this.enemies[(x<id? 0: 1) + x];
			out[x].id = x;
		}
		this.enemies = out;
		
		
	}
	
	//two different sort states
	
	//type a --> save function & run_time function
		//movers first
		//normal objects next
		//... rest
		//Beziers, slopes, circles
		//sprites
	
	//type b --> generate sprites
		//Beziers, Slopes, circles first
		//rest after
		//normal objects at the very end
	
	//type a sort to save:
	public void sort_a_save() {
		GameObject[] movers = new GameObject[0];
		GameObject[] objects = new GameObject[0];
		GameObject[] rest = new GameObject[0];
		GameObject[] fancy_plats = new GameObject[0];
		GameObject[] sprites = new GameObject[0];
		
		for (int x = 0; x<this.objects.length; x++) {
			if (this.objects[x].give_class().equals("mover")) {
				movers = GameObject.add_to_arr(movers, this.objects[x]);
				continue;
			}
			if (this.objects[x].give_class().equals("object")) {
				objects = GameObject.add_to_arr(objects, this.objects[x]);
				continue;
			}
			if (this.objects[x].give_class().equals("bezier_terrain") ||
				this.objects[x].give_class().equals("slope") ||
				this.objects[x].give_class().equals("circle")) {
				fancy_plats = GameObject.add_to_arr(fancy_plats, this.objects[x]);
				continue;
			}
			if (this.objects[x].give_class().equals("sprite")) {
				sprites = GameObject.add_to_arr(sprites, this.objects[x]);
				continue;
			}
			
			rest = GameObject.add_to_arr(rest, this.objects[x]);
		}
		
		//make sure any dependent movers are after their object handles
		boolean check = false;
		do {
			check = false;
			for (int x = 0; x<movers.length; x++) {
				if (movers[x].object_handle > x) {
					GameObject temp = this.objects[x];
					this.objects[x] = this.objects[temp.object_handle];
					this.objects[temp.object_handle] = temp;
					this.objects[temp.object_handle].object_handle = x;
					check = true;
				}
			}
		}while(check);
		
		this.objects = new GameObject[0];
		
		this.objects = GameObject.merge_arrays(this.objects, movers);
		this.objects = GameObject.merge_arrays(this.objects, objects);
		this.objects = GameObject.merge_arrays(this.objects, rest);
		this.objects = GameObject.merge_arrays(this.objects, fancy_plats);
		this.objects = GameObject.merge_arrays(this.objects, sprites);
		
		int count = 0;
		while(this.objects[count].give_class().equals("mover")) {
			System.out.println(this.objects[count].give_class());
			
			for (int x = 0; x < this.objects.length; x++) {
				if (this.objects[x].object_handle == this.objects[count].id) {
					this.objects[x].object_handle = count;
				}
			}
			
			count++;
		}
		
		
	}
	
	/*public void sort_room() {
		boolean check = false;
		do {
			check = false;
			for (int x = 0; x<this.objects.length; x++) {
				
				if (x + 1 < this.objects.length && !this.objects[x].give_class().equals("sprite") && this.objects[x + 1].give_class().equals("sprite")) {
					GameObject temp = this.objects[x];
					this.objects[x] = this.objects[x + 1];
					this.objects[x + 1] = temp;
					check = true;
				}
				
				if (this.objects[x].object_handle == -1) continue;
				
				else if (this.objects[x].object_handle > x) {
					GameObject temp = this.objects[x];
					this.objects[x] = this.objects[temp.object_handle];
					this.objects[temp.object_handle] = temp;
					this.objects[temp.object_handle].object_handle = x;
					check = true;
				}
				
			}
		}while(check);
	}*/
	
	public void print_room(BufferedWriter write, double scale) {
		this.sort_a_save();
		try {
			this.scale_room(scale);
			
			write.write(this.toString() + "\n");
			
			write.write(this.objects.length + " \n");
			
			for (int x = 0; x<this.objects.length; x++) {
				write.write(this.objects[x] + "\n");
				
				System.out.println("TS: " + this.objects[x].object_handle);
			}
			
			this.scale_room(1.0 / scale);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void scale_room(double scale) {
		this.scale(scale);
		
		for (int x = 0; x<this.objects.length; x++) {
			this.objects[x].scale(scale);
		}
	}
}
