import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

public class Main extends JFrame implements KeyListener{

	/**
	 * (C) 2017 Andrew Athalye. Read attached README.md for specific details.
	 * This program is not to be modified for a commercial purpose, or sold.
	 * NO WARRANTY OR GUARANTEE WHATSOEVER IS PROVIDED ALONGSIDE THIS SOFTWARE
	 */
	public static final long serialVersionUID = -6544615776460473513L;
	//window vars
	public final int MAX_FPS;
	public final int WIDTH;
	public final int HEIGHT;

	//double buffer
	private BufferStrategy strategy;

	//loop variables
	public boolean isRunning = true;
	private long rest = 0;
	private boolean continueOn=false;


	//timing variables
	public float dt;
	public long lastFrame;
	public long startFrame;
	public int fps;
	private boolean showfps = false;
	private boolean parity=false;
	private int defaultAnimationRunCounter=4;
	private int animationRunCounter=defaultAnimationRunCounter;
	public int fpserrors=0;
	public long lastFrameError=0;

	//collision vars
	private boolean xcollide = false;
	private boolean ycollide = false;

	//enemy vars
	private boolean enemyDefeated = false;
	private Random attackRandom = new Random();

	//smooth key input
	private ArrayList<Integer> keys = new ArrayList<Integer>();

	//cloud variables
	private int cloudx = 0;
	//private int cloudy;

	//castle variables
	private int castlex = 0;
	private int castley;
	private int stageNumber = 0;
	private boolean enteringStage = true;

	//textures and fonts are now at TextureSource

	//Initialise objects
	private Friendly friendly;
	private Enemy enemy;
	private Projectile friendlyProjectile = new Projectile();
	private Projectile enemyProjectile = new Projectile();
	private TextureSource textures = new TextureSource();
	private AudioSource audio = new AudioSource();

	//Resource directory
	public static String resourceDir="/resources/";

	private enum Gamestate {
		INTRODUCTION,PLAYING,PAUSED,DEFEAT,VICTORY,COMPLETE
	}
	private Gamestate currentGameState = Gamestate.INTRODUCTION;

	public Main(int width, int height, int fps){
		super("shoot'em v10r2");
		this.MAX_FPS = fps;
		this.WIDTH = width;
		this.HEIGHT = height;
		enemy=new Enemy(WIDTH,HEIGHT);
		friendly=new Friendly(WIDTH,HEIGHT);
		castley=HEIGHT-7*HEIGHT/10;
		//setInvincible();
	}
	public void setInvincible(){
		friendly.health=Integer.MAX_VALUE;
		friendly.maxHealth=Integer.MAX_VALUE;
		friendly.defaultFullHealth=Integer.MAX_VALUE;
	}

	private void init(){
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
		if(enemy.jumping){
			enemy.y-=enemy.height/20;
			if( (System.currentTimeMillis() - enemy.jumpTime ) > 499)
				enemy.jumping=false;
		}
		if((enemy.y < (HEIGHT - enemy.height - 1)) && !enemy.jumping)
			enemy.y+=enemy.height/62;
		if(!enemyDefeated && isFighting())
			friendly.barrierRight=WIDTH-friendly.width-30;
		else
			friendly.barrierRight=WIDTH;
		if(friendly.x > friendly.barrierRight && ( enemyDefeated || !(isFighting()))){
			friendly.x=30;
			stageNumber++;
			enteringStage=true;
		}
		if(enemy.x > enemy.barrierRight-30-enemy.width && enemy.accessible){
			enemy.x=enemy.barrierRight-30-enemy.width;
		}
		if(friendly.x < friendly.barrierLeft){
			friendly.x=friendly.barrierLeft;
		}
		if(enemy.x < friendly.barrierLeft){
			enemy.x=friendly.barrierLeft;
		}
		if(isFighting() && enemy.accessible){
			if(enemy.x - friendly.x > 0)
				enemy.side = false;
			else
				enemy.side=true;
			if(attackRandom.nextInt(400) == 5){
				if(((System.currentTimeMillis()-enemy.jumpTime) > enemy.getJumpDelay()) && (!enemy.jumping) || (enemy.jumpTime == 0)){
					enemy.jumping=true;
					enemy.jumpTime=System.currentTimeMillis();
				}
			}
			if(attackRandom.nextInt(10) == 4 && !enemy.side){
				enemy.x-=20;
			}
			if(attackRandom.nextInt(10) == 3 && enemy.side){
				enemy.x+=20;
			}
			if(Math.abs(friendly.y-enemy.y) < 201){
				if(attackRandom.nextInt(50) == 2){
					if(!enemy.attacking){
						enemy.attacking=true;
						enemyProjectile.calibrateTo(enemy.x+enemy.width/2,enemy.y+enemy.height/2+50);
						enemyProjectile.launch(enemy.side);
					}
				}
			}
		}
		collisionDetect();
		if(friendly.health < 1)
			currentGameState=Gamestate.DEFEAT;
		if((enemy.health<1) && (friendly.health>0))
			currentGameState=Gamestate.VICTORY;
		if(friendlyProjectile.launched)
			friendlyProjectile.advance();
		if(enemyProjectile.launched)
			enemyProjectile.advance();
		//Update projectile states
		if(friendlyProjectile.x > WIDTH || friendlyProjectile.x < 0){
			friendly.attacking=false;
			friendlyProjectile.launched=false;
			friendlyProjectile.calibrateTo(0, 0);
		}
		if(enemyProjectile.x > WIDTH || enemyProjectile.x < 0){
			enemy.attacking=false;
			enemyProjectile.launched=false;
			enemyProjectile.calibrateTo(0, 0);
		}
		if(friendlyProjectile.launched || enemyProjectile.launched)
			projectileCollisionDetect();
		if(isFighting() && enteringStage)
			enemy.variant++;
	}
	private void projectileCollisionDetect(){
		xcollide=true;
		ycollide=true;
		while(xcollide && ycollide){
			xcollide=false;
			ycollide=false;
			//if(Math.abs(friendlyProjectile.x - enemy.x) < enemy.width+1 && friendlyProjectile.x > enemy.x)
			if(enemy.x+enemy.width > friendlyProjectile.x && enemy.x < friendlyProjectile.x)
				xcollide=true;
			//if(Math.abs(friendlyProjectile.y - enemy.y) < enemy.height+1 && friendlyProjectile.y < enemy.y)
			if(enemy.y+enemy.height > friendlyProjectile.y && enemy.y < friendlyProjectile.y)
				ycollide=true;
			if(xcollide && ycollide){
				enemy.y-=enemy.height;
				enemy.health-=2;
				friendly.attacking=false;
				friendlyProjectile.launched=false;
				friendlyProjectile.calibrateTo(0, 0);
			}
		}
		xcollide=true;
		ycollide=true;
		while(xcollide && ycollide){
			xcollide=false;
			ycollide=false;
			//if(Math.abs(enemyProjectile.x - friendly.x) < friendly.width+1 && enemyProjectile.x > friendly.x)
			if(friendly.x+friendly.width > enemyProjectile.x && friendly.x < enemyProjectile.x)
				xcollide=true;
			//if(Math.abs(enemyProjectile.y - friendly.y) < friendly.height+1 && enemyProjectile.y < friendly.y)
			if(friendly.y+friendly.height > enemyProjectile.y && friendly.y < enemyProjectile.y)
				ycollide=true;
			if(xcollide && ycollide){
				friendly.y-=2*friendly.height;
				friendly.health-=2;
				enemy.attacking=false;
				enemyProjectile.launched=false;
				enemyProjectile.calibrateTo(0, 0);
			}
		}
	}
	private void collisionDetect(){
		xcollide=true;
		ycollide=true;
		while(xcollide && ycollide){
			xcollide=false;
			ycollide=false;
			if(enemy.x+enemy.width > friendly.x && enemy.x < friendly.x)
				xcollide=true;
			if(enemy.y+enemy.height > friendly.y && enemy.y < friendly.y)
				ycollide=true;
			if(xcollide && ycollide){
				//friendly.x-=enemy.width;
				friendly.y-=2*enemy.height;
				friendly.health-=2;
				enemy.health-=2;
			}
		}
	}
	private void draw(){
		if(isFighting() && enteringStage){
			castley=HEIGHT-6*HEIGHT/10;
			friendly.setVariant(1);
			friendly.update(88, 125);
		}else if(enteringStage){
			castley=HEIGHT-7*HEIGHT/10;
			friendly.setVariant(0);
			friendly.update(350, 500);
		}
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
		g.drawImage(textures.castle, null, castlex, castley); 
		g.drawImage(textures.clouds, null, cloudx, 0);


		//draw friendly
		g.drawImage(textures.friendly[friendly.variant][intFromBool(friendly.side)],null,friendly.x,friendly.y);

		//draw enemy
		if(isFighting()){
			if(enteringStage){
				enemy.makeAccessible();
				enemy.setHealth(enemy.maxHealth+1);
			}
			if(enemy.variant == 6)
				enemy.animated=true;
			else
				enemy.animated=false;
			if(!enemy.animated){
				if(enteringStage){
					enemy.update(250, 300);
				}
				g.drawImage(textures.enemy[enemy.variant][intFromBool(enemy.side)],null,enemy.x,enemy.y);
			}
			if(enemy.animated){
				if(enteringStage){
					if(enemy.variant == 6)
						enemy.update(200, 100);
					textures.populateEnemyAnimated(enemy.variant);
				}
				g.drawImage(textures.enemyAnimated[enemy.variant][intFromBool(enemy.side)][enemy.animation], null, enemy.x, enemy.y);
				if(enemy.animation<7){
					if(parity && animationRunCounter == 0){
						enemy.animation++;
						animationRunCounter=defaultAnimationRunCounter;
					}
					if(parity && animationRunCounter >0)
						animationRunCounter--;
				}
				else
					enemy.animation=0;
			}
		} else{
			enemy.makeInaccessible();
		}
		//draw projectiles
		if(friendlyProjectile.launched)
			g.drawImage(textures.projectile[intFromBool(friendlyProjectile.direction)], null, friendlyProjectile.x, friendlyProjectile.y);
		if(enemyProjectile.launched)
			g.drawImage(textures.projectile[intFromBool(enemyProjectile.direction)], null, enemyProjectile.x, enemyProjectile.y);

		//draw stage welcome
		if(enteringStage){
			g.setColor(Color.white);
			g.setFont(textures.bigFont);
			g.drawString("STAGE "+stageNumber, 960, 300);
			enemyDefeated=false;
		}
		//draw player health
		g.setColor(Color.black);
		g.setFont(textures.medFont);
		g.drawString("Health: "+friendly.health, 100, 70);
		//draw enemy health
		if(isFighting() && !enemyDefeated)
			g.drawString("Enemy: "+enemy.health, 1600, 70);
		//draw fps
		if(showfps){
			g.setColor(Color.red);
			g.setFont(textures.smallFont);
			g.drawString(Long.toString(fps)+" fps "+fpserrors+" errors.", 10, 40);
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
		g.drawImage(textures.loading, null, 0, 10);
		g.dispose();
		strategy.show();
		sleep(2000);
	}
	private void drawIntro(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(textures.introduction, null, 0, 0);
		g.setColor(Color.black);
		g.setFont(textures.bigFont);
		g.drawString("Welcome to the world of shoot'em!",500,300);
		g.drawString("You play as Antonov, a magician. The ",500,450);
		g.drawString("goal of the game is simple: defeat the",500,600);
		g.drawString("enemies, advance stages, and survive.",500,750);
		g.drawString("Press C to continue on, or Q to abort.",500,900);
		g.dispose();
		strategy.show();
		continueOn=false;
		while(!continueOn){
			handleSmoothKeys();
			currentGameState=Gamestate.PAUSED;
		}
	}
	private boolean isFighting(){
		return !((stageNumber/2)*2 == stageNumber);
	}
	private void drawPauseMenu(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(textures.pauseMenu, null, 0, 0);
		g.setFont(textures.bigFont);
		g.drawString("Stage: "+stageNumber, 200, 300);
		g.drawString("Controls:", 200, 400);
		g.drawString("C to resume/continue", 200, 450);
		g.drawString("F to show fps/errors", 200, 500);
		g.drawString("Q to quit", 200, 550);
		g.drawString("ESC to pause", 200, 600);
		g.drawString("SPACE to jump", 200, 650);
		g.drawString("LEFT to move left", 200, 700);
		g.drawString("RIGHT to move right", 200, 750);
		g.drawString("S to attack", 200, 800);
		g.dispose();
		strategy.show();
		continueOn=false;
		sleep(200);
		while(!continueOn){
			sleep(10);
			handleSmoothKeys();
		}
		currentGameState=Gamestate.PLAYING;
	}
	private void drawEndMenu(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		if(currentGameState == Gamestate.DEFEAT){
			g.drawImage(textures.defeat,null,0,0);
			sleep(100);
		}
		if(currentGameState == Gamestate.VICTORY){
			g.drawImage(textures.victory,null,0,0);
			enemy.makeInaccessible();
			enemy.health=1;
			enemy.direction=false;
			enemy.side=false;
		}
		if(currentGameState == Gamestate.COMPLETE){
			g.drawImage(textures.complete, null, 0, 0);
			isRunning=false;
		}
		g.dispose();
		strategy.show();
		if(currentGameState == Gamestate.COMPLETE){
			while(true){
				sleep(10);
				handleSmoothKeys();
			}
		}
		if(currentGameState == Gamestate.VICTORY){
			currentGameState = Gamestate.PLAYING;
			sleep(5000);
			friendlyProjectile.calibrateTo(0, 0);
			friendlyProjectile.launched=false;
			enemyProjectile.calibrateTo(0, 0);
			enemyProjectile.launched=false;
			friendly.attacking=false;
			friendly.x=WIDTH-friendly.width-30;
			friendly.y=HEIGHT-friendly.height;
			enemyDefeated=true;
			enemy.attacking=false;
		}
		if(currentGameState == Gamestate.DEFEAT){
			continueOn=false;
			while(!continueOn){
				sleep(10);
				handleSmoothKeys();
			}

			friendly.health = friendly.maxHealth;
			friendly.x=friendly.barrierLeft;
			friendly.y=HEIGHT - friendly.height;
			friendly.setVariant(0);
			friendlyProjectile.calibrateTo(0, 0);
			friendlyProjectile.launched=false;
			friendly.side=true;
			friendly.direction=true;
			friendly.attacking=false;

			enemy.side=false;
			enemy.direction=false;
			enemy.setVariant(-1);
			enemy.health=enemy.defaultFullHealth;
			enemyDefeated=false;
			enemy.maxHealth=enemy.defaultFullHealth;
			enemyProjectile.calibrateTo(0, 0);
			enemyProjectile.launched=false;
			enemy.attacking=false;

			enteringStage=true;
			stageNumber=0;
			currentGameState = Gamestate.PLAYING;
			isRunning=true;
		}
	}
	private void drawFramesError(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(textures.framesError, null, 0, 0);
		g.dispose();
		strategy.show();
		sleep(20000);
		System.exit(1);
	}
	private void sleep(int time){
		try{
			Thread.sleep(time);
		} catch(Exception e){

		}
	}
	private int intFromBool(boolean bool){
		return (bool) ? 1 : 0;
	}
	private void handleSmoothKeys(){
		if(keys.size()>0){
			for(int i=0;i<keys.size();i++){
				if(currentGameState == Gamestate.PLAYING){
					switch(keys.get(i)){
					case KeyEvent.VK_S:
						if(!friendly.attacking){
							friendly.attacking=true;
							friendlyProjectile.calibrateTo(friendly.x+friendly.width/2,friendly.y+friendly.height/2);
							friendlyProjectile.launch(friendly.side);
						}
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
						friendly.x-=friendly.width/20;
						if(friendly.direction){
							friendly.side=!friendly.side;
							friendly.direction=!friendly.direction;
						}
						break;
					case KeyEvent.VK_RIGHT:
						friendly.x+=friendly.width/20;
						if(!friendly.direction){
							friendly.side=!friendly.side;
							friendly.direction=!friendly.direction;
						}
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
				if(keys.get(i) == KeyEvent.VK_C)
					continueOn=true;
				if(keys.get(i) == KeyEvent.VK_Q)
					System.exit(0);
			}
		}
	}
	public void run(){
		init();
		drawLoading();
		if(currentGameState == Gamestate.INTRODUCTION)
			drawIntro();
		audio.play(audio.music0);
		while(isRunning){
			if(currentGameState == Gamestate.PAUSED)
				drawPauseMenu();
			//new loop, clock the start
			startFrame = System.currentTimeMillis();
			//calculate delta time
			dt = (float)(startFrame - lastFrame)/1000;
			//log the current time
			lastFrame = startFrame;
			//handle keys 
			handleSmoothKeys();
			//call update and draw methods
			if(currentGameState == Gamestate.PLAYING){
				update();
				if(enteringStage){
					audio.close();
					audio.create();
					if(isFighting() && enemy.variant < 4){
						audio.music1=audio.load("music1");
						audio.play(audio.music1);
					} else if(isFighting() && enemy.variant > 3){
						audio.music2=audio.load("music2");
						audio.play(audio.music2);
					} else{
						audio.music0=audio.load("music0");
						audio.play(audio.music0);
					}
				}
				if(System.currentTimeMillis()-lastFrameError > 3000)
					fpserrors=0;
				if(fps<25){
					lastFrameError=System.currentTimeMillis();
					fpserrors++;
					if(fpserrors>20)
						drawFramesError();
				}
				if(enemy.variant > enemy.variants){
					currentGameState = Gamestate.COMPLETE;
					drawEndMenu();
				}
				draw();
			}
			parity=!parity;
			cleanup();
			if(currentGameState == Gamestate.DEFEAT || currentGameState == Gamestate.VICTORY)
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
		if(keyEvent.getKeyCode() == KeyEvent.VK_F)
			showfps=!showfps;
		if(!keys.contains(keyEvent.getKeyCode())){
			keys.add(keyEvent.getKeyCode());
		}
	}
	@Override
	public void keyTyped(KeyEvent keyEvent){

	}
	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		for(int i=keys.size()-1; i>-1; i--){
			if(keys.get(i) == keyEvent.getKeyCode())
				keys.remove(i);
		}
	}

	public static void main(String[] args){
		Main game = new Main(1920, 1000, 50);
		game.run();
	}

}