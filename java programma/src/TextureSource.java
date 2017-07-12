import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
public class TextureSource {
	BufferedImage[][] friendly = new BufferedImage[2][2];
	BufferedImage[][] enemy = new BufferedImage[3][2];
	BufferedImage[] projectile = new BufferedImage[2];
	BufferedImage defeat;
	BufferedImage victory;
	BufferedImage clouds;
	BufferedImage castle;
	BufferedImage pausemenu;
	BufferedImage introduction;
	BufferedImage loading;
	Font smallFont = new Font ("Courier New", 1, 18);
	Font medFont = new Font ("Courier New", 1, 30);
	Font bigFont = new Font ("Courier New", 1, 60);
	Font hugeFont = new Font ("Courier New", 1, 90);
	String prefix="resources/";
	public TextureSource(){
		//Load complex images
		for(int i=0;i<2;i++){
			for(int i2=0;i2<2;i2++)
				friendly[i][i2]=makeImage(prefix+"friendly"+i+".png_"+boolFromInt(i2));
		}
		for(int i=0;i<3;i++){
			for(int i2=0;i2<2;i2++)
				enemy[i][i2]=makeImage(prefix+"enemy"+i+".png_"+boolFromInt(i2));
		}
		projectile[0]=makeImage(prefix+"projectile.png_false");
		projectile[1]=makeImage(prefix+"projectile.png_true");
		//Load simple images
		defeat=makeImage(prefix+"defeat.png");
		victory=makeImage(prefix+"victory.png");
		clouds=makeImage(prefix+"clouds.png");
		castle=makeImage(prefix+"castle.png");
		pausemenu=makeImage(prefix+"pausemenu.png");
		introduction=makeImage(prefix+"introduction.png");
		loading=makeImage(prefix+"loading.png");
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
	private boolean boolFromInt(int i){
		return i == 1;
	}
}
