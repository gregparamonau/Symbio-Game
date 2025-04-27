package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Bezier;
import Logic.Line;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Main.Start;

public class BezierTerrain extends GameObject{
	public static int default_num = 3;
	protected static boolean default_start_full = false, default_render_padding = true;
	
	protected int num = 3;
	protected boolean start_full = false, render_padding = true;
	
	public Bezier shape;
	
	Line intersect = new Line(new Vector2(0, 0), new Vector2(0, 0));
	
	protected static String texture_file = "/object_textures/mouth.png";
	
	public BezierTerrain(double num, double[] pnts, double object_handle, String start_full, String render_padding, String sprite, int id) {
		this.shape = new Bezier(num, pnts);
		this.num = (int)num;
		
		this.object_handle = (int)object_handle;
		
		this.start_full = (start_full.equals("true"));
		this.render_padding = (render_padding.equals("true"));
		
		this.sprite_name = sprite;
		
		this.vis_solid = true;
		
		this.id = id;
		
		this.assign_rect();
		
		this.nodes = this.shape.control_points;
		
		this.start_nodes();
	}
	
	public void scale(double in) {
		this.pos.mult(in);;
		this.shape.bounding_box.width *= in;
		this.shape.bounding_box.height *= in;
		this.width *= in;
		this.height *= in;
		
		for (int x = 0; x<this.shape.control_points.length; x++) this.shape.control_points[x].mult(in);
		this.nodes = this.shape.control_points;
	}
	
	
	//default_object(Vector2 loc)
			//update() [needed if the object does an action / changes sprite?]
			//collide_with(Rectangle in, boolean col_action)
			//collision_action()
			//displace_player(int direction)
			//4 momentum methods:
				//dash_jump
				//jump
				//dash_wall_jump
				//wall_jump
			//draw_object()
			//generate_sprite(GameObject[] objects, String in)
			//return_sprite_type(Vector2 in, GameObject[] objects)
			//return_sprite_array(String in)
			//toString()
			//give_class()
		
		//TODO: 
		//generate sprite
		//collision detection
		//print class
		//draw
	
	public boolean collide_with(Rectangle in, boolean col_action) {
		//TODO: rework collision code so that it always works, not just when 
		if (!Rectangle.intersect(this.shape.bounding_box, in)) return false;
		
		this.intersect = this.shape.intersect_rect(in);
		
		return this.intersect != null;
		
	}
	
	public void move(int grid_size) {
		if (!LevelEditor.mouse_pressed) return;
		
		this.nodes = Vector2.move_arr(this.save_nodes, Vector2.sub(LevelEditor.mouse_pos, LevelEditor.click));
		this.clip_nodes(grid_size);
		
		this.pos = Vector2.mult(Vector2.add(this.nodes[0], this.nodes[2]), 0.5);
		
		this.shape.control_points = this.nodes;
	
	}
	
	public void displace_entity(Rectangle in, int direction) {
		//if (this.intersect == null) return;
		
		//Line.disp_rect(this.intersect, in, direction);
		
	}
	
	//TODO: 4 momentum methods
	
	
	public void draw_object(Graphics g, JPanel pane, double xin, double yin, String location) {
		if (location.equals("edit")) {
			g.setColor(Color.black);
			Vector2[] vecs = {Vector2.converted_pos(this.shape.pnt(0), pane, xin, yin, location), null};
			for (int x = 0; x <= Bezier.res; x++) {
				vecs[Math.abs((x + 1) % 2)] = Vector2.converted_pos(this.shape.pnt((double)x / Bezier.res), pane, xin, yin, location);
				
				g.drawLine((int)vecs[0].x, (int)vecs[0].y, (int)vecs[1].x, (int)vecs[1].y);
			}
			
			for (int x = 0; x<this.shape.control_points.length; x++) {
				this.shape.control_points[x].draw_node(g, pane, xin, yin, location, Color.magenta);
			}
			
		}
		if (location.equals("game")) {
			this.draw_with_sprite(g, pane, xin, yin, this.sprite, location);
			
			if (!Game.debug_mode) return;
			
			this.shape.bounding_box.draw_border(g, pane, xin, yin, location);
			this.draw_border(g, pane, xin, yin, location);
			
			this.shape.test.draw_border(g, pane, xin, yin, location);
			
			Vector2[] vecs = {this.shape.pnt(0), null};
			for (int x = 1; x <= Bezier.res; x++) {
				vecs[x % 2] = this.shape.pnt((double)x / Bezier.res);
				
				new Line(vecs[0], vecs[1]).draw_line(g, fill, pane, xin, yin, location);
			}
			
			
			for (int x = 0; x<this.shape.control_points.length; x++) {
				this.shape.control_points[x].draw_node(g, pane, xin, yin, location, Color.magenta);
			}
		}
	}
	
	public String give_class() {
		return "bezier_terrain";
	}
	
	void assign_rect() {
		this.pos = this.shape.bounding_box.pos;
		this.width = this.shape.bounding_box.width;
		this.height = this.shape.bounding_box.height;
	}
	
	public void start_nodes() {
		this.nodes = this.shape.control_points;
	}
	
	public void generate_sprite(GameObject[] objects, String in) {
		
		if (this.render_padding) this.height += 12;
		//this.width += 12;
		
		long a = System.nanoTime();
		
		this.sprite = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.sprite.getGraphics();
		
		BufferedImage source = new BufferedImage(1, 1, 1);
		try {
			source = ImageIO.read(getClass().getResource(in));
		} catch (IOException e) {e.printStackTrace();}
		
		g.setColor(Color.magenta);
		
		double[][] total_roots = new double[this.sprite.getWidth()][];
		
		//GENERATE TOTAL ROOTS
		for (int x = 0; x < (int)this.width; x++) {
			double[] roots = this.shape.intersect_roots(x + this.pos.x - this.width / 2, false, true, this.pos.y + this.height / 2);

			Utility.sort(roots);
			
			double[] full = {0};
			full = Utility.append(full, roots);
			full = Utility.append(full, new double[] {this.height});
			
			total_roots[x] = full;
			
		}
		
		int[] cols = {source.getRGB(source.getWidth() - 1, source.getHeight() - 1),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 2),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 3),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 4),
				source.getRGB(source.getWidth() - 1, source.getHeight() - 5)};
		
		for (int x = 0; x<total_roots.length; x++) {
			
			
			for (int i = (this.start_full ? 0 : 1); i<total_roots[x].length - 1; i += 2) {
				
				for (int y = (int)total_roots[x][i]; y < total_roots[x][i + 1]; y++) {
					double dist = find_closest_dist(total_roots, x, y);
					
					double rand = Math.random();
					
					if (dist < 2) {
						if (rand <= 0.8) this.sprite.setRGB(x, y, cols[0]);
						if (rand > 0.8) this.sprite.setRGB(x, y, cols[1]);
						continue;
					}
					if (dist < 4) {
						if (rand <= 0.75) this.sprite.setRGB(x, y, cols[1]);
						if (rand > 0.75) this.sprite.setRGB(x, y, cols[2]);
						continue;
					}
					if (dist < 5) {
						if (rand <= 0.1) this.sprite.setRGB(x, y, cols[1]);
						if (rand > 0.1 && rand <= 0.8) this.sprite.setRGB(x, y, cols[2]);
						if (rand > 0.7) this.sprite.setRGB(x, y, cols[3]);
						continue;
					}
					if (dist < 6) {
						if (rand <= 0.6) this.sprite.setRGB(x, y, cols[3]);
						if (rand > 0.6) this.sprite.setRGB(x, y, cols[4]);
						continue;
					}
					this.sprite.setRGB(x, y, cols[4]);
					
					
					
				}
			}
		}
		
		long b = System.nanoTime();
		
		//System.out.println("TIME FOR SPRITE: " + (double)(b - a) / 1000000);

		
		this.write_image("/Users/gregoryparamonau/Downloads/BEZIER_IMAGE.png", this.sprite);
	}
	
	public boolean vis_intersect(Rectangle in) {
		if (this.sprite == null) return false;
		if (!Rectangle.intersect(this, in)) return false;
		
		Vector2 temp = new Vector2(this.pos.x - this.width / 2, this.pos.y + this.height / 2);
		
		Vector2 rel = Vector2.sub(temp, in.pos);
		rel.x *= -1;

		//System.out.println("REL: " + rel + " THIS: " + this); 
		return this.sprite.getRGB((int)rel.x, (int)rel.y) != 0;
	}
	
	public double find_closest_dist(double[][] in, int x, int y) {
		double out = 10;
		
		for (int i = Math.max(0, x - 5); i < Math.min(in.length, x + 5); i++) {
			for (int j = 1; j<in[i].length - 1; j++) {
				double temp = Utility.dist(i, in[i][j], x, y);
				if (temp < out) {
					out = temp;
				}
			}
		}
		
		return out;
	}
	
	public void write_image(String FILE, BufferedImage img) {
		
		System.out.println("WRITING IMAGE");
		try {
			File out = new File(FILE);
			ImageIO.write(img, "png", out);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
