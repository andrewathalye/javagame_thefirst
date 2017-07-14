
public class Projectile {
	boolean launched = false;
	int x;
	int y;
	boolean direction;
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
			x+=40;
		if(!direction)
			x-=40;
	}
}
