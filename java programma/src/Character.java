public abstract class Character {
	int x;
	int y;
	int animation;
	boolean animated;
	boolean side;
	//boolean direction;
	boolean attacking = false;
	boolean accessible=true;
	int variant;
	int variants;
	int barrierLeft;
	int barrierRight;
	protected int screenWidth;
	protected int screenHeight;
	protected int jumpDelay=2500;
	protected int switchDelay=1250;
	int width;
	int height;
	int health;
	int maxHealth;
	int defaultFullHealth;
	boolean jumping=false;
	long jumpTime = 0;
	long switchTime = 0;
	public abstract void update(int width,int height);
	public void setVariant(int variant){
		this.variant = variant;
	}
	public boolean isAttacking(){
		return attacking;
	}
	public int getJumpDelay(){
		return jumpDelay;
	}
	public void setHealth(int health){
		this.health=health;
		this.maxHealth=health;
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
}
