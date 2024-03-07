package Symbio.Rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Symbio.Platform;
import Symbio.Logic.Utility;
import Symbio.Logic.Vector2;

public class Background extends Platform{
	public static final String[] sprite_sheets = {"/extra_textures/back_sprite_sheet_1.png", "/extra_textures/back_sprite_sheet_2.png"};//TBD
	
	public Background() {}
	public Background(double a, double b, double c, double d, String sprite) {
		this.pos = new Vector2(a, b);
		this.width = (int)c;
		this.height = (int)d;
		this.type = find_in_array(sprite);
		this.sprite_name = sprite;
		this.fill = new Color(60, 73, 63);
		this.start_nodes();
	}
	public String toString() {
		return this.pos.x + " " + this.pos.y + " " + this.width + " " + this.height + " " + this.sprite_name + " ";
	}
	public int find_in_array(String in) {
		for (int x = 0; x<sprite_sheets.length; x++) {
			if (in.equals(sprite_sheets[x])) return x;
		}
		return 0;
	}
}
