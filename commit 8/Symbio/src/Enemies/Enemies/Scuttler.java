package Enemies.Enemies;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Enemies.Enemy;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;

public class Scuttler extends Enemy{
	public static final double save_width = 16, save_height = 10;
	
	public static final double vel = 0.6; //subject to change
	
	public static final int health_save = 20;
	
	int dir = 1;
	
	public Color fill = Color.cyan;
	
	boolean flipped = true;
	
	public Scuttler(double a, double b) {
		this.pos = new Vector2(a, b);
		
		this.width = save_width;
		this.height = save_height;
		
		this.health = health_save;
		
		this.anim = new Animation("/enemy_textures/scuttler.png", this.pos, 10, 6, this.flipped, true);
		this.anim.start();
		
		long l = System.nanoTime();
		this.dir = (l % 2 == 0 ? -1 : 1);
	}
	
	public void update() {
		this.anim.flip = this.dir == 1;
		if (this.damage_cooldown > 0) this.damage_cooldown--;
		if (this.freeze > 0) {
			this.freeze--;
			return;
		}
		
		this.move(this.ai());
	}
	
	public Vector2 ai() {
		Vector2 out = new Vector2(this.dir * vel, 0);
		
		if (this.collide(out) || !this.collide(Vector2.add(out, new Vector2(this.dir * this.width, -1.2)))) {
			dir *= -1;
			if (this.momentum.length() == 0) this.freeze = 5;
		}
		
		return new Vector2(this.dir * vel, 0);
	}
	
	public void damage_function() {
		this.freeze = 5;
		int temp = Utility.sign(Game.player.slash_dir.x);
		this.dir = (temp == 0 ? this.dir : temp);
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
	
	public void draw_enemy(Graphics g, JPanel pane, double xin, double yin, String location) {
		this.anim.play(false, Vector2.zero, this.dir == 1, g, pane, xin, yin, location);
		
		////g.drawString("health: " + this.health, (int)temp.x, (int)temp.y);
		
		//this.draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, this.sprite, "game");
	}
	
	public String toString() {
		return "Scuttler " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.code;
	}
}
