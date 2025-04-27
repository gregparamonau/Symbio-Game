package GameObject.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import GameObject.GameObject;
import LevelEdit.LevelEditor;
import Logic.Rectangle;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;
import Rendering.Camera;

public class OneWay extends GameObject{
	protected int variant = 0;
	protected static int width_default = 2, height_default = 1;
	protected static String sprite_name_default = "/object_textures/oneway.png";
	
	protected static Color fill_save = new Color(255, 151, 112);
	
	public OneWay() {}
	public OneWay(double a, double b, double c, double d, double e, double f, String sprite, int id) {
		this.pos = new Vector2(a, b);
		this.width = c;
		this.height = d;
		
		this.variant = (int)e;
		
		this.object_handle = (int)f;
		
		this.start_nodes();
		
		this.sprite_name = sprite;
		this.id = id;
		
		this.fill = fill_save;
	}
	
	//whether you are physically colliding with the platform for movement & physics
	public boolean collide_with(Rectangle in, boolean col_action) {
		return Rectangle.intersect(this, in)
		&& ((this.variant == 0 && Game.player.pos.y > this.pos.y)
		|| (this.variant == 1 && Game.player.pos.x < this.pos.x)
		|| (this.variant == 2 && Game.player.pos.x > this.pos.x)
		|| (this.variant == 3 && Game.player.pos.y < this.pos.y));
	}
	public void displace_player(int direction) {
		//direction: 0 = any, 1 = vertical, 2 = horizontal
		if (!this.solid || !Rectangle.intersect(this, Game.player) || !this.displaceable) return;
		
		Rectangle temp = Rectangle.intersect_area(this, Game.player);
		
		if (this.variant == 0 || this.variant == 3) {
			Game.player.pos.y += Utility.sign(this.dir_vector().y) * (temp.height + 1);
		}
		if (this.variant == 1 || this.variant == 2) {
			Game.player.pos.x += Utility.sign(this.dir_vector().x) * (temp.width + 1);
		}
	}
	public void generate_sprite(GameObject[] objects, String in) {
		BufferedImage[] sprites_temp = return_sprite_array(in);
		
		this.sprite = new BufferedImage((int)this.width, (int)this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.sprite.getGraphics();
		
		for (int y = 0; y < this.height / Camera.tile_size; y++) {
			for (int x = 0; x<this.width / Camera.tile_size; x++) {
				g.drawImage(sprites_temp[this.return_sprite_type(new Vector2(Math.max(x, y), 0), objects)], x * Camera.tile_size, y * Camera.tile_size, null);
			}
		}
	}
	public int return_sprite_type(Vector2 in, GameObject[] objects) {
		return this.variant * 3 + ((int)in.x != 0 && (int)in.x != Math.max(this.width, this.height) / Camera.tile_size - 1 ? 1 : 0) + ((int)in.x == Math.max(this.width, this.height) / Camera.tile_size - 1 ? 2 : 0);
	}
	public BufferedImage[] return_sprite_array(String in) {
		BufferedImage[] out = new BufferedImage[12];
		try {
			BufferedImage source = ImageIO.read(getClass().getResource(in));
			for (int x = 0; x<out.length; x++) {
				out[x] = source.getSubimage((x % 3) * Camera.tile_size, (x / 3) * Camera.tile_size, Camera.tile_size, Camera.tile_size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public Vector2 dir_vector() {
		Vector2[] temp = {new Vector2(0, 2 * LevelEditor.grid_size), new Vector2(-2 * LevelEditor.grid_size, 0), new Vector2(2 * LevelEditor.grid_size, 0), new Vector2(0, -2 * LevelEditor.grid_size)};
		return temp[this.variant];
	}
}
