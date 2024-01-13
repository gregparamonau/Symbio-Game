package Symbio;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;
import Symbio.Rendering.Background;

public class Platform extends Rectangle{
	public static int walkable_platform_num = 0, danger_platform_num = 1, hazard_respawn_num = 2;
	public static Color[] color_types = {Color.black, Color.red, Color.green};
	public static String[] plats = {"/platform_textures/plat_0.png", "/platform_textures/plat_1.png", "/platform_textures/plat_2.png"};
	public String sprite_name = "";
	public BufferedImage[] sprites = {};
	public int[] sprites_num;
	public int type;
	public static final int tile_size = 5;
	public double cam_dist = 1;
	
	//TODO: maybe add platform type where you don't regain your dash when colliding
	public Platform() {}
	public Platform(int a, int b, int c, int d, int e, String f) {
		this.type = a;
		this.pos = new Vector2(b, c);
		this.width = d;
		this.height = e;
		this.sprite_name = f;
		this.start_nodes();
		this.fill = color_types[type];
	}
	
	//COLLISION
	public boolean collide_action() {
		if (this.type == danger_platform_num) {
			Game.player.draw_player(Game.g);
			Game.player.set_position(Game.player_xpos, Game.player_ypos);
			return true;
		}
		else if (this.type == hazard_respawn_num) {
			Game.player_xpos = (int)(this.pos.x);
			Game.player_ypos = (int)(this.pos.y + Player.height/2);
			return false;
		}
		return true;
	}
	
	
	//LOGIC METHODS (BASICS)
	public void draw_platform(Graphics g, JPanel pane, int xpos, int ypos, String location) {
		if (location.equals("game")) this.draw(g, pane, xpos, ypos, location);
		if (location.equals("edit")) this.draw(g, pane, xpos, ypos, location);
	}
	public void draw_platform_better(Graphics g, JPanel pane, int xpos, int ypos, String location) {
		if (location.equals("game")) {
			int count = 0;
			for (int y = 0; y<this.height / grid_size; y++) {
				for (int x = 0; x<this.width / grid_size; x++) {
					new Rectangle(this.pos.x - this.width/2 + (x + 0.5) * grid_size, this.pos.y + this.height/2 - (y + 0.5) * grid_size, grid_size, grid_size).draw_with_sprite(g, pane, xpos, ypos, this.sprites[this.sprites_num[count]], location);
					//g.drawImage(this.sprites[this.sprites_num[count]], (int)(this.pos.x + pane.getWidth()/2 - xpos - this.width/2 + x * grid_size), (int)(pane.getHeight() - (this.pos.y - ypos + pane.getHeight()/2) - this.height/2 + y * grid_size), pane);
					count++;
				}
			}
			if (Game.draw_hitboxes) this.draw_border(g, pane, xpos, ypos, location);
		}
		else if (location.equals("edit")){
			this.draw_platform(g, pane, ypos, ypos, location);
		}
	}
	public void generate_sprites(Platform[] plats) {
		int count = 0;
		this.sprites_num = new int[this.width / grid_size * this.height / grid_size];
		for (int y = this.height / grid_size - 1; y >= 0; y--) {
			for (int x = 0; x<this.width / grid_size; x++) {
				this.sprites_num[count] = this.return_sprite_type(x, y, new Vector2(this.pos.x - this.width/2 + grid_size * (x + 0.5), this.pos.y - this.height/2 + grid_size * (y + 0.5)), plats, this.type);
				count++;
			}
		}
	}
	public int return_sprite_type(int x, int y, Vector2 in, Platform[] plats, int type) {
		boolean l = !platforms_intersect(plats, new Rectangle(in.x - grid_size, in.y, 0, 0), type);
		boolean r = !platforms_intersect(plats, new Rectangle(in.x + grid_size, in.y, 0, 0), type);
		boolean u = !platforms_intersect(plats, new Rectangle(in.x, in.y + grid_size, 0, 0), type);
		boolean d = !platforms_intersect(plats, new Rectangle(in.x, in.y - grid_size, 0, 0), type);
		
		boolean ul = !platforms_intersect(plats, new Rectangle(in.x - grid_size, in.y + grid_size, 0, 0), type);
		boolean ur = !platforms_intersect(plats, new Rectangle(in.x + grid_size, in.y + grid_size, 0, 0), type);
		boolean dl = !platforms_intersect(plats, new Rectangle(in.x - grid_size, in.y - grid_size, 0, 0), type);
		boolean dr = !platforms_intersect(plats, new Rectangle(in.x + grid_size, in.y - grid_size, 0, 0), type);
		
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
	public boolean platforms_intersect(Platform[] plats, Rectangle pos, int type) {
		for (int x = 0; x<plats.length; x++) {
			if (plats[x].type != type) continue;
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
		return this.type + " " + (int)this.pos.x + " " + (int)this.pos.y + " " + this.width + " " + this.height + " " + this.sprite_name + " ";
	}
}
