package LevelEdit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import DataManager.Room;
import GameObject.GameObject;
import Logic.Line;
import Logic.Rectangle;
import Logic.Vector2;

public class RoomEditor {
	public String sprite = "/platform_textures/mouth_sprite_sheet.png";
	
	public static Room room = null;
	public static int room_editing = 0;
	
	public static int[] object_select = {-1, -1};
	public static String object_type = "mover";
	
	public static String[] object_names = {"object", "bouncer", "circle", "mover", "slope"};
	
	public static boolean in_managing_menu = false;
	
	public static void update() {
		
		if (in_managing_menu) return;
		
		if (LevelEditor.mouse_pressed && object_select[0] != -1 && object_select[1] != -1) {
			room.objects[object_select[0]].update_nodes(LevelEditor.mouse_pos, object_select[1], LevelEditor.grid_size);
		}
		if (LevelEditor.mouse_pressed && object_select[0] != -1 && object_select[1] == -1) {
			room.objects[object_select[0]].pos = new Vector2(LevelEditor.mouse_pos);
			room.objects[object_select[0]].move(LevelEditor.grid_size);
		}
		
	}
	
	public static void draw_view(Graphics g) {
				
		System.out.println("26");
		try {
			room.draw(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
		}catch(Exception e) {e.printStackTrace();}	
		
		
		//room.draw(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
		
		for (int x = 0; x<room.objects.length; x++) room.objects[x].draw_object(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
		
		if (object_select[0] != -1 && object_select[1] != -1) room.objects[object_select[0]].nodes[object_select[1]].draw_node(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit", Color.red);
		
		draw_tile_grid(g);

	}
	
	public static void draw_tile_grid(Graphics g) {
		
		Vector2 corner = new Vector2(room.pos.x - room.width / 2, room.pos.y + room.height / 2);
		int tile_temp = LevelEditor.grid_size * 16;
		
		g.setColor(Color.red);
		for (int x = 0; x<room.width / tile_temp; x++) {
			for (int y = 0; y<room.height / tile_temp; y++) {
				new Rectangle(corner.x + tile_temp * (x + 0.5), corner.y - tile_temp * (y + 0.5), tile_temp - 2, tile_temp - 2).draw_border(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
				new Rectangle(corner.x + tile_temp * (x + 0.5), corner.y - tile_temp * (y + 0.5), tile_temp, tile_temp).draw_border(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
			}
		}
	}
	
	public static void mouse_pressed_function(MouseEvent e) {
		find_select();
		LevelEditor.mouse_pressed = true;
		
		if (e.getButton() == 1) {
			//left click: ie mouse 1
				//if inside shape itself: move shape / platform around
				//if clicking on node: change shape & drag node around
		}
		if (e.getButton() == 2) {
			
		}
		if (e.getButton() == 3) {
			display_options();
			
			
			//right click: ie mouse 3
				//if inside shape:
					//create shape
					//delete shape
				//if outisde of any shape:
					//create shape
		}
	}
	
	public static void mouse_released_function(MouseEvent e) {
		LevelEditor.mouse_pressed = false;
		
		object_select = new int[] {-1, -1};
	}
	
	public static void display_options() {
		in_managing_menu = true;
		
		JList list = new JList(new String[] {""});
		
		//TODO: make object properties window & make changes to stuff
		
		if (object_select[0] == -1) list = new JList(new String[] {"Properties", "New Object"});
		else list = new JList(new String[] {"Properties", "New Object", "Delete Object"});
		
		JOptionPane.showMessageDialog(LevelEditor.frame, list, "Select Action", JOptionPane.PLAIN_MESSAGE);
		
		if (list.getSelectedIndices()[0] == 0) {
			//properties
			if (object_select[0] == -1) show_object_select();
			
			if (object_select[0] != -1) show_properties_panel();
			
		}
		if (list.getSelectedIndices()[0] == 1) {
			//create
			add_object();
			object_select = new int[] {-1, -1};
		}
		if (list.getSelectedIndices()[0] == 2) {
			//delete
			delete_object(object_select[0]);
			
			object_select = new int[] {-1, -1};
		}
		
		in_managing_menu = false;
	}
	
	public static void show_properties_panel() {
		in_managing_menu = true;
		
		in_managing_menu = room.objects[object_select[0]].properties();
				
		//TODO: Make code ahaha
	}
	public static void show_object_select() {
		in_managing_menu = true;
		
		JList names = new JList(object_names);
		
		JOptionPane.showMessageDialog(LevelEditor.frame, names, "Select Object Type", JOptionPane.PLAIN_MESSAGE);

		object_type = object_names[names.getSelectedIndices()[0]];
	}
	
	public static void find_select() {
		
		for (int x = 0; x<room.objects.length; x++) {
			for (int y = 0; y<room.objects[x].nodes.length; y++) {
				if (Vector2.dist(room.objects[x].nodes[y], LevelEditor.click) < Vector2.radius_edit) {
					object_select = new int[] {x, y};
					return;
				}
			}
		}
		
		for (int x = 0; x<room.objects.length; x++) {
			if (Rectangle.intersect(room.objects[x], new Rectangle(LevelEditor.click.x, LevelEditor.click.y, 0, 0))) {
				object_select = new int[] {x, -1};
				return;
			}
		}
		
		

		//left click: ie mouse 1
			//if inside shape itself: move shape / platform around
			//if clicking on node: change shape & drag node around
		//right click: ie mouse 3
			//if inside shape:
				//create shape
				//delete shape
			//if outisde of any shape:
				//create shape
		
		
		
		
		
		/*for (int x = 0; x<room.objects.length; x++) {
			if (Rectangle.intersect(room.objects[x], new Rectangle(LevelEditor.click.x, LevelEditor.click.y, 0, 0))) {
				delete_object(x);
				return;
			}
		}
		
		add_object();*/
	}
	
	public static void delete_object(int place) {
		GameObject[] temp = new GameObject[room.objects.length - 1];
		for (int x = 0; x<temp.length; x++) {
			temp[x] = room.objects[x >= place ? x + 1 : x];
		}
		room.objects = temp;
	}
	
	public static void add_object() {
		GameObject[] temp = new GameObject[room.objects.length + 1];
		for (int x = 0; x<room.objects.length; x++) {
			temp[x] = room.objects[x];
		}
		
		temp[room.objects.length] = GameObject.create_default_game_object(object_type, LevelEditor.click);
		temp[room.objects.length].move(LevelEditor.grid_size);
		
		room.objects = temp;
	}

}
