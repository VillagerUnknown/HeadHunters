package me.villagerunknown.headhunters.feature;

import me.villagerunknown.headhunters.block.GiantHeadBlock;
import me.villagerunknown.platform.util.RegistryUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static me.villagerunknown.headhunters.Headhunters.MOD_ID;

public class giantZombieHeadBlockFeature {
	
	public static final String BLOCK_ID = "giant_zombie_head";
	public static Block BLOCK = null;
	
	public static void execute() {
		registerBlock();
	}
	
	private static void registerBlock() {
		Block block = new GiantHeadBlock(AbstractBlock.Settings.copy(Blocks.PLAYER_HEAD).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID,BLOCK_ID))));
		
		RegistryUtil.addItemToGroup( ItemGroups.FUNCTIONAL, RegistryUtil.registerItem( BLOCK_ID, new BlockItem( block, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID,BLOCK_ID))) ), MOD_ID ) );
		
		BLOCK = RegistryUtil.registerBlock( BLOCK_ID, block, MOD_ID );
	}
	
}
