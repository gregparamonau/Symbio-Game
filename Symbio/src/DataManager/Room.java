package DataManager;

import java.io.BufferedWriter;
import java.io.IOException;

import GameObject.GameObject;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;
import Rendering.Camera;

public class Room extends Rectangle{
	
	public GameObject[] objects;
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
		
		for (int x = 0; x<this.objects.length; x++) {
			this.objects[x].generate_sprite(this.objects, this.objects[x].sprite_name);
		}
		
		//TODO: start textures
		long b = System.nanoTime();
		System.out.println("	room load time: " + (double)(b-a) / 1000000);
	}
	
	public static void update() {
		for (int x = 0; x<bounds.length; x++) {
			if (Rectangle.intersect(Game.player, bounds[x]) && x == room_num) return;
			if (Rectangle.intersect(Game.player, bounds[x]) && x != room_num) {
				//for (Rectangle temp: bounds) System.out.println(temp);
				//System.out.println("CHANGE ROOM");
				room_num = x;
				respawn_point_set = false;
				Game.current_room = DataManager.load_room(room_num, Camera.tile_size);
				Game.cam.recalculate_bounds();
				return;
			}
		}
		Game.player.hazard_respawn(true);
	}
	public void sort_room() {
		boolean check = false;
		do {
			check = false;
			for (int x = 0; x<this.objects.length; x++) {
				
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
	}
	
	public void print_room(BufferedWriter write, double scale) {
		this.sort_room();
		try {
			this.scale_room(scale);
			
			write.write(this.toString() + "\n");
			
			write.write(this.objects.length + " \n");
			
			for (int x = 0; x<this.objects.length; x++) {
				write.write(this.objects[x].toString() + "\n");
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
