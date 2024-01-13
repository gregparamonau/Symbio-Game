package Symbio.Rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Vector2;
import Symbio.Logic.Utility;

public class Camera {
	public Vector2 pos;
	int max_x, min_x, max_y, min_y;
	BufferedImage back;
	BufferedImage[] parallax = new BufferedImage[3];
	
	public static final int pixel_size = 5;
	
	public int screen_shake_count = 0;
	public static int screen_shake_strength = 5;
	public Vector2 screen_shake_dir;
	
	public Camera() {}
	public Camera(double a, double b) {
		this.pos = new Vector2(a, b);
		this.start_sprite();
	}
	
	public void draw_view(Graphics g, int delta_time) {

		this.draw_background(g);
		//this.draw_grid(g);
		for (int x = 0; x<Game.current_room.background.length; x++) {
			if (Utility.on_screen(Game.current_room.background[x], this.pos)) 
				Game.current_room.background[x].draw_platform_better(g, Game.pane, (int)this.pos.x, (int)this.pos.y, "game");
		}
		
		for (int x = 0; x<Game.current_room.plat_ents.length; x++) {
			if (Utility.on_screen(Game.current_room.plat_ents[x], this.pos)) 
				Game.current_room.plat_ents[x].draw_plat_ent(g, Game.pane, (int)this.pos.x, (int)this.pos.y, "game");
		}
		
		for (int x = 0; x<Game.current_room.platforms.length; x++) {
			if (Utility.on_screen(Game.current_room.platforms[x], this.pos)) 
				Game.current_room.platforms[x].draw_platform_better(g, Game.pane, (int)this.pos.x, (int)this.pos.y, "game");
		}
		
		Game.player.draw_player(g);
		if (Game.debug_mode) {
			this.draw_debug_menu(g, delta_time, Game.player);
			g.setColor(Color.magenta);
			g.drawRect(0, 0, Game.pane.getWidth() / (5 / pixel_size), Game.pane.getHeight() / (5 / pixel_size));
		}
	}
	public void draw_background(Graphics g) {
		//g.setColor(Color.black);
		//g.fillRect(0, 0, Game.pane.getWidth(), Game.pane.getHeight());
		new Rectangle(this.pos.x, this.pos.y, 1, 1).draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, back, "game");
		//g.drawImage(this.back, Game.pane.getWidth()/2 - this.back.getWidth()/2, Game.pane.getHeight()/2 - this.back.getHeight()/2, Game.pane);
		for (int x = 0; x<this.parallax.length; x++) {
			Rectangle temp = new Rectangle(this.pos.x * (1 - 0.05 * (x + 1)), this.pos.y * (1 - 0.025 * (x + 1)) - this.back.getHeight()/2 + this.parallax[x].getHeight()/2, this.parallax[x].getWidth(), this.parallax[x].getHeight());
			while (!Utility.on_screen(temp, this.pos)) temp.pos.x += Utility.sign(this.pos.x - temp.pos.x) * this.parallax[x].getWidth();
			
			temp.draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, this.parallax[x], "game");
			
			temp.pos.x += Utility.sign(this.pos.x - temp.pos.x) * this.parallax[x].getWidth();
			temp.draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, this.parallax[x], "game");
		}
	}
	public void draw_grid(Graphics g) {
		g.setColor(Color.black);
		for (int x = -1; x<Game.pane.getWidth()/this.back.getWidth(Game.pane) + 2; x++) {
			for (int y = -1; y<Game.pane.getHeight()/this.back.getHeight(Game.pane) + 1; y++) {
				//g.drawImage(this.back, (int)(Game.pane.getWidth()/2 - this.pos.x - this.back.getWidth()), (int)(Game.pane.getHeight() - ( - this.pos.y + Game.pane.getHeight()/2) - this.back.getHeight()), Game.pane);
				g.drawImage(this.back, (int)(x * this.back.getWidth(Game.pane) - this.pos.x % this.back.getWidth(Game.pane)), (int)(y * this.back.getHeight(Game.pane) + this.pos.y % this.back.getHeight(Game.pane)), Game.pane);
			}
		}
	}
	public void draw_debug_menu(Graphics g, int delta_time, Player in) {
		g.setColor(new Color(255, 255, 255, 192));
		g.fillRect(25, 25, 200, 225);
		
		g.setColor(Color.black);
		g.drawString("FPS: " + (1000/delta_time) + " dt: " + delta_time, 50, 50);
		g.drawString("Position: " + in.pos.round(), 50, 75);
		g.drawString("Velocity: " + in.vel.round(), 50, 100);
		g.drawString("Momentum: " + in.momentum.round(), 50, 125);
		g.drawString("Last Move: " + in.last_move.round(), 50, 150);
		g.drawString("Momentum Timer: " + in.plat_ent_momentum_timer, 50, 175);
		g.drawString("Gravity: " + in.gravity, 50, 200);
		g.drawString("Friction: " + in.friction, 50, 225);
		
	}
	public void recalculate_bounds() {
		this.min_x = Game.current_room.bounds[0];
		this.max_x = Game.current_room.bounds[1];
		this.min_y = Game.current_room.bounds[2];
		this.max_y = Game.current_room.bounds[3];
	}
	public void move() {
		this.pos.x = (int)Utility.clamp(Utility.move_toward(this.pos.x, Game.player.pos.x + Game.player.last_dir * 200, (int)Utility.clamp(Math.abs(this.pos.x - (Game.player.pos.x + Game.player.last_dir * 200))/10, 1, 100000)), this.min_x + Game.pane.getWidth()/2, this.max_x - Game.pane.getWidth()/2);
		this.pos.y = (int)Utility.clamp(Utility.move_toward(this.pos.y, Game.player.pos.y, (int)Utility.clamp(Math.abs(this.pos.y - Game.player.pos.y)/10, 1, 100000)), this.min_y + Game.pane.getHeight()/2, this.max_y - Game.pane.getHeight()/2);
		
		if (this.max_x - this.min_x < Game.pane.getWidth()) this.pos.x = (this.max_x + this.min_x)/2;
		if (this.max_y - this.min_y < Game.pane.getHeight()) this.pos.y = (this.max_y + this.min_y)/2;
		if (this.screen_shake_count > 0) this.screen_shake(new Vector2(0, 0));
	}
	public void screen_shake(Vector2 in) {
		if (this.screen_shake_count == 0) {
			this.screen_shake_count = 10;
			this.screen_shake_dir = new Vector2(in);
		}
		else {
			this.pos.x += (Math.random() * screen_shake_strength * 2 - screen_shake_strength) * this.screen_shake_dir.x;
			this.pos.y += (Math.random() * screen_shake_strength * 2 - screen_shake_strength) * this.screen_shake_dir.y;
			this.screen_shake_count--;
		}
	}
	
	//LOGIC
	public void set_position(Vector2 in) {
		this.pos = new Vector2(in);
		this.pos.x = (int)Utility.clamp(this.pos.x, this.min_x, this.max_x);
		this.pos.y = (int)Utility.clamp(this.pos.y, this.min_y, this.max_y);
	}
	
	//RENDER
	public void start_sprite() {
		try {
			this.back = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/background.png")), pixel_size, Color.black, 1);
			this.parallax[0] = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/parallax_0.png")), pixel_size, Color.black, 1);
			this.parallax[1] = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/parallax_1.png")), pixel_size, Color.black, 1);
			this.parallax[2] = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/parallax_2.png")), pixel_size, Color.black, 1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
