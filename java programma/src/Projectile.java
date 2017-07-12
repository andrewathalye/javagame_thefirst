
public class Projectile {
	boolean launched = false;
	int x;
	int y;
	boolean direction;
	private String texture="projectile";
	public Projectile(){

	}
	public void calibrateTo(int x,int y){
		this.x=x;
		this.y=y;
	}
	public void launch(boolean direction){
		launched=true;
		this.direction=direction;
	}
	public void advance(){
		if(direction)
			x+=20;
		if(!direction)
			x-=20;
	}
	public String getTexture(){
		return "resources/"+texture+".png_"+direction;
	}
}
