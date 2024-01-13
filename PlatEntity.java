package Symbio.Entity;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import LevelEdit.LevelCreator;
import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Entity.PlatEntities.Bouncer;
import Symbio.Entity.PlatEntities.Hair;
import Symbio.Entity.PlatEntities.Mover;
import Symbio.Entity.PlatEntities.RoomT;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;

public class PlatEntity extends Rectangle{
	public int id = 0;
	public int radius;
	public int variant;
	public Vector2[] stops;
	public boolean sliceable = true;
	public double slice_strength = 35;
	public String sprite_source = "";
	
	public Vector2 vel = new Vector2(0, 0);
	public int xvel = 0, yvel = 0;
	
	//roomt
	public int room_to, room_to_xpos, room_to_ypos;
	
	public static PlatEntity create_plat_entity(String[] in, int id, Platform[] platforms) {
		//System.out.println(in[0] + "!");
		if (in[0].equals("roomt")) {
			return new RoomT(Integer.parseInt(in[1]), Integer.parseInt(in[2]), Integer.parseInt(in[3]), Integer.parseInt(in[4]), Integer.parseInt(in[5]), Integer.parseInt(in[6]), Integer.parseInt(in[7]), id);
		}
		if (in[0].equals("hair")) {
			return new Hair(Integer.parseInt(in[1]), Integer.parseInt(in[2]), Integer.parseInt(in[3]), Integer.parseInt(in[4]), id, platforms);
		}
		if (in[0].equals("bouncer")) {
			return new Bouncer(Integer.parseInt(in[1]), Integer.parseInt(in[2]), Integer.parseInt(in[3]), id);
		}
		if (in[0].equals("mover")) {
			return new Mover(Utility.parse_array(Utility.sub_array(in, 1, in.length)), id);
		}
		PlatEntity temp = new PlatEntity();
		temp.pos = new Vector2(Integer.parseInt(in[1]), Integer.parseInt(in[2]));
		temp.width = Integer.parseInt(in[3]);
		temp.height = Integer.parseInt(in[4]);
		temp.fill = Color.gray;
		return temp;
	}
	public void update() {
		//write code here
	}
	public boolean collide_with(Player in) {
		return Rectangle.intersect(this, new Rectangle(in));
	}
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
		in.momentum = Vector2.add(in.momentum, Vector2.mult(Vector2.neg(in.swipe_dir), this.slice_strength));
		
		if (Math.abs(in.swipe_dir.y) > Math.abs(in.swipe_dir.x)) in.momentum.y = Utility.clamp(in.momentum.y, -this.slice_strength, this.slice_strength);
		else if (Math.abs(in.swipe_dir.y) < Math.abs(in.swipe_dir.x)) in.momentum.x = Utility.clamp(in.momentum.x, -this.slice_strength * 2, this.slice_strength * 2);

		in.vel = new Vector2(0, 0);
		in.gravity = 1;
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
	public void start_stops() {
		this.stops = new Vector2[2];
		for (int x = 0; x<2; x++) {
			this.stops[x] = new Vector2(this.pos.x + 100 + 25 * x, this.pos.y);
		}
	}
	public String toString() {
		return "plat_ent " + (int)this.pos.x + " " + (int)this.pos.y + " " + this.width + " " + this.height + " ";
	}
	public String give_class() {
		return "plat_ent";
	}
}
