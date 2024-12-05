package GameObject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GameObject.Objects.Acid;
import GameObject.Objects.Bouncer;
import GameObject.Objects.Circle;
import GameObject.Objects.EnemySpawner;
import GameObject.Objects.Mover;
import GameObject.Objects.OneWay;
import GameObject.Objects.Slope;
import LevelEdit.LevelEditor;
import LevelEdit.RoomEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Camera;

public class GameObject extends Rectangle{
	public int id;
	public boolean sliceable;
	public boolean solid = true;
	public boolean vis_solid = true;
	
	public Color fill = Color.gray;
	
	public Vector2 last_pos;
	
	public int object_handle = -1;
	
	public static String sprite_name_default = "/object_textures/mouth.png";
	
	public static final double dash_jump_mom_mult = 2.5, dash_jump_mom_y_mult = 0.1;
	public static final double dash_wall_jump_mom_mult = 1.5, dash_wall_jump_x_mom_mult = 0.75;
	
	public String sprite_name = sprite_name_default;

	
	public boolean displaceable = true;
	
	public GameObject() {}
	
	public GameObject(double a, double b, double c, double d, double e, String sprite, int id) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.start_nodes();
		
		this.object_handle = (int)e;
		
		this.sprite_name = sprite;
		
		this.id = id;
	}
	
	public static GameObject create_game_object(String[] in, int id) {
		if (in[0].equals("spawner")) {
			return new EnemySpawner(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), in[4], Double.parseDouble(in[5]) == 1 ? true : false, id);
		}
		if (in[0].equals("acid")) {
			return new Acid(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6], id);
		}
		if (in[0].equals("bouncer")) {
			return new Bouncer(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), in[5], id);
		}
		if (in[0].equals("circle")) {
			return new Circle(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), in[5], id);
		}
		if (in[0].equals("mover")) {
			return new Mover(Utility.parse_array(Utility.sub_array(in, 1, in.length - 1)), in[in.length - 1], id);
		}
		if (in[0].equals("oneway")) {
			return new OneWay(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), Double.parseDouble(in[6]), in[7], id);
		}
		if (in[0].equals("slope")) {
			return new Slope(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6], id);
		}
		GameObject temp = new GameObject(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6], id);
		temp.fill = Color.gray;
		return temp;
	}
	//needed methods in every class for 'game'
	//default_object(Vector2 loc)
	//update() [needed if the object does an action / changes sprite?]
	//collide_with(Rectangle in, boolean col_action)
	//collision_action()
	//displace_player(int direction)
	//4 momentum methods:
		//dash_jump
		//jump
		//dash_wall_jump
		//wall_jump
	//draw_object()
	//generate_sprite(GameObject[] objects, String in)
	//return_sprite_type(Vector2 in, GameObject[] objects)
	//return_sprite_array(String in)
	//toString()
	//give_class()
	
	//needed methods for 'edit'
	//public void update_nodes(Vector2 in, int place, int grid_size)
	//public void move(int grid_size)
	//public void update_dimensions()
	//public void start_nodes()
	//public void scale(double in)
	
	
	public static GameObject create_default_game_object(String type, Vector2 loc) {
		if (type.equals("spawner")) return EnemySpawner.default_spawner(loc);
		if (type.equals("acid")) return Acid.default_acid(loc);
		if (type.equals("bouncer")) return Bouncer.default_bouncer(loc);
		if (type.equals("circle")) return Circle.default_circle(loc);
		if (type.equals("mover")) return Mover.default_mover(loc);
		if (type.equals("oneway")) return OneWay.default_oneway(loc);
		if (type.equals("slope")) return Slope.default_slope(loc);
		return default_object(loc);
	}
	public void update() {
		if (this.object_handle == -1) return;
		
		this.last_pos = new Vector2(this.pos);
		
		this.move(Vector2.sub(Game.current_room.objects[this.object_handle].pos, Game.current_room.objects[this.object_handle].last_pos));
		//write code here
		
		if (Game.player.object_intersect_id == this.id) {
			Game.player.pos.add(Vector2.sub(this.pos, this.last_pos));
			
			Game.player.displace(0); 
		}
	}
	//whether you are physically colliding with the platform for movement & physics
	public boolean collide_with(Rectangle in, boolean col_action) {
		return Rectangle.intersect(this, in);
	}
	public void collision_action() {
		//write code here
	}
	public void displace_entity(Rectangle in, int direction) {
		//direction: 0 = any, 1 = vertical, 2 = horizontal
		if (!this.solid || !Rectangle.intersect(this, in) || !this.displaceable) return;
		
		Rectangle temp = Rectangle.intersect_area(this, in);
		if (temp.height <= temp.width) {
			in.pos.y += Utility.sign(in.pos.y - this.pos.y) * (temp.height + 1);
		}
		else if (temp.height > temp.width) {
			in.pos.x += Utility.sign(in.pos.x - this.pos.x) * (temp.width + 1);
		}
	}
	
	public void scale(double in) {
		this.pos = Vector2.mult(this.pos, in);
		this.width *= in;
		this.height *= in;
		
		this.start_nodes();
		if (this.nodes != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("game")) this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
		if (location.equals("edit")) this.draw(g, pane, xin, yin, location);
	}
	public boolean properties() {
		this.object_handle = Integer.parseInt(JOptionPane.showInputDialog(LevelEditor.frame, "Attach to Object"));
		if (!RoomEditor.room.objects[this.object_handle].give_class().equals("mover")) this.object_handle = -1;
		return false;
	}
	public static GameObject default_object(Vector2 loc) {
		return new GameObject(loc.x, loc.y, LevelEditor.grid_size, LevelEditor.grid_size, -1, sprite_name_default, 0);
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
		boolean l = objects_intersect(objects, new Rectangle(in.x - Camera.tile_size, in.y, 0, 0));
		boolean r = objects_intersect(objects, new Rectangle(in.x + Camera.tile_size, in.y, 0, 0));
		boolean u = objects_intersect(objects, new Rectangle(in.x, in.y + Camera.tile_size, 0, 0));
		boolean d = objects_intersect(objects, new Rectangle(in.x, in.y - Camera.tile_size, 0, 0));
		
		boolean ul = objects_intersect(objects, new Rectangle(in.x - Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean ur = objects_intersect(objects, new Rectangle(in.x + Camera.tile_size, in.y + Camera.tile_size, 0, 0));
		boolean dl = objects_intersect(objects, new Rectangle(in.x - Camera.tile_size, in.y - Camera.tile_size, 0, 0));
		boolean dr = objects_intersect(objects, new Rectangle(in.x + Camera.tile_size, in.y - Camera.tile_size, 0, 0));
				
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
	public boolean objects_intersect(GameObject[] objects, Rectangle pos) {
		for (int x = 0; x<objects.length; x++) {
			if (!objects[x].vis_solid || objects[x].object_handle != -1) continue;
			if (Rectangle.intersect(objects[x], pos)) {
				return true;
			}
		}
		return false;
	}
	public BufferedImage[] return_sprite_array(String in) {
		
		System.out.println(in);
		BufferedImage[] out = new BufferedImage[49];
		try {
			BufferedImage source = ImageIO.read(getClass().getResource(in));
			for (int x = 0; x<out.length; x++) {
				out[x] = source.getSubimage((x % 7) * Camera.tile_size, (x / 7) * Camera.tile_size, Camera.tile_size, Camera.tile_size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	public void move(Vector2 in) {
		for (int x = 0; x<this.nodes.length; x++) this.nodes[x].add(in);
		this.pos.add(in);
	}
	public String toString() {
		return "object " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "object";
	}
}
