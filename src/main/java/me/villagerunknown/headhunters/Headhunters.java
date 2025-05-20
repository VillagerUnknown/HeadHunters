package me.villagerunknown.headhunters;

import me.villagerunknown.headhunters.feature.giantZombieHeadBlockFeature;
import me.villagerunknown.headhunters.feature.headDropFeature;
import me.villagerunknown.headhunters.feature.headhunterVillagerFeature;
import me.villagerunknown.platform.Platform;
import me.villagerunknown.platform.PlatformMod;
import me.villagerunknown.platform.manager.featureManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Headhunters implements ModInitializer {
	
	public static PlatformMod<HeadhuntersConfigData> MOD = Platform.register( "headhunters", Headhunters.class, HeadhuntersConfigData.class );
	public static String MOD_ID = MOD.getModId();
	public static Logger LOGGER = MOD.getLogger();
	public static HeadhuntersConfigData CONFIG = MOD.getConfig();
	
	@Override
	public void onInitialize() {
		// # Register mod with Platform
		Platform.init_mod( MOD );
		
		// # Activate Features
		featureManager.addFeature( "head-drop", headDropFeature::execute );
		featureManager.addFeature( "giant-zombie-head-block", giantZombieHeadBlockFeature::execute );
		featureManager.addFeature( "headhunter-villager", headhunterVillagerFeature::execute );
		
		// # Load Features
		featureManager.loadFeatures();
	}
	
}
