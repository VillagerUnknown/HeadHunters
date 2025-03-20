package me.villagerunknown.headhunters.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.villagerunknown.headhunters.HeadhuntersConfigData;

public class ModMenuIntegration implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(HeadhuntersConfigData.class, parent).get();
	}
	
}
