package GameObject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Player;
import Rendering.Camera;

public class Platform extends Rectangle{
	public static int walkable_platform_num = 0, danger_platform_num = 1, hazard_respawn_num = 2;
	public static Color[] color_types = {Color.black, Color.red, Color.green};
	public static String[] plat_names = {"/platform_textures/mouth_sprite_sheet.png", "/platform_textures/plat_1.png", "/platform_textures/plat_2.png"};
	public String sprite_name = "";
	public BufferedImage[] sprites = {};
	public int[] sprites_num;
	public int type;
	public double cam_dist = 1;
	
	//TODO: maybe add platform type where you don't regain your dash when colliding
	public Platform() {}
	public Platform(double a, double b, double c, double d, double e, String f) {
		this.type = (int)a;
		this.pos = new Vector2(b, c);
		this.width = (int)d;
		this.height = (int)e;
		this.sprite_name = f;
		this.start_nodes();
		this.fill = color_types[type];
	}
	
	//COLLISION
	public boolean collide_action() {
		if (this.type == danger_platform_num) {
			Game.player.draw_player(Game.g);
			//Game.player.set_position(Game.player_xpos, Game.player_ypos);
			return true;
		}
		else if (this.type == hazard_respawn_num) {
			//Game.player_xpos = (int)(this.pos.x);
			//Game.player_ypos = (int)(this.pos.y + Player.height_save/2);
			return false;
		}
		return true;
	}
	
	
	//LOGIC METHODS (BASICS)
	public void draw_platform(Graphics g, JPanel pane, int xpos, int ypos, String location) {
		this.draw(g, pane, xpos, ypos, location);
	}
	public void draw_platform_better(Graphics g, JPanel pane, int xpos, int ypos, String location) {
		if (location.equals("game")) {
			//this.draw(g, pane, xpos, ypos, location);
			this.draw_with_sprite(g, pane, xpos, ypos, this.sprite, location);
			if (Game.debug_mode) this.draw_border(g, pane, xpos, ypos, location);
		}
		else if (location.equals("edit")){
			this.draw_platform(g, pane, ypos, ypos, location);
		}
	}
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
		if (this.nodes != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	public void generate_sprite(Platform[] plats, String in) {
		BufferedImage[] sprites_temp = return_sprite_array(in);
		
		this.sprite = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.sprite.getGraphics();
		
		for (int y = 0; y < this.height / Camera.tile_size; y++) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				g.drawImage(sprites_temp[this.return_sprite_type(new Vector2(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y + this.height/2 - Camera.tile_size * (y + 0.5)), plats, this.type)], x * Camera.tile_size, y * Camera.tile_size, null);
			}
		}
	}
	public int return_sprite_type(Vector2 in, Platform[] plats, int type) {
		//where there are tiles
		boolean l = platforms_intersect(plats, new Rectangle(in.x - Camera.tile_size, in.y, 0, 0), type);
		boolean r = platforms_intersect(plats, new Rectangle(in.x + Camera.tile_size, in.y, 0, 0), type);
		boolean u = platforms_intersect(plats, new Rectangle(in.x, in.y + Camera.tile_size, 0, 0), type);
		boolean d = platforms_intersect(plats, new Rectangle(in.x, in.y - Camera.tile_size, 0, 0), type);
		
		boolean ul = platforms_intersect(plats, new Rectangle(in.x - Camera.tile_size, in.y + Camera.tile_size, 0, 0), type);
		boolean ur = platforms_intersect(plats, new Rectangle(in.x + Camera.tile_size, in.y + Camera.tile_size, 0, 0), type);
		boolean dl = platforms_intersect(plats, new Rectangle(in.x - Camera.tile_size, in.y - Camera.tile_size, 0, 0), type);
		boolean dr = platforms_intersect(plats, new Rectangle(in.x + Camera.tile_size, in.y - Camera.tile_size, 0, 0), type);
		
		//if (l && r && u && d && ul && ur && dl && dr) return 0;
		
		//one line with 2 corners
		if (!l && r && u && d && !ur && !dr) return 35;
		if (l && r && !u && d && !dl && !dr) return 36;
		if (l && r && u && !d && !ul && !ur) return 42;
		if (l && !r && u && d && !ul && !dl) return 43;
		
		//one line with 1 corner (left)
		if (!l && r && u && d && ur && !dr) return 37;
		if (l && r && !u && d && !dl && dr) return 38;
		if (l && r && u && !d && ul && !ur) return 44;
		if (l && !r && u && d && !ul && dl) return 45;
		//one line with 1 corner (right)
		if (l && r && !u && d && dl && !dr) return 39;
		if (l && !r && u && d && ul && !dl) return 40;
		if (!l && r && u && d && !ur && dr) return 46;
		if (l && r && u && !d && !ul && ur) return 47;
		
		//single wide ones
		
		//vertical line
		if (!l && !r && !u && d) return 3;
		if (!l && !r && u && d) return 10;
		if (!l && !r && u && !d) return 17;
		
		//horizontal line
		if (!l && r && !u && !d) return 4;
		if (l && r && !u && !d) return 5;
		if (l && !r && !u && !d) return 6;
		
		//4 single corners
		if (!l && r && !u && d && !dr) return 11;
		if (l && !r && !u && d && !dl) return 12;
		if (!l && r && u && !d && !ur) return 18;
		if (l && !r && u && !d && !ul) return 19;
		
		//one single block
		if (!l && !r && !u && !d) return 13;
		
		
		//all the odd corners
		
		//4 only corners
		if (l && r && u && d && ul && ur && dl && !dr) return 21;
		if (l && r && u && d && ul && ur && !dl && dr) return 22;
		if (l && r && u && d && ul && !ur && dl && dr) return 28;
		if (l && r && u && d && !ul && ur && dl && dr) return 29;
		
		//2 adjacent corner pieces
		if (l && r && u && d && ul && !ur && dl && !dr) return 23;
		if (l && r && u && d && ul && ur && !dl && !dr) return 24;
		if (l && r && u && d && !ul && !ur && dl && dr) return 30;
		if (l && r && u && d && !ul && ur && !dl && dr) return 31;
		
		//2 diagonal corner pieces
		if (l && r && u && d && !ul && ur && dl && !dr) return 25;
		if (l && r && u && d && ul && !ur && !dl && dr) return 32;
		
		//3 corner pieces
		if (l && r && u && d && ul && !ur && !dl && !dr) return 26;
		if (l && r && u && d && !ul && ur && !dl && !dr) return 27;
		if (l && r && u && d && !ul && !ur && dl && !dr) return 33;
		if (l && r && u && d && !ul && !ur && !dl && dr) return 34;
		
		//4 corner piece
		if (l && r && u && d && !ul && !ur && !dl && !dr) return 20;
		
		
		//9 converntional pieces
		if (!l && r && !u && d) return 0;
		if (l && r && !u && d) return 1;
		if (l && !r && !u && d) return 2;
		if (!l && r && u && d) return 7;
		if (l && !r && u && d) return 9;
		if (!l && r && u && !d) return 14;
		if (l && r && u && !d) return 15;
		if (l && !r && u && !d) return 16;
		return 8;
		
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
	public BufferedImage[] return_sprite_array(String in) {
		BufferedImage[] out = new BufferedImage[49];
		try {
			BufferedImage source = ImageIO.read(getClass().getResource(in));
			out[0] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[1] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[2] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[3] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[4] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[5] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[6] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 0 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
			out[7] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[8] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[9] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[10] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[11] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[12] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[13] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 1 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);

			out[14] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[15] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[16] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[17] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[18] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[19] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[20] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 2 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);

			out[21] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[22] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[23] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[24] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[25] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[26] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[27] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 3 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			
			out[28] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[29] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[30] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[31] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[32] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[33] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[34] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 4 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);

			out[35] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[36] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[37] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[38] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[39] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[40] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[41] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 5 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);

			out[42] = Utility.transformed_instance(source.getSubimage(0 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[43] = Utility.transformed_instance(source.getSubimage(1 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[44] = Utility.transformed_instance(source.getSubimage(2 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[45] = Utility.transformed_instance(source.getSubimage(3 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[46] = Utility.transformed_instance(source.getSubimage(4 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[47] = Utility.transformed_instance(source.getSubimage(5 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
			out[48] = Utility.transformed_instance(source.getSubimage(6 * Camera.tile_size, 6 * Camera.tile_size, Camera.tile_size, Camera.tile_size), 1, Color.black, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	public String toString() {
		return this.type + " " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.sprite_name + " ";
	}
}
