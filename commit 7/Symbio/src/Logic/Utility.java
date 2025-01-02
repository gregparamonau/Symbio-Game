package Logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import GameObject.GameObject;
import Main.Game;
import Main.Start;

public class Utility {
	public static boolean gray_scale;
	public static double clamp(double in, double a, double b) {
		double out = in;
		if (out > Math.max(a, b)) out = Math.max(a, b);
		if (out < Math.min(a, b)) out = Math.min(a, b);
		return out;
	}
	public static boolean within_bounds(double in, double a, double b) {
		return (in >= Math.min(a, b) && in <= Math.max(a, b));
	}
	public static double clamp_above(double in, double min) {
		return Math.max(in, min);
	}
	public static double move_toward(double a, double b, double step) {
		double out = a;
		if (a > b) step *= -1;
		out += step;
		out = clamp(out, Math.min(a, b), Math.max(a, b));
		return out;
	}
	public static BufferedImage replace_color(BufferedImage in, Color col) {
		BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x<in.getWidth(); x++) {
			for (int y = 0; y<in.getHeight(); y++) {
				if (new Color(in.getRGB(x, y), true).getAlpha() != 0) out.setRGB(x, y, col.getRGB());
			}
		}
		return out;
	}
	public static void draw_scale(Graphics g, BufferedImage in, int mult) {
		long a = System.nanoTime();
		
		for (int x = 0; x<in.getWidth(); x++) {
			for (int y = 0; y<in.getHeight(); y++) {
				g.setColor(new Color(in.getRGB(x, y)));
				g.fillRect(x * mult, y * mult, mult, mult);
			}
		}
		long b = System.nanoTime();
		System.out.println("	IMAGE_SCALE: " + (double)(b - a) / 1000000);
	}
	public static BufferedImage transformed_instance(BufferedImage in, int mult, Color change, double alpha) {
		BufferedImage out = new BufferedImage(in.getWidth() * mult, in.getHeight() * mult, BufferedImage.TYPE_INT_ARGB);
		Graphics g = out.getGraphics();
		
		for (int y = 0; y<in.getHeight(); y++) {
			for (int x = 0; x<in.getWidth(); x++) {
				Color temp = new Color(in.getRGB(x, y));
				if (!temp.equals(Color.black)) {
					if (!change.equals(Color.black)) temp = new Color((temp.getRed() + change.getRed())/2, (temp.getGreen() + change.getGreen())/2, (temp.getBlue() + change.getBlue())/2);
					temp = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), (int)(255 * alpha));
					//System.out.println(temp.getRed() + " " + temp.getGreen() + " " + temp.getBlue() + " " + (int)(255 * alpha));
					g.setColor(temp);
					g.fillRect(x * mult, y * mult, mult, mult);
				}
			}
		}
		//System.exit(0);
		return out;
	}
	public static BufferedImage flip(BufferedImage in, boolean fliph, boolean flipv) {
		//fliph = flip across horizontal axis
		//flipv = flip across vertical axis
		BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y<out.getHeight(); y++) {
			for (int x = 0; x<out.getWidth(); x++) {
				int xtake = x, ytake = y;
				if (fliph) ytake = in.getHeight() - 1 - y;
				if (flipv) xtake = in.getWidth() - 1 - x;
				
				out.setRGB(x, y, in.getRGB(xtake, ytake));
			}
		}
		return out;
	}
	public static BufferedImage blur(BufferedImage in, int blur_radius) {
		BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y<out.getHeight(); y++) {
			for (int x = 0; x<out.getWidth(); x++) {
				//if (new Color(in.getRGB(x, y)).equals(Color.black)) continue;
				int average_div = 0;
				int[] temp = {0, 0, 0, 255};
				for (int ytemp = y - blur_radius; ytemp<y + blur_radius; ytemp++) {
					if (ytemp > out.getHeight() - 1) break;
					if (ytemp < 0) continue;
					for (int xtemp = x - blur_radius; xtemp<x + blur_radius; xtemp++) {
						if (xtemp < 0 || xtemp > out.getWidth() - 1) continue;
						Color temp2 = new Color(in.getRGB(xtemp, ytemp));
						if (temp2.equals(Color.black)) continue;
						temp[0] += temp2.getRed();
						temp[1] += temp2.getGreen();
						temp[2] += temp2.getBlue();
						average_div++;
					}
				}
				if (average_div != 0) {
					temp[0] /= average_div;
					temp[1] /= average_div;
					temp[2] /= average_div;
					out.setRGB(x, y, new Color(temp[0], temp[1], temp[2]).getRGB());
				}
			}
		}
		return out;
	}
	public static boolean platforms_intersect(Rectangle in, GameObject[] objects) {
		for (int x = 0; x<objects.length; x++) {
			if (Rectangle.intersect(in, objects[x])) return true;
		}
		return false;
	}
	public static boolean on_screen_custom(Rectangle in, Rectangle screen) {
		return Rectangle.intersect(in, screen);
	}
	public static boolean on_screen(Rectangle in, Vector2 pos) {
		//TODO: FIX THIS BITCH GAME BREAKER
		//return true;
		return dx(in, pos) < Start.pane.getWidth() / 2 / 8;// && dy(in, pos) < Game.pane.getHeight() / 2 / 8;
	}
	public static boolean on_screen_x(Rectangle in, Vector2 pos) {
		return dx(in, pos) < Start.pane.getWidth()/2;
	}
	public static boolean on_screen_y(Rectangle in, Vector2 pos) {
		return dy(in, pos) < Start.pane.getHeight()/2;
	}
	public static double dx(Rectangle in, Vector2 pos) {
		return (Math.abs(pos.x - in.pos.x) - in.width/2);
	}
	public static double dy(Rectangle in, Vector2 pos) {
		return (Math.abs(pos.y - in.pos.y) - in.height/2);
	}
	public static double dist(double a, double b, double c, double d) {
		return Math.sqrt((a - c) * (a - c) + (b - d) * (b - d));
	}
	public static double round(double in, int decimal_place) {
		//decimal place = places after the decimal
		return Math.round(in * Math.pow(10, decimal_place)) / (double)Math.pow(10, decimal_place);
	}
	public static String[] separate(String line) {
		int count = 0;
		for (int x = 0; x<line.length(); x++) if (line.charAt(x) == ' ') count++;
		String[] out = new String[count];
		for (int x = 0; x<count; x++) {
			out[x] = line.substring(0, line.indexOf(" "));
			line = line.substring(line.indexOf(" ") + 1);
		}
		return out;
	}
	public static double[] parse_array(String[] in) {
		double[] out = new double[in.length];
		for (int x = 0; x<in.length; x++) {
			out[x] = Double.parseDouble(in[x]);
		}
		return out;
	}
	public static String[] sub_array(String[] in, int start, int end) {
		//start = the one we start with
		//end = the one after the last one we copy
		String[] out = new String[end - start];
		for (int x = start; x<end; x++) {
			out[x - start] = in[x];
		}
		return out;
	}
	public static String[] add_to_array(String[] in, String[] add) {
		String[] out = new String[in.length + add.length];
		for (int x = 0; x<in.length; x++) out[x] = in[x];
		for (int x = 0; x<add.length; x++) out[in.length + x] = add[x];
		return out;
	}
	public static int[] add_to_array(int[] in, int add) {
		int[] out = new int[in.length + 1];
		for (int x = 0; x<in.length; x++) out[x] = in[x];
		out[in.length] = add;
		return out;
	}
	public static int sign(double a) {
		if (a == 0) return 0;
		else if (a >= 0) return 1;
		else return -1;
	}
}
