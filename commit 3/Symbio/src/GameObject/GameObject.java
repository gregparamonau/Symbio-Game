package GameObject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import GameObject.Objects.Bouncer;
import GameObject.Objects.Circle;
import GameObject.Objects.Mover;
import GameObject.Objects.Slope;
import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Player;
import Rendering.Camera;

public class GameObject extends Rectangle{
	public int id;
	public boolean sliceable;
	public boolean solid = true;
	public double slice_strength = 35;
	public String sprite_source = "/platform_textures/mouth_sprite_sheet.png";
	
	public boolean displaceable = true;
	
	public GameObject() {}
	
	public GameObject(double a, double b, double c, double d) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
	}
	
	public static GameObject create_game_object(String[] in, int id) {
		if (in[0].equals("bouncer")) {
			return new Bouncer(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), id);
		}
		if (in[0].equals("mover")) {
			return new Mover(Utility.parse_array(Utility.sub_array(in, 1, in.length)), id);
		}
		if (in[0].equals("slope")) {
			return new Slope(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), id);
		}
		if (in[0].equals("circle")) {
			return new Circle(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), id);
		}
		GameObject temp = new GameObject(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]));
		temp.fill = Color.gray;
		return temp;
	}
	
	public static GameObject create_default_game_object(String type, Vector2 loc) {
		if (type.equals("bouncer")) {
			return Bouncer.default_bouncer(loc);
		}
		if (type.equals("circle")) {
			return Circle.default_circle(loc);
		}
		if (type.equals("mover")) {
			return Mover.default_mover(loc);
		}
		if (type.equals("slope")) {
			return Slope.default_slope(loc);
		}
		return default_object(loc);
	}
	public void update() {
		//write code here
	}
	//whether you are physically colliding with the platform
	public boolean collide_with(Player in) {
		return Rectangle.intersect(this, in);
	}
	//whether the game should use your collision or ignore it for you to walk on stuff
	public boolean collide_with(Rectangle in, boolean player) {
		return Rectangle.intersect(this, in) && this.solid;
	}
	public void collision_action() {
		//write code here
	}
	public Vector2 snap_player_to_plat_ent() {
		return null;
	}
	public void give_slice_momentum(Player in) {
		in.momentum = Vector2.sub(in.momentum, Vector2.mult(in.swipe_dir, this.slice_strength));
		
		if (Math.abs(in.swipe_dir.y) > Math.abs(in.swipe_dir.x)) in.momentum.y = Utility.clamp(in.momentum.y, -this.slice_strength, this.slice_strength);
		else if (Math.abs(in.swipe_dir.y) < Math.abs(in.swipe_dir.x)) in.momentum.x = Utility.clamp(in.momentum.x, -this.slice_strength * 2, this.slice_strength * 2);

		in.vel = new Vector2(0, 0);
		in.gravity = 1;
	}
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
		
		this.start_nodes();
		//if (this.nodes != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("game")) this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
	public void generate_sprites(GameObject[] platforms) {
		//nothing here
	}
	public void start_sprites(String in) {
		//nothing here either lmao
	}
	public void start_assets(Platform[] in) {
		//nothing here too lol
	}
	public boolean properties() {
		return false;
	}
	//public void update_nodes(Vector2 in, int place, int grid_size)
	//public void move(int grid_size)
	//public void update_dimensions()
	//public void start_nodes()
	//public void scale(double in)
	public static GameObject default_object(Vector2 loc) {
		return new GameObject(loc.x, loc.y, LevelEditor.grid_size, LevelEditor.grid_size);
	}
	public void generate_sprite(GameObject[] objects, String in) {
		BufferedImage[] sprites_temp = return_sprite_array(in);
		
		this.sprite = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.sprite.getGraphics();
		
		for (int y = 0; y < this.height / Camera.tile_size; y++) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				g.drawImage(sprites_temp[this.return_sprite_type(new Vector2(this.pos.x - this.width/2 + Camera.tile_size * (x + 0.5), this.pos.y + this.height/2 - Camera.tile_size * (y + 0.5)), objects)], x * Camera.tile_size, y * Camera.tile_size, null);
			}
		}
	}
	public int return_sprite_type(Vector2 in, GameObject[] objects) {
		//where there are tiles
		boolean l = platforms_intersect(objects, new Rectangle(in.x - Camera.tile_size, in.y, 0, 0));
		boolean r = platforms_intersect(objects, new Rectangle(in.x + Camera.tile_size, in.y, 0, 0));
		boolean u = platforms_intersect(objects, new Rectangle(in.x, in.y + Camera.tile_size, 0, 0));
		boolean d = platforms_intersect(objects, new Rectangle(in.x, in.y - Camera.tile_size, 0, 0));
		
		boolean ul = platforms_intersect(objects, new Rectangle(in.x - Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean ur = platforms_intersect(objects, new Rectangle(in.x + Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean dl = platforms_intersect(objects, new Rectangle(in.x - Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		boolean dr = platforms_intersect(objects, new Rectangle(in.x + Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		
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
	public boolean platforms_intersect(GameObject[] objects, Rectangle pos) {
		for (int x = 0; x<objects.length; x++) {
			//if (plats[x].type != type) continue;
			if (Rectangle.intersect(objects[x], pos)) {
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
		return "object " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " ";
	}
}
