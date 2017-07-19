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
		if(e.getKeyCode() == KeyEvent.VK_Z && Main.currentGameState == Main.Gamestate.PAUSED)
			Main.loadingKey=true;
		if(e.getKeyCode() == KeyEvent.VK_X && Main.currentGameState == Main.Gamestate.PAUSED)
			Main.savingKey=true;
		if(e.getKeyCode() == KeyEvent.VK_Q){
			System.out.println("Exiting...");
			System.exit(0);
		}
		if(!keys.contains(e.getKeyCode()))
			keys.add(e.getKeyCode());
	}
	void handleSmoothKeys(){
		if(keys.size()>0){
			for(int i=0;i<keys.size();i++){
				if(Main.currentGameState == Main.Gamestate.PLAYING){
					switch(keys.get(i)){
					case KeyEvent.VK_S:
						if(!Main.friendly.attacking){
							Main.friendly.attacking=true;
							Main.friendlyProjectile.calibrateTo(Main.friendly.x+Main.friendly.width/2,Main.friendly.y+Main.friendly.height/2);
							Main.friendlyProjectile.launch(Main.friendly.side);
						}
						break;
					case KeyEvent.VK_ESCAPE:
						Main.currentGameState = Main.Gamestate.PAUSED;
						break;
					case KeyEvent.VK_SPACE:
						if(((System.currentTimeMillis()-Main.friendly.jumpTime) > Main.friendly.getJumpDelay()) && (!Main.friendly.jumping) || (Main.friendly.jumpTime == 0)){
							Main.friendly.jumping=true;
							Main.friendly.jumpTime=System.currentTimeMillis();
						}
						break;
					case KeyEvent.VK_A:
						Main.friendly.x-=Main.friendly.width/20;
						Main.friendly.side=false;
						break;
					case KeyEvent.VK_D:
						Main.friendly.x+=Main.friendly.width/20;
						Main.friendly.side=true;
						break;
						//Debug code for moving enemy
						/*
					case KeyEvent.VK_J:
						enemy.x-=enemy.width/20;
						if(enemy.direction){
							enemy.side=!enemy.side;
							enemy.direction=!enemy.direction;
						}
						break;
					case KeyEvent.VK_L:
						enemy.x+=enemy.width/20;
						if(!enemy.direction){
							enemy.side=!enemy.side;
							enemy.direction=!enemy.direction;
						}
						break;
					case KeyEvent.VK_I:
						//enemy.y-=enemy.height/10;
						if(((System.currentTimeMillis()-enemy.jumpTime) > enemy.getJumpDelay()) && (!enemy.jumping) || (enemy.jumpTime == 0)){
							enemy.jumping=true;
							enemy.jumpTime=System.currentTimeMillis();
						}
						break;
					case KeyEvent.VK_SEMICOLON:
						if(!enemy.attacking){
							enemy.attacking=true;
							enemyProjectile.calibrateTo(enemy.x+enemy.width/2,enemy.y+enemy.height/2+50);
							enemyProjectile.launch(enemy.side);
						}
						break;
						 */
					}
				}
				if(keys.get(i) == KeyEvent.VK_ENTER)
					Main.continueOn=true;
			}
		}
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
