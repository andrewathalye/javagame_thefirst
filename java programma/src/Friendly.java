
public class Friendly extends Character {
	public Friendly(int width, int height){
		x=30;
		side=true;
		//direction=true;
		variant=0;
		variants=1;
		this.width=350;
		this.height=500;
		health=10;
		maxHealth=10;
		defaultFullHealth=maxHealth;
		
		screenWidth=width;
		screenHeight=height;
		barrierLeft=30;
		barrierRight=screenWidth;
		y= screenHeight - this.height;
	}
	public void update(int width, int height){
		this.width=width;
		this.height=height;
		y= screenHeight - this.height;
	}
}
