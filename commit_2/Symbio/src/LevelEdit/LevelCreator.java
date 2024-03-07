package LevelEdit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Symbio.Game;
import Symbio.Platform;
import Symbio.Entity.PlatEntity;
import Symbio.Entity.PlatEntities.RoomT;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Vector2;
import Symbio.Rendering.Background;
import DataManager.DataManager;

public class LevelCreator {
	static JFrame frame;
	public static JPanel pane;
	static JPanel select;
	static Graphics g, g2;
	static BufferedImage[] imgs = {new BufferedImage(1250, 811, 1), new BufferedImage(1250, 811, 1)};
	static Graphics[] gs = {imgs[0].getGraphics(), imgs[1].getGraphics()};
	
	public static Platform[] platforms = new Platform[0];
	public static PlatEntity[] plat_ents = new PlatEntity[0];
	public static Background[] background = new Background[0];
	static JButton[] selection_panel = new JButton[15];
	static JButton properties;
	
	public static Vector2 pos = new Vector2(0, 0);
	public static int grid_size = 25;
	public static int[] plat_select = {-1, -1};
	public static int[] plat_ent_select = {-1, -1};
	public static int[] back_select = {-1, -1};
	static boolean game_saved = false, draw_nodes = true;
	
	//platform save
	public static int current_plat_type = 0;
	
	//plat_ent_save
	public static String plat_ent_type = "roomt";
	public static int plat_ent_variant = 0;
	
	//back_save
	public static String back_sprite = "/extra_textures/back_sprite_sheet_1.png";
	
	public static String DATA_FILE = System.getProperty("user.home") + File.separator + "save_game.txt";
	public static String texture = "/test_art1.png";
	
	//TODO
	public static void main(String[] args) {
		System.out.println("61");
		level_selector_menu();
	}
	
	public static void level_selector_menu() {
		System.out.println("66");
		frame = new JFrame();
		start_arrays();
		int a = JOptionPane.showConfirmDialog(frame, "would you like to load an existing room?", "choice", JOptionPane.YES_NO_OPTION);
		if (a == JOptionPane.YES_OPTION) {
			DataManager.new_room = false;
			int b = Integer.parseInt(JOptionPane.showInputDialog("if so please specify\nthe room number"));
			load_action(b);
		}
		if (a == JOptionPane.NO_OPTION) {
			DataManager.new_room = true;
			create_action();
		}
	}
	
	public static void level_editor() {
		
		start_panels();
		
		int count = 0;
		do {
			long a = System.currentTimeMillis();
			g.drawImage(imgs[(count+1)%2], 0, 0, null);
			draw_view(gs[count%2]);
			count++;
			long b = System.currentTimeMillis();
			try {Thread.sleep(10-(b-a));}catch(Exception e) {e.printStackTrace();}
		}while(true);
	}
	
	//TODO RENDERING
	public static void draw_view(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, (int)pane.getWidth(), (int)pane.getHeight());
		draw_grid(g);
		
		//if (!drawNodes) return;
		if (background.length != 0) 
			for (int x = 0; x<background.length; x++) background[x].draw(g, pane, pos.x, pos.y, "edit");
		if (platforms.length != 0)
			for (int x = 0; x<platforms.length; x++) platforms[x].draw_platform(g, pane, (int)pos.x, (int)pos.y, "edit");
		if (plat_ents.length != 0)
			for (int x = 0; x<plat_ents.length; x++) plat_ents[x].draw_plat_ent(g, pane, (int)pos.x, (int)pos.y, "edit");
		
		g.setColor(Color.cyan);
		g.fillOval((int)(pane.getWidth()/2 - pos.x - 15), (int)(pane.getHeight()/2 - pos.y - 15), 30, 30);
		
	}
	
	public static void draw_grid(Graphics g) {
		g.setColor(Color.black);
		for (int x = 0; x<pane.getWidth()/grid_size + 2; x++) {
			for (int y = 0; y<pane.getHeight()/grid_size + 2; y++) {
				g.drawRect((int)(x * grid_size - pos.x % grid_size), (int)(y * grid_size - pos.y % grid_size), grid_size, grid_size);
			}
		}
	}
	
	//TODO start panels
	public static void start_panels() {
		frame = new JFrame();
		frame.setSize(1250, 775);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pane = new JPanel();
		pane.setSize(1250, 650);
		frame.add(pane);
		try {Thread.sleep(2000);}catch(Exception e) {e.printStackTrace();}
		g = pane.getGraphics();
		
		select = new JPanel();
		select.setSize(1250, 100);
		select.setLocation(0, 650);
		frame.add(select);
		start_select();
		select.repaint();
		try {Thread.sleep(2000);}catch(Exception e) {e.printStackTrace();}
		g2 = select.getGraphics();
		
		add_keys();
		add_mouse();
	}
	
	//TODO ADD ACTIONS
	public static void add_keys() {
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 37) pos.x -= 20;
				if (e.getKeyCode() == 39) pos.x += 20;
				
				if (e.getKeyCode() == 38) pos.y -= 20;
				if (e.getKeyCode() == 40) pos.y += 20;
				
				//'p' to end
				if (e.getKeyCode() == 80) {
					question_saving();
					System.exit(0);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
	
	public static void add_mouse() {
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				plat_select = plat_intersect(e.getX(), e.getY() - 25);
				if (plat_select[0] == -1) plat_ent_select = plat_ent_intersect(e.getX(), e.getY() - 25);
				if (plat_select[0] == -1 && plat_ent_select[0] == -1) back_select = back_intersect(e.getX(), e.getY() - 25);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Vector2 temp = new Vector2((int)(pos.x + e.getX() - pane.getWidth()/2), (int)(pane.getHeight()/2 - pos.y - e.getY() + 30));
				if (plat_select[0] != -1) {
					platforms[plat_select[0]].update_nodes(temp, plat_select[1]);
				}
				else if (plat_ent_select[0] != -1) {
					System.out.println(plat_ent_select[0] + " " + plat_ent_select[1]);
					plat_ents[plat_ent_select[0]].update_nodes(temp, plat_ent_select[1]);
				}
				else if (back_select[0] != -1) {
					background[back_select[0]].update_nodes(temp, back_select[1]);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});
	}
	
	//TODO intersect/select actions
	public static int select_intersect(int x, int y) {
		return -1;
	}
	public static int[] plat_intersect(int x, int y) {
		int[] out = {-1, -1};
		for (int i = 0; i<platforms.length; i++) {
			for (int j = 0; j<platforms[i].nodes.length; j++) {
				if (d(x, y, platforms[i].nodes[j].x + pane.getWidth()/2 - pos.x, -platforms[i].nodes[j].y + pane.getHeight()/2 - pos.y) < Vector2.radius_edit) {
					out[0] = i;
					out[1] = j;
					return out;
				}
			}
		}
		return out;
	}
	public static int[] plat_ent_intersect(int x, int y) {
		int[] out = {-1, -1};
		for (int i = 0; i<plat_ents.length; i++) {
			for (int j = 0; j<plat_ents[i].nodes.length; j++) {
				if (d(x, y, plat_ents[i].nodes[j].x + pane.getWidth()/2 - pos.x, -plat_ents[i].nodes[j].y + pane.getHeight()/2 - pos.y) < Vector2.radius_edit) {
					out[0] = i;
					out[1] = j;
					return out;
				}
			}
		}
		return out;
	}
	public static int[] back_intersect(int x, int y) {
		int[] out = {-1, -1};
		for (int i = 0; i<background.length; i++) {
			for (int j = 0; j<background[i].nodes.length; j++) {
				if (d(x, y, background[i].nodes[j].x + pane.getWidth()/2 - pos.x, -background[i].nodes[j].y + pane.getHeight()/2 - pos.y) < Vector2.radius_edit) {
					out[0] = i;
					out[1] = j;
					return out;
				}
			}
		}
		return out;
	}
	
	//TODO selection actions
	public static void create_action() {
		level_editor();
	}
	
	public static void load_action(int in) {
		DataManager.load_into_game(in);
		//System.out.println("\np: " + platforms.length + "\npe: " + plat_ents.length);
		level_editor();
	}
	
	//TODO SAVE
	public static void question_saving() {
		int a = JOptionPane.showConfirmDialog(frame, "You have not saved the game:\nwould you like to save?", "save pls", JOptionPane.YES_NO_OPTION);
		if (a == JOptionPane.YES_OPTION) DataManager.save_game_editor(platforms, plat_ents, background);
		if (a == JOptionPane.NO_OPTION) System.exit(0);
	}
	
	//TODO logic
	public static double d(int a, int b, double c, double d) {
		return Math.sqrt((a - c) * (a - c) + (b - d) * (b - d));
	}
	
	//TODO start all arrays
	public static void start_arrays() {
		System.out.println("285");
		start_platforms();
		start_plat_ents();
		start_background();
		//start_select();
	}
	public static void start_platforms() {
		System.out.println("292");
		for (int x = 0; x<platforms.length; x++) platforms[x] = new Platform();
	}
	public static void start_plat_ents() {
		System.out.println("296");
		for (int x = 0; x<plat_ents.length; x++) plat_ents[x] = new PlatEntity();
	}
	public static void start_background() {
		System.out.println("300");
		for (int x = 0; x<background.length; x++) background[x] = new Background();
	}
	public static void start_select() {
		String[] names = {"platform", "plat_ent", "back", "delete", null, null, null, null, null, null, null, null, null, null, "properties"};
		
		for (int x = 0; x<selection_panel.length; x++) {
			if (names[x] == null) continue;
			selection_panel[x] = new JButton(names[x]);
			selection_panel[x].setBounds(25 + 75 * x, 25, 50, 50);
			selection_panel[x].setVisible(true);
			selection_panel[x].setFocusable(false);
			select.add(selection_panel[x]);
		}
		
		Creator.create_platform(selection_panel[0]);
		
		Creator.create_plat_ent(selection_panel[1]);
		
		Creator.create_background(selection_panel[2]);
		
		Creator.delete_platform(selection_panel[3]);
		
		Creator.show_properties(selection_panel[14]);
	}
}
