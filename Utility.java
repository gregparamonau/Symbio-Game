package Symbio.Logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import Symbio.Game;
import Symbio.Platform;

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
	public static boolean platforms_intersect(Rectangle in, Platform[] platforms) {
		for (int x = 0; x<platforms.length; x++) {
			if (Rectangle.intersect(in, platforms[x])) return true;
		}
		return false;
	}
	public static int max_x(Platform[] platforms) {
		int out = (int)(platforms[0].pos.x);
		for (int x = 1; x<platforms.length; x++) {
			if (platforms[x].pos.x > out) out = (int)(platforms[x].pos.x);
		}
		return out;
	}
	public static int min_x(Platform[] platforms) {
		int out = (int)(platforms[0].pos.x);
		for (int x = 1; x<platforms.length; x++) {
			if (platforms[x].pos.x < out) out = (int)(platforms[x].pos.x);
		}
		return out;
	}
	public static int max_y(Platform[] platforms) {
		int out = (int)(platforms[0].pos.y);
		for (int x = 1; x<platforms.length; x++) {
			if (platforms[x].pos.y > out) out = (int)(platforms[x].pos.y);
		}
		return out;
	}
	public static int min_y(Platform[] platforms) {
		int out = (int)(platforms[0].pos.y);
		for (int x = 1; x<platforms.length; x++) {
			System.out.println(platforms[x].pos.y + " posy");
			if (platforms[x].pos.y < out) out = (int)(platforms[x].pos.y);
		}
		System.out.println(out + " out");
		return out;
	}
	public static boolean on_screen_custom(Rectangle in, Rectangle screen) {
		return Rectangle.intersect(in, screen);
	}
	public static boolean on_screen(Rectangle in, Vector2 pos) {
		return dx(in, pos) < Game.pane.getWidth()/2 && dy(in, pos) < Game.pane.getHeight()/2;
	}
	public static boolean on_screen_x(Rectangle in, Vector2 pos) {
		return dx(in, pos) < Game.pane.getWidth()/2;
	}
	public static boolean on_screen_y(Rectangle in, Vector2 pos) {
		return dy(in, pos) < Game.pane.getHeight()/2;
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
	public static int[] parse_array(String[] in) {
		int[] out = new int[in.length];
		for (int x = 0; x<in.length; x++) {
			out[x] = Integer.parseInt(in[x]);
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
	public static int sign(double a) {
		if (a >= 0) return 1;
		else return -1;
	}
}
