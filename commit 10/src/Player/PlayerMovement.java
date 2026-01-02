package Player;

import DataManager.Room;
import Logic.Utility;
import Logic.Vector2;
import Main.Game;

public class PlayerMovement {
	Player player;
	private PlayerState state = new Air();
	
	
	//constants
	//air
	//subject to change
	public static final double gravity_normal = 0.36, gravity_heavy = 0.6, gravity_light = 0.16;
	public static final double max_fall_normal = 5.6, max_fall_fast = 9.6, max_fall_slow = 4.0;
	
	public static final double air_accel = 0.6, momentum_drag = 0.4; //also decel
	public static final double max_x_speed = 2.4;
	
	public static final double float_threshold = 1.2;
	
	//ground
	public static final double ground_accel = 1.0; //also decel
	
	//jump
	public static final double jump_force = 5.5, bunny_hop_mult = 1.2;
	public static final int bunny_hop_count = 3, wall_hop_count = 3;
	
	//dash
	public static final int dash_frame_length = 10, dash_length = 80;
	public static final int dash_cooldown_max = 15;
	public static final double dash_end_mult = 0.8;
	
	//wall slide
	public static final double wall_slide_speed = 0.25, wall_jump_force = 8.5, vert_wall_jump_mult = 0.67;
	public static final int wall_jump_max = 2;
	
	//gravity, drag, momentum_dec
	
	//changing numbers
	//air
	public double gravity = gravity_normal, max_fall = max_fall_normal;
	public double x_accel = air_accel, momentum_decel = momentum_drag;
	
	//jump
	public int jump_keep = 1, jump_num = jump_keep;
	
	//dash
	public Vector2 dash_dir;
	public int dash_keep = 1, dash_num = dash_keep;
	public int dash_counter = 0;
	public int dash_cooldown = 0;
	
	//boolean flags
	//jump
	public boolean jumped = false;
	//dash
	public boolean dashed = false;
	
	
	
	public PlayerMovement(Player player) {
		this.player = player;
	}
	
	public void set_state(PlayerState in) {
		state.exit(this.player);
		state = in;
		
		state.enter(this.player);
	}
	
	void update() {
		System.out.println("DB: " + this.player.input.dash_buffer);
		this.state.update(this.player, this.player.input);
		
		this.update_cooldowns();
		this.update_physics();
		
		System.out.println(this.state + " " + this.player.collider.frames_grounded + " " + this.player.object_intersect_id + " " + this.player.collider.col_down);
	}
	
	public void update_cooldowns() {
		if (!this.player.input.jump) this.jumped = false;
		if (!this.player.input.dash) this.dashed = false;
		
		
		if (this.dash_cooldown > 0) this.dash_cooldown--;
	}
	
	public void update_physics() {
		
		//gravity --> y
		this.player.vel.y -= this.gravity * Game.speed_mult;
		if (player.vel.y < -this.max_fall) this.player.vel.y = -this.max_fall;
		
		//apply x
		//test this, mayve this is better:
		/*
		 * if (input.left || input.right) {
				player.vel.x += input.last_dir * air_accel * Game.speed_mult;
				
				if (Utility.sign(player.vel.x) == input.last_dir && Math.abs(player.vel.x) > max_x_speed) {
					player.vel.x = Utility.sign(player.vel.x) * max_x_speed;
				}
				
			}
			
			else {
				player.vel.x = Utility.move_toward(player.vel.x, 0, air_accel * Game.speed_mult);
			}*/
		double target = (this.player.input.right ? 1 : (this.player.input.left ? -1 : 0)) * max_x_speed;
		this.player.vel.x = Utility.move_toward(this.player.vel.x, target, this.x_accel * Game.speed_mult);
		
		//scale momentum
		this.player.momentum.scale_to_length(Math.max(0, this.player.momentum.l() - this.momentum_decel * Game.speed_mult));
		
		//move player
		this.player.collider.move(Vector2.mult(Vector2.add(this.player.vel, this.player.momentum), Game.speed_mult));
		
		
		this.player.collider.object_action();
		this.player.collider.enemy_action();
		//this.player.collider.displace(0);
	}
	
	//each and every action has a start, middle, and end
	
	public void jump(boolean hop) {
		this.player.vel.y = jump_force;
		this.jump_num--;
		this.gravity = gravity_normal;
		this.player.input.jump_buffer = 0;
		this.jumped = true;
		
		if (hop) this.player.momentum.x *= bunny_hop_mult;
	}
	
	public void jump_gravity() {
		//if (true) return;
		if (this.player.input.jump && this.player.movement.jumped) {
			if (player.vel.y < 0) this.player.movement.gravity = gravity_heavy;
			
			if (Math.abs(this.player.vel.y) < float_threshold) this.player.movement.gravity = gravity_light;
		}
		else {
			this.player.movement.jumped = false;
			this.player.movement.gravity = gravity_heavy;
		}
	}
	
	public void wall_jump(int in) {
		if (in == -1) {
			this.player.vel.set(Vector2.mult(Vector2.up_left, wall_jump_force));
		}
		if (in == 1) {
			this.player.vel.set(Vector2.mult(Vector2.up_right, wall_jump_force));
		}
		if (in == 0) {
			this.player.vel.set(Vector2.mult(Vector2.up, wall_jump_force * vert_wall_jump_mult));
		}
		
		//TODO: make this line work...?
		//this.player.momentum.x = Math.abs(this.player.momentum.x) * -in;
		
		//this.player.momentum.set(this.player.vel.norm().mult_r(this.player.momentum.l()));
		
		this.jumped = true;
		this.player.input.wall_jump_direction_buffer = 0;
	}
	
	public void dash() {
		this.dash_dir = new Vector2(this.player.input.face_dir);
		this.dash_counter = dash_frame_length;
		this.dash_num--;
		
		player.vel.set(Vector2.add(Vector2.mult(this.dash_dir.norm(), dash_length / dash_frame_length), player.momentum));
		player.input.dash_buffer = 0;
		
		Game.cam.screen_shake(this.dash_dir);
		this.dashed = true;
	}
	
	public void end_dash() {
		this.player.momentum.add(player.vel);
		this.player.momentum.mult(dash_end_mult);
		
		this.player.vel.set(Vector2.zero);
		
		this.dash_cooldown = dash_cooldown_max;
	}
	
	//CLASSES
	interface PlayerState {
	    void enter(Player player);
	    void update(Player player, PlayerInput input);
	    void exit(Player player);
	}

	//implement coyote buffer!
	class Ground implements PlayerState {
		boolean momentum_cleared = false;

		@Override
		public void enter(Player player) {
			//reset jump
			player.movement.jump_num = player.movement.jump_keep;
			player.movement.dash_num = player.movement.dash_keep;
			
			player.movement.gravity = gravity_normal;
			player.movement.x_accel = ground_accel;
			player.movement.momentum_decel = momentum_drag;
		}

		@Override
		public void update(Player player, PlayerInput input) {
			
			if (!momentum_cleared && player.collider.frames_grounded > bunny_hop_count) {
				player.momentum.set(Vector2.zero);
				player.movement.gravity = 0;
				player.vel.set(Vector2.zero);
				momentum_cleared = true;
			}
			
			//transitions
			if ((input.jump || input.jump_buffer > 0) &&
				!player.movement.jumped) {
				
				player.movement.jump(player.collider.frames_grounded > bunny_hop_count);
				player.movement.set_state(new Air());
				return;
			}
			if (!player.collider.col) {
				player.movement.set_state(new Air());
				return;
			}
			
			if ((input.dash || input.dash_buffer > 0) &&
				!player.movement.dashed) {
				player.movement.set_state(new Dash());
				return;
			}

		}

		@Override
		public void exit(Player player) {
			// TODO Auto-generated method stub
			//player.vel.set(Vector2.zero);
		}
		
	}

	class Air implements PlayerState {

		@Override
		public void enter(Player player) {
			player.movement.gravity = gravity_normal;
			player.movement.x_accel = air_accel;
			player.movement.momentum_decel = momentum_drag;
		}

		@Override
		public void update(Player player, PlayerInput input) {
			
			//fall-speeds
			if (input.down) player.movement.max_fall = max_fall_fast;
			else if (input.up) player.movement.max_fall = max_fall_slow;
			else player.movement.max_fall = max_fall_normal;
			
			//gravities
			player.movement.jump_gravity();
			
			//============================================================
			//input-based state-change
			if (input.dash && player.movement.dash_num > 0 && !player.movement.dashed && player.movement.dash_cooldown == 0) {
				player.movement.set_state(new Dash());
				return;
			}
			
			if (input.jump && !player.movement.jumped) {
				//jump
				if (player.collider.ground_coyote > 0) {
					if (player.movement.jump_num > 0) {
						player.movement.jump(player.collider.frames_grounded < bunny_hop_count);
					}
				} //else if (player.collider.ground_coyote == 0) player.movement.jump_num = 0;
				
				
				//wall jump
				else if (player.collider.wall_coyote > 0) {
					player.movement.wall_jump(input.last_dir);
				}
			}
			
			//collision-based state-change
			if (player.collider.col) {
				if (player.collider.col_down) {
					player.movement.set_state(new Ground());
					return;
				}
				if (!player.collider.col_up && (player.collider.col_left || player.collider.col_right) && player.collider.frames_on_wall > bunny_hop_count) {
					player.movement.set_state(new WallSlide());
					return;
				}
			}
		}

		@Override
		public void exit(Player player) {
			player.movement.gravity = gravity_normal;
		}
		
	}
	
	class Dash implements PlayerState {

		@Override
		public void enter(Player player) {
			//when dashing, all conditions conserved
			player.movement.gravity = 0;
			player.movement.x_accel = 0;
			player.momentum.set(Vector2.zero);
			//player.movement.momentum_decel = 0;
			
			//set velocity to zero when deciding direction
			player.vel.set(Vector2.zero);
			
			
			player.input.dash_direction_buffer = PlayerInput.max_direction_buffer;
		}

		@Override
		public void update(Player player, PlayerInput input) {
			if (input.dash_direction_buffer > 0) {
				
				input.dash_direction_buffer--;
				if (input.dash_direction_buffer == 0) {
					player.movement.dash();
				}
				return;
			}
			
			player.movement.dash_counter--;
			
			
			//state transitions
			if (player.movement.dash_counter == 0) {
				if (player.collider.col_down) player.movement.set_state(new Ground());
				if (player.collider.col_left || player.collider.col_right) player.movement.set_state(new WallSlide());
				
				player.movement.set_state(new Air());
			}
			
		}

		@Override
		public void exit(Player player) {
			//difference between add and set here?
			player.movement.end_dash();
		}
		
	}
	
	//TODO: wall_slide exit buffer doesn't work since doesn't check for contiguous, 
	//never reset to zero, and doesn't check direction
	
	
	//you need at least three frames pressing 'off' the wall
	//you are registered as 'on the wall' when colliding left-right
		//momentum and velocity only get set to zero ~3 frames after you touch
		//thus is buffering a jump, you can get EXTRA speed when wall jumping
	//can wall jump away from wall, or towards wall, or straight up wall,
	//depending on direction you're holding
	//momentum gets redirected in the direction you jumped
	//when in the 'jumped' state, you cannot lose momentum or velocity
	class WallSlide implements PlayerState {
		boolean momentum_cleared = false;

		@Override
		public void enter(Player player) {
			//player.momentum.set(Vector2.zero);
			//player.vel.set(new Vector2(0, -wall_slide_speed));
			
			//player.movement.gravity = 0;
			player.movement.x_accel = 0;
			
			player.movement.dash_num = player.movement.dash_keep;
		}

		@Override
		public void update(Player player, PlayerInput input) {
			
			if (player.movement.jumped && player.vel.y > 0) {
				player.movement.gravity = gravity_normal;
				//player.movement.jump_gravity();
			}
			else player.movement.gravity = 0;
			
			if (input.wall_jump_direction_buffer > 0) input.wall_jump_direction_buffer--;
			
			//problem statement
			if (!momentum_cleared && player.collider.frames_on_wall > wall_hop_count && !player.movement.jumped) {
				
				player.momentum.set(Vector2.zero);
				player.vel.set(new Vector2(0, -wall_slide_speed));
				
				momentum_cleared = true;
			}
			
			//xaccel
			if ((input.count_left > PlayerInput.max_key_buffer && player.collider.col_right) ||
				(input.count_right > PlayerInput.max_key_buffer && player.collider.col_left))
				player.movement.x_accel = air_accel;
			
			if (input.dash && !player.movement.dashed && player.movement.dash_cooldown == 0) {
				player.movement.set_state(new Dash());
				return;
			}
			
			if (player.input.wall_jump_direction_buffer == 0 && 
				(input.jump || input.jump_buffer > 0) && 
				!player.movement.jumped) {
				
				int input_dir = (input.left ? -1 : input.right ? 1 : 0);
				int col_dir = (player.collider.col_left ? -1 : player.collider.col_right ? 1 : 0);
				
				int jump_dir = 0;
				
				if (input_dir == 0) jump_dir = 0;
				else if (input_dir == col_dir) {
					if (!momentum_cleared) {
						jump_dir = input_dir;
					}
					else jump_dir = 0;
				}
				else jump_dir = input_dir;
				
				System.out.println("JUMP_dir " + jump_dir);
				
				//THIS LINE HERE THIS ONE THIS OEN
				player.movement.wall_jump(jump_dir);//(input.left ? -1 : input.right ? 1 : 0);
				player.movement.set_state(new Air());
				return;
			}
			
			if ((input.jump || input.jump_buffer > 0) &&
				!player.movement.jumped &&
				input.wall_jump_direction_buffer == 0) {
				
				input.wall_jump_direction_buffer = PlayerInput.max_direction_buffer;
			}
			
			if (!player.collider.col_left && !player.collider.col_right) {
				player.movement.set_state(new Air());
				return;
			}
			
			if (player.collider.col_down) {
				player.movement.set_state(new Ground());
				return;
			}
			
		}

		@Override
		public void exit(Player player) {
			
		}
		
	}
}