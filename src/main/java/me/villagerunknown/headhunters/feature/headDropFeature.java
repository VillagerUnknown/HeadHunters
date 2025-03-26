package me.villagerunknown.headhunters.feature;

import me.villagerunknown.headhunters.Headhunters;
import me.villagerunknown.platform.util.HeadUtil;
import me.villagerunknown.platform.util.MathUtil;
import me.villagerunknown.platform.util.StringUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class headDropFeature {
	
	public static final float COMMON_DROP_CHANCE = Headhunters.CONFIG.commonDropChance;
	public static final float UNCOMMON_DROP_CHANCE = Headhunters.CONFIG.uncommonDropChance;
	public static final float RARE_DROP_CHANCE = Headhunters.CONFIG.rareDropChance;
	public static final float EPIC_DROP_CHANCE = Headhunters.CONFIG.epicDropChance;
	public static final float LEGENDARY_DROP_CHANCE = Headhunters.CONFIG.legendaryDropChance;
	public static final float ALWAYS_DROP_CHANCE = 1F;
	
	public static Map<String, Float> HEAD_DROPS = new HashMap<>() {{
		put( "allay", EPIC_DROP_CHANCE );
		put( "armadillo", UNCOMMON_DROP_CHANCE );
		
		put( "axolotl", RARE_DROP_CHANCE );
		put( "lucy_axolotl", RARE_DROP_CHANCE );
		put( "wild_axolotl", RARE_DROP_CHANCE );
		put( "gold_axolotl", RARE_DROP_CHANCE );
		put( "cyan_axolotl", RARE_DROP_CHANCE );
		put( "blue_axolotl", ALWAYS_DROP_CHANCE );
		
		put( "bat", UNCOMMON_DROP_CHANCE );
		
		put( "bee", UNCOMMON_DROP_CHANCE );
		put( "pollinated_bee", RARE_DROP_CHANCE );
		put( "angry_bee", RARE_DROP_CHANCE );
		put( "angry_pollinated_bee", RARE_DROP_CHANCE );
		
		put( "blaze", LEGENDARY_DROP_CHANCE );
		put( "bogged", EPIC_DROP_CHANCE );
		put( "breeze", EPIC_DROP_CHANCE );
		put( "camel", RARE_DROP_CHANCE );
		
		put( "cat", COMMON_DROP_CHANCE );
		put( "tabby_cat", COMMON_DROP_CHANCE );
		put( "tuxedo_cat", COMMON_DROP_CHANCE );
		put( "ginger_cat", COMMON_DROP_CHANCE );
		put( "siamese_cat", COMMON_DROP_CHANCE );
		put( "british_shorthair_cat", COMMON_DROP_CHANCE );
		put( "calico_cat", COMMON_DROP_CHANCE );
		put( "persian_cat", COMMON_DROP_CHANCE );
		put( "ragdoll_cat", COMMON_DROP_CHANCE );
		put( "white_cat", COMMON_DROP_CHANCE );
		put( "jellie_cat", COMMON_DROP_CHANCE );
		put( "black_cat", COMMON_DROP_CHANCE );
		
		put( "cave_spider", RARE_DROP_CHANCE );
		put( "chicken", COMMON_DROP_CHANCE );
		put( "cod", COMMON_DROP_CHANCE );
		put( "cow", COMMON_DROP_CHANCE );
		
		put( "creeper", LEGENDARY_DROP_CHANCE );
		put( "charged_creeper", LEGENDARY_DROP_CHANCE );
		
		put( "dolphin", UNCOMMON_DROP_CHANCE );
		put( "donkey", UNCOMMON_DROP_CHANCE );
		put( "dragon", ALWAYS_DROP_CHANCE );
		put( "drowned", RARE_DROP_CHANCE );
		
		put( "elder_guardian", ALWAYS_DROP_CHANCE );
		put( "enderman", RARE_DROP_CHANCE );
		put( "endermite", RARE_DROP_CHANCE );
		put( "evoker", EPIC_DROP_CHANCE );
		
		put( "fox", RARE_DROP_CHANCE );
		put( "snow_fox", RARE_DROP_CHANCE );
		put( "red_fox", RARE_DROP_CHANCE );
		
		put( "frog", UNCOMMON_DROP_CHANCE );
		put( "cold_frog", UNCOMMON_DROP_CHANCE );
		put( "temperate_frog", UNCOMMON_DROP_CHANCE );
		put( "warm_frog", UNCOMMON_DROP_CHANCE );
		
		put( "ghast", EPIC_DROP_CHANCE );
		put( "glow_squid", RARE_DROP_CHANCE );
		
		put( "goat", RARE_DROP_CHANCE );
		put( "screaming_goat", EPIC_DROP_CHANCE );
		
		put( "guardian", RARE_DROP_CHANCE );
		put( "hoglin", EPIC_DROP_CHANCE );
		
		put( "horse", COMMON_DROP_CHANCE );
		put( "white_horse", COMMON_DROP_CHANCE );
		put( "creamy_horse", COMMON_DROP_CHANCE );
		put( "chestnut_horse", COMMON_DROP_CHANCE );
		put( "brown_horse", COMMON_DROP_CHANCE );
		put( "black_horse", COMMON_DROP_CHANCE );
		put( "gray_horse", COMMON_DROP_CHANCE );
		put( "dark_brown_horse", COMMON_DROP_CHANCE );
		
		put( "husk", RARE_DROP_CHANCE );
		put( "illusioner", LEGENDARY_DROP_CHANCE );
		put( "iron_golem", RARE_DROP_CHANCE );
		
		put( "llama", UNCOMMON_DROP_CHANCE );
		put( "creamy_llama", UNCOMMON_DROP_CHANCE );
		put( "white_llama", UNCOMMON_DROP_CHANCE );
		put( "brown_llama", UNCOMMON_DROP_CHANCE );
		put( "gray_llama", UNCOMMON_DROP_CHANCE );
		
		put( "magma_cube", EPIC_DROP_CHANCE );
		
		put( "mooshroom", RARE_DROP_CHANCE );
		put( "brown_mooshroom", RARE_DROP_CHANCE );
		put( "red_mooshroom", RARE_DROP_CHANCE );
		
		put( "mule", UNCOMMON_DROP_CHANCE );
		put( "ocelot", RARE_DROP_CHANCE );
		
		put( "panda", RARE_DROP_CHANCE );
		put( "aggressive_panda", RARE_DROP_CHANCE );
		put( "lazy_panda", RARE_DROP_CHANCE );
		put( "playful_panda", RARE_DROP_CHANCE );
		put( "worried_panda", RARE_DROP_CHANCE );
		put( "brown_panda", RARE_DROP_CHANCE );
		put( "weak_panda", RARE_DROP_CHANCE );
		
		put( "parrot", UNCOMMON_DROP_CHANCE );
		put( "red_parrot", UNCOMMON_DROP_CHANCE );
		put( "blue_parrot", UNCOMMON_DROP_CHANCE );
		put( "green_parrot", UNCOMMON_DROP_CHANCE );
		put( "light_blue_parrot", UNCOMMON_DROP_CHANCE );
		put( "yellow_blue_parrot", UNCOMMON_DROP_CHANCE );
		put( "gray_parrot", UNCOMMON_DROP_CHANCE );
		
		put( "phantom", RARE_DROP_CHANCE );
		put( "pig", UNCOMMON_DROP_CHANCE );
		put( "piglin", LEGENDARY_DROP_CHANCE );
		put( "piglin_brute", LEGENDARY_DROP_CHANCE );
		put( "pillager", RARE_DROP_CHANCE );
		put( "polar_bear", RARE_DROP_CHANCE );
		put( "pufferfish", RARE_DROP_CHANCE );
		
		put( "rabbit", UNCOMMON_DROP_CHANCE );
		put( "toast_rabbit", ALWAYS_DROP_CHANCE );
		put( "brown_rabbit", UNCOMMON_DROP_CHANCE );
		put( "white_rabbit", UNCOMMON_DROP_CHANCE );
		put( "black_rabbit", UNCOMMON_DROP_CHANCE );
		put( "black_and_white_rabbit", UNCOMMON_DROP_CHANCE );
		put( "gold_rabbit", UNCOMMON_DROP_CHANCE );
		put( "salt_and_pepper_rabbit", UNCOMMON_DROP_CHANCE );
		put( "killer_rabbit", ALWAYS_DROP_CHANCE );
		
		put( "ravager", EPIC_DROP_CHANCE );
		put( "salmon", UNCOMMON_DROP_CHANCE );
		put( "shulker", LEGENDARY_DROP_CHANCE );
		
		put( "sheep", COMMON_DROP_CHANCE );
		put( "black_sheep", COMMON_DROP_CHANCE );
		put( "blue_sheep", COMMON_DROP_CHANCE );
		put( "brown_sheep", COMMON_DROP_CHANCE );
		put( "cyan_sheep", COMMON_DROP_CHANCE );
		put( "gray_sheep", COMMON_DROP_CHANCE );
		put( "green_sheep", COMMON_DROP_CHANCE );
		put( "jeb_sheep", COMMON_DROP_CHANCE );
		put( "light_blue_sheep", COMMON_DROP_CHANCE );
		put( "light_gray_sheep", COMMON_DROP_CHANCE );
		put( "lime_sheep", COMMON_DROP_CHANCE );
		put( "magenta_sheep", COMMON_DROP_CHANCE );
		put( "orange_sheep", COMMON_DROP_CHANCE );
		put( "pink_sheep", COMMON_DROP_CHANCE );
		put( "purple_sheep", COMMON_DROP_CHANCE );
		put( "red_sheep", COMMON_DROP_CHANCE );
		put( "white_sheep", COMMON_DROP_CHANCE );
		put( "yellow_sheep", COMMON_DROP_CHANCE );
		
		put( "silverfish", RARE_DROP_CHANCE );
		put( "skeleton", EPIC_DROP_CHANCE );
		put( "skeleton_horse", LEGENDARY_DROP_CHANCE );
		put( "slime", RARE_DROP_CHANCE );
		put( "sniffer", LEGENDARY_DROP_CHANCE );
		put( "snow_golem", RARE_DROP_CHANCE );
		put( "spider", RARE_DROP_CHANCE );
		put( "squid", UNCOMMON_DROP_CHANCE );
		put( "stray", EPIC_DROP_CHANCE );
		
		put( "strider", EPIC_DROP_CHANCE );
		put( "cold_strider", EPIC_DROP_CHANCE );
		
		put( "tadpole", RARE_DROP_CHANCE );
		
		put( "trader_llama", UNCOMMON_DROP_CHANCE );
		put( "creamy_trader_llama", UNCOMMON_DROP_CHANCE );
		put( "white_trader_llama", UNCOMMON_DROP_CHANCE );
		put( "brown_trader_llama", UNCOMMON_DROP_CHANCE );
		put( "gray_trader_llama", UNCOMMON_DROP_CHANCE );
		
		put( "tropical_fish", RARE_DROP_CHANCE );
		put( "turtle", RARE_DROP_CHANCE );
		
		put( "vex", EPIC_DROP_CHANCE );
		put( "angry_vex", EPIC_DROP_CHANCE );
		
		put( "villager", COMMON_DROP_CHANCE );
		put( "armorer_villager", COMMON_DROP_CHANCE );
		put( "butcher_villager", COMMON_DROP_CHANCE );
		put( "cartographer_villager", COMMON_DROP_CHANCE );
		put( "cleric_villager", COMMON_DROP_CHANCE );
		put( "farmer_villager", COMMON_DROP_CHANCE );
		put( "fisherman_villager", COMMON_DROP_CHANCE );
		put( "fletcher_villager", COMMON_DROP_CHANCE );
		put( "leatherworker_villager", COMMON_DROP_CHANCE );
		put( "librarian_villager", COMMON_DROP_CHANCE );
		put( "mason_villager", COMMON_DROP_CHANCE );
		put( "nitwit_villager", COMMON_DROP_CHANCE );
		put( "shepherd_villager", COMMON_DROP_CHANCE );
		put( "toolsmith_villager", COMMON_DROP_CHANCE );
		put( "weaponsmith_villager", COMMON_DROP_CHANCE );
		
		put( "vindicator", EPIC_DROP_CHANCE );
		put( "wandering_trader", COMMON_DROP_CHANCE );
		put( "warden", ALWAYS_DROP_CHANCE );
		put( "witch", RARE_DROP_CHANCE );
		
		put( "wither", ALWAYS_DROP_CHANCE );
		put( "wither_projectile", ALWAYS_DROP_CHANCE );
		put( "blue_wither_projectile", ALWAYS_DROP_CHANCE );
		
		put( "wolf", UNCOMMON_DROP_CHANCE );
		put( "ashen_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_ashen_wolf", UNCOMMON_DROP_CHANCE );
		put( "black_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_black_wolf", UNCOMMON_DROP_CHANCE );
		put( "chestnut_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_chestnut_wolf", UNCOMMON_DROP_CHANCE );
		put( "pale_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_pale_wolf", UNCOMMON_DROP_CHANCE );
		put( "rusty_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_rusty_wolf", UNCOMMON_DROP_CHANCE );
		put( "snowy_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_snowy_wolf", UNCOMMON_DROP_CHANCE );
		put( "spotty_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_spotty_wolf", UNCOMMON_DROP_CHANCE);
		put( "striped_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_striped_wolf", UNCOMMON_DROP_CHANCE );
		put( "woods_wolf", UNCOMMON_DROP_CHANCE );
		put( "angry_woods_wolf", UNCOMMON_DROP_CHANCE );
		
		put( "zoglin", LEGENDARY_DROP_CHANCE );
		put( "zombie", RARE_DROP_CHANCE );
		put( "zombie_horse", ALWAYS_DROP_CHANCE );
		
		put( "zombie_villager", RARE_DROP_CHANCE );
		put( "zombie_armorer", RARE_DROP_CHANCE );
		put( "zombie_butcher", RARE_DROP_CHANCE );
		put( "zombie_cartographer", RARE_DROP_CHANCE );
		put( "zombie_cleric", RARE_DROP_CHANCE );
		put( "zombie_farmer", RARE_DROP_CHANCE );
		put( "zombie_fisherman", RARE_DROP_CHANCE );
		put( "zombie_fletcher", RARE_DROP_CHANCE );
		put( "zombie_leatherworker", RARE_DROP_CHANCE );
		put( "zombie_librarian", RARE_DROP_CHANCE );
		put( "zombie_mason", RARE_DROP_CHANCE );
		put( "zombie_nitwit", RARE_DROP_CHANCE );
		put( "zombie_shepherd", RARE_DROP_CHANCE );
		put( "zombie_toolsmith", RARE_DROP_CHANCE );
		put( "zombie_weaponsmith", RARE_DROP_CHANCE );
		
		put( "zombified_piglin", RARE_DROP_CHANCE );
	}};
	
	public static List<String> CREATIVE = List.of(
			"illusioner",
			"zombie_horse"
	);
	
	public static void execute() {
		registerHeadDrop();
	}
	
	private static void registerHeadDrop() {
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
			if( entity.isPlayer() && MathUtil.hasChance( Headhunters.CONFIG.playerHeadDropChance ) ) {
				if( null != damageSource.getAttacker() ) {
					if (damageSource.getAttacker().isPlayer()) {
						entity.dropStack( HeadUtil.getPlayerHeadStack((PlayerEntity) entity) );
					} // if
				} // if
			} else if( HEAD_DROPS.containsKey( entity.getType().getUntranslatedName() ) ) {
				String id = entity.getType().getUntranslatedName();
				float dropChance = HEAD_DROPS.get( id );
				float modifiedDropChance = dropChance;

				if( null != damageSource.getAttacker() ) {
					if( damageSource.getAttacker().isPlayer() ) {
						World world = entity.getWorld();
						Entity source = damageSource.getSource();

						if( null != source ) {
							ItemStack weapon = damageSource.getWeaponStack();

							if( null != weapon ) {
								ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(weapon);
								DynamicRegistryManager drm = world.getRegistryManager();

								if( null != drm ) {
									Registry<Enchantment> registry = drm.get(RegistryKeys.ENCHANTMENT);
									Enchantment lootingEnchantment = registry.get(Enchantments.LOOTING);
									RegistryEntry<Enchantment> lootingEntry = registry.getEntry(lootingEnchantment);

									int enchantmentLevel = enchantments.getLevel( lootingEntry );

									if( enchantmentLevel > 0 ) {
										modifiedDropChance = modifiedDropChance * (1 + ( Headhunters.CONFIG.lootingBonusPerLevel * enchantmentLevel ));
									} // if
								} // if
							} // if

							if (MathUtil.hasChance(modifiedDropChance)) {
								HeadUtil.Head head = HeadUtil.getHead( id );
								ItemStack headStack = HeadUtil.buildHeadStack(entity, head.TEXTURE, head.NOTE_BLOCK_SOUND);
								
								if (!headStack.isEmpty()) {
									addRarityToStack( headStack, dropChance );
									
									entity.dropStack(headStack);
								} // if
							} // if
						} // if
					} // if
				} // if
			} // if, else if
			
			return true;
		});
	}
	
	public static void addRarityToStack(ItemStack headStack, float dropChance ) {
		Rarity rarity = getRarity(dropChance);
		headStack.set(DataComponentTypes.RARITY, rarity);
		
		if( Headhunters.CONFIG.addRarityLoreToHeads) {
			String rarityString = Rarity.COMMON.name();
			
			if (UNCOMMON_DROP_CHANCE == dropChance) {
				rarityString = Rarity.UNCOMMON.name();
			} else if (RARE_DROP_CHANCE == dropChance) {
				rarityString = Rarity.RARE.name();
			} else if (EPIC_DROP_CHANCE == dropChance) {
				rarityString = Rarity.EPIC.name();
			} else if (LEGENDARY_DROP_CHANCE == dropChance || ALWAYS_DROP_CHANCE == dropChance) {
				rarityString = "legendary";
			} // if, else if
			
			headStack.set(DataComponentTypes.LORE, new LoreComponent(List.of(Text.of(StringUtil.capitalizeAll(rarityString.toLowerCase())))));
		} // if
	}
	
	public static @NotNull Rarity getRarity(float dropChance) {
		Rarity rarity = Rarity.COMMON;
		
		if( UNCOMMON_DROP_CHANCE == dropChance) {
			rarity = Rarity.UNCOMMON;
		} else if( RARE_DROP_CHANCE == dropChance) {
			rarity = Rarity.RARE;
		} else if( EPIC_DROP_CHANCE == dropChance || LEGENDARY_DROP_CHANCE == dropChance || ALWAYS_DROP_CHANCE == dropChance ) {
			rarity = Rarity.EPIC;
		} // if, else if
		
		return rarity;
	}
	
}
