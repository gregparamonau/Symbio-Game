package Symbio;

import Symbio.Entity.Creature;
import Symbio.Entity.PlatEntity;
import Symbio.Logic.Utility;
import Symbio.Rendering.Background;

public class Room {
	public Platform[] platforms;
	public PlatEntity[] plat_ents;
	public Creature[] creatures;
	public Background[] background;
	public int[] bounds = new int[4];
	
	public Room() {}
	public Room(Platform[] platforms, PlatEntity[] plat_ents, Creature[] creatures) {
		this.platforms = platforms;
		this.plat_ents = plat_ents;
		this.creatures = creatures;
	}
	public void start() {
		this.start_platform_textures();
		this.start_plat_ent_textures();
		this.start_background_textures();
		this.start_bounds();
	}
	public void start_platform_textures() {
		for (int x = 0; x<this.platforms.length; x++) {
			this.platforms[x].generate_sprites(this.platforms);
			this.platforms[x].start_sprites(this.platforms[x].sprite_name);
		}
	}
	public void start_plat_ent_textures() {
		for (int x = 0; x<this.plat_ents.length; x++) {
			this.plat_ents[x].generate_sprites(this.plat_ents);
			this.plat_ents[x].start_sprites(this.plat_ents[x].sprite_source);
		}
	}
	public void start_background_textures() {
		for (int x = 0; x<this.background.length; x++) {
			this.background[x].generate_sprites(this.background);
			this.background[x].start_sprites(this.background[x].sprite_name);
		}
	}
	public void start_bounds() {
		this.bounds[0] = Utility.min_x(this.platforms);
		this.bounds[1] = Utility.max_x(this.platforms);
		this.bounds[2] = Utility.min_y(this.platforms);
		this.bounds[3] = Utility.max_y(this.platforms);
	}
}
