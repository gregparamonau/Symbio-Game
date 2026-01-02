package UI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Camera;
import Rendering.Text;

public class UI {
	//logic
	static Vector2 current_mouse_pos;
	static MouseListener mouse_listener;
	static boolean in_main_menu;
	public static boolean in_pause_menu;
	static Button[] buttons = new Button[0];
	static BufferedImage title, cursor, cursor_hover;
	
	public static boolean cursor_hover_state = false;	
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
		
		Start.count = 0;
		do {
			long a = System.currentTimeMillis();
			current_mouse_pos = convert_mouse_pos(current_mouse_pos(), Start.cam.pos);
			
			hovering();
			//for (int x = 0; x<buttons.length; x++) buttons[x].on_button(current_mouse_pos);
			
			//draw & update script
			draw_main(Start.gs[Start.count % 2]);
			Start.pane.repaint();
			//Start.g.drawImage(Start.op.filter(Start.imgs[Start.count % 2], null), 0, 0, Start.pane);
			Start.count++;
			
			long b = System.currentTimeMillis();
			
			try {Thread.sleep(Math.max(1000/60 - (b -     a), 0)); }catch(Exception e) {e.printStackTrace();}

			
			
			
		}while(in_main_menu);
		
		Start.frame.removeMouseListener(mouse_listener);
		
		if (Start.choice == 0) {
			Game.game();
		}
		if (Start.choice == 1) {
			Camera.pixel_size = Camera.pixel_size_edit;
			Start.pane.setSize(Start.pane.getWidth() * Camera.tile_size * 200, 200 * Start.pane.getHeight() * Camera.tile_size);
			
			LevelEditor.level_editor();
		}
		if (Start.choice == 2) ;//options
	}
	public static void hovering() {
		for (int x = 0; x<buttons.length; x++) {
			if (buttons[x].on_button(current_mouse_pos)) {
				cursor_hover_state = true;
				return;
			}
		}
		cursor_hover_state = false;
	}
	public static void start_pause_menu() {
		in_pause_menu = true;
		
		pause_init();
	}
	public static void update_pause_menu() {
		current_mouse_pos = convert_mouse_pos(current_mouse_pos(), Game.cam.pos);
	}
	public static void end_pause_menu() {
		Start.frame.removeMouseListener(mouse_listener);
		
		in_pause_menu = false;
	}
	
	//visual
	public static void draw_main(Graphics g) {
		new Rectangle(0, 0, 0, 0).draw_with_sprite(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, Start.cam.back, "game");
		//TODO: draw background
		
		new Rectangle(title_offset.x, title_offset.y, 0, 0).draw_with_sprite(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, title, "game");
		
		for (int x = 0; x<buttons.length; x++) buttons[x].draw_button(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, "game");
		
		if (!cursor_hover_state) {
			new Rectangle(current_mouse_pos.x + 4, current_mouse_pos.y - 4, 0, 0).draw_with_sprite(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, cursor, "game");
		}
		if (cursor_hover_state) {
			new Rectangle(current_mouse_pos.x + 4, current_mouse_pos.y - 4, 0, 0).draw_with_sprite(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, cursor_hover, "game");
		}
		
		
		new Rectangle(0, 0, 0, 0).draw_with_sprite(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, rotateImageByDegrees(title, (System.currentTimeMillis() % 5000) / 5000.0 * 360), "game");
		
		//current_mouse_pos.draw_node(g, Start.pane, Start.cam.pos.x, Start.cam.pos.y, "game", Color.green);
		
		//TODO: draw title screen + other text
	}
	
	public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
		
		double a = System.nanoTime();
	    double rads = Math.toRadians(angle);
	    double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
	    int w = img.getWidth();
	    int h = img.getHeight();
	    int newWidth = (int) Math.floor(w * cos + h * sin);
	    int newHeight = (int) Math.floor(h * cos + w * sin);

	    BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = rotated.createGraphics();
	    AffineTransform at = new AffineTransform();
	    at.translate((newWidth - w) / 2, (newHeight - h) / 2);

	    int x = w / 2;
	    int y = h / 2;

	    at.rotate(rads, x, y);
	    g2d.setTransform(at);
	    g2d.drawImage(img, 0, 0, null);
	    g2d.setColor(new Color(0, 0, 0, 0));
	    g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
	    g2d.dispose();
	    
	    double b = System.nanoTime();
	    
	    System.out.println("ROTATE: " + (double)(b - a) / 1000000);

	    return rotated;
	}
	public static void draw_pause(Graphics g) {
		g.setColor(new Color(39, 65, 86, 192));
		g.fillRect(0, 0, Camera.res_width, Camera.res_height + 2);
		
		for (int x = 0; x<buttons.length; x++) buttons[x].draw_button(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		
		if (!cursor_hover_state) {
			new Rectangle(current_mouse_pos.x + 4, current_mouse_pos.y - 4, 0, 0).draw_with_sprite(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, cursor, "game");
		}
		if (cursor_hover_state) {
			new Rectangle(current_mouse_pos.x + 4, current_mouse_pos.y - 4, 0, 0).draw_with_sprite(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, cursor_hover, "game");
		}
		
		//current_mouse_pos.draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.red);
	}
	
	//start assets
	public static void main_init() {
		start_sprites();
		start_mouse_listener();
		corner_offset = new Vector2(-Camera.res_width / 2 + padding, Camera.res_height / 2 - padding);
		title_offset = new Vector2(corner_offset.x + title.getWidth() / 2, corner_offset.y - title.getHeight() / 2);
		
		//main menu setup
		buttons = new Button[] {
				new Button(corner_offset.x + button_dimensions.x / 2, - padding / 2, button_dimensions.x, button_dimensions.y, "play ", Button::main_play_function),
				new Button(corner_offset.x + button_dimensions.x / 2, - button_dimensions.y - button_spacing - padding / 2, button_dimensions.x, button_dimensions.y, "editor ", Button::level_edit_function),
				new Button(corner_offset.x + button_dimensions.x / 2, - 2 * (button_dimensions.y + button_spacing) - padding / 2, button_dimensions.x, button_dimensions.y, "options ", Button::main_options_function),
				new Button(corner_offset.x + button_dimensions.x / 2, - 3 * (button_dimensions.y + button_spacing) - padding / 2, button_dimensions.x, button_dimensions.y, "quit ", Button::main_quit_function)
		};
	}
	public static void pause_init() {
		start_sprites();
		start_mouse_listener();
		corner_offset = new Vector2(Game.cam.pos.x - Camera.res_width / 2 + padding, Game.cam.pos.y + Camera.res_height / 2 - padding);
		title_offset = new Vector2(corner_offset.x + title.getWidth() / 2, corner_offset.y - title.getHeight() / 2);
		
		//pause menu setup
		buttons = new Button[] {
				new Button(Game.cam.pos.x, Game.cam.pos.y + button_dimensions.y + button_spacing, button_dimensions.x, button_dimensions.y, "play ", Button::pause_play_function),
				new Button(Game.cam.pos.x, Game.cam.pos.y + 0, button_dimensions.x, button_dimensions.y, "options ", Button::pause_options_function),
				new Button(Game.cam.pos.x, Game.cam.pos.y - button_dimensions.y - button_spacing, button_dimensions.x, button_dimensions.y, "quit ", Button::pause_quit_function)
		};
	}
	public static void start_mouse_listener() {
		mouse_listener = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.getButton() == 3) System.exit(0);
				//code here
				for (int x = 0; x<buttons.length; x++) buttons[x].button_clicked(current_mouse_pos);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//code here
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				//removing cursor
				Start.frame.setCursor(Start.frame.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), ""));
			}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		};
		
		Start.frame.addMouseListener(mouse_listener);
	}
	public static Vector2 current_mouse_pos() {
		return new Vector2(MouseInfo.getPointerInfo().getLocation().getX() - Start.frame.getLocationOnScreen().getX(), MouseInfo.getPointerInfo().getLocation().getY() - Start.frame.getLocationOnScreen().getY() - Start.frame.getInsets().top);
	}
	public static Vector2 convert_mouse_pos(Vector2 in, Vector2 pos) {
		return new Vector2(pos.x - (Start.pane.getWidth() / 2 - in.x) / Camera.pixel_size, pos.y - (in.y - Start.pane.getHeight() / 2) / Camera.pixel_size);
		//return new Vector2((int)(Game.cam.pos.x + in.x - Game.pane.getWidth()/2) / Camera.pixel_size, (int)(Game.pane.getHeight()/2 + Game.cam.pos.y - in.y) / Camera.pixel_size);
	}
	
	public static void start_sprites() {
		try {
			title = ImageIO.read(UI.class.getResource("/UI_textures/symbio_title.png"));
			cursor = ImageIO.read(UI.class.getResource("/UI_textures/cursor.png")).getSubimage(0, 0, 8, 8);
			cursor_hover = ImageIO.read(UI.class.getResource("/UI_textures/cursor.png")).getSubimage(8, 0, 8, 8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
