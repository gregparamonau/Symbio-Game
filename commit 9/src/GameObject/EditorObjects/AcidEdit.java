package GameObject.EditorObjects;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import GameObject.EditObject;
import GameObject.Objects.Acid;
import LevelEdit.LevelEditor;
import Logic.Vector2;

public class AcidEdit extends Acid{
	
	public AcidEdit() {}
	public AcidEdit(double a, double b, double c, double d, double e, String sprite, int id) {
		super(a, b, c, d, e, sprite, id);
	}
	
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		this.draw(g, pane, xin, yin, location);
	}
	public static AcidEdit default_acid(Vector2 loc) {
		return new AcidEdit(loc.x, loc.y, LevelEditor.grid_size, LevelEditor.grid_size, -1, sprite_name_default, 0);
	}
	
	public String toString() {
		return "acid " + this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.object_handle + " " + sprite_name_default + " ";
	}
	public String give_class() {
		return "acid";
	}
}
