import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Main extends JFrame{

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
	static boolean continueOn=false;


	//timing variables
	public float dt;
	public long lastFrame;
	public long startFrame;
	public int fps;
	static boolean showfps = false;
	private boolean parity=false;
	private int defaultAnimationRunCounter=4;
	private int animationRunCounter=defaultAnimationRunCounter;
	public int fpserrors=0;
	public long lastFrameError=0;

	//DRM variables
	private static int versionNumber=11;
	private static int versionVariant=4;

	//collision vars
	private boolean xcollide = false;
	private boolean ycollide = false;

	//enemy vars
	private boolean enemyDefeated = false;
	private Random attackRandom = new Random();


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
	static Friendly friendly;
	static Enemy enemy;
	static Projectile friendlyProjectile = new Projectile();
	static boolean loadingKey=false;
	private Projectile enemyProjectile = new Projectile();
	private TextureSource textures = new TextureSource();
	private AudioSource audio = new AudioSource();
	private KeyHandler keyHandler;

	//Resource directory
	public static String resourceDir="/resources/";

	static enum Gamestate {
		INTRODUCTION,PLAYING,PAUSED,DEFEAT,VICTORY,COMPLETE
	}
	static Gamestate currentGameState = Gamestate.INTRODUCTION;

	public Main(int width, int height, int fps){
		super("shoot'em v"+versionNumber+"r"+versionVariant);
		this.MAX_FPS = fps;
		this.WIDTH = width;
		this.HEIGHT = height;
		System.out.println("Created projectiles...");
		System.out.println("Created texture and music maps...");
		System.out.println("Creating enemy...");
		enemy=new Enemy(WIDTH,HEIGHT);
		System.out.println("Creating friendly...");
		friendly=new Friendly(WIDTH,HEIGHT);
		System.out.println("Creating key handler...");
		keyHandler=new KeyHandler(WIDTH,HEIGHT);
		castley=HEIGHT-7*HEIGHT/10;
		//setInvincible();
		//currentGameState=Gamestate.PLAYING;
		//enemy.variant=enemy.variants;
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
		addKeyListener(keyHandler);
		setFocusable(true);
		requestFocusInWindow();
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
			/*if(((friendly.x>enemy.x) && (friendly.x<enemy.x+enemy.width)) || ((friendly.x+friendly.width<enemy.x+enemy.width) && (friendly.x+friendly.width>enemy.x)))
				xcollide=true;*/
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
			if(enemy.variant == 6 || enemy.variant == 7 || enemy.variant == 8)
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
					else
						enemy.update(250,  300);
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
		System.out.println("Drawing loading screen...");
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(textures.loading, null, 0, 10);
		g.dispose();
		strategy.show();
		sleep(2000);
	}
	private void drawIntro(){
		System.out.println("Drawing introduction screen...");
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(textures.introduction, null, 0, 0);
		g.setColor(Color.black);
		g.setFont(textures.bigFont);
		g.drawString("Welcome to the world of shoot'em!",500,300);
		g.drawString("You play as Antonov, a magician. The ",500,450);
		g.drawString("goal of the game is simple: defeat the",500,600);
		g.drawString("enemies, advance stages, and survive.",500,750);
		g.drawString("Press ENTER to continue, or Q to abort.",500,900);
		g.dispose();
		strategy.show();
		continueOn=false;
		while(!continueOn){
			sleep(10);
			handleSmoothKeys();
			currentGameState=Gamestate.PAUSED;
		}
	}
	private boolean isFighting(){
		return !((stageNumber/2)*2 == stageNumber);
	}
	private boolean boolFromInt(int i){
		return i == 1;
	}
	private String makeContinueKey(){
		int[] keyArray = {stageNumber,enemy.variant,friendly.variant,friendly.x,friendly.y,enemy.x,enemy.y,friendly.health,enemy.health,enemy.maxHealth,friendly.width,friendly.height,enemy.width,enemy.height,intFromBool(enemy.animated),intFromBool(enemy.accessible),DigitalRightsManagement.magicNumber,versionNumber};
		String[] stringArray = new String[keyArray.length];
		for(int i=0;i<keyArray.length;i++)
			stringArray[i]=Integer.toString(keyArray[i]+DigitalRightsManagement.additionNumber);
		String keyDelim = String.join(",", stringArray);
		//System.out.println(DigitalRightsManagement.base64encode(keyDelim));
		return DigitalRightsManagement.encryptAES(DigitalRightsManagement.base64encode(keyDelim));
	}
	void loadContinueKey(){
		String key;
		try {
			key=(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch(Exception e){
			key="";
			e.printStackTrace();
		}
		//System.out.println("Decrypted value: "+DigitalRightsManagement.decryptAES(key));
		try{
			key=DigitalRightsManagement.base64decode(DigitalRightsManagement.decryptAES(key));
		}
			catch(Exception e){
			System.err.println("An attempt was made to load an invalid continue key...");
			System.err.println("Hacking detected! Exiting for safety...");
			System.exit(1);
		}
		String[] keyArray=key.split(",");
		if(keyArray.length != DigitalRightsManagement.continueKeySize){
			System.err.println("Invalid continue key size...");
			System.err.println("Hacking detected! Exiting for safety...");
			System.exit(1);
		}
		else{
			int[] keyIntArray=new int[keyArray.length];
			for(int i=0;i<keyArray.length;i++)
				keyIntArray[i]=Integer.parseInt(keyArray[i])-DigitalRightsManagement.additionNumber;
			if(keyIntArray[0]<0){
				System.err.println("Invalid stage number...Hacking detected!");
				System.err.println("Exiting for safety!");
				System.exit(1);
			} else
				stageNumber=keyIntArray[0];
			if(enteringStage && isFighting() && keyIntArray[1]>-1)
				keyIntArray[1]--;
			if(!enteringStage && keyIntArray[1]<0)
				keyIntArray[1]++;
			enemy.variant=keyIntArray[1];
			if(friendly.variant<friendly.variants+1)
				friendly.variant=keyIntArray[2];
			else{
				System.err.println("Invalid friendly variant! Exiting for safety...");
				System.exit(1);
			}
			friendly.x=keyIntArray[3];
			friendly.y=keyIntArray[4];
			enemy.x=keyIntArray[5];
			enemy.y=keyIntArray[6];
			friendly.health=keyIntArray[7];
			enemy.health=keyIntArray[8];
			enemy.maxHealth=keyIntArray[9];
			friendly.width=keyIntArray[10];
			friendly.height=keyIntArray[11];
			enemy.width=keyIntArray[12];
			enemy.height=keyIntArray[13];
			enemy.animated=boolFromInt(keyIntArray[14]);
			enemy.accessible=boolFromInt(keyIntArray[15]);
			if(keyIntArray[16] != DigitalRightsManagement.magicNumber){
				System.err.println("Invalid magic number! Exiting for safety...");
				System.exit(1);
			}
			if(keyIntArray[17] != versionNumber){
				System.err.println("This continue key was made by a different version of ShootEm (Version "+keyIntArray[17]+")! Exiting for safety...");
				System.exit(1);
			}
			friendly.attacking=false;
			enemy.attacking=false;
			enemy.animation=0;
			friendly.side=true;
			enemy.side=false;
			friendlyProjectile.launched=false;
			enemyProjectile.launched=false;
			friendlyProjectile.calibrateTo(0, 0);
			enemyProjectile.calibrateTo(0, 0);
			System.out.println("Loaded continue key...");
		}
	}
	private void drawPauseMenu(){
		System.out.println("Drawing pause menu...");
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(textures.pauseMenu, null, 0, 0);
		g.setFont(textures.bigFont);
		g.drawString("Continue key is on Clipboard. ", 200, 300);
		g.drawString("Stage: "+stageNumber, 200, 350);
		//g.setFont(textures.medFont);
		//g.drawString(makeContinueKey(), 700, 350);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(makeContinueKey()),null);
		g.setFont(textures.bigFont);
		g.drawString("Controls:", 200, 400);
		g.drawString("ENTER to resume/continue", 200, 450);
		g.drawString("F to show fps/errors", 200, 500);
		g.drawString("Q to quit", 200, 550);
		g.drawString("ESC to pause", 200, 600);
		g.drawString("SPACE to jump", 200, 650);
		g.drawString("A to move left", 200, 700);
		g.drawString("D to move right", 200, 750);
		g.drawString("S to attack", 200, 800);
		g.drawString("W to switch sides", 200, 850);
		g.drawString("Z to load continue key from Clipboard", 200, 900);
		g.dispose();
		strategy.show();
		continueOn=false;
		sleep(200);
		loadingKey=false;
		while(!continueOn){
			sleep(10);
			handleSmoothKeys();
			if(loadingKey){
				loadContinueKey();
				loadingKey=false;
				continueOn=true;
			}
		}
		currentGameState=Gamestate.PLAYING;
	}
	public int getScore(){
		return 100000*(stageNumber-1)/50;
	}
	private void drawEndMenu(){
		System.out.println("Drawing end menu with type "+currentGameState.toString()+"...");
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		if(currentGameState == Gamestate.DEFEAT){
			g.drawImage(textures.defeat,null,0,0);
			g.setColor(Color.black);
			g.setFont(textures.hugeFont);
			g.drawString("Press ENTER to continue.", WIDTH/2-WIDTH/4, HEIGHT/2);
			g.drawString("Press Q to quit.", WIDTH/2-WIDTH/4, HEIGHT/2+HEIGHT/10);
			g.setColor(Color.red);
			g.drawString("Score: "+getScore(),WIDTH/2-WIDTH/4, HEIGHT/2+HEIGHT/5);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""),null);
			sleep(100);
		}
		if(currentGameState == Gamestate.VICTORY){
			g.drawImage(textures.victory,null,0,0);
			enemy.makeInaccessible();
			enemy.health=1;
			enemy.side=false;
			enemy.side=false;
		}
		if(currentGameState == Gamestate.COMPLETE){
			g.drawImage(textures.complete, null, 0, 0);
			g.setColor(Color.red);
			g.setFont(textures.hugeFont);
			g.drawString("Score: "+getScore(), WIDTH-WIDTH/2, HEIGHT-HEIGHT/10);
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
			sleep(2500);
			friendlyProjectile.calibrateTo(0, 0);
			friendlyProjectile.launched=false;
			enemyProjectile.calibrateTo(0, 0);
			enemyProjectile.launched=false;
			friendly.attacking=false;
			friendly.x=WIDTH-friendly.width-30;
			friendly.y=HEIGHT-friendly.height;
			enemyDefeated=true;
			enemy.attacking=false;
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(makeContinueKey()),null);
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
			//friendly.direction=true;
			friendly.attacking=false;

			enemy.side=false;
			//enemy.direction=false;
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
		System.out.println("Drawing low framerate error...");
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
					} else if(isFighting() && enemy.variant > 3 && enemy.variant != 6){
						audio.music2=audio.load("music2");
						audio.play(audio.music2);
					} else if(isFighting() && enemy.variant == 6){
						audio.music3=audio.load("music3");
						audio.play(audio.music3);
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
				if(fps>60){
					System.err.println("Exiting due to framerate hacking...");
					System.exit(1);
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
	private void handleSmoothKeys(){
		keyHandler.handleSmoothKeys();
	}
	private void cleanup(){
		//dynamic thread sleep, only sleep the time we need to cap the framerate
		rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
		if(rest >0){
			try{ Thread.sleep(rest); }
			catch (InterruptedException e){ e.printStackTrace(); }
		}
	}

	public static void main(String[] args){
		Main game = new Main(1920, 1000, 50);
		game.run();
	}
	public int getHeight(){
		return HEIGHT;
	}
	public int getWidth(){
		return WIDTH;
	}

}