package Rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Player;

public class Camera {
	public Vector2 pos;
	int max_x, min_x, max_y, min_y;
	public BufferedImage back;
	BufferedImage[] parallax = new BufferedImage[3];
	
	//HUD
	public static final String health_sprite_name = "/player_textures/health_bolt.png";
	public static Vector2 HUD_offset;
	public Animation health_sprite;
	
	public static final int tile_size = 8;
	public static int pixel_size = 5;
	public static int pixel_height = 160;
	
	public static int look_ahead = 0;
	
	public int screen_shake_count = 0;
	public static int screen_shake_strength = 1;
	public Vector2 screen_shake_dir;
	
	public static int res_width, res_height;
	
	public Camera() {}
	public Camera(double a, double b) {
		this.pos = new Vector2(a, b);
		this.start_sprite();
	}
	
	public void draw_view(Graphics2D g, int delta_time) {
		
		long a = System.nanoTime();

		this.draw_background(g);
		//this.draw_grid(g);
		
		for (int x = 0; x<Game.current_room.objects.length; x++) Game.current_room.objects[x].draw_object(g, Game.pane, this.pos.x, this.pos.y, "game");
				
		Game.player.draw_player(g);
		if (Game.debug_mode) {
			this.draw_debug_menu(g, delta_time, Game.player);
			g.setColor(Color.magenta);
			g.drawRect(0, 0, Game.pane.getWidth() / pixel_size, Game.pane.getHeight() / pixel_size);
			
			new Vector2(Game.player.respawn_point).draw_node(g, Game.pane, this.pos.x, this.pos.y, "game", Color.green);
			new Rectangle(0, 0, 16 * tile_size, 16 * tile_size).draw_border(g, Game.pane, this.pos.x, this.pos.y, "game");
		}
		
		this.draw_HUD(g);		
		
		long b = System.nanoTime();
		
		//System.out.println("dv: " + (double)(b-a) / 1000000);

	}
	public void draw_background(Graphics g) {
		new Rectangle(this.pos.x, this.pos.y, res_width, res_height).draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, back, "game");
		for (int x = 0; x<this.parallax.length; x++) {
			Rectangle temp = new Rectangle(this.pos.x * (1 - 0.05 * (x + 1)), this.pos.y * (1 - 0.025 * (x + 1)) - this.back.getHeight()/2 + this.parallax[x].getHeight()/2, this.parallax[x].getWidth(), this.parallax[x].getHeight());
			
			while (!Utility.on_screen(temp, this.pos)) temp.pos.x += Utility.sign(this.pos.x - temp.pos.x) * this.parallax[x].getWidth();
			
			temp.draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, this.parallax[x], "game");
			
			temp.pos.x += Utility.sign(this.pos.x - temp.pos.x) * this.parallax[x].getWidth();
			
			temp.draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, this.parallax[x], "game");
		}
	}
	public void draw_HUD(Graphics g) {
		for (int x = 0; x<Game.player.health; x++) {
			new Rectangle(HUD_offset.x + this.pos.x + (x * 2 + 1) * tile_size, HUD_offset.y + this.pos.y - tile_size, 0, 0).draw_with_sprite(g, Game.pane, this.pos.x, this.pos.y, this.health_sprite.sprites[0], "game");
		}
	}
	public void draw_grid(Graphics g) {
		g.setColor(Color.black);
		for (int x = -1; x<Game.pane.getWidth()/this.back.getWidth(Game.pane) + 2; x++) {
			for (int y = -1; y<Game.pane.getHeight()/this.back.getHeight(Game.pane) + 1; y++) {
				g.drawImage(this.back, (int)(x * this.back.getWidth(Game.pane) - this.pos.x % this.back.getWidth(Game.pane)), (int)(y * this.back.getHeight(Game.pane) + this.pos.y % this.back.getHeight(Game.pane)), Game.pane);
			}
		}
	}
	public void draw_debug_menu(Graphics2D g, int delta_time, Player in) {
		int width = 12;
		
		g.setColor(new Color(255, 255, 255, 192));
		g.fillRect(0, 0, width * 10, width * 8);
		
		g.setColor(Color.black);
		g.setFont(new Font("Dialog", Font.PLAIN, 8));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		//System.out.println("rh: "+g.getRenderingHints());
		
		g.drawString("FPS: " + (1000/delta_time) + " dt: " + delta_time, width, width);
		g.drawString("Position: " + in.pos.round(2), width, width*2);
		g.drawString("Velocity: " + in.vel.round(3), width, width*3);
		g.drawString("Momentum: " + in.momentum.round(3), width, width*4);
		g.drawString("OBJECT ID: " + in.object_intersect_id, width, width * 5);
		g.drawString("GRAVITY: " + in.gravity, width, width * 6);
		g.drawString("Game Speed: " + Game.speed_mult, width, width * 7);

		
	}
	public void recalculate_bounds() {
		
		this.min_x = (int)(Game.current_room.pos.x - Game.current_room.width / 2 + tile_size);
		this.max_x = (int)(Game.current_room.pos.x + Game.current_room.width / 2 - tile_size);
		this.min_y = (int)(Game.current_room.pos.y - Game.current_room.height / 2 + tile_size);
		this.max_y = (int)(Game.current_room.pos.y + Game.current_room.height / 2 - tile_size);
	}
	public void move() {
		this.pos.x = (int)Utility.clamp(Utility.move_toward(this.pos.x, Game.player.pos.x + Game.player.last_dir * look_ahead, (int)Utility.clamp(Math.abs(this.pos.x - (Game.player.pos.x + Game.player.last_dir * look_ahead))/10, 1, 100000)), this.min_x + Game.pane.getWidth() / 2 / Camera.pixel_size, this.max_x - Game.pane.getWidth() / 2 / Camera.pixel_size);
		this.pos.y = (int)Utility.clamp(Utility.move_toward(this.pos.y, Game.player.pos.y, (int)Utility.clamp(Math.abs(this.pos.y - Game.player.pos.y)/10, 1, 100000)), this.min_y + Game.pane.getHeight() / 2 / Camera.pixel_size, this.max_y - Game.pane.getHeight() / 2 / Camera.pixel_size);
		
		if (this.max_x - this.min_x < Game.pane.getWidth() / pixel_size) this.pos.x = (this.max_x + this.min_x)/2;
		if (this.max_y - this.min_y < Game.pane.getHeight() / pixel_size) this.pos.y = (this.max_y + this.min_y)/2;
		if (this.screen_shake_count > 0) this.screen_shake(new Vector2(0, 0));
		
		//this.pos = new Vector2(Game.player.pos);
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
	}
	
	//RENDER
	public void start_sprite() {
		try {
			this.back = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/background.png")), 1, Color.black, 1);
			this.parallax[0] = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/parallax_0.png")), 1, Color.black, 1);
			this.parallax[1] = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/parallax_1.png")), 1, Color.black, 1);
			this.parallax[2] = Utility.transformed_instance(ImageIO.read(getClass().getResource("/extra_textures/parallax_2.png")), 1, Color.black, 1);
			this.health_sprite = new Animation(health_sprite_name, 16, 16, 0, false, false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
