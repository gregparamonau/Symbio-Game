package Symbio.Entity;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import LevelEdit.LevelCreator;
import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Entity.PlatEntities.Bouncer;
import Symbio.Entity.PlatEntities.Circle;
import Symbio.Entity.PlatEntities.Hair;
import Symbio.Entity.PlatEntities.Mover;
import Symbio.Entity.PlatEntities.RoomT;
import Symbio.Entity.PlatEntities.Slope;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Camera;

public class PlatEntity extends Rectangle{
	public int id = 0;
	public int radius;
	public int variant;
	public Vector2[] stops;
	public boolean sliceable = true;
	public double slice_strength = 35;
	public String sprite_source = "";
	
	public Vector2 vel = new Vector2(0, 0);
	public int vel_extra = 0;
	
	//roomt
	public int room_to, room_to_xpos, room_to_ypos;
	
	public static PlatEntity create_plat_entity(String[] in, int id, Platform[] platforms) {
		if (in[0].equals("roomt")) {
			return new RoomT(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), Double.parseDouble(in[6]), Double.parseDouble(in[7]), id);
		}
		if (in[0].equals("hair")) {
			return new Hair(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), id, platforms);
		}
		if (in[0].equals("bouncer")) {
			return new Bouncer(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), id);
		}
		if (in[0].equals("mover")) {
			return new Mover(Utility.parse_array(Utility.sub_array(in, 1, in.length)), id);
		}
		if (in[0].equals("slope")) {
			return new Slope(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Integer.parseInt(in[5]), id);
		}
		if (in[0].equals("circle")) {
			return new Circle(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), id);
		}
		PlatEntity temp = new PlatEntity();
		temp.pos = new Vector2(Double.parseDouble(in[1]), Double.parseDouble(in[2]));
		temp.width = (int)Double.parseDouble(in[3]);
		temp.height = (int)Double.parseDouble(in[4]);
		temp.fill = Color.gray;
		return temp;
	}
	public void update() {
		//write code here
	}
	//whether you are physically colliding with the platform
	public boolean collide_with(Player in) {
		return Rectangle.intersect(this, new Rectangle(in));
	}
	//whether the game should use your collision or ignore it for you to walk on stuff
	public boolean collide_with(Rectangle in, boolean player) {
		return Rectangle.intersect(this, in);
	}
	public void collision_action() {
		//write code here
	}
	public Vector2 snap_player_to_plat_ent() {
		return null;
	}
	public void transfer_player_momentum() {
		//write code here
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
		if (this.stops != null) for (int x = 0; x<this.stops.length; x++) this.stops[x] = Vector2.mult(this.stops[x], in);
		if (this.nodes != null) for (int x = 0; x<this.nodes.length; x++) this.nodes[x] = Vector2.mult(this.nodes[x], in);
	}
	public void draw_plat_ent(Graphics g, JPanel pane, int xin, int yin, String location) {
		this.draw(g, pane, xin, yin, location);
	}
	public void generate_sprites(PlatEntity[] platforms) {
		//nothing here
	}
	public void start_sprites(String in) {
		//nothing here either lmao
	}
	public void start_assets(Platform[] in) {
		//nothing here too lol
	}
	public void start_stops() {
		this.stops = new Vector2[2];
		for (int x = 0; x<2; x++) {
			this.stops[x] = new Vector2(this.pos.x + 100 + 25 * x, this.pos.y);
		}
	}
	public String toString() {
		return "plat_ent " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " ";
	}
	public String give_class() {
		return "plat_ent";
	}
	public static void create_props(JFrame temp, JTextField[] fields, JLabel[] labels) {
		fields[0] = new JTextField(LevelCreator.plat_ent_type + "");
		fields[0].setBounds(50, 50, 200, 25);
		fields[0].setVisible(true);
		temp.add(fields[0]);
		
		labels[0] = new JLabel("type");
		labels[0].setBounds(300, 50, 100, 50);
		labels[0].setVisible(true);
		temp.add(labels[0]);
	}
}
