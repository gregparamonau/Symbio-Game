package GameObject.EditorObjects;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JList;
import javax.swing.JOptionPane;

import GameObject.EditObject;
import GameObject.GameObject;
import GameObject.Objects.EnemySpawner;
import LevelEdit.LevelEditor;
import LevelEdit.RoomEditor;
import Logic.Vector2;
import Main.Start;
import UI.OptionPane;

public class EnemySpawnerEdit extends EnemySpawner{
	public EnemySpawnerEdit(double a, double b, double c, String enemy_type, boolean respawn, int id) {
		super(a, b, c, enemy_type, respawn, id);
	}
	
	public static EnemySpawnerEdit default_spawner(Vector2 loc) {
		return new EnemySpawnerEdit(loc.x, loc.y, 1, default_enemy, default_respawn, 0);
	}
	
	public void generate_sprite(GameObject[] objects, String in) {
		this.sprite = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		
		this.pos = in;
		
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}
	public void update_dimensions() {
		
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {
				new Vector2(this.pos.x, this.pos.y)
		};
		//System.out.println("rect " + this.nodes.length);
		this.nodes = temp;
		start = false;
	}
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		if (this.nodes[0] != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	public boolean properties() {
		
		Start.o_pane = new OptionPane(LevelEditor.click_save, new String[] {"attach to object", "enemy type", "enemy count", "respawn"}, new Runnable[] {
				//TODO: make text addition functionality for OptionPane
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
				}, 
				() -> {
					Start.o_pane = new OptionPane(LevelEditor.click_save, "enemy type", () -> {
						String input = Start.o_pane.input_text;
						default_enemy = input;
						this.enemy_type = input;
					});
				}, 
				() -> {
					Start.o_pane = new OptionPane(LevelEditor.click_save, "enemy count", () -> {
						int input = Integer.parseInt(Start.o_pane.input_text);
						
						default_enemy_count = input;
						this.enemy_count = input;
					});
				},
				() -> {
					Start.o_pane = new OptionPane(LevelEditor.click_save, "respawn enemies", () -> {
						boolean input = Start.o_pane.input_text.equals("true");
						
						default_respawn = input;
						this.respawn = input;
					});
				}
		});
		
		/*JList mods = new JList(new String[] {"Attach to Object", "Enemy Type", "Enemy Count", "Respawn"});
		
		if (mods.getSelectedIndex() == 0) {
			this.object_handle = Integer.parseInt(JOptionPane.showInputDialog(Start.frame, "Attach to Object"));
			if (!RoomEditor.room.objects[this.object_handle].give_class().equals("mover")) this.object_handle = -1;
		}
		if (mods.getSelectedIndex() == 1) {
			default_enemy = JOptionPane.showInputDialog(Start.frame, "Enemy Type [full path]");
			this.enemy_type = default_enemy;
		}
		if (mods.getSelectedIndex() == 2) {
			default_enemy_count = Integer.parseInt(JOptionPane.showInputDialog(Start.frame, "How Many Enemies to Spawn?"));
			this.enemy_count = default_enemy_count;
		}
		if (mods.getSelectedIndex() == 3) {
			default_respawn = JOptionPane.showInputDialog(Start.frame, "Respawn Enemies? [yes/no]").equals("yes");
			this.respawn = default_respawn;
		}*/
		
		
		return false;
		
	}
	
	public String toString() {
		return "spawner " + this.pos.x + " " + this.pos.y + " " + this.enemy_count + " " + this.enemy_type + " " + (this.respawn? 1 : 0) + " ";
	}
}
