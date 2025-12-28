package GameObject;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GameObject.EditorObjects.AcidEdit;
import GameObject.EditorObjects.BezierTerrainEdit;
import GameObject.EditorObjects.BouncerEdit;
import GameObject.EditorObjects.CircleEdit;
import GameObject.EditorObjects.EnemySpawnerEdit;
import GameObject.EditorObjects.MoverEdit;
import GameObject.EditorObjects.OneWayEdit;
import GameObject.EditorObjects.SlopeEdit;
import GameObject.EditorObjects.SpriteEdit;
import GameObject.Objects.Acid;
import GameObject.Objects.BezierTerrain;
import GameObject.Objects.Bouncer;
import GameObject.Objects.Circle;
import GameObject.Objects.EnemySpawner;
import GameObject.Objects.Mover;
import GameObject.Objects.OneWay;
import GameObject.Objects.Slope;
import GameObject.Objects.Sprite;
import LevelEdit.LevelEditor;
import LevelEdit.RoomEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Start;
import UI.OptionPane;

public class EditObject extends GameObject{
	public int id;
	public boolean solid = true;
	public boolean vis_solid = true;
	public boolean sliceable = false;
	
	public int object_handle = -1;
	
	public static String sprite_name_default = "/object_textures/mouth.png";
	
	
	public String sprite_name = sprite_name_default;
	public boolean displaceable = true;
	
	
	public EditObject() {}
	
	public EditObject(double a, double b, double c, double d, double e, String sprite, int id) {
		super(a, b, c, d, e, sprite, id);
		/*this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.start_nodes();
		
		this.object_handle = (int)e;
		
		this.sprite_name = sprite;
		
		this.id = id;*/
	}
	public static GameObject create_game_object(String[] in, int id) {
		if (in[0].equals("spawner")) {
			return new EnemySpawnerEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), in[4], Double.parseDouble(in[5]) == 1 ? true : false, id);
		}
		if (in[0].equals("acid")) {
			return new AcidEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6], id);
		}
		if (in[0].equals("bezier_terrain")) {
			return new BezierTerrainEdit(Double.parseDouble(in[1]), Utility.parse_array(Utility.sub_array(in, 2, in.length - 4)), Double.parseDouble(in[in.length - 4]), in[in.length - 3], in[in.length - 2], in[in.length - 1], id);
		}
		if (in[0].equals("bouncer")) {
			return new BouncerEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), in[5], id);
		}
		if (in[0].equals("circle")) {
			return new CircleEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), in[5], id);
		}
		if (in[0].equals("mover")) {
			return new MoverEdit(Utility.parse_array(Utility.sub_array(in, 1, in.length - 1)), in[in.length - 1], id);
		}
		if (in[0].equals("oneway")) {
			return new OneWayEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), Double.parseDouble(in[6]), in[7], id);
		}
		if (in[0].equals("slope")) {
			return new SlopeEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6], id);
		}
		if (in[0].equals("sprite")) {
			return new SpriteEdit(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6]);
		}
		EditObject temp = new EditObject(Double.parseDouble(in[1]), Double.parseDouble(in[2]), Double.parseDouble(in[3]), Double.parseDouble(in[4]), Double.parseDouble(in[5]), in[6], id);
		temp.fill = Color.gray;
		return temp;
	}
	
	public static GameObject create_default_game_object(String type, Vector2 loc) {
		if (type.equals("spawner")) return EnemySpawnerEdit.default_spawner(loc);
		if (type.equals("acid")) return AcidEdit.default_acid(loc);
		if (type.equals("bezier_terrain")) return BezierTerrainEdit.default_bezier_terrain(loc);
		if (type.equals("bouncer")) return BouncerEdit.default_bouncer(loc);
		if (type.equals("circle")) return CircleEdit.default_circle(loc);
		if (type.equals("mover")) return MoverEdit.default_mover(loc);
		if (type.equals("oneway")) return OneWayEdit.default_oneway(loc);
		if (type.equals("slope")) return SlopeEdit.default_slope(loc);
		if (type.equals("sprite")) return SpriteEdit.default_sprite(loc);
		return default_object(loc);
	}
	
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		this.draw(g, pane, xin, yin, location);
		this.draw_nodes(g, pane, xin, yin, location);
	}
	public static EditObject default_object(Vector2 loc) {
		return new EditObject(loc.x, loc.y, LevelEditor.grid_size, LevelEditor.grid_size, -1, sprite_name_default, 0);
	}
	
}
