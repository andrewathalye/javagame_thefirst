
public class Enemy extends Character {
	public Enemy(int width, int height){
		side=false;
		animation=0;
		animated=false;
		//direction=false;
		variant=-1;
		variants=8;
		health=5;
		maxHealth=health;
		defaultFullHealth=maxHealth;
		this.width=250;
		this.height=300;
		
		screenWidth=width;
		screenHeight=height;
		barrierLeft=30;
		barrierRight=screenWidth - this.width - 30;
		x=screenWidth - this.width - 30;
		y= screenHeight - this.height;
	}
	public void update(int width, int height){
		this.width=width;
		this.height=height;
		x=screenWidth - this.width - 30;
		y= screenHeight - this.height;
	}
}
