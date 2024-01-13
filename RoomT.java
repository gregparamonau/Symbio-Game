package Symbio.Entity.PlatEntities;

import Symbio.Entity.PlatEntity;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

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
	public RoomT(int a, int b, int c, int d, int e, int f, int g, int id) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.room_to = e;
		this.room_to_xpos = f;
		this.room_to_ypos = g;
		this.start_nodes();
		this.fill = Color.blue;
		
		this.id = id;
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
	
	public void draw(Graphics g, JPanel pane, int xin, int yin, String location) {
		g.setColor(Color.blue);
		if (location.equals("edit")) g.fillRect((int)(this.pos.x - this.width/2 + pane.getWidth()/2 - xin), (int)(- this.pos.y - this.height/2 + pane.getHeight()/2 - yin), this.width, this.height);
		else if (location.equals("game")) {
			g.fillRect((int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2) - this.height/2), this.width, this.height);
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
			}
		}
	}
	
	public String toString() {
		return ("roomt " + (int)this.pos.x + " " + (int)this.pos.y + " " + this.width + " " + this.height + this.room_to + " " + this.room_to_xpos + " " + this.room_to_ypos + " ");
	}
	public String give_class() {
		return "roomt";
	}
}
