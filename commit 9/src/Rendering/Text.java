package Rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import UI.UI;

public class Text {
	//String rendering
	public static String font_name = "/UI_textures/symbio_font.png";
	//maybe make two fonts, leveleditor and normal game font?
	public static BufferedImage[] symbio_font = new BufferedImage[50];
	public static char[] symbio_font_dictionary = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'.', ',', '\'', '?', '!', '-', '*', '(', ')', ':',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
			'u', 'v', 'w', 'x', 'y', 'z', '/', ' ', '~', '~'
			};
	public static int text_width = 3, text_height = 5;
	public static int h_spacing = 1, v_spacing = 2;
	public static int EDIT_MULT = 1;
	public static Color font_color = Color.white;
	
	//draw logic
	public static void draw_string(Graphics g, JPanel pane, double xin, double yin, String location, String in, Rectangle bounds, int padding) {
		
		if (in == null) return;
		
		bounds.draw(g, pane, xin, yin, location);
		String[] sep = Utility.separate((in + " ").toLowerCase());
		
		Vector2 conv_pos = Vector2.converted_pos(bounds.pos, pane, xin, yin, location);
		Rectangle conv = new Rectangle(conv_pos.x, conv_pos.y, bounds.width, bounds.height);
		
		Vector2 place = new Vector2((int)(conv.pos.x - conv.width / 2 + padding), (int)(conv.pos.y - conv.height / 2 + padding));
			
		for (int x = 0; x<sep.length; x++) {
			if (place.x + sep[x].length() * (text_width + 1) > conv.pos.x + conv.width / 2 - padding) {
				place = new Vector2((int)(conv.pos.x - conv.width / 2 + padding), place.y + text_height + v_spacing);
			}
			
			for (int y = 0; y<sep[x].length(); y++) {
				g.drawImage(symbio_font[char_match(sep[x].charAt(y))].getScaledInstance(text_width * (location.equals("edit") ? EDIT_MULT : 1), text_height * (location.equals("edit") ? EDIT_MULT : 1), Image.SCALE_DEFAULT), (int)place.x, (int)place.y, Start.pane);
				place.x += (text_width + h_spacing) * (location.equals("edit") ? EDIT_MULT : 1);
			}
			place.x += (text_width + h_spacing) * (location.equals("edit") ? EDIT_MULT : 1);
		}
	}
	public static int text_width(String in) {
		return (in.length()) * (symbio_font[0].getWidth() + h_spacing) * (Start.location.equals("EDIT") ? EDIT_MULT : 1);
	}
	public static int text_height() {
		return symbio_font[0].getHeight() * (Start.location.equals("EDIT") ? EDIT_MULT : 1);
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
