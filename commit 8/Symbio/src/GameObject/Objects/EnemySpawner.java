package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Enemies.Enemy;
import GameObject.GameObject;
import LevelEdit.LevelEditor;
import LevelEdit.RoomEditor;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Camera;

public class EnemySpawner extends GameObject{
	protected static String default_enemy = "Enemies.Enemies.Scuttler";
	protected static int default_enemy_count = 1;
	protected static boolean default_respawn = false;
	
	int enemy_spawn_timer = 0;
	
	public Color fill = Color.green;
	
	protected String enemy_type;
	protected boolean respawn, spawned = false;
	String code;
	
	protected int enemy_count = 1;
	
	public EnemySpawner(double a, double b, double c, String enemy_type, boolean respawn, int id) {
		this.pos = new Vector2(a, b);
		
		this.enemy_count = (int)c;
		
		this.enemy_type = enemy_type;
		
		this.respawn = respawn;
		
		this.solid = false;
		
		this.id = id;
		
		this.start_code();
		
		this.start_nodes();
	}
	
	public void update() {
		
		int count = 0;
		
		for (int x = 0; x<Game.current_room.enemies.length; x++) {
			if (Game.current_room.enemies[x].code.equals(this.code)) count++;
		}
		this.enemy_spawn_timer--;
		
		if (!this.respawn && this.spawned && count >= this.enemy_count || this.enemy_spawn_timer > 0) return;
		
		this.spawn_enemy();
		this.enemy_spawn_timer = 10;
		
		this.spawned = true;
	}
	
	public void spawn_enemy() {
		
		/*Class c = Class.forName("test.Instance");
			
			//class[] with 4 different input to constructor
			Class[] pars = {Double.TYPE, Double.TYPE, String.class, Boolean.TYPE};
			
			//constructor object which gets constructor from c with pars parameters
			Constructor cons = c.getDeclaredConstructor(pars);
			
			//arguments passed into contructor to initialise a new object
			Object[] arglist = {12.5, 17.5, "hello", false};
			
			//object created with constructor and arguments
			Object temp = cons.newInstance(arglist);
			
			//rpint out object
			System.out.println(temp.toString());*/
		//TODO: write code here (using reflection)
		try {
			Class c = Class.forName(this.enemy_type);
			
			//determine what parameters are gonna be present in all enemies
			//	--> probably just position
			Class[] pars = {Double.TYPE, Double.TYPE};
			
			Constructor cons = c.getDeclaredConstructor(pars);
			
			//position?
			Object[] arglist = {this.pos.x, this.pos.y};
			
			Game.current_room.add_enemy((Enemy)cons.newInstance(arglist));
			
			Game.current_room.enemies[Game.current_room.enemies.length - 1].id = Game.current_room.enemies.length - 1;
			
			Game.current_room.enemies[Game.current_room.enemies.length - 1].code = this.code;
			
			
			//Game.current_room.add_enemy(cons.newInstance(nodes));
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}
	
	public void generate_sprite(GameObject[] objects, String in) {
		//nothing here
	}
	
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("game")) {
			if (Game.debug_mode) this.pos.draw_node(g, pane, xin, yin, location, fill);
		}
		
		
		if (location.equals("edit")) {
			this.pos.draw_node(g, pane, xin, yin, location, fill);
			Vector2 temp =Vector2.converted_pos(this.pos, pane, xin, yin, location);
			g.fillOval((int)temp.x - LevelEditor.grid_size, (int)temp.y - LevelEditor.grid_size, LevelEditor.grid_size * 2, LevelEditor.grid_size * 2);
		}
	}
	
	public void start_code() {
		this.code = "";
		for (int x = 0; x<10; x++) {
			this.code += (char)((int)(Math.random() * 50 + 33));
		}
	}
	
	
	
	//LevelEditor garbage
}
