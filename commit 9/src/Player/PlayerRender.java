package Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;
import Rendering.Animation;

public class PlayerRender {
	public static Animation anim;
	public static boolean interrupt = false;
	public static String current_state = "idle", last_state = "idle";
	
	public static final String idle = "/player_textures/player_idle.png";
	public static final String run = "/player_textures/player_run.png";
	
	public static void draw_player(Graphics g, JPanel pane, double xin, double yin, String location) {
		last_state = current_state;
		current_state = current_state();
		
		if (!current_state.equals(last_state)) {
			interrupt = true;
		}
		
		if (interrupt && anim.interrupt()) {
			
			anim = new Animation(anim_name(), Game.player.pos, 10, 6, true, true);
			
			interrupt = false;
		}
		
		anim.play(false, Vector2.zero, (Game.player.last_dir == -1 && !Game.player.wall_slide) || (Game.player.wall_slide && Game.player.col_right), g, pane, xin, yin, location);
		
		
		if (Game.player.slashing) {
			new Rectangle(Game.player.pos.x + Game.player.combat.slash_dir.x, Game.player.pos.y + Game.player.combat.slash_dir.y, (Game.player.combat.slash_dir.x != 0 ? 1 : 0) * Game.player.combat.slash_bounds.x + (Game.player.combat.slash_dir.x == 0 ? 1: 0) * Game.player.combat.slash_bounds.y, (Game.player.combat.slash_dir.y != 0 ? 1.5 : 0) * Game.player.combat.slash_bounds.x + (Game.player.combat.slash_dir.y == 0 ? 1: 0) * Game.player.combat.slash_bounds.y).draw(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game");
		}
		
		if (Game.player.movement.dash_num == 1) new Vector2(Game.player.pos.x, Game.player.pos.y + 10).draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.green);
		if (Game.player.movement.dash_num == 0) new Vector2(Game.player.pos.x, Game.player.pos.y + 10).draw_node(g, Start.pane, Game.cam.pos.x, Game.cam.pos.y, "game", Color.red);
	}
	public static String anim_name() {
		if (current_state.equals("run")) {
			return run;
		}
		return idle;
	}
	
	public static BufferedImage sprite_to_play(Player in) {
		return new BufferedImage(PlayerCollision.width_save, PlayerCollision.height_save, BufferedImage.TYPE_INT_RGB);
	}
	public static String current_state() {
		//if (Game.player.dashing) return "dash";
		if (Game.player.col_down && Game.player.vel.x == 0) return "idle";
		if (Game.player.col_down && Game.player.vel.x != 0) return "run";
		//if (Game.player.wall_slide) return "wall";
		//return "air";
		return "idle";
	}
	public static void start_animations() {
		anim = new Animation(anim_name(), Game.player.pos, 10, 6, true, true);
	}
}
