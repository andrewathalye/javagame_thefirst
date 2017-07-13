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
		music0 = load("resources/music0.wav");
		music1 = load("resources/music1.wav");
		music2 = load("resources/music2.wav");
		create();
	}
	private AudioInputStream load(String filename){
		try{
			return AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource(filename));
		} catch(Exception e){
			System.err.println(filename);
			e.printStackTrace();
			return null;
		}
	}
	public AudioInputStream reload(AudioInputStream in, String name){
		try{
			return AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("resources/"+name+".wav"));
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
