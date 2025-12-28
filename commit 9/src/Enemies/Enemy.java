package Enemies;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;

public class Enemy extends Rectangle{
	
	public String code;
	public int id;
	public boolean damaged = false, airborn = false;
	public Vector2 momentum = Vector2.zero;
	public int freeze = 0;
	
	public int health;
	public int damage_cooldown = 0;
	
	public static final int enemy_knowckback = 50, contact_damage = 1;
	
	public Enemy() {}
	public Enemy(double a, double b) {
		this.pos = new Vector2(a, b);
	}
	
	public void update() {
		//DAMAGE COOLDOWN IN ALL ENEMIES
	}
	
	public void move(Vector2 move) {
		this.pos.add(Vector2.add(move, this.momentum));
		
		this.momentum.x = Utility.clamp(Utility.sign(this.momentum.x) * (Math.abs(this.momentum.x) - 1), this.momentum.x, 0);
		this.apply_gravity();
		
		//if (this.momentum.length() != 0) this.momentum = Vector2.scale_to_length(this.momentum, Utility.clamp(this.momentum.length() - 1, 0, this.momentum.length()));
		
		this.displace();
	}
	public void apply_gravity() {
		if (this.airborn) {
			this.momentum.y = Utility.clamp(this.momentum.y - 1, 0, this.momentum.y);
			return;
		}
		this.momentum.y = Utility.clamp(this.momentum.y - 0.2, -4, this.momentum.y);
	}
	public void displace() {
		for (int x = 0; x<Game.current_room.objects.length; x++) {
			Game.current_room.objects[x].displace_entity(this, 0);
		}
	}
	
	public Vector2 ai() {
		return new Vector2(0, 0);
	}
	
	public boolean damage(int damage) {
		if (this.damage_cooldown > 0) return false;
		this.health -= damage;
		this.damage_cooldown = 20;
		this.damaged = true;
		if (this.health <= 0) this.die();
		this.damage_function();
		
		return true;
	}
	public void damage_function() {
		
	}
	
	public void die() {
		//play kill animation
		//spawn background object as carcass
		Game.current_room.remove_enemy(this.id);
	}
	
	public void collision_action() {
		if (Rectangle.intersect(this, Game.player)) {
			Game.player.combat.damage(contact_damage, this);
		}
	}
	
	public void draw_enemy(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (this.damaged) this.fill = Color.red;
		else this.fill = Color.gray;
		
		this.damaged = false;
		
		this.draw(g, pane, xin, yin, location);
		Vector2 temp = Vector2.converted_pos(this.pos, pane, xin, yin, location);
		
		g.drawString("health: " + this.health, (int)temp.x, (int)temp.y);
		
		//this.draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, this.sprite, "game");
	}
}
