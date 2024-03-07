package DataManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import LevelEdit.LevelCreator;
import Symbio.Game;
import Symbio.Platform;
import Symbio.Room;
import Symbio.Entity.PlatEntity;
import Symbio.Rendering.Background;
import Symbio.Rendering.Camera;

public class DataManager {
	static int room_num;
	public static boolean new_room;
	public static String DATA_FILE = System.getProperty("user.home") + File.separator + "DATA_FILES" + File.separator + "save_game.txt";
	//LevelEditor stuff
	public static void save_game_editor(Platform[] platforms, PlatEntity[] plat_ents, Background[] background) {
		if (new_room) save_game_new(platforms, plat_ents, background);
		if (!new_room) save_game_edited(room_num, platforms, plat_ents, background);
	}
	public static void save_game_new(Platform[] platforms, PlatEntity[] plat_ents, Background[] background) {
		try (BufferedReader read = new BufferedReader(new FileReader(DATA_FILE))) {
			String temp = copy_rest(read);
			BufferedWriter write = new BufferedWriter(new FileWriter(DATA_FILE));
			write.write(temp);
			write.write(platforms.length + " \n");
			for (int x = 0; x<platforms.length; x++) {
				platforms[x].scale((double)1 / LevelCreator.grid_size);
				write.write(platforms[x].toString());
				write.write("\n");
			}
			write.write(plat_ents.length + " \n");
			for (int x = 0; x<plat_ents.length; x++) {
				plat_ents[x].scale((double)1 / LevelCreator.grid_size);
				write.write(plat_ents[x].toString());
				write.write("\n");
			}
			write.write(background.length + " \n");
			for (int x = 0; x< background.length; x++) {
				background[x].scale((double)1 / LevelCreator.grid_size);
				write.write(background[x].toString());
				write.write("\n");
			}
			write.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void save_game_edited(int q, Platform[] platforms, PlatEntity[] plat_ents, Background[] background) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(DATA_FILE));
			String temp1 = "", temp2 = "";
			
			temp1 = skip_and_copy_to_room(read, q);
			skip_a_room(read);
			temp2 = copy_rest(read);
			
			BufferedWriter write = new BufferedWriter(new FileWriter(DATA_FILE));
			write.write(temp1);
			write.write(platforms.length + " \n");
			for (int x = 0; x<platforms.length; x++) {
				platforms[x].scale((double)1 / LevelCreator.grid_size);
				write.write(platforms[x].toString());
				write.write("\n");
			}
			write.write(plat_ents.length + " \n");
			for (int x = 0; x<plat_ents.length; x++) {
				plat_ents[x].scale((double)1 / LevelCreator.grid_size);
				write.write(plat_ents[x].toString());
				write.write("\n");
			}
			write.write(background.length + " \n");
			for (int x = 0; x< background.length; x++) {
				background[x].scale((double)1 / LevelCreator.grid_size);
				write.write(background[x].toString());
				write.write("\n");
			}
			write.write(temp2);
			write.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//LevelCreator
	public static void load_into_game(int in) {
		room_num = in;
		try (BufferedReader read = new BufferedReader(new FileReader(DATA_FILE))) {
			skip_and_copy_to_room(read, in);
			
			String blank = read.readLine();
			int sector_num = Integer.parseInt(blank.substring(0, blank.indexOf(" ")));
			
			LevelCreator.platforms = new Platform[sector_num];
			
			for (int x = 0; x<sector_num; x++) {
				
				String line = read.readLine();
				
				String[] sep = separate(line);
				
				LevelCreator.platforms[x] = new Platform(Double.parseDouble(sep[0]), Double.parseDouble(sep[1]), Double.parseDouble(sep[2]), Double.parseDouble(sep[3]), Double.parseDouble(sep[4]), sep[5]);
				LevelCreator.platforms[x].scale(LevelCreator.grid_size);
			}
			
			blank = read.readLine();
			int plat_ent_num = Integer.parseInt(blank.substring(0, blank.indexOf(" ")));
			
			LevelCreator.plat_ents = new PlatEntity[plat_ent_num];
			
			for (int x = 0; x<plat_ent_num; x++) {
				String line = read.readLine();
				String[] sep = separate(line);
				
				LevelCreator.plat_ents[x] = PlatEntity.create_plat_entity(sep, x, LevelCreator.platforms);
				LevelCreator.plat_ents[x].scale(LevelCreator.grid_size);
			}
			
			blank = read.readLine();
			int back_num = Integer.parseInt(blank.substring(0, blank.indexOf(" ")));
			
			LevelCreator.background = new Background[back_num];
			
			for (int x = 0; x<back_num; x++) {
				String line = read.readLine();
				String[] sep = separate(line);
				
				LevelCreator.background[x] = new Background(Double.parseDouble(sep[0]), Double.parseDouble(sep[1]), Double.parseDouble(sep[2]), Double.parseDouble(sep[3]), sep[4]);
				LevelCreator.background[x].scale(LevelCreator.grid_size);
			}
			
			read.close();			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//Game room stuff
	public static void load_game(String file) {
		long a = System.nanoTime();
		try {
			BufferedReader read = new BufferedReader(new FileReader(file));
			int room_num = count_rooms(read);
			
			read = new BufferedReader(new FileReader(file));
			Game.rooms = new Room[room_num];
			for (int x = 0; x<room_num; x++) {
				Room add = read_room(read);
				add.start();
				Game.rooms[x] = add;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		long b = System.nanoTime();
		System.out.println("load game: " + (double)(b - a) / 1_000_000);
	}
	//LOGIC METHODS
	public static Room read_room(BufferedReader in) {
		long a = System.nanoTime();
		Room out = new Room();
		try {
			String blank = in.readLine();
			if (blank == null) return null;
			int plat_num = Integer.parseInt(cut(blank));
			out.platforms = new Platform[plat_num];
			for (int x = 0; x<plat_num; x++) {
				String line = in.readLine();
				String[] sep = separate(line);
				out.platforms[x] = new Platform(Double.parseDouble(sep[0]), Double.parseDouble(sep[1]), Double.parseDouble(sep[2]), Double.parseDouble(sep[3]), Double.parseDouble(sep[4]), sep[5]);
				out.platforms[x].fill = Platform.color_types[Integer.parseInt(sep[0])];
				out.platforms[x].scale(Camera.tile_size);
			}
			
			blank = in.readLine();
			int plat_ent_num = Integer.parseInt(cut(blank));
			out.plat_ents = new PlatEntity[plat_ent_num];
			for (int x = 0; x<plat_ent_num; x++) {
				String line = in.readLine();
				String[] sep = separate(line);
				out.plat_ents[x] = PlatEntity.create_plat_entity(sep, x, out.platforms);
				out.plat_ents[x].scale(Camera.tile_size);
			}
			
			blank = in.readLine();
			int back_num = Integer.parseInt(cut(blank));
			out.background = new Background[back_num];
			for (int x = 0; x<back_num; x++) {
				String line = in.readLine();
				String[] sep = separate(line);
				out.background[x] = new Background(Double.parseDouble(sep[0]), Double.parseDouble(sep[1]), Double.parseDouble(sep[2]), Double.parseDouble(sep[3]), sep[4]);
				out.background[x].scale(Camera.tile_size);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		long b = System.nanoTime();
		System.out.println("read room: " + (double)(b - a) / 1_000_000);
		return out;
	}
	//extra logic
	public static String skip_and_copy_to_room(BufferedReader in, int q) {
		String out = "";
		for (int x = 0; x<q; x++) {
			try {
				int temp = Integer.parseInt(cut(in.readLine()));
				out += temp + " \n";
				for (int y = 0; y<temp; y++) {
					out += in.readLine() + "\n";
				}
				
				temp = Integer.parseInt(cut(in.readLine()));
				out += temp + " \n";
				for (int y = 0; y<temp; y++) {
					out += in.readLine() + "\n";
				}
				
				temp = Integer.parseInt(cut(in.readLine()));
				out += temp + " \n";
				for (int y = 0; y<temp; y++) {
					out += in.readLine() + "\n";
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return out;
	}
	
	public static void skip_a_room(BufferedReader in) {
		try {
			
			int temp = Integer.parseInt(cut(in.readLine()));
			for (int y = 0; y<temp; y++) in.readLine();
			
			temp = Integer.parseInt(cut(in.readLine()));
			for (int y = 0; y<temp; y++) in.readLine();
			
			temp = Integer.parseInt(cut(in.readLine()));
			for (int y = 0; y<temp; y++) in.readLine();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void skip_to_room(BufferedReader in, int q) {
		for (int x = 0; x<q; x++) {
			try {
				int temp = Integer.parseInt(cut(in.readLine()));
				for (int y = 0; y<temp; y++) in.readLine();

				temp = Integer.parseInt(cut(in.readLine()));
				for (int y = 0; y<temp; y++) in.readLine();
				
				temp = Integer.parseInt(cut(in.readLine()));
				for (int y = 0; y<temp; y++) in.readLine();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String copy_rest(BufferedReader in) {
		String out = "", test = "";
		try {
			do {
				test = in.readLine();
				if (test != null) out += test + "\n";
			}while(test != null);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public static int count_rooms(BufferedReader in) {
		long a = System.nanoTime();
		int out = 0;
		try {
			while(true) {
				for (int x = 0; x<3; x++) {
					
					String temp_s = in.readLine();
					if (temp_s == null) {
						long b = System.nanoTime();
						System.out.println("count rooms: " + (double)(b - a) / 1_000_000);
						return out;
					}
					int temp = Integer.parseInt(cut(temp_s));
					
					for (int y = 0; y<temp; y++) in.readLine();
				}
				out++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.nanoTime();
		System.out.println("count rooms: " + (double)(b - a) / 1_000_000);
		return out;
	}
	
	public static void add(Room a, Room[] b) {
		Room[] temp = new Room[b.length + 1];
		for (int x = 0; x<b.length; x++) temp[x] = b[x];
		temp[b.length] = a;
		b = temp;
	}
	
	public static int[] parse_strings(String[] in) {
		int[] out = new int[in.length];
		for (int x = 0; x<in.length; x++) out[x] = Integer.parseInt(in[x]);
		return out;
	}
	
	public static String[] separate(String line) {
		int count = 0;
		for (int x = 0; x<line.length(); x++) if (line.charAt(x) == ' ') count++;
		String[] out = new String[count];
		for (int x = 0; x<count; x++) {
			out[x] = cut(line);
			line = clear(line);
		}
		return out;
	}
	
	public static String cut(String line) {
		return line.substring(0, line.indexOf(" "));
	}
	
	public static String clear(String in) {
		return in.substring(in.indexOf(" ") + 1);
	}
	
	public void skip_to_line(int line, BufferedReader in) {
		try {
			for (int x = 0; x<line-1; x++) {
				in.readLine();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
