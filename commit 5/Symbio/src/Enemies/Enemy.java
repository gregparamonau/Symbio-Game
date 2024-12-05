package Enemies;

import java.awt.Graphics;

import javax.swing.JPanel;

import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;

public class Enemy extends Rectangle{
	
	public String code;
	
	public Enemy() {}
	public Enemy(double a, double b) {
		this.pos = new Vector2(a, b);
	}
	
	public void update() {

	}
	
	public void move(Vector2 move) {
		this.pos.add(move);
		
		this.displace();
	}
	public void displace() {
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			Game.current_room.objects[x].displace_entity(this, 0);
		}
	}
	
	public Vector2 ai() {
		return new Vector2(0, 0);
	}
	
	public void draw_enemy(Graphics g, JPanel pane, double xin, double yin, String location) {
		
		this.draw(g, pane, xin, yin, location);
		
		//this.draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, this.sprite, "game");
	}
}
