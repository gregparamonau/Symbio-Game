package UI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Camera;
import Rendering.Text;

public class Button extends Rectangle{
	public String text, function;
	public static Color button_fill = Color.orange;
	public static int padding = 3;
	public Runnable action;
	
	public Button(double a, double b, double c, double d, String text, Runnable run) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.text = text;
		this.fill = button_fill;
		this.action = run;
	}
	public void button_clicked(Vector2 in) {
		if (!this.on_button(in)) return;
		
		this.action.run();
		
		//TODO: convert all leveleditor code to use buttons & lambda functions
	}
	
	//main_menu functions
	public static void main_play_function() {
		Settings.load_settings();
		UI.in_main_menu = false;
		
		Start.location = "GAME";
		Start.choice = 0;
		
		System.out.println("PLAY");
	}
	public static void level_edit_function() {
		UI.in_main_menu = false;
		Start.location = "EDIT";
		Start.choice = 1;
		System.out.println("EDIT");
	}
	public static void main_options_function() {
		//TODO: make code
		Start.choice = 2;
		System.out.println("OPTIONS");
	}
	public static void main_quit_function() {
		System.out.println("QUIT");
		System.exit(0);
		
	}
	
	//pause menu functions
	public static void pause_play_function() {
		UI.end_pause_menu();
	}
	public static void pause_options_function() {
		//TODO: make code
	}
	public static void pause_quit_function() {
		Settings.save_settings();
		System.exit(0);
	}
	
	//visual
	public void draw_button(Graphics g, JPanel pane, double xin, double yin, String location) {
		this.draw(g, pane, xin, yin, location);		
		Text.draw_string(g, pane, xin, yin, location, this.text, this, padding);
	}
	
	//logic
	public boolean on_button(Vector2 in) {
		//TODO: huh?
		boolean out = Rectangle.intersect(new Rectangle(in.x, in.y, 0, 0), this);
		System.out.println("OUT: " + out);
		return Rectangle.intersect(new Rectangle(in.x, in.y, 0, 0), this);
	}
}
