package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import Logic.Rectangle;
import Logic.Vector2;
import Main.Start;
import Rendering.Text;

public class OptionPane extends Rectangle{
	static final int margin = 4, v_spacing = 2;
	static final Color default_fill = new Color(150, 75, 0);
	
	public Button[] buttons;
	
	
	public String input_text = "";
	public Runnable run;
	
	public OptionPane() {}
	
	public OptionPane(Vector2 top_right, String[] texts, Runnable[] runs) {
		
		Vector2 temp = new Vector2(top_right.x + margin, top_right.y - v_spacing);
		
		this.buttons = new Button[texts.length];
		
		for (int x = 0; x<this.buttons.length; x++) {
			
			double width = Text.text_width(texts[x]) + 2 * Button.padding, height = Text.text_height() + 2 * Button.padding;
			
			this.buttons[x] = new Button(temp.x + width / 2, temp.y - height / 2 - x * (height + v_spacing), width, height, texts[x], runs[x]);
		}
		
		this.width = 0;
		this.height = v_spacing;
		for (int x = 0; x<this.buttons.length; x++) {
			if (this.buttons[x].width > this.width) this.width = this.buttons[x].width;
			this.height += this.buttons[x].height + v_spacing;
		}
		this.width += 2 * margin;
		
		this.pos = top_right;
		
		
		for (int x = 0; x<this.buttons.length; x++) {
			this.buttons[x].pos.add(new Vector2(-this.width / 2, this.height / 2));
		}
		
		this.fill = default_fill;
		
		Start.pause = true;
	}
	
	public OptionPane(Vector2 top_right, String text, Runnable run) {
		//Vector2 temp = new Vector2(top_right.x + margin, top_right.y - v_spacing);
		
		double width = Text.text_width(text) + 2 * Button.padding, height = Text.text_height() + 2 * Button.padding;

		
		this.buttons = new Button[] {new Button(0, 0, width, height, input_text, null)};
		
		this.width = this.buttons[0].width + 2 * margin;
		this.height = this.buttons[0].height + 2 * v_spacing;
		
		this.pos = top_right;
		
		this.buttons[0].pos = this.pos;
		
		this.fill = default_fill;
		
		this.run = run;
		
		Start.typing = true;
		//add_keys();
		
		Start.pause = true;
	}
	
	public void update() {
		double temp = Text.text_width(this.buttons[0].text);
		if (temp > this.buttons[0].width) {
			this.buttons[0].width = temp;
			this.width = temp + 2 * margin;
		}
	}
	
	public void click(Vector2 click) {
		if (this.buttons == null) return;
		
		for (int x = 0; x<this.buttons.length; x++) {
			this.buttons[x].button_clicked(click);
		}
	}
	
	
	public void draw(Graphics g, JPanel pane, double xin, double yin, String location) {
		
		System.out.println("OPTION PANE DRAW");
		
		System.out.println("	" + this);
		super.draw(g, pane, xin, yin, location);
		
		for (int x = 0; x<this.buttons.length; x++) {
			this.buttons[x].draw_button(g, pane, xin, yin, location);
		}
	}
	
	public void done() {
		Start.pause = false;
		
		Start.o_pane = null;
	}
}
