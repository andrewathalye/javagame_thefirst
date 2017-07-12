
public class Friendly {
	int x = 30;
	int y;
	boolean side=true;
	boolean direction=true;
	boolean  attacking=false;
	private String texture="friendly";
	int variant=0;
	int barrierLeft;
	int barrierRight;
	private int screenWidth;
	private int screenHeight;
	private int jumpDelay=2500;
	int width=350;
	int height=500;
	int health=10;
	boolean jumping;
	long jumpTime = 0;
	public Friendly(int width, int height){
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
	public String getTexture(){
		return "resources/"+texture+variant+".png_"+side;
	}
	public void setTexture(String texture){
		this.texture = texture;
	}
	public void setVariant(int variant){
		this.variant = variant;
	}
	public boolean isAttacking(){
		return attacking;
	}
	public int getJumpDelay(){
		return jumpDelay;
	}
}
