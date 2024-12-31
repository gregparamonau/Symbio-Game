package Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Animation;

public class PlayerRender {
	public static Animation[] idle = new Animation[2];
	public static Animation[] run = new Animation[2];
	public static Animation current_animation;
	public static Animation current_swipe_animation;
	public static boolean interrupt = false;
	public static String current_state = "idle", last_state = "idle";
	
	public static final String idle_name = "/player_textures/player_idle.png";
	public static final String run_name = "/player_textures/player_run.png";
	public static final String swipe_horizontal_name = "/player_textures/swipe_horizontal.png";
	public static final String swipe_vertical_name = "/player_textures/swipe_vertical.png";
	
	public static void draw_player(Player in, Graphics g) {
		in.draw_with_sprite(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, sprite_to_play(in), "game");
		
		if (in.slashing) {
			new Rectangle(in.pos.x + in.slash_dir.x, in.pos.y + in.slash_dir.y, (in.slash_dir.x != 0 ? 1 : 0) * in.slash_bounds.x + (in.slash_dir.x == 0 ? 1: 0) * in.slash_bounds.y, (in.slash_dir.y != 0 ? 1.5 : 0) * in.slash_bounds.x + (in.slash_dir.y == 0 ? 1: 0) * in.slash_bounds.y).draw(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		}
		
		if (in.dash_num == 1) new Vector2(in.pos.x, in.pos.y + 10).draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.green);
		if (in.dash_num == 0) new Vector2(in.pos.x, in.pos.y + 10).draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.red);
	}
	public static BufferedImage sprite_to_play(Player in) {
		return new BufferedImage(Player.width_save, Player.height_save, BufferedImage.TYPE_INT_RGB);
	}
	public static String current_state(Player in) {
		if (in.dashing) return "dash";
		if (in.col_down && in.vel.x == 0) return "idle";
		if (in.col_down && in.vel.x != 0) return "run";
		if (in.wall_slide) return "wall";
		return "air";
	}
	public static void start_animations() {
		idle[0] = new Animation(idle_name, 20, 20, 0, false, true);
		idle[1] = new Animation(idle_name, 20, 20, 0, false, false);
		
		run[0] = new Animation(run_name, 20, 20, 0, false, true);
		run[1] = new Animation(run_name, 20, 20, 0, false, false);
		
		current_animation = idle[0];
	}
}
