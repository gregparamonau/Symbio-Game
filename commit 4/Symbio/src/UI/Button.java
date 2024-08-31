package UI;

import java.awt.Color;
import java.awt.Graphics;

import Logic.Rectangle;
import Logic.Vector2;
import Main.Game;
import Rendering.Camera;
import Rendering.Text;

public class Button extends Rectangle{
	public String text, function;
	public static Color button_fill = Color.orange;
	public static int padding = 3;
	
	public Button(double a, double b, double c, double d, String text, String function) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.text = text;
		this.fill = button_fill;
		this.function = function;
	}
	public void button_clicked() {
		if (!this.on_button(UI.current_mouse_pos)) return;
		System.out.println("function: " + this.function);
	
		if (this.function.equals("main_play_function")) main_play_function();
		if (this.function.equals("main_options_function")) main_options_function();
		if (this.function.equals("main_quit_function")) main_quit_function();
		
		if (this.function.equals("pause_play_function")) pause_play_function();
		if (this.function.equals("pause_options_function")) pause_options_function();
		if (this.function.equals("pause_quit_function")) pause_quit_function();
	}
	
	//main_menu functions
	public void main_play_function() {
		Settings.load_settings();
		UI.in_main_menu = false;
	}
	public void main_options_function() {
		//TODO: make code
	}
	public void main_quit_function() {		
		System.exit(0);
	}
	
	//pause menu functions
	public void pause_play_function() {
		UI.end_pause_menu();
	}
	public void pause_options_function() {
		//TODO: make code
	}
	public void pause_quit_function() {
		Settings.save_settings();
		System.exit(0);
	}
	
	//visual
	public void draw_button(Graphics g) {
		this.draw(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game");		
		Text.draw_string(g, this.text, this, padding);
	}
	
	//logic
	public boolean on_button(Vector2 in) {
		boolean out = Rectangle.intersect(new Rectangle(in.x, in.y, 0, 0), this);
		System.out.println("OUT: " + out);
		return Rectangle.intersect(new Rectangle(in.x, in.y, 0, 0), this);
	}
}
