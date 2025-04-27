package DataManager;

import java.io.BufferedWriter;
import java.io.IOException;

import Enemies.Enemy;
import GameObject.EditObject;
import GameObject.GameObject;
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
		
		//System.exit(0);
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
		Game.player.hazard_respawn(true);
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
