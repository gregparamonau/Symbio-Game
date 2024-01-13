package Symbio.Logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Symbio.Game;
import Symbio.Platform;
import Symbio.Player;
import Symbio.Entity.Creature;
import Symbio.Entity.PlatEntity;
import Symbio.Rendering.Camera;

public class Rectangle {
	public static int grid_size = 25;
	public Vector2 pos = new Vector2(0, 0); 
	public int width, height;
	public Color fill = new Color(0, 0, 0, 128);
	public BufferedImage sprite;
	public static final int tile_size = 5;
	
	public boolean start = true;
	
	public Vector2[] nodes = new Vector2[4];
	
	public Rectangle() {}
	public Rectangle(double a, double b, int c, int d) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		this.sprite = new BufferedImage(1, 1, 1);
	}
	public Rectangle(Player player) {
		this.pos = new Vector2(player.pos);
		this.width = Player.width;
		this.height = Player.height;
		this.fill = player.fill;
		this.sprite = new BufferedImage(1, 1, 1);
	}
	public Rectangle(Creature creat) {
		this.pos = new Vector2(creat.pos);
		this.width = creat.width;
		this.height = creat.height;
		this.sprite = new BufferedImage(1, 1, 1);
	}
	public Rectangle(Vector2 node, Vector2 add) {
		this.pos = new Vector2(node.x + add.x, node.y + add.y);
		this.width = 1;
		this.height = 1;
		this.sprite = new BufferedImage(1, 1, 1);
	}
	public static boolean intersect(Rectangle a, Rectangle b) {
		return (a.pos.x - a.width/2 < b.pos.x + b.width/2 && a.pos.x + a.width/2 > b.pos.x - b.width/2 && a.pos.y - a.height/2 < b.pos.y + b.height/2 && a.pos.y + a.height/2 > b.pos.y - b.height/2);
	}
	public static Rectangle intersect_area(Rectangle a, Rectangle b) {
		return new Rectangle((Math.min(a.pos.x + a.width/2, b.pos.x + b.width/2) - Math.max(a.pos.x - a.width/2, b.pos.x - b.width/2))/2, (Math.min(a.pos.y + a.height/2, b.pos.y + b.height/2) - Math.max(a.pos.y - a.height/2, b.pos.y - b.height/2))/2, (int)(Math.min(a.pos.x + a.width/2, b.pos.x + b.width/2) - Math.max(a.pos.x - a.width/2, b.pos.x - b.width/2)), (int)(Math.min(a.pos.y + a.height/2, b.pos.y + b.height/2) - Math.max(a.pos.y - a.height/2, b.pos.y - b.height/2)));
	}
	public void draw(Graphics g, JPanel pane, double xin, double yin, String location) {
		this.start_nodes();
		
		g.setColor(this.fill);
		
		if (location.equals("edit")) {
			g.fillRect((int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2), (int)(pane.getHeight()/2 - this.pos.y - this.height/2 - yin), this.width, this.height);
			this.draw_border(g, pane, xin, yin, location);
			this.draw_nodes(g, pane, xin, yin, location);
		}
		else if (location.equals("game")) {
			g.fillRect((int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2) - this.height/2), (int)this.width, (int)this.height);
			if (Game.draw_hitboxes) this.draw_border(g, pane, xin, yin, location);
		}
	}
	public void draw_with_sprite(Graphics g, JPanel pane, double xin, double yin, BufferedImage draw, String location) {
		if (location.equals("edit")) {
			g.drawImage(draw, (int)(this.pos.x + pane.getWidth()/2 - xin - draw.getWidth()/2), (int)(pane.getHeight()/2 - this.pos.y - draw.getHeight()/2 - yin), pane);
		}
		else if (location.equals("game")) {
			g.drawImage(draw, (int)(this.pos.x + pane.getWidth()/2 - xin - draw.getWidth()/2), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2) - draw.getHeight()/2), pane);
		}
		if (Game.draw_hitboxes) this.draw_border(g, pane, xin, yin, location);
	}
	public void draw_border(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("edit")) {
			g.setColor(Color.magenta);
			g.drawRect((int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2), (int)(pane.getHeight()/2 - this.pos.y - this.height/2 - yin), this.width, this.height);
		}
		else if (location.equals("game")) {
			g.setColor(Color.magenta);
			g.drawRect((int)(this.pos.x + pane.getWidth()/2 - xin - this.width/2), (int)(pane.getHeight() - (this.pos.y - yin + pane.getHeight()/2) - this.height/2), (int)this.width, (int)this.height);
		}
	}
	public void draw_nodes(Graphics g, JPanel pane, double xpos, double ypos, String location) {
		for (Vector2 temp: this.nodes) temp.draw_node(g, pane, xpos, ypos, location, Color.magenta);
	}
	//leveleditor stuff
	public void update_nodes(Vector2 in, int place) {
		this.nodes[place] = in;
		switch(place) {
		case 0:
			this.nodes[3].x = in.x;
			this.nodes[1].y = in.y;
			break;
		case 1:
			this.nodes[0].y = in.y;
			this.nodes[2].x = in.x;
			break;
		case 2:
			this.nodes[1].x = in.x;
			this.nodes[3].y = in.y;
			break;
		case 3:
			this.nodes[2].y = in.y;
			this.nodes[0].x = in.x;
			break;
		}
		clip_nodes();
		update_dimensions();
	}
	public void clip_nodes() {
		for (int x = 0; x<this.nodes.length; x++) {
			this.nodes[x].clip_node(grid_size);
		}
	}
	public void update_dimensions() {
		this.pos.x = (int)((this.nodes[0].x + this.nodes[2].x) / 2);
		this.pos.y = (int)((this.nodes[0].y + this.nodes[2].y) / 2);
		this.width = (int)Math.abs(this.nodes[2].x - this.nodes[0].x);
		this.height =(int) Math.abs(this.nodes[2].y - this.nodes[0].y);
		
	}
	public void start_nodes() {
		if (!start) return;
		Vector2[] temp = {new Vector2(this.pos.x - this.width/2, this.pos.y + this.height/2), 
				new Vector2(this.pos.x + this.width/2, this.pos.y + this.height/2),
				new Vector2(this.pos.x + this.width/2, this.pos.y - this.height/2),
				new Vector2(this.pos.x - this.width/2, this.pos.y - this.height/2)};
		//System.out.println("rect " + this.nodes.length);
		this.nodes = temp;
		start = false;
	}
	public String toString() {
		return (int)this.pos.x + " " + (int)this.pos.y + " " + this.width + " " + this.height + " ";
	}
}
