package Rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Logic.Utility;
import Main.Game;

public class Animation {
	final static int fps = 10;
	public BufferedImage[] sprites;
	public int counter = 0;
	
	public Animation(String file, int width, int height, boolean fliph, boolean flipv) {
		try {
			BufferedImage temp = ImageIO.read(getClass().getResource(file));
			temp = Utility.flip(temp, fliph, flipv);
			int length = temp.getWidth() / width;
			
			this.sprites = new BufferedImage[length];
			for (int x = 0; x<length; x++) {
				this.sprites[x] = Utility.transformed_instance(temp.getSubimage(x * width, 0, width, height), 1, Color.black, 1);
			}
			if (flipv) this.flip_sprites();
		}catch(Exception e) {e.printStackTrace();}
	}
	public void play() {
		this.counter = 0;
	}
	public BufferedImage return_sprite() {
		this.counter++;
		return this.sprites[(int)Utility.clamp((this.counter%Game.frame_rate)/(Game.frame_rate / fps), 0, this.sprites.length - 1)];
	}
	public boolean interrupt() {
		if (this.counter%fps == 0) return true;
		return false;
	}
	public void flip_sprites() {
		BufferedImage[] temp = new BufferedImage[this.sprites.length];
		for (int x = 0; x<this.sprites.length; x++) {
			temp[x] = this.sprites[this.sprites.length - 1 - x];
		}
		this.sprites = temp;
	}
}
