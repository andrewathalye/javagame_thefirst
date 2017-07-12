
public class Enemy {
	int x;
	int y;
	int width=250;
	int height=300;
	boolean  attacking=false;
	boolean side=false;
	boolean direction=false;
	boolean accessible=true;;
	boolean jumping;
	long jumpTime = 0;
	private int screenWidth;
	private int screenHeight;
	private int jumpDelay=2500;
	int barrierLeft;
	int barrierRight;
	int health=5;
	int fullHealth=health;
	int defaultFullHealth=health;
	int variant=-1;
	int variants=3;
	public Enemy(int width, int height){
		screenWidth=width;
		screenHeight=height;
		barrierLeft=30;
		barrierRight=screenWidth;
		x=screenWidth - this.width - 30;
		y= screenHeight - this.height;
	}
	public void makeInaccessible(){
		x=2*screenWidth;
		accessible=false;
	}
	public void makeAccessible(){
		x=screenWidth - this.width - 30;
		y= screenHeight - this.height;
		accessible=true;
	}
	public void update(int width, int height){
		this.width=width;
		this.height=height;
		x=screenWidth - this.width - 30;
		y= screenHeight - this.height;
	}
	public boolean isAttacking(){
		return attacking;
	}
	public int getJumpDelay(){
		return jumpDelay;
	}
	public void setHealth(int health){
		this.health=health;
		this.fullHealth=health;
	}
	public void setVariant(int variant){
		this.variant = variant;
	}
}
