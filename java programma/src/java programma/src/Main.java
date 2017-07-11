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
    private boolean isPaused=false;
    private boolean isAttacking=false;

    //timing variables
    private float dt;
    private long lastFrame;
    private long startFrame;
    private int fps;
    private boolean showfps = false;
    private boolean parity=false;
    private boolean jumping;
    private long jumpTime = 0;

    //cloud variables
    private int cloudx = 0;
    //private int cloudy;
    //friendly variables
    private int friendlyx = 30;
    private int friendlyHeight=500;
    private int friendlyWidth=350;
    private int friendlyy;
    private boolean friendlySide=true;
    private boolean previousFriendlyDirection=true;
    //castle variables
    private int castlex = 0;
    private int castley=300;
    private int stageNumber = 0;
    private boolean enteringStage = true;
    //enemy variables
    //textures and fonts
    private String friendlyTexture="friendly";
    private String enemyTexture="enemy";
    private String cloudTexture="clouds";
    private String castleTexture="castlebackground";
    private Font smallFont = new Font ("Courier New", 1, 18);
    private Font medFont = new Font ("Courier New", 1, 30);
    private Font bigFont = new Font ("Courier New", 1, 60);
    private Font hugeFont = new Font ("Courier New", 1, 90);
    
    public Main(int width, int height, int fps){
        super("shoot'em v4");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
        friendlyy= HEIGHT - friendlyHeight;;
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
        if(jumping){
        	friendlyy-=friendlyHeight/20;
        	if( (System.currentTimeMillis() - jumpTime ) > 499)
        		jumping=false;
        }
        if((friendlyy < (HEIGHT - friendlyHeight - 1)) && !jumping)
        	friendlyy+=friendlyHeight/62;
        if(friendlyx > 1700){
        	friendlyx=30;
        	stageNumber++;
        	enteringStage=true;
        }
        if(friendlyx < 30){
        	friendlyx=30;
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
        	friendlyTexture="friendly_small";
        	friendlyWidth=88;
        	friendlyHeight=125;
        	friendlyy= HEIGHT - friendlyHeight;;
        }
        g.drawImage(makeImage("resources/"+castleTexture+".png"), null, castlex, castley); 
        g.drawImage(makeImage("resources/"+cloudTexture+".png"), null, cloudx, 0);
        //draw friendly
        g.drawImage(makeImage("resources/"+friendlyTexture+".png_"+friendlySide),null,friendlyx,friendlyy);
        //draw stage welcome
        if(enteringStage){
        	g.setColor(Color.white);
        	g.setFont(bigFont);
        	g.drawString("STAGE "+stageNumber, 960, 300);
        }
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
    	g.drawImage(makeImage("resources/loading.png"), null, 0, 0);
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
    	isPaused=false;
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
        drawIntro();
        while(isRunning){
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
            if(isPaused)
            	drawPauseMenu();
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
    	isAttacking=true;
    break;
    case KeyEvent.VK_ESCAPE:
    	isPaused=true;
    break;
    case KeyEvent.VK_SPACE:
    	if(((System.currentTimeMillis()-jumpTime) > 2500) && (!jumping) || (jumpTime == 0)){
    	jumping=true;
    	jumpTime=System.currentTimeMillis();
    	}
    break;
    case KeyEvent.VK_LEFT:
    	friendlyx-=friendlyWidth/23;
    	if(previousFriendlyDirection){
    		friendlySide=!friendlySide;
    		previousFriendlyDirection=!previousFriendlyDirection;
    	}
    break;
    case KeyEvent.VK_RIGHT:
    	friendlyx+=friendlyWidth/23;
    	if(!previousFriendlyDirection){
    		friendlySide=!friendlySide;
    		previousFriendlyDirection=!previousFriendlyDirection;
    	}
    break;
    case KeyEvent.VK_Q:
    	System.exit(0);
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
