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

    //timing variables
    private float dt;
    private long lastFrame;
    private long startFrame;
    private int fps;
    private boolean parity=false;

    //cloud variables
    private int cloudx = 0;
    //private int cloudy;


    public Main(int width, int height, int fps){
        super("JFrame Demo");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
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
    }

    private void draw(){
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //clear screen
        g.setColor(Color.white);
        g.fillRect(0,0,WIDTH, HEIGHT);

        
        //draw background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, 1920, 500);
        g.setColor(Color.gray);
        g.fillRect(0, 500,1920,300);
        g.setColor(Color.green);
        g.fillRect(0, 800, 1920, 380);
        g.drawImage(makeImage("resources/clouds.png"), null, cloudx, 0);
        //draw fps
        g.setColor(Color.red);
        g.drawString(Long.toString(fps), 10, 40);
        //release resources, show the buffer
        g.dispose();
        strategy.show();
    }
    BufferedImage makeImage(String path){
    	try{
    return ImageIO.read(new File(getClass().getResource(path).toURI()));
    	} catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }

    public void run(){
        init();

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
            //dynamic thread sleep, only sleep the time we need to cap the framerate
            rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if(rest >0){
                try{ Thread.sleep(rest); }
                catch (InterruptedException e){ e.printStackTrace(); }
            }
        }

    }

    public void keyPressed(KeyEvent keyEvent){
    switch(keyEvent.getKeyCode()){
    
    }
    }
    public void keyTyped(KeyEvent keyEvent){
    	
    }
    public void keyReleased(KeyEvent keyEvent)
    {
    	
    }

    public static void main(String[] args){
        Main game = new Main(1920, 1080, 50);
        game.run();
    }

}
