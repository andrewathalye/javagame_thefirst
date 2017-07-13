//import java.io.*;
//import java.net.URL;
import javax.sound.sampled.*;
//import javax.sound.midi.*;
public class AudioSource {
	AudioInputStream music0;
	AudioInputStream music1;
	AudioInputStream music2;
	Clip clip;
	public AudioSource(){
		music0 = load("music0");
		music1 = load("music1");
		music2 = load("music2");
		create();
	}
	public AudioInputStream load(String name){
		try{
			return AudioSystem.getAudioInputStream(this.getClass().getResource(Main.resourceDir+name+".wav"));
		} catch(Exception e){
			System.err.println(name);
			e.printStackTrace();
			return null;
		}
	}
	public void play(AudioInputStream in){
		try{
			clip.open(in);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.setFramePosition(0);
			clip.start();
		} catch(Exception e){

		}
	}
	public void create(){
		try{
			clip=AudioSystem.getClip();
		} catch(Exception e){

		}
	}
	public void close(){
		clip.stop();
	}
}
