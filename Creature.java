package Symbio.Entity;

import Symbio.Platform;
import Symbio.Logic.Line;
import Symbio.Logic.Rectangle;
import Symbio.Logic.Vector2;

public class Creature extends Rectangle{	
	//TODO: write this part of code & create test example
	void update() {}
	void ai() {}
	
	//MOVEMENT
	public void move(double xplus, double yplus, Platform[] platforms) {
		//to allign everything with edges, to make sure player able to move everywhere
		//for 'y' axis
		if (yplus != 0) {
			for (int y = 0; y<Math.abs(yplus); y++) {
				this.pos.y += yplus/Math.abs(yplus);
				if (this.platforms_intersect(new Vector2(0, 0), platforms)) {
					this.pos.y -= yplus/Math.abs(yplus);
					break;
				}
			}
		}
		//for 'x' axis
		if (xplus != 0) {
			for (int x = 0; x<Math.abs(xplus); x++) {
				this.pos.x += xplus/Math.abs(xplus);
				if (this.platforms_intersect(new Vector2(0, 0), platforms)) {
					this.pos.x -= xplus/Math.abs(xplus);
					break;
				}
			}
		}
	}
	
	//COLLISION
	public boolean platforms_intersect(Vector2 in, Platform[] platforms) {
		pos_add(in);
		if (this.platforms_intersect(platforms)) {
			pos_add(Vector2.neg(in));
			return true;
		}
		pos_add(Vector2.neg(in));
		return false;
	}
	public boolean platforms_intersect(Platform[] platforms) {
		for (int x = 0; x<platforms.length; x++) {
			if (platforms[x] == null) continue;
			if (platform_intersect(platforms[x])) {
				return platforms[x].collide_action();
			}
		}
		return false;
	}
	public boolean platform_intersect(Platform in) {
		return Rectangle.intersect(in, new Rectangle(this));
	}
	
	//LOGIC & OTHER
	void pos_add(Vector2 in) {
		this.pos = Vector2.add(this.pos, in);
	}
}
