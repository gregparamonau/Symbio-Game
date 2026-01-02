package GameObject.EditorObjects;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JList;
import javax.swing.JOptionPane;

import GameObject.EditObject;
import GameObject.GameObject;
import GameObject.Objects.Mover;
import LevelEdit.LevelEditor;
import LevelEdit.RoomEditor;
import Logic.Vector2;
import Main.Start;
import UI.OptionPane;

public class MoverEdit extends Mover{
	
	public MoverEdit() {}
	public MoverEdit(double[] in, String sprite, int id) {
		super(in, sprite, id);
	}
	
	
	public static MoverEdit default_mover(Vector2 loc) {
		
		double[] temp = new double[5 + default_stops * 2];
		
		temp[0] = 2 * LevelEditor.grid_size;
		temp[1] = 2 * LevelEditor.grid_size;
		temp[2] = default_time;
		
		temp[3] = default_stops;
		
		for (int x = 4; x < temp.length - 1; x++) {
			if (x % 2 == 0) temp[x] = loc.x + x * LevelEditor.grid_size;
			if (x % 2 == 1) temp[x] = loc.y;
			
		}
		
		temp[temp.length - 1] = -1;
		
		for (int x = 0; x<temp.length; x++) {
			System.out.print(temp[x] + " ");
		}
		MoverEdit out = new MoverEdit(temp, sprite_name_default, 0);
		
		out.update_dimensions();
		
		return out;
	}
	
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}
	public void update_dimensions() {
		this.pos = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[1]), 0.5);

		this.width = Math.abs(this.nodes[0].x - this.nodes[1].x);
		this.height = Math.abs(this.nodes[0].y - this.nodes[1].y);
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = new Vector2[this.stops.length + 2];
		temp[0] = new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2);
		temp[1] = new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2);
		
		for (int x = 0; x<this.stops.length; x++) temp[x + 2] = this.stops[x];
		
		this.nodes = temp;
		start = false;
	}
	
	public boolean properties() {
		//TODO: maybe make 'add_stop' option
		
		//rethink
		//add stop
		//delete stop
		//time for 1 cycle
		//objecthandle
		Start.o_pane = new OptionPane(LevelEditor.click_save, new String[] {"add stop", "delete stop", "time", "object handle"}, new Runnable[] {
				() -> {
					default_stops++;
					this.stops = Vector2.add_to_arr(this.stops, LevelEditor.click_save);
					Vector2[] temp = {new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2), new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2)};
					
					this.nodes = Vector2.merge_arr(temp, this.stops);
					
					
					Start.o_pane.done();
					return;
				}, 
				() -> {
					default_stops--;
					this.stops = Vector2.remove_from_arr(this.stops, RoomEditor.object_select[1]);
					
					Vector2[] temp = {new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2), new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2)};

					this.nodes = Vector2.merge_arr(temp, this.stops);
					
					Start.o_pane.done();
					return;
				},
				() -> {
					Start.o_pane = new OptionPane(LevelEditor.click_save, "time", () -> {
						double input = Double.parseDouble(Start.o_pane.input_text);
						this.time = input;
					});
				}, 
				() -> {
					Start.o_pane = new OptionPane(LevelEditor.click_save, "object handle", () -> {
						int input = Integer.parseInt(Start.o_pane.input_text);
						if (input >= 0 && input < RoomEditor.room.objects.length) {
						    if (RoomEditor.room.objects[input].give_class().equals("mover")) {
						        RoomEditor.room.objects[RoomEditor.last_object_selected].object_handle = input;
						    } else {
						    	System.out.println("	SET ID -1");
						        RoomEditor.room.objects[RoomEditor.last_object_selected].object_handle = -1;
						    }
						}
						System.out.println(RoomEditor.room.objects[RoomEditor.last_object_selected].object_handle);
					});
				}
		});
		
		/*JList mods = new JList(new String[] {"Stop Number", "Object Handle", "Time"});
		
		JOptionPane.showMessageDialog(Start.frame, mods, "Select Modification", JOptionPane.PLAIN_MESSAGE);
		
		if (mods.getSelectedIndex() == 0) {
			default_stops = Integer.parseInt(JOptionPane.showInputDialog(Start.frame, "How Many Stops?"));
			RoomEditor.room.objects[this.id] = EditObject.create_default_game_object(RoomEditor.object_type, LevelEditor.click);
		}
		if (mods.getSelectedIndex() == 1) {
			this.object_handle = Integer.parseInt(JOptionPane.showInputDialog(Start.frame, "Attach to Object"));
			if (!RoomEditor.room.objects[this.object_handle].give_class().equals("mover")) this.object_handle = -1;
		}
		if (mods.getSelectedIndex() == 2) {
			this.time = Double.parseDouble(JOptionPane.showInputDialog(Start.frame, "Input Time Duration"));
		}	*/	
		
		return false;
	}
	
	public String toString() {
		String stops = "";
		for (int x = 2; x<this.nodes.length; x++) stops += this.nodes[x].x + " " + this.nodes[x].y + " ";
		return "mover " + this.width + " " + this.height + " " + this.time + " " + this.stops.length + " " + stops + this.object_handle + " "  + sprite_name_default + " ";
	}
}
