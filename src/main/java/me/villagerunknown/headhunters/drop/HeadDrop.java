package me.villagerunknown.headhunters.drop;

import net.minecraft.sound.SoundEvent;

public class HeadDrop {
	
	public float DROP_CHANCE;
	
	public SoundEvent NOTE_BLOCK_SOUND;
	
	public String TEXTURE;
	
	public HeadDrop(float chance, SoundEvent sound, String texture ) {
		DROP_CHANCE = chance;
		NOTE_BLOCK_SOUND = sound;
		TEXTURE = texture;
	}
	
}
