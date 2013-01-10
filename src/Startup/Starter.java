package Startup;

import java.io.DataInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import Audio.Capture;
import Audio.Playback;

public class Starter {
	
	public static final boolean verbose = true;
	public static final String error_non_verbose_msg = "Something bad happened [activate verbose-mode to get more information]";
	
	public static void main(String[] args) {
		System.out.println("Welcome to");
		
		System.out.println(" ____  _                    ");
		System.out.println("/ ___|| | ____ ___   ____ _ ");
		System.out.println("\\___ \\| |/ / _` \\ \\ / / _` |");
		System.out.println(" ___) |   < (_| |\\ V / (_| |");
		System.out.println("|____/|_|\\_\\__,_| \\_/ \\__,_|");
		System.out.println();
		                             
		
		if(args.length != 3) {
			System.out.println("Arguments: <ip to connect to> <port to connect to> <port to listen on>");
			System.exit(-1);
		}
		
		
		Playback playb = new Playback(Integer.parseInt(args[2]));
		playb.start();
				
		Capture cap = new Capture(args[0], Integer.parseInt(args[1]));
		cap.start();
		
		waitForEnter("end transmission");
		
		cap.endCapture();
		playb.endPlayback();
		
		System.out.println("End");
	}
	
	
	@SuppressWarnings("unused")
	private static void waitForEnter(String why) {
		DataInputStream in = new DataInputStream(System.in);
		System.out.println();
		System.out.println("Press enter to continue ["+why+"]");
		byte b;
		
		try {
			b = in.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static AudioFormat getFormat() {
		float sampleRate = 8000;
	    int sampleSizeInBits = 8;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = true;
	    
	    return new AudioFormat(
	    		sampleRate, 
	    		sampleSizeInBits, 
	    		channels, 
	    		signed,
	    		bigEndian
	    	);
	}
}
