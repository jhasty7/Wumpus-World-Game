package com.mygdx.wwgdx;

import com.badlogic.gdx.audio.Music;

public class MusicHandler {
	
	private static Music music;
	public MusicHandler(Music obMusic){
		music = obMusic;
		music.setLooping(true);
		music.setVolume(.3f);
	}
	
	public static void play(){
		music.play();
	}
	
	public static void stop(){
		music.stop();
	}
	
	public static void change(Music obMusic){
		music = obMusic;
		music.setLooping(true);
		music.setVolume(.2f);
	}
	
}