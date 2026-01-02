package LevelEdit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JOptionPane;

import DataManager.DataManager;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Start;
import UI.Button;
import UI.OptionPane;

public class GameEditor {
	
	public static int last_selected_room = -1;
	
	public static int[] select = {-1, -1};
	public static Rectangle[] rooms_display;
	
	public static void update() {
		if (LevelEditor.mouse_pressed && select[0] != -1 && select[1] != -1) {
			System.out.println("update_node");
			rooms_display[select[0]].update_nodes(LevelEditor.mouse_pos, select[1], LevelEditor.grid_size);
		}
		else if (LevelEditor.mouse_pressed && select[0] != -1 && select[1] == -1) {
			System.out.println("update_else");
			rooms_display[select[0]].pos = new Vector2(LevelEditor.mouse_pos);
			rooms_display[select[0]].move(LevelEditor.grid_size);
			
			System.out.println("     " + rooms_display[0]);
		}
	}
	
	public static void draw_view(Graphics g) {
		for (int x = 0; x<rooms_display.length; x++) {
			if (x == select[0]) {
				rooms_display[x].fill = new Color(255, 0, 0, 128);
			}
			else if (x != select[0]) {
				rooms_display[x].fill = new Color(128, 128, 128, 128);
			}
			rooms_display[x].draw(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit");
			
			g.setColor(Color.black);
			g.drawString(x + "", (int)Vector2.converted_pos(rooms_display[x].pos, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit").x, (int)Vector2.converted_pos(rooms_display[x].pos, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit").y);
		}
		
		//new Vector2(0, 0).draw_node(g, Start.pane, LevelEditor.pos.x, LevelEditor.pos.y, "edit", Color.red);
	}
	
	public static void mouse_pressed_function(MouseEvent e) {
		for (int x = 0; x<rooms_display.length; x++) rooms_display[x].save_nodes = rooms_display[x].nodes;

		if (e.getButton() == MouseEvent.BUTTON1) {
			find_select();
			LevelEditor.mouse_pressed = true;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			find_select();
			display_room_options();
		}
		
		last_selected_room = select[0];
	}
	
	public static void mouse_released_function(MouseEvent e) {
		LevelEditor.mouse_pressed = false;
		
		select = new int[] {-1, -1};
	}
	
	public static void display_room_options() {
		System.out.println("disp_room_options");
		
		Start.o_pane = new OptionPane(LevelEditor.click, new String[] {"new room", "edit room", "delete room", "cancel"}, new Runnable[] {
				() -> {
					System.out.println("create");
					//create
					add_to_display();
					DataManager.add_room(rooms_display[rooms_display.length - 1], LevelEditor.room_to_tile, LevelEditor.grid_size);
					
					Start.o_pane.done();
				},
				() -> {
					
					System.out.println("EDITING ROOM");
					
					//System.exit(0);
					//edit
					if (last_selected_room == -1) {
						System.out.println("NO ROOM SELECTED");
						System.exit(0);
						return;
					}
					RoomEditor.room_editing = select[0];
					
					RoomEditor.room = DataManager.load_room(last_selected_room, LevelEditor.grid_size, true);
					
					RoomEditor.room.set_params(Vector2.mult(rooms_display[last_selected_room].pos, LevelEditor.room_to_tile).x, Vector2.mult(rooms_display[last_selected_room].pos, LevelEditor.room_to_tile).y, rooms_display[last_selected_room].width * LevelEditor.room_to_tile, rooms_display[last_selected_room].height * LevelEditor.room_to_tile);
					RoomEditor.room.fill = new Color(0, 255, 255, 128);
					
					
					LevelEditor.pos = new Vector2(RoomEditor.room.pos.x, RoomEditor.room.pos.y);
					
					LevelEditor.mouse_pressed = false;
					
					RoomEditor.in_managing_menu = false;
					
					LevelEditor.in_room_editor = true;
					
					RoomEditor.object_select = new int[] {-1, -1};
					
					Start.o_pane.done();
					
					last_selected_room = -1;
				}, 
				() -> {
					System.out.println("delete");
					//delete
					if (last_selected_room == -1) {
						return;
					}
					DataManager.save(null, last_selected_room, LevelEditor.grid_size, true, DataManager.DATA_FILE);
					delete_from_display(last_selected_room);
					
					Start.o_pane.done();
				},
				() -> {
					Start.o_pane.done();
				}
		});
		
		/*JList list = new JList(new String[] {"Create New Room", "Edit Room", "Delete Room"});
		JOptionPane.showMessageDialog(Start.frame, list, "Multi-Select Example", JOptionPane.PLAIN_MESSAGE);
		
		
		if (list.getSelectedIndices()[0] == 0) {
			System.out.println("create");
			//create
			add_to_display();
			DataManager.add_room(rooms_display[rooms_display.length - 1], LevelEditor.room_to_tile, LevelEditor.grid_size);
		}
		if (list.getSelectedIndices()[0] == 1) {
			System.out.println("edit");
			//edit
			if (select[0] == -1) {
				JOptionPane.showMessageDialog(Start.frame, "No Room Selected");
				return;
			}
			if (JOptionPane.showConfirmDialog(Start.frame, "Edit this room?") == 0) {
				RoomEditor.room_editing = select[0];
				
				RoomEditor.room = DataManager.load_room(select[0], LevelEditor.grid_size, true);
				
				RoomEditor.room.set_params(Vector2.mult(rooms_display[select[0]].pos, LevelEditor.room_to_tile).x, Vector2.mult(rooms_display[select[0]].pos, LevelEditor.room_to_tile).y, rooms_display[select[0]].width * LevelEditor.room_to_tile, rooms_display[select[0]].height * LevelEditor.room_to_tile);
				RoomEditor.room.fill = new Color(0, 255, 255, 128);
				
				
				LevelEditor.pos = new Vector2(RoomEditor.room.pos.x, RoomEditor.room.pos.y);
				
				LevelEditor.mouse_pressed = false;
				
				RoomEditor.in_managing_menu = false;
				
				LevelEditor.in_room_editor = true;
				
				RoomEditor.object_select = new int[] {-1, -1};
			}
		}
		if (list.getSelectedIndices()[0] == 2) {
			System.out.println("delete");
			//delete
			if (select[0] == -1) {
				JOptionPane.showMessageDialog(Start.frame, "No Room Selected");
				return;
			}
			
			if (JOptionPane.showConfirmDialog(Start.frame, "Are you sure you want to delete this room?") == 0) {
				DataManager.save(null, select[0], LevelEditor.grid_size, true);
				delete_from_display(select[0]);
			}
			else return;
		}*/
		//return list.getSelectedIndices()[0];
		//System.out.println(Arrays.toString(list.getSelectedIndices()));
	}
	
	public static void find_select() {
		System.out.println("find_select");
		select[0] = -1;
		select[1] = -1;
		for (int x = 0; x<rooms_display.length; x++) {
			for (int y = 0; y<4; y++) {
				if (Vector2.dist(rooms_display[x].nodes[y], LevelEditor.click) < Vector2.radius) {
					select[0] = x;
					select[1] = y;
					return;
				}
			}
			if (Rectangle.intersect(rooms_display[x], new Rectangle(LevelEditor.click.x, LevelEditor.click.y, 0, 0))) {
				select[0] = x;
				return;
			}
		}
	}
	
	
	public static void delete_from_display(int place) {
		System.out.println("-disp");
		Rectangle[] temp = new Rectangle[rooms_display.length - 1];
		for (int x = 0; x<temp.length; x++) {
			temp[x] = rooms_display[x >= place ? x + 1 : x];
		}
		rooms_display = temp;
	}
	
	public static void add_to_display() {
		System.out.println("+disp");
		Rectangle[] temp = new Rectangle[rooms_display.length + 1];
		for (int x = 0; x<rooms_display.length; x++) {
			temp[x] = rooms_display[x];
		}
		temp[rooms_display.length] = new Rectangle(LevelEditor.click.x, LevelEditor.click.y, 64, 64);
		temp[rooms_display.length].move(LevelEditor.grid_size);
		
		System.out.println("HELPPPP");
		
		rooms_display = temp;
	}
	
	
	
}
