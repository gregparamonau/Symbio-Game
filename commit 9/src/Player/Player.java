package Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import DataManager.Room;
import GameObject.GameObject;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Animation;
import Rendering.Camera;

public class Player extends Rectangle{
	//TODO:
	//CLASS AS A SUPER CLASS TO ALL PLAYER SUB_CLASSES
	//HANDLES PLAYER INPUT
	//HANDLES OTHER GENERAL THINGS
	
	public PlayerMovement movement;
	public PlayerCollision collider;
	public PlayerCombat combat;
	public PlayerControls control;
	
	public Vector2 last_pos = new Vector2(0, 0);
	public int last_dir = 1;
	
	//unlocked abilities
	public boolean dash_unlock = true, wall_jump_unlock = true, slash_unlock = true, grab_unlock = true;
	
	//physics
	public boolean col_left, col_right, col_up, col_down, col;
	public Vector2 vel = new Vector2(0, 0);
	public Vector2 momentum = new Vector2(0, 0);
	public Vector2 face_dir;
	
	//jump
	public boolean jumped;
	
	//wall jump
	public boolean wall_jumped;
	public boolean wall_slide;
	
	//dash
	public boolean dashing, dashed;
	Vector2 dash_dir = Vector2.zero;
	
	//extra bs
	public int object_intersect_id = -1;
	
	//Attack code
	public boolean slashing = false, slash_held = false;
	
	
	//CONSTRUCTORS
	public Player() {
		
		this.pos = new Vector2(0, 0);
		this.width = PlayerCollision.width_save;
		this.height = PlayerCollision.height_save;
		
		this.collider = new PlayerCollision(this);
		this.movement = new PlayerMovement(this);
		this.combat = new PlayerCombat(this);
		this.control = new PlayerControls(this);
	}
	//HOOK of GAME: Rooms change shape/size/stretch/etc
	//Muscles: stretch/contract how muscles do
	//lungs: expand away from center of room
	//heart: pulse?
	//intestine: poo moves through the intestines (water too?)
	//stomach: compresses & acid levels rise
	//MOVEMENT 
	public void update() {
		
		
		if (this.combat.slash_count > 0) {
			this.slashing = true;
			this.combat.slash();
		}
		else this.slashing = false;
		
		this.face_dir = find_face_dir();
		this.object_intersect_id = -1;
		
		this.collider.update();
		
		this.movement.update();
		
		this.combat.update();
		
		this.last_pos = new Vector2(this.pos);
	}
	
	
	//LOGIC METHODS (BASICS)
	
	public Vector2 find_face_dir() {
		if (this.control.up) {
			if (this.control.left) return Vector2.up_left;
			else if (this.control.right) return Vector2.up_right;
			else return Vector2.up;
		}
		else if (this.control.down) {
			if (this.control.left) return Vector2.down_left;
			else if (this.control.right) return Vector2.down_right;
			else return Vector2.down;
		}
		else return new Vector2(this.last_dir, 0);
	}
	public void set_position(double x, double y) {
		this.pos.x = x;
		this.pos.y = y;
	}
	public void update_last_dir() {
		if (this.control.left) this.last_dir = -1;
		else if (this.control.right) this.last_dir = 1;
	}
}
