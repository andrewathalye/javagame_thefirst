import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class KeyHandler extends Thread implements KeyListener{
	public ArrayList<Integer> keys = new ArrayList<Integer>();
	int WIDTH=0;
	int HEIGHT=0;
	public KeyHandler(int WIDTH,int HEIGHT) {
		this.WIDTH=WIDTH;
		this.HEIGHT=HEIGHT;
	}

	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_F)
			Main.showfps=!Main.showfps;
		if(e.getKeyCode() == KeyEvent.VK_W){
			if(((System.currentTimeMillis()-Main.friendly.switchTime) > Main.friendly.switchDelay) || (Main.friendly.switchTime == 0)){
				Main.friendly.switchTime=System.currentTimeMillis();
				Main.friendly.x=WIDTH-Main.friendly.x-Main.friendly.width;
				Main.friendly.side=!Main.friendly.side;
				//friendly.direction=!friendly.direction;
			}
		}
		//System.out.println("p "+e.getKeyCode());
		if(!keys.contains(e.getKeyCode()))
			keys.add(e.getKeyCode());
		Main.continueOn=true;
	}
	@Override
	public void keyTyped(KeyEvent e){

	}
	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("v"+keys.toString());
		//System.out.println("r "+e.getKeyCode());
		//keys.remove((Object)e.getKeyCode());
		for(int i=keys.size()-1; i>-1; i--){
			if(keys.get(i) == e.getKeyCode()){
				keys.remove(i);
			}
		}
	}
}
