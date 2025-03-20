package me.villagerunknown.headhunters.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static me.villagerunknown.headhunters.Headhunters.MOD_ID;

public class headhunterVillagerFeature {
	
	private static final int DEFAULT_MAX_USES = 12;
	private static final int COMMON_MAX_USES = 16;
	private static final int RARE_MAX_USES = 3;
	private static final int NOVICE_SELL_XP = 1;
	private static final int NOVICE_BUY_XP = 2;
	private static final int APPRENTICE_SELL_XP = 5;
	private static final int APPRENTICE_BUY_XP = 10;
	private static final int JOURNEYMAN_SELL_XP = 10;
	private static final int JOURNEYMAN_BUY_XP = 20;
	private static final int EXPERT_SELL_XP = 15;
	private static final int EXPERT_BUY_XP = 30;
	private static final int MASTER_TRADE_XP = 30;
	private static final float LOW_PRICE_MULTIPLIER = 0.05F;
	private static final float HIGH_PRICE_MULTIPLIER = 0.2F;
	
	public static final String HEADHUNTER_STRING = "headhunter";
	public static final Identifier HEADHUNTER_IDENTIFIER = Identifier.of( MOD_ID, HEADHUNTER_STRING );
	
	public static PointOfInterestType HEADHUNTER_POI_TYPE = null;
	public static RegistryKey<PointOfInterestType> HEADHUNTER_POI_TYPE_REGISTRY_KEY = null;
	public static RegistryEntry<PointOfInterestType> HEADHUNTER_POI_TYPE_REGISTRY = null;
	public static VillagerProfession HEADHUNTER_PROFESSION = null;
	public static RegistryEntry<VillagerProfession> HEADHUNTER_PROFESSION_REGISTRY = null;

	public static void execute() {
		registerPointOfInterest();
		registerVillagerProfession();
		registerVillagerTrades();
	}
	
	private static void registerPointOfInterest() {
		ImmutableList<BlockState> blockStates = giantZombieHeadBlockFeature.BLOCK.getStateManager().getStates();
		
		HEADHUNTER_POI_TYPE = PointOfInterestHelper.register( HEADHUNTER_IDENTIFIER, 1, 1, blockStates );
		
		Registry.register( Registries.POINT_OF_INTEREST_TYPE, HEADHUNTER_IDENTIFIER, HEADHUNTER_POI_TYPE );
		
		HEADHUNTER_POI_TYPE_REGISTRY = Registries.POINT_OF_INTEREST_TYPE.getEntry( HEADHUNTER_POI_TYPE );
		HEADHUNTER_POI_TYPE_REGISTRY_KEY =  RegistryKey.of( RegistryKeys.POINT_OF_INTEREST_TYPE, HEADHUNTER_IDENTIFIER );
	}
	
	private static void registerVillagerProfession() {
		Predicate<RegistryEntry<PointOfInterestType>> predicate = (entry) -> entry.matchesKey( HEADHUNTER_POI_TYPE_REGISTRY_KEY );
		
		HEADHUNTER_PROFESSION = new VillagerProfession( HEADHUNTER_STRING, predicate, predicate, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_FLINTANDSTEEL_USE );
		
		Registry.register( Registries.VILLAGER_PROFESSION, HEADHUNTER_IDENTIFIER, HEADHUNTER_PROFESSION );
		
		HEADHUNTER_PROFESSION_REGISTRY = Registries.VILLAGER_PROFESSION.getEntry( HEADHUNTER_PROFESSION );
	}
	
	private static void registerVillagerTrades() {
		headDropFeature.MOB_DROPS.forEach((type, drop) -> {
			if( !drop.TEXTURE.isEmpty() && !headDropFeature.CREATIVE.contains( type ) ) {
				ItemStack headStack = headDropFeature.buildHeadStack( type, drop.TEXTURE, drop.NOTE_BLOCK_SOUND, drop.DROP_CHANCE );
				
				UnaryOperator<ComponentPredicate.Builder> operator = builder -> {
					for (ComponentType componentType : headStack.getComponents().getTypes()) {
						builder.add(componentType, headStack.getComponents().get(componentType));
					} // for
					
					return builder;
				};
				
				TradedItem tradeStack = new TradedItem( headStack.getItem(), 1 ).withComponents( operator );
				
				// Level 1
				if( drop.DROP_CHANCE == headDropFeature.COMMON_DROP_CHANCE ) {
					TradeOfferHelper.registerVillagerOffers( HEADHUNTER_PROFESSION, 1, f -> {
						f.add( (entity, random) -> new TradeOffer(
								tradeStack,
								new ItemStack( Items.EMERALD, 4 ),
								DEFAULT_MAX_USES,
								NOVICE_BUY_XP,
								LOW_PRICE_MULTIPLIER
						));
						
						f.add( (entity, random) -> new TradeOffer(
								new TradedItem( Items.EMERALD, 4 ),
								headStack,
								DEFAULT_MAX_USES,
								NOVICE_SELL_XP,
								LOW_PRICE_MULTIPLIER
						));
					});
				} // if
				
				// Level 2
				if( drop.DROP_CHANCE == headDropFeature.UNCOMMON_DROP_CHANCE ) {
					TradeOfferHelper.registerVillagerOffers( HEADHUNTER_PROFESSION, 2, f -> {
						f.add( (entity, random) -> new TradeOffer(
								tradeStack,
								new ItemStack( Items.EMERALD, 8 ),
								DEFAULT_MAX_USES,
								APPRENTICE_BUY_XP,
								LOW_PRICE_MULTIPLIER
						));
						
						f.add( (entity, random) -> new TradeOffer(
								new TradedItem( Items.EMERALD, 8 ),
								headStack,
								DEFAULT_MAX_USES,
								APPRENTICE_SELL_XP,
								LOW_PRICE_MULTIPLIER
						));
					});
				} // if
				
				// Level 3
				if( drop.DROP_CHANCE == headDropFeature.RARE_DROP_CHANCE ) {
					TradeOfferHelper.registerVillagerOffers( HEADHUNTER_PROFESSION, 3, f -> {
						f.add( (entity, random) -> new TradeOffer(
								tradeStack,
								new ItemStack( Items.EMERALD, 16 ),
								COMMON_MAX_USES,
								JOURNEYMAN_BUY_XP,
								LOW_PRICE_MULTIPLIER
						));
						
						f.add( (entity, random) -> new TradeOffer(
								new TradedItem( Items.EMERALD, 16 ),
								headStack,
								COMMON_MAX_USES,
								JOURNEYMAN_SELL_XP,
								LOW_PRICE_MULTIPLIER
						));
					});
				} // if
				
				if( drop.DROP_CHANCE == headDropFeature.EPIC_DROP_CHANCE ) {
					TradeOfferHelper.registerVillagerOffers( HEADHUNTER_PROFESSION, 4, f -> {
						f.add( (entity, random) -> new TradeOffer(
								tradeStack,
								new ItemStack( Items.EMERALD, 32 ),
								COMMON_MAX_USES,
								EXPERT_BUY_XP,
								HIGH_PRICE_MULTIPLIER
						));
						
						f.add( (entity, random) -> new TradeOffer(
								new TradedItem( Items.EMERALD, 32 ),
								headStack,
								COMMON_MAX_USES,
								EXPERT_SELL_XP,
								HIGH_PRICE_MULTIPLIER
						));
					});
				} // if
				
				if( drop.DROP_CHANCE == headDropFeature.LEGENDARY_DROP_CHANCE || drop.DROP_CHANCE == headDropFeature.ALWAYS_DROP_CHANCE ) {
					TradeOfferHelper.registerVillagerOffers( HEADHUNTER_PROFESSION, 5, f -> {
						f.add( (entity, random) -> new TradeOffer(
								tradeStack,
								new ItemStack( Items.EMERALD, 64 ),
								RARE_MAX_USES,
								MASTER_TRADE_XP,
								HIGH_PRICE_MULTIPLIER
						));
						
						f.add( (entity, random) -> new TradeOffer(
								new TradedItem( Items.EMERALD, 64 ),
								headStack,
								RARE_MAX_USES,
								MASTER_TRADE_XP,
								HIGH_PRICE_MULTIPLIER
						));
					});
				} // if
				
			} // if
		});
	}
	
}
