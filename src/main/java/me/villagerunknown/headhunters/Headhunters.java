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
	
	public static PlatformMod<HeadhuntersConfigData> MOD = null;
	public static String MOD_ID = null;
	public static Logger LOGGER = null;
	public static HeadhuntersConfigData CONFIG = null;
	
	@Override
	public void onInitialize() {
		// # Register Mod w/ Platform
		MOD = Platform.register( "headhunters", Headhunters.class, HeadhuntersConfigData.class );
		
		MOD_ID = MOD.getModId();
		LOGGER = MOD.getLogger();
		CONFIG = MOD.getConfig();
		
		// # Initialize Mod
		init();
	}
	
	private static void init() {
		Platform.init_mod( MOD );
		
		// # Activate Features
		featureManager.addFeature( "headDrop", headDropFeature::execute );
		featureManager.addFeature( "giantZombieHeadBlock", giantZombieHeadBlockFeature::execute );
		featureManager.addFeature( "headhunterVillager", headhunterVillagerFeature::execute );
		
		// # Load Features
		featureManager.loadFeatures();
	}
	
}
