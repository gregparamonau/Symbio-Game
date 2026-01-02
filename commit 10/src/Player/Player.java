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
	public PlayerInput input;
	public PlayerRender render;
	
	public Vector2 last_pos = new Vector2(0, 0);
	public Vector2 last_move = new Vector2(0, 0);
	
	//unlocked abilities
	public boolean dash_unlock = true, wall_jump_unlock = true, slash_unlock = true, grab_unlock = true;
	
	//physics
	public Vector2 vel = new Vector2(0, 0);
	public Vector2 momentum = new Vector2(0, 0);
	
	
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
		this.input = new PlayerInput(this);
		this.render = new PlayerRender(this);
	}
	//HOOK of GAME: Rooms change shape/size/stretch/etc
	//Muscles: stretch/contract how muscles do
	//lungs: expand away from center of room
	//heart: pulse?
	//intestine: poo moves through the intestines (water too?)
	//stomach: compresses & acid levels rise
	//MOVEMENT 
	public void update() {
		
		this.last_move.set(Vector2.sub(this.pos, this.last_pos));
		
		
		if (this.combat.slash_count > 0) {
			this.slashing = true;
			this.combat.slash();
		}
		else this.slashing = false;
		
		this.object_intersect_id = -1;
		
		this.input.update();
		
		this.movement.update();
		
		this.combat.update();
		
		this.collider.update();
		
		this.last_pos = new Vector2(this.pos);
	}
	
	
	//LOGIC METHODS (BASICS)
	
	//These might fit better in '
	public void set_position(double x, double y) {
		this.pos.x = x;
		this.pos.y = y;
	}
}
