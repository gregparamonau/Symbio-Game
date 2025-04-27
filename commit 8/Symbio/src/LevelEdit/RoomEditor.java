package LevelEdit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import DataManager.Room;
import GameObject.EditObject;
import GameObject.GameObject;
import Logic.Line;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Start;
import UI.OptionPane;

public class RoomEditor {	
	public static Room room = null;
	public static int room_editing = 0;
	
	public static int[] object_select = {-1, -1};
	public static int last_object_selected;
	
	public static String object_type = "object";
	public static String current_area = "mouth";
	
	public static String[] object_names = {"object", "spawner", "acid", "bezier_terrain", "bouncer", "circle", "mover", "oneway", "slope", "sprite"};
	public static String[] area_names = {"mouth"};
	
	public static boolean in_managing_menu = false;
	
	public static void update() {
		
		if (in_managing_menu) return;
		
		if (LevelEditor.mouse_pressed && object_select[0] != -1 && object_select[1] != -1) {
			room.objects[object_select[0]].update_nodes(LevelEditor.mouse_pos, object_select[1], LevelEditor.grid_size);
		}
		if (LevelEditor.mouse_pressed && object_select[0] != -1 && object_select[1] == -1) {
			room.objects[object_select[0]].move(LevelEditor.grid_size);
		}
		
		for (int x = 0; x<room.objects.length; x++) {
			System.out.println("	" + room.objects[x] + " " + room.objects[x].object_handle);
		}
		
	}
	
	public static void draw_view(Graphics g) {
		try {
			room.draw(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
		}catch(Exception e) {e.printStackTrace();}	
		
		
		//room.draw(g, LevelEditor.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
		
		for (int x = 0; x<room.objects.length; x++) {
			if (x == last_object_selected) {
				room.objects[x].fill = Color.red;
			}
			if (x != last_object_selected) room.objects[x].fill = new Color(153, 217, 140, 192);
			
			
			room.objects[x].draw_object(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
			
			Vector2 conv = Vector2.converted_pos(room.objects[x].pos, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
			g.setColor(Color.black);
			g.drawString(x + "", (int)conv.x, (int)conv.y);
		}
		
		if (object_select[0] != -1 && object_select[1] != -1) room.objects[object_select[0]].nodes[object_select[1]].draw_node(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit", Color.red);
		
		draw_tile_grid(g);

	}
	
	public static void draw_tile_grid(Graphics g) {
		
		Vector2 corner = new Vector2(room.pos.x - room.width / 2, room.pos.y + room.height / 2);
		int tile_temp = LevelEditor.grid_size * 16;
		
		g.setColor(Color.red);
		for (int x = 0; x<room.width / tile_temp; x++) {
			for (int y = 0; y<room.height / tile_temp; y++) {
				new Rectangle(corner.x + tile_temp * (x + 0.5), corner.y - tile_temp * (y + 0.5), tile_temp - 2, tile_temp - 2).draw_border(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
				new Rectangle(corner.x + tile_temp * (x + 0.5), corner.y - tile_temp * (y + 0.5), tile_temp, tile_temp).draw_border(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
			}
		}
	}
	
	public static void mouse_pressed_function(MouseEvent e) {
		find_select();
		for (int x = 0; x<room.objects.length; x++) room.objects[x].save_nodes = room.objects[x].nodes;
		LevelEditor.mouse_pressed = true;
		
		if (e.getButton() == 1) {
			//for (int x = 0; x<room.objects.length; x++) room.objects[x].save_nodes = room.objects[x].nodes;
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
		
		last_object_selected = object_select[0];
	}
	
	public static void mouse_released_function(MouseEvent e) {
		LevelEditor.mouse_pressed = false;
		
		object_select = new int[] {-1, -1};
	}
	
	public static void display_options() {
		in_managing_menu = true;
		
		LevelEditor.click_save = LevelEditor.click;
		
		String[] list = new String[0];
		
		if (last_object_selected == -1) list = new String[] {"properties", "new object", "cancel"};
		else list = new String[] {"properties", "new object", "cancel", "delete object"};
		
		Start.o_pane = new OptionPane(LevelEditor.click_save, list, new Runnable[] {
				() -> {
					System.out.println("ROOM PROPERTIES");
					
					if (last_object_selected == -1) show_room_properties_panel();
					
					if (last_object_selected != -1) show_object_properties_panel();
				},
				() -> {
					add_object();
					object_select = new int[] {-1, -1};
					
					Start.o_pane.done();
					return;
				}, 
				() -> {
					Start.o_pane.done();
					return;
				},
				() -> {
					delete_object(last_object_selected);
					
					object_select = new int[] {-1, -1};
					
					Start.o_pane.done();
					return;
				}
		});
		
		/*JList list = new JList(new String[] {""});
		
		//TODO: make object properties window & make changes to stuff
		
		if (object_select[0] == -1) list = new JList(new String[] {"Properties", "New Object"});
		else list = new JList(new String[] {"Properties", "New Object", "Delete Object"});
		
		JOptionPane.showMessageDialog(Start.frame, list, "Select Action", JOptionPane.PLAIN_MESSAGE);
		
		if (list.getSelectedIndices()[0] == 0) {
			//properties
			if (object_select[0] == -1) show_room_properties_panel();
			
			if (object_select[0] != -1) show_object_properties_panel();
			
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
		}*/
		
		in_managing_menu = false;
	}
	
	public static void show_object_properties_panel() {
		in_managing_menu = true;
		
		System.out.println("OBJECT PROPERTIES PANEL");
		
		System.out.println("OBJECT SELECT: " + object_select[0] + " " + object_select[1]);
		
		//TODO: properties panels for all objects
		in_managing_menu = room.objects[last_object_selected].properties();
		
		object_select = new int[] {-1, -1};
				
		//TODO: Make code ahaha
	}
	public static void show_room_properties_panel() {
		in_managing_menu = true;
		
		System.out.println("ROOM PROPERTIES PANEL");
		
		String[] option_names = {"object type", "area", "cancel"};

		
		Start.o_pane = new OptionPane(LevelEditor.click_save, option_names, new Runnable[] {
				() -> {
					System.out.println("OBJECT SELECT");
					show_object_select();
				},
				() -> {
					System.out.println("AREA SELECT");
					show_area_select();
				},
				() -> {
					Start.o_pane.done();
					return;
				}
		});
		
		System.out.println("BUTTON LENGTH: " + Start.o_pane.buttons.length);
		
		/*JList options = new JList(option_names);
		
		JOptionPane.showMessageDialog(Start.frame, options, "Select Option Menu", JOptionPane.PLAIN_MESSAGE);
		
		if (options.getSelectedIndices()[0] == 0) {
			show_object_select();
		}
		if (options.getSelectedIndices()[0] == 1) {
			show_area_select();
		}*/
	}
	public static void show_object_select() {
		in_managing_menu = true;
		
		Runnable[] temp = new Runnable[object_names.length];
		
		for (int x = 0; x < temp.length; x++) {
			final int index = x;
			temp[x] = () -> {
				
				System.out.println("INDEX");
				object_type = cut(object_names[index] + " ");
				Start.o_pane.done();
				return;
			};
		}
		
		System.out.println("RUNNING OBJECT SELECT");
		
		Start.o_pane = new OptionPane(LevelEditor.click_save, object_names, temp);
		
		/*JList names = new JList(object_names);
		
		JOptionPane.showMessageDialog(Start.frame, names, "Select Object Type", JOptionPane.PLAIN_MESSAGE);

		object_type = object_names[names.getSelectedIndices()[0]];*/
	}
	public static void show_area_select() {
		in_managing_menu = true;
		
		Runnable[] temp = new Runnable[area_names.length];
		
		for (int x = 0; x < temp.length; x++) {
			final int index = x;
			temp[x] = () -> {
				current_area = cut(area_names[index]);
				Start.o_pane.done();
				return;
			};
		}
		
		Start.o_pane = new OptionPane(LevelEditor.click_save, area_names, temp);
		
		/*JList areas = new JList(area_names);
		
		JOptionPane.showMessageDialog(Start.frame, areas, "Select Area", JOptionPane.PLAIN_MESSAGE);
		
		current_area = area_names[areas.getSelectedIndices()[0]];*/
	}
	
	public static void find_select() {
		
		for (int x = 0; x<room.objects.length; x++) {
			for (int y = 0; y<room.objects[x].nodes.length; y++) {
				if (Vector2.dist(room.objects[x].nodes[y], LevelEditor.click) < Vector2.radius_edit) {
					
					System.out.println("OBJECT SELECT: " + x + " " + y);
					object_select = new int[] {x, y};
					return;
				}
			}
		}
		
		for (int x = 0; x<room.objects.length; x++) {
			if (Rectangle.intersect(room.objects[x], new Rectangle(LevelEditor.click.x, LevelEditor.click.y, 0, 0))) {
				System.out.println("OBJECT SELECT: " + x + " -1");
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
		
		temp[room.objects.length] = EditObject.create_default_game_object(object_type, LevelEditor.click);
		temp[room.objects.length].clip_nodes(LevelEditor.grid_size);
		
		temp[room.objects.length].id = room.objects.length;
		
		room.objects = temp;
	}
	
	
	public static String[] separate(String line) {
		int count = 0;
		for (int x = 0; x<line.length(); x++) if (line.charAt(x) == ' ') count++;
		String[] out = new String[count];
		for (int x = 0; x<count; x++) {
			out[x] = cut(line);
			line = clear(line);
		}
		return out;
	}
	
	public static String cut(String line) {
		return line.substring(0, line.indexOf(" "));
	}
	
	public static String clear(String in) {
		return in.substring(in.indexOf(" ") + 1);
	}

}
