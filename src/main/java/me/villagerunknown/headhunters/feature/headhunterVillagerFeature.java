package me.villagerunknown.headhunters.feature;

import me.villagerunknown.platform.util.HeadUtil;
import me.villagerunknown.platform.util.VillagerUtil;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradedItem;

import java.util.function.UnaryOperator;

import static me.villagerunknown.headhunters.Headhunters.MOD_ID;

public class headhunterVillagerFeature {
	
	public static final String HEADHUNTER_STRING = "head_hunter";
	public static final Identifier HEADHUNTER_IDENTIFIER = Identifier.of( MOD_ID, HEADHUNTER_STRING );
	
	public static VillagerUtil.CustomVillager HEADHUNTER = new VillagerUtil.CustomVillager( HEADHUNTER_IDENTIFIER, giantZombieHeadBlockFeature.BLOCK.getStateManager().getStates(), HEADHUNTER_STRING, SoundEvents.ENTITY_ZOMBIE_AMBIENT );

	public static void execute() {
		registerVillagerTrades();
	}
	
	private static void registerVillagerTrades() {
		headDropFeature.HEAD_DROPS.forEach((type, dropChance) -> {
			HeadUtil.Head head = HeadUtil.getHead( type );
			
			if( !head.TEXTURE.isEmpty() && !headDropFeature.CREATIVE.contains( type ) ) {
				ItemStack headStack = HeadUtil.buildHeadStack( type, head.TEXTURE, head.NOTE_BLOCK_SOUND );
				
				headDropFeature.addRarityToStack( headStack, dropChance );
				
				UnaryOperator<ComponentPredicate.Builder> operator = builder -> {
					for (ComponentType componentType : headStack.getComponents().getTypes()) {
						builder.add(componentType, headStack.getComponents().get(componentType));
					} // for
					
					return builder;
				};
				
				TradedItem tradeStack = new TradedItem( headStack.getItem(), 1 ).withComponents( operator );
				
				// Level 1
				if( dropChance == headDropFeature.COMMON_DROP_CHANCE ) {
					registerLevelTrades( 1, tradeStack, headStack, 4, 4 );
				} // if
				
				// Level 2
				else if( dropChance == headDropFeature.UNCOMMON_DROP_CHANCE ) {
					registerLevelTrades( 2, tradeStack, headStack, 6, 8 );
				}
				
				// Level 3
				else if( dropChance == headDropFeature.RARE_DROP_CHANCE ) {
					registerLevelTrades( 3, tradeStack, headStack, 12, 16 );
				}
				
				// Level 4
				else if( dropChance == headDropFeature.EPIC_DROP_CHANCE ) {
					registerLevelTrades( 4, tradeStack, headStack, 24, 32 );
				}
				
				// Level 5
				else if( dropChance == headDropFeature.LEGENDARY_DROP_CHANCE || dropChance == headDropFeature.ALWAYS_DROP_CHANCE ) {
					registerLevelTrades( 5, tradeStack, headStack, 48, 64 );
				} // if, else if
				
			} // if
		});
	}
	
	public static void registerLevelTrades( int level, TradedItem tradeStack, ItemStack headStack, int buyValue, int sellCost ) {
		TradeOfferHelper.registerVillagerOffers( HEADHUNTER.PROFESSION, level, f -> {
			f.add( ( entity, random ) -> VillagerUtil.buyTradeOffer( level, tradeStack, new ItemStack( Items.EMERALD, buyValue ) ) );
			f.add( ( entity, random ) -> VillagerUtil.sellTradeOffer( level, new TradedItem( Items.EMERALD, sellCost ), headStack ) );
		});
	}
	
}
