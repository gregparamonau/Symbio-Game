package Symbio.Entity.PlatEntities;

import Symbio.Entity.PlatEntity;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import LevelEdit.LevelCreator;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Vector2;
import Symbio.Game;
import Symbio.Player;

public class RoomT extends PlatEntity{		
	public RoomT() {}
	public RoomT(int a, int b) {
		this.pos = new Vector2(a, b);
		this.width = 50;
		this.height = 125;
	}
	public RoomT(double a, double b, double c, double d, double e, double f, double g, int id) {
		this.pos = new Vector2(a, b);
		this.width = (int)c;
		this.height = (int)d;
		this.room_to = (int)e;
		this.room_to_xpos = (int)f;
		this.room_to_ypos = (int)g;
		this.start_nodes();
		this.fill = Color.blue;
		
		this.id = id;
	}
	
	public boolean collide_with(Rectangle in, boolean player) {
		return false;
	}
	
	public void collision_action() {
		Game.current_room = Game.rooms[room_to];
		Game.player_xpos = this.room_to_xpos;
		Game.player_ypos = this.room_to_ypos;
		Game.player.set_position(Game.player_xpos, Game.player_ypos);
		Game.player.start_trail(0);
		Game.cam.recalculate_bounds();
		Game.cam.set_position(Game.player.pos);
	}
	
	public void draw_plat_ent(Graphics g, JPanel in, int xin, int yin, String location) {
		if (location.equals("edit")) this.draw(g, in, xin, yin, location);
		else if (location.equals("game")) {
			if (Game.debug_mode) {
				this.draw_border(g, in, xin, yin, location);
				this.draw(g, in, xin, yin, location);
			}
		}
	}
	
	public String toString() {
		return ("roomt " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.room_to + " " + this.room_to_xpos + " " + this.room_to_ypos + " ");
	}
	public String give_class() {
		return "roomt";
	}
	public static void create_props(JFrame temp, JTextField[] fields, JLabel[] labels) {		
		fields[0] = new JTextField(LevelCreator.plat_ent_type + "");
		fields[0].setBounds(50, 50, 200, 25);
		fields[0].setVisible(true);
		temp.add(fields[0]);
		
		labels[0] = new JLabel("type");
		labels[0].setBounds(300, 50, 100, 50);
		labels[0].setVisible(true);
		temp.add(labels[0]);
		
		fields[1] = new JTextField(LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]].room_to + "");
		fields[1].setBounds(50, 100, 200, 25);
		fields[1].setVisible(true);
		temp.add(fields[1]);
		
		labels[1] = new JLabel("room_to");
		labels[1].setBounds(300, 100, 100, 50);
		labels[1].setVisible(true);
		temp.add(labels[1]);
		
		fields[2] = new JTextField(LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]].room_to_xpos + "");
		fields[2].setBounds(50, 150, 200, 25);
		fields[2].setVisible(true);
		temp.add(fields[2]);
		
		labels[2] = new JLabel("room_to_xpos");
		labels[2].setBounds(300, 150, 100, 50);
		labels[2].setVisible(true);
		temp.add(labels[2]);
		
		fields[3] = new JTextField(LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]].room_to_ypos + "");
		fields[3].setBounds(50, 200, 200, 25);
		fields[3].setVisible(true);
		temp.add(fields[3]);
		
		labels[3] = new JLabel("room_to_ypos");
		labels[3].setBounds(300, 200, 100, 50);
		labels[3].setVisible(true);
		temp.add(labels[3]);
	}
}
