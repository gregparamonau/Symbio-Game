package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Camera;
import Rendering.Text;

public class UI {
	//logic
	static Vector2 current_mouse_pos;
	static MouseListener mouse_listener;
	static boolean in_main_menu;
	public static boolean in_pause_menu;
	static Button[] buttons = new Button[0];
	static BufferedImage title;
	
	//visual & spacing
	
	//top left corner of everything
	static int padding = 20;
	static Vector2 button_dimensions = new Vector2(34, 11);
	static int button_spacing = 5;
	
	//main menu offsets
	static Vector2 corner_offset;
	static Vector2 title_offset;
	
	static Vector2 main_play_button_offset;
	static Vector2 main_options_button_offset;
	static Vector2 main_quit_button_offset;
	
	//pause menu offsets
	static Vector2 pause_play_button_offset;
	static Vector2 pause_options_button_offset;
	static Vector2 pause_quit_button_offset;
	
	public static void main_menu() {
		in_main_menu = true;
		
		main_init();
		
		Game.count = 0;
		do {
			current_mouse_pos = convert_mouse_pos(current_mouse_pos());
			
			for (int x = 0; x<buttons.length; x++) buttons[x].on_button(current_mouse_pos);
			
			//draw & update script
			draw_main(Game.gs[Game.count % 2]);
			Game.g.drawImage(Game.op.filter(Game.imgs[Game.count % 2], null), 0, 0, Game.pane);
			Game.count++;
			
		}while(in_main_menu);
		
		Game.frame.removeMouseListener(mouse_listener);
	}
	public static void start_pause_menu() {
		in_pause_menu = true;
		
		pause_init();
	}
	public static void update_pause_menu() {
		current_mouse_pos = convert_mouse_pos(current_mouse_pos());
	}
	public static void end_pause_menu() {
		Game.frame.removeMouseListener(mouse_listener);
		
		in_pause_menu = false;
	}
	
	//visual
	public static void draw_main(Graphics g) {
		new Rectangle(0, 0, 0, 0).draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, Game.cam.back, "game");
		//TODO: draw background
		new Rectangle(title_offset.x, title_offset.y, 0, 0).draw_with_sprite(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, title, "game");
		
		for (int x = 0; x<buttons.length; x++) buttons[x].draw_button(g);
		
		current_mouse_pos.draw_node(g, Game.pane, 0, 0, "game", Color.red);
		
		//TODO: draw title screen + other text
	}
	public static void draw_pause(Graphics g) {
		g.setColor(new Color(39, 65, 86, 192));
		g.fillRect(0, 0, Camera.res_width, Camera.res_height + 2);
		
		for (int x = 0; x<buttons.length; x++) buttons[x].draw_button(g);
		
		current_mouse_pos.draw_node(g, Game.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.red);
	}
	
	//start assets
	public static void main_init() {
		start_sprites();
		start_mouse_listener();
		corner_offset = new Vector2(-Camera.res_width / 2 + padding, Camera.res_height / 2 - padding);
		title_offset = new Vector2(corner_offset.x + title.getWidth() / 2, corner_offset.y - title.getHeight() / 2);
		
		//main menu setup
		buttons = new Button[] {
				new Button(corner_offset.x + button_dimensions.x / 2, - padding / 2, button_dimensions.x, button_dimensions.y, "play ", "main_play_function"),
				new Button(corner_offset.x + button_dimensions.x / 2, - button_dimensions.y - button_spacing - padding / 2, button_dimensions.x, button_dimensions.y, "options ", "main_options_function"),
				new Button(corner_offset.x + button_dimensions.x / 2, - 2 * (button_dimensions.y + button_spacing) - padding / 2, button_dimensions.x, button_dimensions.y, "quit ", "main_quit_function")
		};
	}
	public static void pause_init() {
		start_sprites();
		start_mouse_listener();
		corner_offset = new Vector2(Game.cam.pos.x - Camera.res_width / 2 + padding, Game.cam.pos.y + Camera.res_height / 2 - padding);
		title_offset = new Vector2(corner_offset.x + title.getWidth() / 2, corner_offset.y - title.getHeight() / 2);
		
		//pause menu setup
		buttons = new Button[] {
				new Button(Game.cam.pos.x, Game.cam.pos.y + button_dimensions.y + button_spacing, button_dimensions.x, button_dimensions.y, "play ", "pause_play_function"),
				new Button(Game.cam.pos.x, Game.cam.pos.y + 0, button_dimensions.x, button_dimensions.y, "options ", "pause_options_function"),
				new Button(Game.cam.pos.x, Game.cam.pos.y - button_dimensions.y - button_spacing, button_dimensions.x, button_dimensions.y, "quit ", "pause_quit_function")
		};
	}
	public static void start_mouse_listener() {
		mouse_listener = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				//code here
				for (int x = 0; x<buttons.length; x++) buttons[x].button_clicked();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//code here
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		};
		
		Game.frame.addMouseListener(mouse_listener);
	}
	public static Vector2 current_mouse_pos() {
		return new Vector2(MouseInfo.getPointerInfo().getLocation().getX() - Game.frame.getLocationOnScreen().getX(), MouseInfo.getPointerInfo().getLocation().getY() - Game.frame.getLocationOnScreen().getY() - Game.frame.getInsets().top);
	}
	public static Vector2 convert_mouse_pos(Vector2 in) {
		return new Vector2(Game.cam.pos.x - (Game.pane.getWidth() / 2 - in.x) / Camera.pixel_size, Game.cam.pos.y - (in.y - Game.pane.getHeight() / 2) / Camera.pixel_size);
		//return new Vector2((int)(Game.cam.pos.x + in.x - Game.pane.getWidth()/2) / Camera.pixel_size, (int)(Game.pane.getHeight()/2 + Game.cam.pos.y - in.y) / Camera.pixel_size);
	}
	
	public static void start_sprites() {
		try {
			title = ImageIO.read(UI.class.getResource("/UI_textures/symbio_title.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
