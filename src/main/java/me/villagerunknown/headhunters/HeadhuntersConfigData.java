package me.villagerunknown.headhunters;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "villagerunknown-headhunters")
public class HeadhuntersConfigData implements me.shedaniel.autoconfig.ConfigData {
	
	/**
	 * General
	 */
	
	@ConfigEntry.Category("General")
	public boolean addRarityLoreToHeads = false;
	
	/**
	 * Drop Chances
	 */
	
	@ConfigEntry.Category("DropChances")
	public float lootingBonusPerLevel = 0.1F;
	
	@ConfigEntry.Category("DropChances")
	public float playerHeadDropChance = 1F;
	
	@ConfigEntry.Category("DropChances")
	public float commonDropChance = 0.33F;
	
	@ConfigEntry.Category("DropChances")
	public float uncommonDropChance = 0.2F;
	
	@ConfigEntry.Category("DropChances")
	public float rareDropChance = 0.1F;
	
	@ConfigEntry.Category("DropChances")
	public float epicDropChance = 0.05F;
	
	@ConfigEntry.Category("DropChances")
	public float legendaryDropChance = 0.005F;
	
}
