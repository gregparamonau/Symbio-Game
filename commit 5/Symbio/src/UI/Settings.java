package UI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Settings {
	//
	public static String settings_save = System.getProperty("user.home") + File.separator + "DATA_FILES" + File.separator + "settings.txt";
	//game
	public static int up_key = 38;
	public static int down_key = 40;
	public static int left_key = 37;
	public static int right_key = 39;
	
	public static int jump_key = 67;
	public static int dash_key = 88;
	public static int attack_key = 90;
	public static int grab_key = 16;
	
	//debug
	public static int pause_key = 27; //esc
	public static int slow_key = 77; // m
	public static int debug_key = 86; // v
	public static int exit_key = 80; //p
	public static int respawn_key = 82; //r
	
	
	public static void load_settings() {
		try {
			
			BufferedReader read = new BufferedReader(new FileReader(settings_save));
			
			up_key = Integer.parseInt(cut(clear(read.readLine())));
			down_key = Integer.parseInt(cut(clear(read.readLine())));
			left_key = Integer.parseInt(cut(clear(read.readLine())));
			right_key = Integer.parseInt(cut(clear(read.readLine())));
			
			jump_key = Integer.parseInt(cut(clear(read.readLine())));
			dash_key = Integer.parseInt(cut(clear(read.readLine())));
			attack_key = Integer.parseInt(cut(clear(read.readLine())));
			grab_key = Integer.parseInt(cut(clear(read.readLine())));
			
			pause_key = Integer.parseInt(cut(clear(read.readLine())));
			slow_key = Integer.parseInt(cut(clear(read.readLine())));
			debug_key = Integer.parseInt(cut(clear(read.readLine())));
			exit_key = Integer.parseInt(cut(clear(read.readLine())));
			respawn_key = Integer.parseInt(cut(clear(read.readLine())));
			
			read.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void save_settings() {
		try {
			
			BufferedWriter write = new BufferedWriter(new FileWriter(settings_save));
			
			write.write("up_key " + up_key + " \n");
			write.write("down_key " + down_key + " \n");
			write.write("left_key " + left_key + " \n");
			write.write("right_key " + right_key + " \n");
			
			write.write("jump_key " + jump_key + " \n");
			write.write("dash_key " + dash_key + " \n");
			write.write("attack_key " + attack_key + " \n");
			write.write("grab_key " + grab_key + " \n");
			
			write.write("pause_key " + pause_key + " \n");
			write.write("slow_key " + slow_key + " \n");
			write.write("debug_key " + debug_key + " \n");
			write.write("exit_key " + exit_key + " \n");
			write.write("respawn_key " + respawn_key + " \n");
			
			write.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//logic
	public static String cut(String line) {
		return line.substring(0, line.indexOf(" "));
	}
	
	public static String clear(String in) {
		return in.substring(in.indexOf(" ") + 1);
	}
	
}
