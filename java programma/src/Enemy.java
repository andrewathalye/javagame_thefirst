
public class Enemy {
	int x;
	int y;
	int width=250;
	int height=300;
	boolean side=false;
	boolean direction=false;
	boolean accessible=true;;
	private int screenWidth;
	private int screenHeight;
	int barrierLeft;
    int barrierRight;
    int health=5;
	private int variant=0;
	private String texture="enemy";
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
		accessible=true;
	}
	public void update(int width, int height){
		this.width=width;
		this.height=height;
		x=screenWidth - this.width - 30;
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
}
