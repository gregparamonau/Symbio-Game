package Enemies.Enemies;

import java.awt.Color;

import Enemies.Enemy;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;

public class Scuttler extends Enemy{
	public static final double save_width = 12, save_height = 8;
	
	public static final double vel = 0.6; //subject to change
	
	int dir = 1;
	
	public Color fill = Color.cyan;
	
	public Scuttler(double a, double b) {
		this.pos = new Vector2(a, b);
		
		this.width = save_width;
		this.height = save_height;
	}
	
	public void update() {
		
		this.move(this.ai());
	}
	
	public Vector2 ai() {
		Vector2 out = new Vector2(this.dir * vel, 0);
		
		if (this.collide(out) || !this.collide(Vector2.add(out, new Vector2(this.dir * this.width, -1.2)))) dir *= -1;
		
		return new Vector2(this.dir * vel, 0);
	}
	
	public boolean collide(Vector2 move) {
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			if (!Game.current_room.objects[x].solid) continue;
			if (Game.current_room.objects[x].collide_with(new Rectangle(this.pos.x + move.x, this.pos.y + move.y, this.width, this.height), false)) {
				return true;
			}
		}
		return false;
	}
	public String toString() {
		return "Scuttler " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.code;
	}
}
