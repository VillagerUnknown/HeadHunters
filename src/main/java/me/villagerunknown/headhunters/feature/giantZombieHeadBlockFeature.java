package me.villagerunknown.headhunters.feature;

import me.villagerunknown.headhunters.block.GiantHeadBlock;
import me.villagerunknown.platform.util.RegistryUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static me.villagerunknown.headhunters.Headhunters.MOD_ID;

public class giantZombieHeadBlockFeature {
	
	public static final String BLOCK_ID = "giant_zombie_head";
	public static Block BLOCK = null;
	
	public static void execute() {
		registerBlock();
	}
	
	private static void registerBlock() {
		Block block = new GiantHeadBlock(AbstractBlock.Settings.copy(Blocks.PLAYER_HEAD));
		
		RegistryUtil.addItemToGroup( ItemGroups.FUNCTIONAL, RegistryUtil.registerItem( BLOCK_ID, new BlockItem( block, new Item.Settings() ), MOD_ID ) );
		
		BLOCK = RegistryUtil.registerBlock( BLOCK_ID, block, MOD_ID );
	}
	
}
