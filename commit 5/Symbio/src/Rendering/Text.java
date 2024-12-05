package Rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import UI.UI;

public class Text {
	//String rendering
	public static String font_name = "/UI_textures/symbio_font.png";
	public static BufferedImage[] symbio_font = new BufferedImage[50];
	public static char[] symbio_font_dictionary = {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
			'u', 'v', 'w', 'x', 'y', 'z', '~', '~', '~', '~', 
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'.', ',', '\'', '?', '!', '-', '*', '(', ')', ':'
			};
	public static int text_width = 3, text_height = 5;
	public static int h_spacing = 1, v_spacing = 2;
	public static Color font_color = Color.white;
	
	//draw logic
	public static void draw_string(Graphics g, String in, Rectangle bounds, int padding) {
		String[] sep = Utility.separate(in.toLowerCase());
		
		Vector2 conv_pos = Vector2.converted_pos(bounds.pos, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		Rectangle conv = new Rectangle(conv_pos.x, conv_pos.y, bounds.width, bounds.height);
		
		Vector2 place = new Vector2((int)(conv.pos.x - conv.width / 2 + padding), (int)(conv.pos.y - conv.height / 2 + padding));
			
		for (int x = 0; x<sep.length; x++) {
			if (place.x + sep[x].length() * (text_width + 1) > conv.pos.x + conv.width / 2 - padding) {
				place = new Vector2((int)(conv.pos.x - conv.width / 2 + padding), place.y + text_height + v_spacing);
			}
			
			for (int y = 0; y<sep[x].length(); y++) {
				g.drawImage(symbio_font[char_match(sep[x].charAt(y))], (int)place.x, (int)place.y, Game.pane);
				place.x += text_width + h_spacing;
			}
			place.x += text_width + h_spacing;
		}
	}
	public static int char_match(char in) {
		for (int x = 0; x<symbio_font_dictionary.length; x++) {
			if (symbio_font_dictionary[x] == in) return x;
		}
		return 0;
	}
	
	//setup logic
	public static void init_UI() {
		start_font(font_name);
	}
	public static void start_font(String in) {
		try {
			BufferedImage source = Utility.replace_color(ImageIO.read(Text.class.getResource(in)), font_color);

			for (int x = 0; x<symbio_font.length; x++) {
				symbio_font[x] = source.getSubimage((x % 10) * text_width, (x / 10) * text_height, text_width, text_height);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
