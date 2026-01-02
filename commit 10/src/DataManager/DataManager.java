package DataManager;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import GameObject.EditObject;
import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Rectangle;

public class DataManager {
	static int room_num;
	public static String DATA_FILE = System.getProperty("user.home") + File.separator + "DATA_FILES" + File.separator + "save_game.txt";
	public static String BACKUP = System.getProperty("user.home") + File.separator + "DATA_FILES" + File.separator + "backup.txt";

	//ALL TOGETHER
	
	//leveleditor
	public static void save(Room room, int room_num, int grid_size, boolean delete_room, String file_to) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(DATA_FILE));
			
			String start = skip_and_copy_to_room(read, room_num);
			skip_a_room(read);
			String rest = copy_rest(read);
			
			BufferedWriter write = new BufferedWriter(new FileWriter(file_to));
			
			write.write(start);
			
			System.out.println("START: { \n" + start + " }");
			
			if (!delete_room) {
				room.print_room(write, (double)1 / LevelEditor.grid_size);
			}
		//	System.out.println("ROOM: { \n " + room.print_room + " }");
			
			write.write(rest);
			System.out.println("END: { \n" + rest + " }");
			
			write.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void add_room(Rectangle in, int room_to_tile, int grid_size) {
		try {
			in.scale((double)room_to_tile / grid_size);
			
			BufferedReader read = new BufferedReader(new FileReader(DATA_FILE));
			
			String file = copy_rest(read);
			
			BufferedWriter write = new BufferedWriter(new FileWriter(DATA_FILE));

			write.write(file);
			
			write.write(in.toString() + "\n0 ");
			
			write.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//both
	public static Room load_room(int room, int grid_size, boolean in_editor) {
		try {
			
			System.out.println(room + " ROOM");
			//System.exit(0);
			
			BufferedReader read = new BufferedReader(new FileReader(DATA_FILE));
			
			skip_to_room(read, room);
			
			return read_room(read, grid_size, true, in_editor);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Room read_room(BufferedReader read, int grid_size, boolean load_contents, boolean in_editor) {
		Room out = new Room();
		try {
			String line = read.readLine();
			System.out.println(line);
			String[] bound = separate(line);
			out = new Room(Double.parseDouble(bound[0]) * grid_size, Double.parseDouble(bound[1]) * grid_size, Double.parseDouble(bound[2]) * grid_size, Double.parseDouble(bound[3]) * grid_size);
			out.fill = new Color(0, 255, 255, 128);
			
			//if (!load_contents) return out;
			
			
			int num = Integer.parseInt(cut(read.readLine()));
			
			out.objects = new GameObject[num];
			
			for (int x = 0; x<num; x++) {
				String[] sep = separate(read.readLine());
				
				for (int y = 0; y<sep.length; y++) {
					System.out.print(sep[y] + " ");
				}
				System.out.println();
				
				if (!load_contents) continue;
				
				if (in_editor) out.objects[x] = EditObject.create_game_object(sep, x);
				if (!in_editor) out.objects[x] = GameObject.create_game_object(sep, x);
				
				
				System.out.println("   OH: "+out.objects[x].object_handle);
				out.objects[x].scale(grid_size);
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		if (load_contents && grid_size == 8) out.start();
		return out;
	}
	public static Rectangle[] load_rects(int grid_size, int room_to_tile) {
		//if loading into game, make room_to_tile = 1
		try {
			BufferedReader read = new BufferedReader(new FileReader(DATA_FILE));
			int room_num = count_rooms(read);
			read = new BufferedReader(new FileReader(DATA_FILE));
			
			Rectangle[] rects = new Rectangle[room_num];
			
			for (int x = 0; x<rects.length; x++) {
				rects[x] = read_room(read, grid_size, false, false);
				rects[x].scale((double)1/room_to_tile);
				
				System.out.println(rects[x]);
			}
			
			return rects;
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//extra logic
	public static String skip_and_copy_to_room(BufferedReader in, int q) {
		String out = "";
		if (q == 0) return out;
		for (int x = 0; x<q; x++) {
			try {
				out += in.readLine() + "\n";
				
				int temp = Integer.parseInt(cut(in.readLine()));
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
			in.readLine();
			
			int temp = Integer.parseInt(cut(in.readLine()));
			for (int y = 0; y<temp; y++) in.readLine();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void skip_to_room(BufferedReader in, int q) {
		for (int x = 0; x<q; x++) {
			try {
				in.readLine();
				
				int temp = Integer.parseInt(cut(in.readLine()));
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
				
				String temp_s = in.readLine();
				if (temp_s == null) {
					return out;
				}
				
				int temp = Integer.parseInt(cut(in.readLine()));
				for (int y = 0; y<temp; y++) in.readLine();
				
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
