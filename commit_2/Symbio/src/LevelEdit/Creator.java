package LevelEdit;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Symbio.Platform;
import Symbio.Entity.PlatEntity;
import Symbio.Entity.PlatEntities.Bouncer;
import Symbio.Entity.PlatEntities.Circle;
import Symbio.Entity.PlatEntities.Mover;
import Symbio.Entity.PlatEntities.RoomT;
import Symbio.Entity.PlatEntities.Slope;
import Symbio.Logic.Utility;
import Symbio.Rendering.Background;

public class Creator {
	static JFrame frame = new JFrame();
	static JTextField[] fields = new JTextField[10];
	static JLabel[] labels = new JLabel[10];
	
	public static void create_platform(JButton in) {
		AbstractAction create = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Platform[] Platform_temp = new Platform[LevelCreator.platforms.length + 1];
				
				for (int x = 0; x<LevelCreator.platforms.length; x++) {
					Platform_temp[x] = LevelCreator.platforms[x];
				}
				
				Platform_temp[LevelCreator.platforms.length] = new Platform(LevelCreator.current_plat_type, (int)LevelCreator.pos.x, -(int)LevelCreator.pos.y, 100, 100, Platform.plat_names[LevelCreator.current_plat_type]);
				LevelCreator.platforms = Platform_temp;
				
				LevelCreator.platforms[LevelCreator.platforms.length - 1].clip_nodes();
			}
			
		};
		in.addActionListener(create);
	}
	public static void create_plat_ent(JButton in) {
		AbstractAction create = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				PlatEntity[] temp = new PlatEntity[LevelCreator.plat_ents.length + 1];
				
				for (int x = 0; x<LevelCreator.plat_ents.length; x++) {
					temp[x] = LevelCreator.plat_ents[x];
				}
				String[] temp_string = plat_ent_string_default();
				temp[LevelCreator.plat_ents.length] = PlatEntity.create_plat_entity(temp_string, 0, LevelCreator.platforms);
				LevelCreator.plat_ents = temp;				
			}
			
		};
		in.addActionListener(create);
	}
	public static void create_background(JButton in) {
		AbstractAction create = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Background[] temp = new Background[LevelCreator.background.length + 1];
				
				for (int x = 0; x<LevelCreator.background.length; x++) {
					temp[x] = LevelCreator.background[x];
				}
				temp[LevelCreator.background.length] = new Background((int)LevelCreator.pos.x, (int)-LevelCreator.pos.y, 100, 100, LevelCreator.back_sprite);
				LevelCreator.background = temp;
				
			}
			
		};
		in.addActionListener(create);
	}
	public static void delete_platform(JButton in) {
		AbstractAction delete = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				//platforms
				if (LevelCreator.plat_select[0] != -1) {
					if (LevelCreator.platforms.length <= 1) {
						LevelCreator.platforms = new Platform[0];
						return;
					}
					LevelCreator.platforms[LevelCreator.plat_select[0]] = null;
					
					for (int x = LevelCreator.plat_select[0]; x <LevelCreator.platforms.length - 1; x++) {
						LevelCreator.platforms[x] = LevelCreator.platforms[x + 1];
					}
					
					Platform[] temp = new Platform[LevelCreator.platforms.length - 1];
					for (int x = 0; x<temp.length; x++) temp[x] = LevelCreator.platforms[x];
					LevelCreator.platforms = temp;
					
					LevelCreator.plat_select[0] = -1;
					LevelCreator.plat_select[1] = -1;
				}
				//plat_ents
				if (LevelCreator.plat_ent_select[0] != -1) {
					if (LevelCreator.plat_ents.length <= 1) {
						LevelCreator.plat_ents = new RoomT[0];
						return;
					}
					LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]] = null;
					
					for (int x = LevelCreator.plat_ent_select[0]; x <LevelCreator.plat_ents.length - 1; x++) {
						LevelCreator.plat_ents[x] = LevelCreator.plat_ents[x + 1];
					}
					
					PlatEntity[] temp = new PlatEntity[LevelCreator.plat_ents.length - 1];
					for (int x = 0; x<temp.length; x++) temp[x] = LevelCreator.plat_ents[x];
					LevelCreator.plat_ents = temp;
					
					LevelCreator.plat_ent_select[0] = -1;
				}
				
				if (LevelCreator.back_select[0] != -1) {
					if (LevelCreator.background.length <= 1) {
						LevelCreator.background = new Background[0];
						return;
					}
					LevelCreator.background[LevelCreator.back_select[0]] = null;
					
					for (int x = LevelCreator.back_select[0]; x <LevelCreator.background.length - 1; x++) {
						LevelCreator.background[x] = LevelCreator.background[x + 1];
					}
					
					Background[] temp = new Background[LevelCreator.background.length - 1];
					for (int x = 0; x<temp.length; x++) temp[x] = LevelCreator.background[x];
					LevelCreator.background = temp;
					
					LevelCreator.back_select[0] = -1;
				}
			}
		};
		in.addActionListener(delete);
	}
	public static void show_properties(JButton in) {
		AbstractAction props = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				determine_type();
			}
		};
		in.addActionListener(props);
	}
	
	//determine which to chose
	public static void determine_type() {
		if (LevelCreator.plat_select[0] != -1) plat_props();
		if (LevelCreator.plat_ent_select[0] != -1) plat_ent_props();
		if (LevelCreator.back_select[0] != -1) back_props();
	}

	//platforms
	public static void plat_props() {
		frame = new JFrame();
		frame.setSize(500, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JTextField[] texts = new JTextField[2];
		texts[0] = new JTextField(LevelCreator.current_plat_type + "");
		texts[0].setBounds(50, 50, 100, 25);
		texts[0].setVisible(true);
		frame.add(texts[0]);
		
		texts[1] = new JTextField(LevelCreator.platforms[LevelCreator.plat_select[0]].sprite_name + "");
		texts[1].setBounds(50, 100, 200, 50);
		texts[1].setVisible(true);
		frame.add(texts[1]);
		
		JLabel sprite_path = new JLabel("sprite_path");
		sprite_path.setBounds(300, 100, 100, 50);
		sprite_path.setVisible(true);
		frame.add(sprite_path);
		
		JButton save = new JButton("save");
		save.setBounds(200, 225, 100, 25);
		save.setVisible(true);
		save_plats(save, frame, texts);
		frame.add(save);
		
		JButton dummy = new JButton();
		frame.add(dummy);
		frame.remove(dummy);
		
		frame.repaint();
	}
	
	public static void save_plats(JButton in, JFrame frame, JTextField[] texts) {
		AbstractAction save = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LevelCreator.current_plat_type = Integer.parseInt(texts[0].getText());
				LevelCreator.platforms[LevelCreator.plat_select[0]].type = LevelCreator.current_plat_type;
				LevelCreator.platforms[LevelCreator.plat_select[0]].sprite_name = texts[1].getText();
				frame.dispose();
			}
			
		};
		in.addActionListener(save);
	}


	//plat_ents
	public static void plat_ent_props() {
		frame = new JFrame();
		frame.setSize(500, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		fields = new JTextField[10];
		labels = new JLabel[10];
		
		if (LevelCreator.plat_ent_type.equals("roomt")) {
			RoomT.create_props(frame, fields, labels);	
		}
		else if (LevelCreator.plat_ent_type.equals("bouncer")) {
			Bouncer.create_props(frame, fields, labels);
		}
		else if (LevelCreator.plat_ent_type.equals("mover")) {
			Mover.create_props(frame, fields, labels);
		}
		else if (LevelCreator.plat_ent_type.equals("slope")) {
			Slope.create_props(frame, fields, labels);
		}
		else if (LevelCreator.plat_ent_type.equals("circle")) {
			Circle.create_props(frame, fields, labels);
		}
		else PlatEntity.create_props(frame, fields, labels);
		
		
		JButton save = new JButton("save");
		save.setBounds(100, 225, 100, 25);
		save.setVisible(true);
		save_plat_ent(save);
		frame.add(save);
		
		JButton reload = new JButton("reload");
		reload.setBounds(300, 225, 100, 25);
		reload.setVisible(true);
		reload(reload);
		frame.add(reload);
		
		JButton dummy = new JButton();
		frame.add(dummy);
		frame.remove(dummy);
		
		frame.repaint();
	}
	
	public static void save_plat_ent(JButton in) {
		AbstractAction save = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LevelCreator.plat_ent_type = fields[0].getText();
				
				//if (!fields[4].getText().equals("")) LevelCreator.plat_ent_variant = Integer.parseInt(fields[4].getText());
				String[] temp_string = {"plat_ent", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 100 + "", 100 + ""};
				switch(LevelCreator.plat_ent_type) {
				case "roomt":
					String[] temp_roomt = {"roomt", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 100 + "", 100 + "", fields[1].getText(), fields[2].getText(), fields[3].getText()};
					temp_string = temp_roomt;
					break;
				case "hair":
					String[] temp_hair = {"hair", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 25 + "", 100 + ""};
					temp_string = temp_hair;
					break;
				case "bouncer":
					String[] temp_bouncer = {"bouncer", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", fields[1].getText()};
					temp_string = temp_bouncer;
					break;
				case "mover":
					String[] nodes_temp = new String[Integer.parseInt(fields[1].getText()) * 2 + 1];
					nodes_temp[0] = fields[1].getText();
					nodes_temp[1] = ((int)LevelCreator.pos.x - 100) + "";
					nodes_temp[2] = (int)-LevelCreator.pos.y + "";
					for (int x = 3; x<nodes_temp.length; x++) nodes_temp[x] = -1 + "";
					
					String[] temp_mover = {"mover", 100 + "", 100 + ""};
					temp_string = Utility.add_to_array(temp_mover, nodes_temp);
					
					for (String temp: temp_string) System.out.print(temp + " "); 
					//System.exit(0);
					break;
				case "slope":
					String[] temp_slope = {"slope", (int)(LevelCreator.pos.x - 50) + "", (int)(-LevelCreator.pos.y - 50) + "", (int)(LevelCreator.pos.x + 50) + "", (int)(-LevelCreator.pos.y + 50) + "", fields[1].getText() + ""};
					temp_string = temp_slope;
					break;
				case "circle":
					String[] temp_circle = {"circle", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", Double.parseDouble(fields[1].getText()) * LevelCreator.grid_size + ""};
					temp_string = temp_circle;
					break;
				}
				//String[] temp_string = {LevelCreator.plat_ent_type, (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 100 + "", 100 + "", 0 + "", 0 + "", 0 + ""};
				
				LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]] = PlatEntity.create_plat_entity(temp_string, 0, LevelCreator.platforms);
				
				frame.dispose();
			}
			
		};
		in.addActionListener(save);
	}
	
	public static void reload(JButton in) {
		AbstractAction reload = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LevelCreator.plat_ent_type = fields[0].getText();
				System.out.println(LevelCreator.plat_ent_type);
				if (LevelCreator.plat_ent_type.equals("mover")) {
					LevelCreator.plat_ents[LevelCreator.plat_ent_select[0]].start_stops();
				}
				frame.dispose();
				plat_ent_props();
			}
		};
		in.addActionListener(reload);
	}

	public static String[] plat_ent_string_default() {
		if (LevelCreator.plat_ent_type.equals("roomt")) {
			String[] out = {"roomt", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 100 + "", 100 + "", 0 + "", 0 + "", 0 + ""};
			return out;
		}
		if (LevelCreator.plat_ent_type.equals("hair")) {
			String[] out = {"hair", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 25 + "", 100 + ""};
			return out;
		}
		if (LevelCreator.plat_ent_type.equals("bouncer")) {
			String[] out = {"bouncer", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 0 + ""};
			return out;
		}
		if (LevelCreator.plat_ent_type.equals("mover")) {
			String[] out = {"mover", 100 + "", 100 + "", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", (int)(LevelCreator.pos.x + 100) + "", (int)-LevelCreator.pos.y + ""};
			return out;
		}
		if (LevelCreator.plat_ent_type.equals("slope")) {
			String[] out = {"slope", (int)(LevelCreator.pos.x - 50) + "", (int)(-LevelCreator.pos.y - 50) + "", (int)(LevelCreator.pos.x + 50) + "", (int)(-LevelCreator.pos.y + 50) + "", 0 + ""};
			return out;
		}
		if (LevelCreator.plat_ent_type.equals("circle")) {
			String[] out = {"circle", (int)(LevelCreator.pos.x - 50) + "", (int)(-LevelCreator.pos.y - 50) + "", 50 + ""};
			return out;
		}
		String[] out = {"plat_ent", (int)LevelCreator.pos.x + "", (int)-LevelCreator.pos.y + "", 100 + "", 100 + ""};
		return out;
	}
	

	//background
	public static void back_props() {
		JFrame temp = new JFrame();
		temp.setSize(500, 300);
		temp.setLocationRelativeTo(null);
		temp.setVisible(true);
		temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JTextField sprite_name = new JTextField(LevelCreator.back_sprite);
		sprite_name.setBounds(50, 50, 200, 50);
		sprite_name.setVisible(true);
		temp.add(sprite_name);
		
		JLabel sprite_path = new JLabel("sprite_path");
		sprite_path.setBounds(300, 50, 100, 50);
		sprite_path.setVisible(true);
		temp.add(sprite_path);
		
		JButton save = new JButton("save");
		save.setBounds(200, 225, 100, 25);
		save.setVisible(true);
		save_back(save, temp, sprite_name);
		temp.add(save);
		
		JButton dummy = new JButton();
		temp.add(dummy);
		temp.remove(dummy);
		
		temp.repaint();
	}
	
	public static void save_back(JButton in, JFrame frame, JTextField sprite) {
		AbstractAction save = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LevelCreator.back_sprite = sprite.getText();
				LevelCreator.background[LevelCreator.back_select[0]].sprite_name = LevelCreator.back_sprite;
			}
			
		};
		in.addActionListener(save);
	}
}
