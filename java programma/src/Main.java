import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main extends JFrame implements KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6544615776460473513L;
	//window vars
	private final int MAX_FPS;
	private final int WIDTH;
	private final int HEIGHT;

	//double buffer
	private BufferStrategy strategy;

	//loop variables
	private boolean isRunning = true;
	private long rest = 0;
	private boolean continueOn=false;
	//private boolean isPaused=false;

	//timing variables
	private float dt;
	private long lastFrame;
	private long startFrame;
	private int fps;
	private boolean showfps = false;
	private boolean parity=false;

	//collision vars
	private boolean xcollide = false;
	private boolean ycollide = false;

	//cloud variables
	private int cloudx = 0;
	//private int cloudy;
	//castle variables
	private int castlex = 0;
	private int castley=300;
	private int stageNumber = 0;
	private boolean enteringStage = true;
	//textures and fonts
	private String cloudTexture="clouds";
	private String castleTexture="castlebackground";
	private Font smallFont = new Font ("Courier New", 1, 18);
	private Font medFont = new Font ("Courier New", 1, 30);
	private Font bigFont = new Font ("Courier New", 1, 60);
	//private Font hugeFont = new Font ("Courier New", 1, 90);

	private Friendly friendly;
	private Enemy enemy;
	private Projectile friendlyProjectile;
	//TODO: Add buffered image arrays here for simplicity
	private enum Gamestate {
		INTRODUCTION,PLAYING,PAUSED,DEFEAT,VICTORY
	}
	private Gamestate currentGameState = Gamestate.INTRODUCTION;
	public Main(int width, int height, int fps){
		super("shoot'em v6");
		this.MAX_FPS = fps;
		this.WIDTH = width;
		this.HEIGHT = height;
		enemy=new Enemy(WIDTH,HEIGHT);
		friendly=new Friendly(WIDTH,HEIGHT);
		friendlyProjectile=new Projectile();
	}

	void init(){
		//initialize JFrame
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(0, 0, WIDTH, HEIGHT);

		setIgnoreRepaint(true);

		setResizable(false);
		setVisible(true);

		//create double buffer strategy
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		lastFrame = System.currentTimeMillis();
		addKeyListener(this);
		setFocusable(true);
	}

	private void update(){
		//update current fps
		fps = (int)(1f/dt);
		if(parity)
			cloudx--;
		if(cloudx < -350)
			cloudx=400;
		if(friendly.jumping){
			friendly.y-=friendly.height/20;
			if( (System.currentTimeMillis() - friendly.jumpTime ) > 499)
				friendly.jumping=false;
		}
		if((friendly.y < (HEIGHT - friendly.height - 1)) && !friendly.jumping)
			friendly.y+=friendly.height/62;
		if(friendly.x > friendly.barrierRight){
			friendly.x=30;
			stageNumber++;
			enteringStage=true;
		}
		if(friendly.x < friendly.barrierLeft){
			friendly.x=friendly.barrierLeft;
		}
		collisionDetect();
		if(friendly.health < 1)
			currentGameState=Gamestate.DEFEAT;
		if((enemy.health<1) && (friendly.health>0))
			currentGameState=Gamestate.VICTORY;
		if(friendlyProjectile.launched)
			friendlyProjectile.advance();
	}
	private void collisionDetect(){
		xcollide=true;
		ycollide=true;
		while(xcollide && ycollide){
			xcollide=false;
			ycollide=false;
			/*
			if (friendly.x > enemy.x){
				if ((friendly.x - enemy.x) < friendly.width)
					xcollide=true;
			}
			if (friendly.x < enemy.x){
				if ((enemy.x - friendly.x) < enemy.width)
					xcollide=true;
			}
			if (friendly.y > enemy.y){
				if ((friendly.y - enemy.y) < friendly.height)
					ycollide=true;
			}
			if (enemy.y > friendly.y){
				if ((enemy.y - friendly.y) < enemy.height)
					ycollide=true;
			}
			System.out.println(friendly.x+" "+friendly.y+","+enemy.x+" "+enemy.y);
			}
			 */
			if(Math.abs(friendly.x - enemy.x) < enemy.width)
				xcollide=true;
			if(Math.abs(friendly.y - enemy.y) < enemy.height)
				ycollide=true;
			//System.out.println(Math.abs(friendly.y - enemy.y));
			//System.out.println(Math.abs(friendly.x - enemy.x));
			if(xcollide && ycollide){
				//friendly.x-=enemy.width;
				friendly.y-=2*enemy.height;
				friendly.health-=4;
				enemy.health-=2;
			}
		}
	}
	private void draw(){
		//get canvas
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

		//clear screen
		g.setColor(Color.white);
		g.fillRect(0,0,WIDTH, HEIGHT);


		//draw background
		g.setColor(Color.cyan);
		g.fillRect(0, 0, 1920, 300);
		//g.setColor(Color.gray);
		g.fillRect(0, 300,1920,600);
		g.setColor(Color.green);
		g.fillRect(0, 900, 1920, 380);
		if((stageNumber == 1) && enteringStage){
			castley=HEIGHT-600;
			friendly.setVariant(1);
			friendly.update(88, 125);
		}
		g.drawImage(makeImage("resources/"+castleTexture+".png"), null, castlex, castley); 
		g.drawImage(makeImage("resources/"+cloudTexture+".png"), null, cloudx, 0);
		//draw friendly
		g.drawImage(makeImage(friendly.getTexture()),null,friendly.x,friendly.y);
		//draw enemy
		if(stageNumber > 0){
			if(!enemy.accessible){
				enemy.makeAccessible();
			}
			enemy.setVariant(stageNumber);
			g.drawImage(makeImage(enemy.getTexture()),null,enemy.x,enemy.y);
		} else{
			enemy.makeInaccessible();
		}
		//draw projectiles
		g.drawImage(makeImage(friendlyProjectile.getTexture()), null, friendlyProjectile.x, friendlyProjectile.y);
		//draw stage welcome
		if(enteringStage){
			g.setColor(Color.white);
			g.setFont(bigFont);
			g.drawString("STAGE "+stageNumber, 960, 300);
		}
		//draw player health
				g.setColor(Color.black);
				g.setFont(medFont);
				g.drawString("Health: "+friendly.health, 100, 70);
		//draw fps
		if(showfps){
			g.setColor(Color.red);
			g.setFont(smallFont);
			g.drawString(Long.toString(fps), 10, 40);
		}
		//release resources, show the buffer
		g.dispose();
		strategy.show();
		if(enteringStage){
			sleep(2000);
			enteringStage=false;
		}
	}
	private void drawLoading(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(makeImage("resources/loading.png"), null, 0, 10);
		g.dispose();
		strategy.show();
		sleep(2000);
	}
	private void drawIntro(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(makeImage("resources/introduction.png"), null, 0, 0);
		g.setColor(Color.black);
		g.setFont(bigFont);
		g.drawString("Welcome to the world of shoot'em!",500,300);
		g.drawString("You play as Antonov, a magician. The ",500,450);
		g.drawString("goal of the game is simple: defeat the",500,600);
		g.drawString("enemies, advance stages, and survive.",500,750);
		g.drawString("Press C to continue on, or Q to abort.",500,900);
		g.dispose();
		strategy.show();
		continueOn=false;
		while(!continueOn){
			sleep(100);
			currentGameState=Gamestate.PAUSED;
		}
	}
	private void drawPauseMenu(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(makeImage("resources/pausemenu.png"), null, 0, 0);
		g.setFont(bigFont);
		g.drawString("Stage: "+stageNumber, 200, 300);
		g.drawString("Controls:", 200, 400);
		g.drawString("C to resume", 200, 450);
		g.drawString("F to show fps", 200, 500);
		g.drawString("Q to quit", 200, 550);
		g.drawString("ESC to pause", 200, 600);
		g.drawString("SPACE to move", 200, 650);
		g.drawString("LEFT to move", 200, 700);
		g.drawString("RIGHT to move", 200, 750);
		g.drawString("S to attack", 200, 800);
		g.dispose();
		strategy.show();
		continueOn=false;
		while(!continueOn){
			sleep(100);
		}
		currentGameState=Gamestate.PLAYING;
	}
	private void drawEndMenu(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		if(currentGameState == Gamestate.DEFEAT)
			g.drawImage(makeImage("resources/defeat.png"),null,0,0);
		if(currentGameState == Gamestate.VICTORY)
			g.drawImage(makeImage("resources/victory.png"),null,0,0);
		g.dispose();
		strategy.show();
		isRunning=false;
	}
	private void sleep(int time){
		try{
			Thread.sleep(time);
		} catch(Exception e){

		}
	}
	private BufferedImage makeImage(String path){
		try{
			return ImageIO.read(new File(getClass().getResource(path).toURI()));
		} catch(Exception e){
			System.err.println(path);
			e.printStackTrace();
			return null;
		}
	}

	public void run(){
		init();
		drawLoading();
		if(currentGameState == Gamestate.INTRODUCTION)
			drawIntro();
		while(isRunning){
			if(currentGameState == Gamestate.PAUSED)
				drawPauseMenu();
			//new loop, clock the start
			startFrame = System.currentTimeMillis();
			//calculate delta time
			dt = (float)(startFrame - lastFrame)/1000;
			//log the current time
			lastFrame = startFrame;
			//call update and draw methods
			update();
			draw();
			parity=!parity;
			cleanup();
			if(currentGameState == Gamestate.DEFEAT)
				drawEndMenu();
		}

	}
	private void cleanup(){
		//dynamic thread sleep, only sleep the time we need to cap the framerate
		rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
		if(rest >0){
			try{ Thread.sleep(rest); }
			catch (InterruptedException e){ e.printStackTrace(); }
		}
	}
	@Override
	public void keyPressed(KeyEvent keyEvent){
		switch(keyEvent.getKeyCode()){
		case KeyEvent.VK_F:
			showfps=!showfps;
			break;
		case KeyEvent.VK_C:
			continueOn=true;
			break;
		case KeyEvent.VK_S:
			if(!friendly.attacking)
				friendly.attacking=true;
				friendlyProjectile.calibrateTo(friendly.x+friendly.width/2,friendly.y+friendly.height/2);
				friendlyProjectile.launch(friendly.side);
			break;
		case KeyEvent.VK_ESCAPE:
			currentGameState = Gamestate.PAUSED;
			break;
		case KeyEvent.VK_SPACE:
			if(((System.currentTimeMillis()-friendly.jumpTime) > friendly.getJumpDelay()) && (!friendly.jumping) || (friendly.jumpTime == 0)){
				friendly.jumping=true;
				friendly.jumpTime=System.currentTimeMillis();
			}
			break;
		case KeyEvent.VK_LEFT:
			friendly.x-=friendly.width/23;
			if(friendly.direction){
				friendly.side=!friendly.side;
				friendly.direction=!friendly.direction;
			}
			break;
		case KeyEvent.VK_RIGHT:
			friendly.x+=friendly.width/23;
			if(!friendly.direction){
				friendly.side=!friendly.side;
				friendly.direction=!friendly.direction;
			}
			break;
			//Debug code for moving enemy
		case KeyEvent.VK_NUMPAD4:
			enemy.x-=20;
			if(enemy.direction){
				enemy.side=!enemy.side;
				enemy.direction=!enemy.direction;
			}
			break;
		case KeyEvent.VK_NUMPAD6:
			enemy.x+=20;
			if(!enemy.direction){
				enemy.side=!enemy.side;
				enemy.direction=!enemy.direction;
			}
			break;
		case KeyEvent.VK_NUMPAD8:
			enemy.y-=20;
			break;
		case KeyEvent.VK_NUMPAD5:
			enemy.y+=20;
			break;
		case KeyEvent.VK_Q:
			System.exit(0);
			break;
		case KeyEvent.VK_NUMPAD2:/*
			if(!enemy.attacking)
				enemy.attacking=true;
				enemyProjectile.calibrateTo(friendly.x+friendly.width/2,friendly.y+friendly.height/2);
				enemyProjectile.launch(enemy.side);*/
			break;
		}
	}
	@Override
	public void keyTyped(KeyEvent keyEvent){

	}
	@Override
	public void keyReleased(KeyEvent keyEvent)
	{

	}

	public static void main(String[] args){
		Main game = new Main(1920, 1000, 50);
		game.run();
	}

}