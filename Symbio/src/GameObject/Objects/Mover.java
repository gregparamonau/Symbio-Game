package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import LevelEdit.RoomEditor;
import Logic.Line;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Player;
import Rendering.Camera;

public class Mover extends GameObject{
	double speed = -1;
	double time = -1;
	static double default_time = 5;
	//TODO: determine this please
	public static final double mover_mom_mult = 0.2;//12.5
	
	public static Color fill_save = new Color(175, 43, 30);
	
	Vector2 move, vel;
	int last_target = 0, target = 1;
	BufferedImage[] sprites;
	int[] sprites_num;
	
	public static int default_stops = 2;
	
	public Vector2[] stops;
	public Mover() {}
	public Mover(double[] in, String sprite, int id) {
		this.width = in[0];
		this.height = in[1];
		
		this.speed = in[2];
		
		if (this.speed == -1) this.time = in[3];
		
		this.stops = new Vector2[(int)in[4]];
		for (int x = 0; x<in[4]; x++) {
			this.stops[x] = new Vector2(in[4 + 2 * x + 1], in[4 + 2 * x + 2]);
		}
		
		this.object_handle = (int)in[4 + 2 * (int)in[4] + 1];
		
		this.solid = true;
		this.vis_solid = false;
		this.fill = fill_save;
		
		this.sliceable = false;
		
		this.pos = new Vector2(this.stops[0]);
		
		this.sprite_name = sprite;
		
		//if (in[5] == -1) this.start_nodes();
		this.start_nodes();
		
		this.start_speed();
		
		this.start_move();
		
		this.id = id;
	}
	public void update() {
		boolean player_on_mover = Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y + 0.2, this.width + 0.8, this.height + 0.4), Game.player);
		this.last_pos = new Vector2(this.pos);
		
		if (this.object_handle != -1) {
			this.move(Vector2.sub(Game.current_room.objects[this.object_handle].pos, Game.current_room.objects[this.object_handle].last_pos));
		}
		
		this.pos.add(move);
		
		if (!Utility.within_bounds(this.pos.x, this.stops[this.last_target].x, this.stops[this.target].x) || !Utility.within_bounds(this.pos.y, this.stops[this.last_target].y, this.stops[this.target].y)) {
			//this.move_dir *= -1;
			this.pos = new Vector2(this.stops[this.target]);
			
			this.last_target = this.target;
			this.target = (this.target + 1) % this.stops.length;
			this.start_move();
		}
		
		if (player_on_mover) this.drag_player();
	}
	/*
	public void give_dash_jump_momentum() {
		Game.player.momentum = Vector2.mult(Game.player.momentum, dash_jump_mom_mult / Vector2.dot(Game.player.momentum, new Vector2(Game.player.last_dir, 0)));
		Game.player.momentum.y *= -dash_jump_mom_y_mult;
		
		Game.player.momentum = Vector2.add(Game.player.momentum, Vector2.mult(this.vel, mover_mom_mult));
	}
	public void give_jump_momentum() {
		Game.player.momentum = Vector2.add(Game.player.momentum, Vector2.mult(this.vel, mover_mom_mult));
	}
	public void give_dash_wall_jump_momentum() {
		Game.player.momentum = Vector2.mult(Game.player.momentum, dash_wall_jump_mom_mult);
		Game.player.momentum.x *= dash_wall_jump_x_mom_mult;
		
		Game.player.momentum = Vector2.add(Game.player.momentum, Vector2.mult(this.vel, mover_mom_mult));
	}
	public void give_wall_jump_momentum() {
		Game.player.momentum = Vector2.mult(this.vel, mover_mom_mult);
	}
	*/
	public void drag_player() {
		Game.player.pos.add(Vector2.sub(this.pos, this.last_pos));
		
		Game.player.displace(0);
	}
	public void displace_player() {
		Rectangle temp = Rectangle.intersect_area(this, Game.player);
		if (Math.min(temp.width, temp.height) > Player.squash_death_threshold) {
			Game.player.hazard_respawn(true);
			return;
		}
		if (temp.height <= temp.width) {
			Game.player.pos.y += Utility.sign(Game.player.pos.y - this.pos.y) * (temp.height + 1);
		}
		else if (temp.height > temp.width) {
			Game.player.pos.x += Utility.sign(Game.player.pos.x - this.pos.x) * (temp.width + 1);
		}
	}
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		
		if (location.equals("game")) {
			//this.draw(g, pane, xin, yin, location);
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			
			/*int count = 0;
			for (int y = 0; y<this.height / Camera.tile_size; y++) {
				for (int x = 0; x<this.width / Camera.tile_size; x++) {
					new Rectangle(this.pos.x - this.width/2 + (x + 0.5) * Camera.tile_size, this.pos.y + this.height/2 - (y + 0.5) * Camera.tile_size, Camera.tile_size, Camera.tile_size).draw_with_sprite(g, pane, xin, yin, this.sprites[this.sprites_num[count]], location);
					//g.drawImage(this.sprites[this.sprites_num[count]], (int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2 + x * grid_size), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2) - this.height/2 + y * grid_size), pane);
					count++;
				}
			}
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
				this.draw_border(g, pane, xin, yin, location);
			}*/
		}
		if (location.equals("edit")) {
			this.draw(g, pane, xin, yin, location);
			for (int x = 0; x<this.nodes.length - 2; x++) {
				new Line(this.nodes[x + 2], this.nodes[(x + 1) % (this.nodes.length - 2) + 2]).draw_line(g, fill, pane, xin, yin, location);
			}
			new Line(this.pos, this.nodes[2]).draw_line(g, fill, pane, xin, yin, location);
		}		
	}
	public void draw_nodes(Graphics g, JPanel pane, double xpos, double ypos, String location) {
		for (Vector2 temp: this.nodes) temp.draw_node(g, pane, xpos, ypos, location, Color.magenta);
	}
	public void start_speed() {
		if (this.speed != -1) return;
		double dist = 0;
		for (int x = 1; x<this.stops.length + 1; x++) dist += Vector2.sub(this.stops[x % this.stops.length], this.stops[(x - 1) % this.stops.length]).length();
		
		this.speed = (double)dist * Camera.tile_size / this.time / Game.frame_rate;
	}
	public void start_move() {
		this.move = Vector2.sub(this.stops[this.target], this.stops[this.last_target]);
		this.move = Vector2.scale_to_length(this.move, speed);
		
		this.vel = Vector2.mult(this.move, Game.frame_rate);
	}
	public void start_stops() {
		this.stops = new Vector2[2];
		for (int x = 0; x<2; x++) {
			this.stops[x] = new Vector2(this.pos.x + 100 + 25 * x, this.pos.y);
		}
	}
	public void generate_sprite(GameObject[] objects, String in) {
		
		BufferedImage[] sprites_temp = return_sprite_array(in);
		
		this.sprite = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.sprite.getGraphics();
		
		/*for (int y = 0; y < this.height / Camera.tile_size; y++) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				g.drawImage(sprites_temp[this.return_sprite_type(new Vector2(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y + this.height/2 - Camera.tile_size * (y + 0.5)), objects)], x * Camera.tile_size, y * Camera.tile_size, null);
			}
		}*/
		
		
		
		this.sprites_num = new int[(int)(this.width / Camera.tile_size * this.height / Camera.tile_size)];
		for (int y = (int)(this.height / Camera.tile_size - 1); y >= 0; y--) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				//g.draw
				g.drawImage(sprites_temp[this.return_sprite_type(new Vector2(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y - this.height/2 + Camera.tile_size * (y + 0.5)), new GameObject[] {this})], x * Camera.tile_size, y * Camera.tile_size, null);

				//this.sprites_num[count] = this.return_sprite_type(new Vector2(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y - this.height/2 + Camera.tile_size * (y + 0.5)), new GameObject[] {this});
			}
		}
	}
	public int return_sprite_type(Vector2 in, GameObject[] obs) {
		boolean l = !objects_intersect(obs, new Rectangle(in.x - Camera.tile_size, in.y, 0, 0));
		boolean r = !objects_intersect(obs, new Rectangle(in.x + Camera.tile_size, in.y, 0, 0));
		boolean u = !objects_intersect(obs, new Rectangle(in.x, in.y + Camera.tile_size, 0, 0));
		boolean d = !objects_intersect(obs, new Rectangle(in.x, in.y - Camera.tile_size, 0, 0));
		
		boolean ul = !objects_intersect(obs, new Rectangle(in.x - Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean ur = !objects_intersect(obs, new Rectangle(in.x + Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean dl = !objects_intersect(obs, new Rectangle(in.x - Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		boolean dr = !objects_intersect(obs, new Rectangle(in.x + Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		
		//standard
		if (l && !r && !u && d && !ur && dl) return 0;
		if (!l && !r && !u && d) return 1;
		if (!l && r && !u && d && !ul && dr) return 2;
		if (l && !r && !u && !d) return 7;
		if (!l && r && !u && !d) return 9;
		if (l && !r && u && !d && ul && !dr) return 14;
		if (!l && !r && u && !d) return 15;
		if (!l && r && u && !d && ur && !dl) return 16;
		
		//corners
		if (!l && !r && !u && !d && !ul && !ur && !dl && dr) return 21;
		if (!l && !r && !u && !d && !ul && !ur && dl && !dr) return 22;
		if (!l && !r && !u && !d && !ul && ur && !dl && !dr) return 28;
		if (!l && !r && !u && !d && ul && !ur && !dl && !dr) return 29;
		
		//horizontal single
		if (l && !r && u && d && ul && ur && dl && dr) return 4;
		if (!l && !r && u && d && ul && ur && dl && dr) return 5;
		if (!l && r && u && d && ul && ur && dl && dr) return 6;
		
		//vertical single
		if (l && r && u && !d && ul && ur && dl && dr) return 3;
		if (l && r && !u && !d && ul && ur && dl && dr) return 10;
		if (l && r && !u && d && ul && ur && dl && dr) return 17;

		return 8;
	}
	public boolean objects_intersect(GameObject[] objects, Rectangle pos) {
		for (int x = 0; x<objects.length; x++) {
			if (Rectangle.intersect(objects[x], pos)) {
				return true;
			}
		}
		return false;
	}
	public static Mover default_mover(Vector2 loc) {
		
		double[] temp = new double[6 + default_stops * 2];
		
		temp[0] = 2 * LevelEditor.grid_size;
		temp[1] = 2 * LevelEditor.grid_size;
		temp[2] = -1;
		temp[3] = default_time;
		
		temp[4] = default_stops;
		
		for (int x = 5; x < temp.length; x++) {
			if (x % 2 == 1) temp[x] = loc.x + x * LevelEditor.grid_size;
			if (x % 2 == 0) temp[x] = loc.y;
			
		}
		
		temp[temp.length - 1] = -1;
		
		for (int x = 0; x<temp.length; x++) {
			System.out.print(temp[x] + " ");
		}
		Mover out = new Mover(temp, sprite_name_default, 0);
		
		out.update_dimensions();
		
		return out;
	}
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}
	/*public void move(int grid_size) {
		System.out.println("BEING A BITCH I SEE 290 mover");
		Vector2 center = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[1]), -0.5);
		Vector2[] offsets = new Vector2[this.nodes.length];
		
		for (int x = 0; x<offsets.length; x++) offsets[x] = Vector2.add(this.nodes[x], center);
		
		this.pos.clip_node(grid_size);
		
		for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.add(this.pos, offsets[x]);
		
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}*/
	public void update_dimensions() {
		this.pos = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[1]), 0.5);

		this.width = Math.abs(this.nodes[0].x - this.nodes[1].x);
		this.height = Math.abs(this.nodes[0].y - this.nodes[1].y);
	}
	/*public void start_nodes() {
		if (!start) return;
		Vector2[] temp = new Vector2[this.stops.length + 4];
		temp[0] = new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2);
		temp[1] = new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2);
		temp[2] = new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2);
		temp[3] = new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2);
		
		for (int x = 0; x<this.stops.length; x++) temp[x + 4] = new Vector2(this.pos.x + 100 + 25 * x, this.pos.y);
		this.nodes = temp;
		start = false;
	}*/
	
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = new Vector2[this.stops.length + 2];
		temp[0] = new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2);
		temp[1] = new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2);
		
		for (int x = 0; x<this.stops.length; x++) temp[x + 2] = this.stops[x];
		
		this.nodes = temp;
		start = false;
	}
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
		if (this.stops != null) for (int x = 0; x<this.stops.length; x++) this.stops[x] = Vector2.mult(this.stops[x], in);
		if (this.nodes != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	
	public boolean properties() {
		
		JList mods = new JList(new String[] {"Stop Number", "Object Handle", "Speed", "Time"});
		
		JOptionPane.showMessageDialog(LevelEditor.frame, mods, "Select Modification", JOptionPane.PLAIN_MESSAGE);
		
		if (mods.getSelectedIndex() == 0) {
			default_stops = Integer.parseInt(JOptionPane.showInputDialog(LevelEditor.frame, "How Many Stops?"));
		}
		if (mods.getSelectedIndex() == 1) {
			this.object_handle = Integer.parseInt(JOptionPane.showInputDialog(LevelEditor.frame, "Attach to Object"));
			if (!RoomEditor.room.objects[this.object_handle].give_class().equals("mover")) this.object_handle = -1;
		}
		if (mods.getSelectedIndex() == 2) {
			System.out.println("	ST: " + this.speed + " " + this.time);
			this.speed = Double.parseDouble(JOptionPane.showInputDialog(LevelEditor.frame, "Input Speed"));
			this.time = -1;
			System.out.println("	ST: " + this.speed + " " + this.time);

		}
		if (mods.getSelectedIndex() == 3) {
			this.time = Double.parseDouble(JOptionPane.showInputDialog(LevelEditor.frame, "Input Time Duration"));
			this.speed = -1;
		}		
		
		return false;
	}
	public void move(Vector2 in) {
		for (int x = 0; x<this.stops.length; x++) this.stops[x].add(in);
		this.pos.add(in);
	}
	
	public String toString() {
		String stops = "";
		for (int x = 2; x<this.nodes.length; x++) stops += this.nodes[x].x + " " + this.nodes[x].y + " ";
		return "mover " + this.width + " " + this.height + " " + this.speed + " " + this.time + " " + this.stops.length + " " + stops + this.object_handle + " "  + sprite_name_default + " ";
	}
	public String give_class() {
		return "mover";
	}
}
