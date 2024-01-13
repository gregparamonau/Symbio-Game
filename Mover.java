package Symbio.Entity.PlatEntities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Entity.PlatEntity;
import Symbio.Logic.Line;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class Mover extends PlatEntity{
	static final int speed = 5;
	Vector2 last_pos, move;
	int last_target = 0, target = 1;
	int frames_to_move = 60;
	int stop_num = 1;
	BufferedImage[] sprites;
	int[] sprites_num;
	public Mover() {}
	public Mover(int[] in, int id) {
		this.width = in[0];
		this.height = in[1];
		
		this.stops = new Vector2[in[2]];
		for (int x = 0; x<in[2]; x++) {
			this.stops[x] = new Vector2(in[2 + 2 * x + 1], in[2 + 2 * x + 2]);
			//System.out.println(this.stops[x]);
		}
		//System.exit(0);
		this.pos = new Vector2(this.stops[0]);
		//this.pos1 = new Vector2(in[2], in[3]);
		//this.pos2 = new Vector2(in[4], in[5]);
		this.fill = new Color(175, 43, 30);
		
		this.sprite_source = "/plat_ent_textures/mover.png";
		
		if (in[5] == -1) this.start_nodes();
		this.start_better_nodes();
		
		this.start_move();
		this.id = id;
	}
	public void update() {
		//if (this.target == 0) System.exit(0);
		//TODO: implement the ability to have multiple nodes (that are looped through)
		//TODO: implement ability to have platforms that are 'attached' to a moving one
		boolean player_on_mover = Rectangle.intersect(new Rectangle(this.pos.x, this.pos.y + 0.5, this.width + 2, this.height + 1), new Rectangle(Game.player));
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
		Game.player.pos = Vector2.add(Vector2.add(this.pos, Vector2.neg(this.last_pos)), Game.player.pos);
		
		if (Rectangle.intersect(this, new Rectangle(Game.player))) {
			this.displace_player();
		}
	}
	public void displace_player() {
		Rectangle temp = Rectangle.intersect_area(this, new Rectangle(Game.player));
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
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {
		if (location.equals("game")) {
			int count = 0;
			for (int y = 0; y<this.height / grid_size; y++) {
				for (int x = 0; x<this.width / grid_size; x++) {
					new Rectangle(this.pos.x - this.width/2 + (x + 0.5) * grid_size, this.pos.y + this.height/2 - (y + 0.5) * grid_size, grid_size, grid_size).draw_with_sprite(g, pane, xin, yin, this.sprites[this.sprites_num[count]], location);
					//g.drawImage(this.sprites[this.sprites_num[count]], (int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2 + x * grid_size), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2) - this.height/2 + y * grid_size), pane);
					count++;
				}
			}
			if (Game.debug_mode) {
				g.setColor(Color.white);
				g.drawString(this.id + "", (int)(this.pos.x + pane.getWidth()/2 - xin), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2)));
				this.draw_border(g, pane, xin, yin, location);
			}
		}
		if (location.equals("edit")) {
			this.draw(g, pane, xin, yin, location);
			for (int x = 0; x<this.nodes.length - 4; x++) {
				new Line(this.nodes[x + 4], this.nodes[(x + 1) % (this.nodes.length - 4) + 4]).draw_line(g, fill, pane, xin, yin, location, 2);
			}
		}		
	}
	public void draw_nodes(Graphics g, JPanel pane, double xpos, double ypos, String location) {
		for (Vector2 temp: this.nodes) temp.draw_node(g, pane, xpos, ypos, location, Color.magenta);
	}
	public void update_nodes(Vector2 in, int place) {
		System.out.println(place + " || " + in);
		this.nodes[place] = in;
		switch(place) {
		case 0:
			this.nodes[3].x = in.x;
			this.nodes[1].y = in.y;
			break;
		case 1:
			this.nodes[0].y = in.y;
			this.nodes[2].x = in.x;
			break;
		case 2:
			this.nodes[1].x = in.x;
			this.nodes[3].y = in.y;
			break;
		case 3:
			this.nodes[2].y = in.y;
			this.nodes[0].x = in.x;
			break;
		}
		for (Vector2 temp: this.nodes) System.out.println(temp);
		this.clip_nodes();
		this.update_dimensions();
	}
	public void start_move() {
		this.move = new Vector2((this.stops[this.target].x - this.stops[this.last_target].x)/frames_to_move, (this.stops[this.target].y - this.stops[this.last_target].y)/frames_to_move);
		this.move = Vector2.scale_to_length(this.move, speed);
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = new Vector2[this.stops.length + 4];
		temp[0] = new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2);
		temp[1] = new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2);
		temp[2] = new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2);
		temp[3] = new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2);
		
		for (int x = 0; x<this.stops.length; x++) temp[x + 4] = new Vector2(this.pos.x + 100 + 25 * x, this.pos.y);
		this.nodes = temp;
		start = false;
	}
	public void start_better_nodes() {
		if (!start) return;
		Vector2[] temp = new Vector2[this.stops.length + 4];
		temp[0] = new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2);
		temp[1] = new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2);
		temp[2] = new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2);
		temp[3] = new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2);
		
		for (int x = 0; x<this.stops.length; x++) temp[x + 4] = this.stops[x];
		
		this.nodes = temp;
		start = false;
	}
	public void generate_sprites(PlatEntity[] plats) {
		int count = 0;
		this.sprites_num = new int[this.width / grid_size * this.height / grid_size];
		for (int y = this.height / grid_size - 1; y >= 0; y--) {
			for (int x = 0; x<this.width / grid_size; x++) {
				this.sprites_num[count] = this.return_sprite_type(x, y, new Vector2(this.pos.x - this.width/2 + grid_size * (x + 0.5), this.pos.y - this.height/2 + grid_size * (y + 0.5)), plats);
				count++;
			}
		}
	}
	public int return_sprite_type(int x, int y, Vector2 in, PlatEntity[] plats) {
		boolean l = !platforms_intersect(plats, new Rectangle(in.x - grid_size, in.y, 0, 0));
		boolean r = !platforms_intersect(plats, new Rectangle(in.x + grid_size, in.y, 0, 0));
		boolean u = !platforms_intersect(plats, new Rectangle(in.x, in.y + grid_size, 0, 0));
		boolean d = !platforms_intersect(plats, new Rectangle(in.x, in.y - grid_size, 0, 0));
		
		boolean ul = !platforms_intersect(plats, new Rectangle(in.x - grid_size, in.y + grid_size, 0, 0));
		boolean ur = !platforms_intersect(plats, new Rectangle(in.x + grid_size, in.y + grid_size, 0, 0));
		boolean dl = !platforms_intersect(plats, new Rectangle(in.x - grid_size, in.y - grid_size, 0, 0));
		boolean dr = !platforms_intersect(plats, new Rectangle(in.x + grid_size, in.y - grid_size, 0, 0));
		
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
	public boolean platforms_intersect(PlatEntity[] plats, Rectangle pos) {
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
			this.sprites[0] = Utility.transformed_instance(source.getSubimage(0, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[1] = Utility.transformed_instance(source.getSubimage(tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[2] = Utility.transformed_instance(source.getSubimage(2 * tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[3] = Utility.transformed_instance(source.getSubimage(0, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[4] = Utility.transformed_instance(source.getSubimage(tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
				this.sprites[5] = Utility.transformed_instance(source.getSubimage(5 * tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
				this.sprites[6] = Utility.transformed_instance(source.getSubimage(5 * tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			
			this.sprites[7] = Utility.transformed_instance(source.getSubimage(2 * tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[8] = Utility.transformed_instance(source.getSubimage(0, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[9] = Utility.transformed_instance(source.getSubimage(tile_size, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[10] = Utility.transformed_instance(source.getSubimage(2 * tile_size, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			
			//corners
			this.sprites[11] = Utility.transformed_instance(source.getSubimage(3 * tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[12] = Utility.transformed_instance(source.getSubimage(4 * tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[13] = Utility.transformed_instance(source.getSubimage(3 * tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[14] = Utility.transformed_instance(source.getSubimage(4 * tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			
			//horizontal line
			this.sprites[15] = Utility.transformed_instance(source.getSubimage(3 * tile_size, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[16] = Utility.transformed_instance(source.getSubimage(4 * tile_size, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[17] = Utility.transformed_instance(source.getSubimage(5 * tile_size, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			
			//vertical line
			this.sprites[18] = Utility.transformed_instance(source.getSubimage(6 * tile_size, 0, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[19] = Utility.transformed_instance(source.getSubimage(6 * tile_size, tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			this.sprites[20] = Utility.transformed_instance(source.getSubimage(6 * tile_size, 2 * tile_size, tile_size, tile_size), Camera.pixel_size, Color.black, 1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String toString() {
		String stops = "";
		for (int x = 4; x<this.nodes.length; x++) stops += (int)this.nodes[x].x + " " + (int)this.nodes[x].y + " ";
		return "mover " + this.width + " " + this.height + " "  + this.stops.length + " " + stops;
	}
	public String give_class() {
		return "mover";
	}
}
