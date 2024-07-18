package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Line;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Player;
import Rendering.Camera;

public class Mover extends GameObject{
	static final int speed = 1;
	Vector2 last_pos, move;
	int last_target = 0, target = 1;
	int frames_to_move = 60;
	BufferedImage[] sprites;
	int[] sprites_num;
	
	public static int default_stops = 2;
	
	public Vector2[] stops;
	public Mover() {}
	public Mover(double[] in, int id) {
		this.width = (int)in[0];
		this.height = (int)in[1];
		
		this.stops = new Vector2[(int)in[2]];
		for (int x = 0; x<in[2]; x++) {
			this.stops[x] = new Vector2(in[2 + 2 * x + 1], in[2 + 2 * x + 2]);
		}
		
		this.solid = true;
		
		this.sliceable = false;
		
		this.pos = new Vector2(this.stops[0]);
		this.fill = new Color(175, 43, 30);
		
		this.sprite_source = "/plat_ent_textures/mover.png";
		
		//if (in[5] == -1) this.start_nodes();
		this.start_nodes();
		
		this.start_move();
		this.id = id;
	}
	public void update() {
		//if (this.target == 0) System.exit(0);
		//TODO: implement the ability to have multiple nodes (that are looped through)
		//TODO: implement ability to have platforms that are 'attached' to a moving one
		boolean player_on_mover = Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y + 0.2, this.width + 0.8, this.height + 0.4), Game.player);
		this.last_pos = new Vector2(this.pos);
		this.pos.x += this.move.x;
		this.pos.y += this.move.y;
		
		if (!Utility.within_bounds(this.pos.x, this.stops[this.last_target].x, this.stops[this.target].x) || !Utility.within_bounds(this.pos.y, this.stops[this.last_target].y, this.stops[this.target].y)) {
			//this.move_dir *= -1;
			this.pos = new Vector2(this.stops[this.target]);
			
			this.last_target = this.target;
			this.target = (this.target + 1) % this.stops.length;
			this.start_move();
		}
		
		if (player_on_mover) this.drag_player();
	}
	public void drag_player() {
		Game.player.pos = Vector2.add(Vector2.sub(this.pos, this.last_pos), Game.player.pos);
		
		if (Rectangle.intersect(this, Game.player)) {
			this.displace_player();
		}
	}
	public void displace_player() {
		Rectangle temp = Rectangle.intersect_area(this, Game.player);
		if (Math.min(temp.width, temp.height) > Player.squash_death_threshold) {
			Game.player.respawn();
			return;
		}
		if (temp.height <= temp.width) {
			Game.player.pos.y += Utility.sign(Game.player.pos.y - this.pos.y) * (temp.height + 1);
		}
		else if (temp.height > temp.width) {
			Game.player.pos.x += Utility.sign(Game.player.pos.x - this.pos.x) * (temp.width + 1);
		}
		System.out.println("DISPLACED");
	}
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		
		if (location.equals("game")) {
			this.draw(g, pane, xin, yin, location);
			
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
	public void start_move() {
		this.move = new Vector2((this.stops[this.target].x - this.stops[this.last_target].x)/frames_to_move, (this.stops[this.target].y - this.stops[this.last_target].y)/frames_to_move);
		this.move = Vector2.scale_to_length(this.move, speed);
	}
	public void start_stops() {
		this.stops = new Vector2[2];
		for (int x = 0; x<2; x++) {
			this.stops[x] = new Vector2(this.pos.x + 100 + 25 * x, this.pos.y);
		}
	}
	public void generate_sprites(GameObject[] objects) {
		int count = 0;
		this.sprites_num = new int[(int)(this.width / Camera.tile_size * this.height / Camera.tile_size)];
		for (int y = (int)(this.height / Camera.tile_size - 1); y >= 0; y--) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				this.sprites_num[count] = this.return_sprite_type(x, y, new Vector2(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y - this.height/2 + Camera.tile_size * (y + 0.5)), objects);
				count++;
			}
		}
	}
	public int return_sprite_type(int x, int y, Vector2 in, GameObject[] plats) {
		boolean l = !platforms_intersect(plats, new Rectangle(in.x - Camera.tile_size, in.y, 0, 0));
		boolean r = !platforms_intersect(plats, new Rectangle(in.x + Camera.tile_size, in.y, 0, 0));
		boolean u = !platforms_intersect(plats, new Rectangle(in.x, in.y + Camera.tile_size, 0, 0));
		boolean d = !platforms_intersect(plats, new Rectangle(in.x, in.y - Camera.tile_size, 0, 0));
		
		boolean ul = !platforms_intersect(plats, new Rectangle(in.x - Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean ur = !platforms_intersect(plats, new Rectangle(in.x + Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean dl = !platforms_intersect(plats, new Rectangle(in.x - Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		boolean dr = !platforms_intersect(plats, new Rectangle(in.x + Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		
		//standard
		if (l && !r && u && !d && ul && !dr) return 0;
		if (!l && !r && u && !d) return 1;
		if (!l && r && u && !d && ur && !dl) return 2;
		if (l && !r && !u && !d) return 3;
		if (!l && r && !u && !d) return 7;
		if (l && !r && !u && d && !ur && dl) return 8;
		if (!l && !r && !u && d) return 9;
		if (!l && r && !u && d && !ul && dr) return 10;
		
		//corners
		if (!l && !r && !u && !d && !ul && !ur && !dl && dr) return 11;
		if (!l && !r && !u && !d && !ul && !ur && dl && !dr) return 12;
		if (!l && !r && !u && !d && !ul && ur && !dl && !dr) return 13;
		if (!l && !r && !u && !d && ul && !ur && !dl && !dr) return 14;
		
		//horizontal single
		if (l && !r && u && d && ul && ur && dl && dr) return 15;
		if (!l && !r && u && d && ul && ur && dl && dr) return 16;
		if (!l && r && u && d && ul && ur && dl && dr) return 17;
		
		//vertical single
		if (l && r && u && !d && ul && ur && dl && dr) return 18;
		if (l && r && !u && !d && ul && ur && dl && dr) return 19;
		if (l && r && !u && d && ul && ur && dl && dr) return 20;

		return 4 + (int)(Math.random() * 3 - 0.001);
	}
	public boolean platforms_intersect(GameObject[] plats, Rectangle pos) {
		for (int x = 0; x<plats.length; x++) {
			if (!plats[x].equals(this)) continue;
			if (Rectangle.intersect(plats[x], pos)) {
				return true;
			}
		}
		return false;
	}
	public void start_sprites(String in) {
		this.sprites = new BufferedImage[21];
		try {
			//standard
			BufferedImage source = ImageIO.read(getClass().getResource(in));
			this.sprites[0] = Utility.transformed_instance(source.getSubimage(0, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[1] = Utility.transformed_instance(source.getSubimage(Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[2] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[3] = Utility.transformed_instance(source.getSubimage(0, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[4] = Utility.transformed_instance(source.getSubimage(Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
				this.sprites[5] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
				this.sprites[6] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
			this.sprites[7] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[8] = Utility.transformed_instance(source.getSubimage(0, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[9] = Utility.transformed_instance(source.getSubimage(Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[10] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
			//corners
			this.sprites[11] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[12] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[13] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[14] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
			//horizontal line
			this.sprites[15] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[16] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[17] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
			//vertical line
			this.sprites[18] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 0, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[19] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			this.sprites[20] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Mover default_mover(Vector2 loc) {
		
		double[] temp = new double[3 + default_stops * 2];
		
		temp[0] = 2 * LevelEditor.grid_size;
		temp[1] = 2 * LevelEditor.grid_size;
		temp[2] = default_stops;
		
		for (int x = 3; x < temp.length; x++) {
			if (x % 2 == 1) temp[x] = loc.x + x * LevelEditor.grid_size;
			if (x % 2 == 0) temp[x] = loc.y;
			
		}
		
		for (int x = 0; x<temp.length; x++) {
			System.out.print(temp[x] + " ");
		}
		Mover out = new Mover(temp, 0);
		
		out.update_dimensions();
		
		return out;
	}
	public void update_nodes(Vector2 in, int place, int grid_size) {
		this.nodes[place] = in;
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}
	public void move(int grid_size) {
		Vector2 center = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[1]), -0.5);
		Vector2[] offsets = new Vector2[this.nodes.length];
		
		for (int x = 0; x<offsets.length; x++) offsets[x] = Vector2.add(this.nodes[x], center);
		
		this.pos.clip_node(grid_size);
		
		for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.add(this.pos, offsets[x]);
		
		this.clip_nodes(grid_size);
		this.update_dimensions();
	}
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
		
		default_stops = Integer.parseInt(JOptionPane.showInputDialog(LevelEditor.frame, "How Many Stops?"));
		
		
		return false;
	}
	
	public String toString() {
		String stops = "";
		for (int x = 2; x<this.nodes.length; x++) stops += this.nodes[x].x + " " + this.nodes[x].y + " ";
		return "mover " + this.width + " " + this.height + " "  + this.stops.length + " " + stops;
	}
	public String give_class() {
		return "mover";
	}
}
