package me.villagerunknown.headhunters.feature;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ProfileResult;
import me.villagerunknown.headhunters.Headhunters;
import me.villagerunknown.headhunters.drop.HeadDrop;
import me.villagerunknown.platform.util.MathUtil;
import me.villagerunknown.platform.util.StringUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class headDropFeature {
	
	public static final float COMMON_DROP_CHANCE = Headhunters.CONFIG.commonDropChance;
	public static final float UNCOMMON_DROP_CHANCE = Headhunters.CONFIG.uncommonDropChance;
	public static final float RARE_DROP_CHANCE = Headhunters.CONFIG.rareDropChance;
	public static final float EPIC_DROP_CHANCE = Headhunters.CONFIG.epicDropChance;
	public static final float LEGENDARY_DROP_CHANCE = Headhunters.CONFIG.legendaryDropChance;
	public static final float ALWAYS_DROP_CHANCE = 1F;
	
	// @todo Identify a permanent solution, if needed. Temporarily set to a pesky bird.
	public static final UUID DEFAULT_UUID = UUID.fromString("5f8eb73b-25be-4c5a-a50f-d27d65e30ca0");
	
	public static Map<String, HeadDrop> MOB_DROPS = new HashMap<>() {{
		put( "allay", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MwMzg5MTc3ZGJhYTkyZjBkNWZmZGY4NDg4NjJjN2Y5YjM2ZGYyMjJmYmZkNzM3ZTI2MzlkYzMwNTllMGNmMyJ9fX0=" ) );
		put( "armadillo", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_ARMADILLO_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjYwN2FlN2E3MDE3NjczMzZjMDZiMGM0ZmZhODFkZmYyY2ZkOGJjMDcwZDk1NzE0YTZiYWRmMGVmYjcyNjNlMSJ9fX0=" ) );
		
		put( "axolotl", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_AXOLOTL_IDLE_AIR, "" ) );
		put( "lucy_axolotl", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_AXOLOTL_IDLE_AIR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY3ZTE1ZWFiNzMwNjRiNjY4MGQxZGI5OGJhNDQ1ZWQwOTE0YmEzNWE3OTk5OTdjMGRhMmIwM2ZmYzNhODgyNiJ9fX0=" ) );
		put( "wild_axolotl", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_AXOLOTL_IDLE_AIR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdjZjAyNzQ5OThiZjVhN2YzOGIzNzAzNmUxNTRmMTEyZmEyZTI4YmFkNDBkNWE3Yzk0NzY1ZmU0ZjUyMjExZSJ9fX0=" ) );
		put( "gold_axolotl", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_AXOLOTL_IDLE_AIR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTU4NTYwMTE1ZmFhZDExNjE5YjNkNTVkZTc5ZWYyYTA1M2Y0NzhhNjcxOTRiYmU5MjQ3ZWRlYTBiYzk4ZTgzNCJ9fX0=" ) );
		put( "cyan_axolotl", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_AXOLOTL_IDLE_AIR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODUxMTk2ZDQzOTMwNjU5ZDcxN2UxYjZhMDQ2YTA4ZDEyMjBmY2I0ZTMxYzQ4NTZiYzMzZTc1NTE5ODZlZjFkIn19fQ==" ) );
		put( "blue_axolotl", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_AXOLOTL_IDLE_AIR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhmZDEwYjBmZWY0NTk1OTYwYjFmNjQxOTNiYzhhMTg2NWEyZDJlZDQ4YjJlMmNlMDNkOTk0NTYzMDI3ZGY5NSJ9fX0=" ) );
		
		put( "bat", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_BAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViNTg4ZGNkMGJiNTdjZTZkZGFiOGUzYWZiNmZkNDMzMDA2NGVhYWMwMWI2MWE4ZTk3NjlmMDQ3NmY1MmY1MCJ9fX0=" ) );
		
		put( "bee", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_BEE_LOOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlhYzE2ZjI5NmI0NjFkMDVlYTA3ODVkNDc3MDMzZTUyNzM1OGI0ZjMwYzI2NmFhMDJmMDIwMTU3ZmZjYTczNiJ9fX0=" ) );
		put( "pollinated_bee", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_BEE_LOOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjcyN2QwYWIwM2Y1Y2QwMjJmODcwNWQzZjdmMTMzY2E0OTIwZWFlOGUxZTQ3YjUwNzQ0MzNhMTM3ZTY5MWU0ZSJ9fX0=" ) );
		put( "angry_bee", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_BEE_LOOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQwMDIyM2YxZmE1NDc0MWQ0MjFkN2U4MDQ2NDA5ZDVmM2UxNWM3ZjQzNjRiMWI3Mzk5NDAyMDhmM2I2ODZkNCJ9fX0=" ) );
		put( "angry_pollinated_bee", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_BEE_LOOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZiNzRlMDUyYjc0Mjg4Nzk5YmE2ZDlmMzVjNWQwMjIxY2Y4YjA0MzMxNTQ3ZWMyZjY4ZDczNTk3YWUyYzliIn19fQ==" ) );
		
		put( "blaze", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_BLAZE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGVlMjNkYzdhMTBjNmE4N2VmOTM3NDU0YzBlOTRlZDQyYzIzYWE2NDFhOTFlZDg0NzBhMzA0MmQwNWM1MmM1MiJ9fX0=" ) );
		put( "bogged", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_BOGGED_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY4MDdhMTg3MDc3ZjgzNmI5MzgyMGIzMmQ4ZDgzNDFkNGQzMmNkNGM4YzExMTVjZjFkYTYzNzRlMGZiZDNmZiJ9fX0=" ) );
		put( "breeze", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_BREEZE_IDLE_AIR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI3NTcyOGFmN2U2YTI5Yzg4MTI1YjY3NWEzOWQ4OGFlOTkxOWJiNjFmZGMyMDAzMzdmZWQ2YWIwYzQ5ZDY1YyJ9fX0=" ) );
		put( "camel", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_CAMEL_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY3ZDQ1OTczNDAxNjZlMTk3OGE2NjhhMDZiZjU3NTZjMTdiNGNiNWI0MGFiOGZmMjQ0MDkzYjZiOGJjNzVkMyJ9fX0=" ) );
		
		put( "cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "" ) );
		put( "tabby_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUyOGQzMGRiM2Y4YzNmZTUwY2E0ZjI2ZjMwNzVlMzZmMDAzYWU4MDI4MTM1YThjZDY5MmYyNGM5YTk4YWUxYiJ9fX0=" ) );
		put( "tuxedo_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZkMTBjOGU3NWY2NzM5OGM0NzU4N2QyNWZjMTQ2ZjMxMWMwNTNjYzVkMGFlYWI4NzkwYmNlMzZlZTg4ZjVmOCJ9fX0=" ) );
		put( "ginger_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjExM2RiZDNjNmEwNzhhMTdiNGVkYjc4Y2UwN2Q4MzZjMzhkYWNlNTAyN2Q0YjBhODNmZDYwZTdjYTdhMGZjYiJ9fX0=" ) );
		put( "siamese_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDViM2Y4Y2E0YjNhNTU1Y2NiM2QxOTQ0NDk4MDhiNGM5ZDc4MzMyNzE5NzgwMGQ0ZDY1OTc0Y2M2ODVhZjJlYSJ9fX0=" ) );
		put( "british_shorthair_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4OWUwZDVkM2U4MWY4NGI1NzBlMjk3ODI0NGIzYTczZTVhMjJiY2RiNjg3NGI0NGVmNWQwZjY2Y2EyNGVlYyJ9fX0=" ) );
		put( "calico_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQwMDk3MjcxYmI2ODBmZTk4MWU4NTllOGJhOTNmZWEyOGI4MTNiMTA0MmJkMjc3ZWEzMzI5YmVjNDkzZWVmMyJ9fX0=" ) );
		put( "persian_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY0MGM3NDYyNjBlZjkxYzk2YjI3MTU5Nzk1ZTg3MTkxYWU3Y2UzZDVmNzY3YmY4Yzc0ZmFhZDk2ODlhZjI1ZCJ9fX0=" ) );
		put( "ragdoll_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3YTQ1ZDI1ODg5ZTNmZGY3Nzk3Y2IyNThlMjZkNGU5NGY1YmMxM2VlZjAwNzk1ZGFmZWYyZTgzZTBhYjUxMSJ9fX0=" ) );
		put( "white_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFkMTVhYzk1NThlOThiODlhY2E4OWQzODE5NTAzZjFjNTI1NmMyMTk3ZGQzYzM0ZGY1YWFjNGQ3MmU3ZmJlZCJ9fX0=" ) );
		put( "jellie_cat", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBkYjQxMzc2Y2E1N2RmMTBmY2IxNTM5ZTg2NjU0ZWVjZmQzNmQzZmU3NWU4MTc2ODg1ZTkzMTg1ZGYyODBhNSJ9fX0=" ) );
		put( "black_cat", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_CAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjMWU4MWZmMDNlODJhM2U3MWUwY2Q1ZmJlYzYwN2UxMTM2MTA4OWFhNDdmMjkwZDQ2YzhhMmMwNzQ2MGQ5MiJ9fX0=" ) );
		
		put( "cave_spider", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_SPIDER_STEP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZhMWMyNTk5ZmM5MTIwM2E2NWEwM2Q0NzljOGRjODdmNjYyZGVhYzM2NjNjMTZjNWUwNGQ2MjViMzk3OGEyNSJ9fX0=" ) );
		put( "chicken", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_CHICKEN_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhZjZlNTg0N2VlYTA5OWUxYjBhYjhjMjBhOWU1ZjNjNzE5MDE1OGJkYTU0ZTI4MTMzZDliMjcxZWMwY2I0YiJ9fX0=" ) );
		put( "cod", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_COD_FLOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI0NmUxOWIzMmNmNzg0NTQ5NDQ3ZTA3Yjk2MDcyZTFmNjU2ZDc4ZTkzY2NjYTU2Mzc0ODVlNjc0OTczNDY1MiJ9fX0=" ) );
		put( "cow", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_COW_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkNjIxMTAwZmVhNTg4MzkyMmU3OGJiNDQ4MDU2NDQ4Yzk4M2UzZjk3ODQxOTQ4YTJkYTc0N2Q2YjA4YjhhYiJ9fX0=" ) );
		
		put( "creeper", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), "" ) );
		put( "charged_creeper", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUxMWU0YTNkNWFkZDZhNTQ0OTlhYmFkMTBkNzk5ZDA2Y2U0NWNiYTllNTIwYWZkMjAwODYwOGE2Mjg4YjdlNyJ9fX0=" ) );
		
		put( "dolphin", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU5Njg4Yjk1MGQ4ODBiNTViN2FhMmNmY2Q3NmU1YTBmYTk0YWFjNmQxNmY3OGU4MzNmNzQ0M2VhMjlmZWQzIn19fQ==" ) );
		put( "donkey", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_DONKEY_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUyNWVlOTI3M2FkNTc5ZDQ0YmY0MDZmNmY2Mjk1NTg2NDgxZWExOThmZDU3MjA3NmNkMGM1ODgyZGE3ZTZjYyJ9fX0=" ) );
		put( "dragon", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, "" ) );
		put( "drowned", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_DROWNED_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNmN2NjZjYxZGJjM2Y5ZmU5YTYzMzNjZGUwYzBlMTQzOTllYjJlZWE3MWQzNGNmMjIzYjNhY2UyMjA1MSJ9fX0=" ) );
		
		put( "elder_guardian", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGEyZDY0ZjRhMDBlOWM4NWY2NzI2MmVkY2FjYjg0NTIzNTgxYWUwZjM3YmRhYjIyZGQ3MDQ1MjRmNjJlMTY5ZiJ9fX0=" ) );
		put( "enderman", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ENDERMAN_SCREAM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODk3N2E5NGYwMjQ5OGNhZDBjZmRiNjVjYTdjYjcyZTIzMTExYTkxNGQ4YzY3MGFjY2NjN2E2NWIzNDdkNzc3NiJ9fX0=" ) );
		put( "endermite", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ENDERMITE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM2YjY1YzIyYjQ0NjViYTY3OTNiMjE5NWNkNTA4NGNlODNiODhkY2E2ZTU1ZWI5NDg0NTQwYWNkNzM1MmE1MCJ9fX0=" ) );
		put( "evoker", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkwZmJkODhmNjU5ZDM5NjNjNjhjYmJjYjdjNzEyMWQ4MTk1YThiZTY1YmJkMmJmMTI1N2QxZjY5YmNjYzBjNyJ9fX0=" ) );
		
		put( "fox", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FOX_AMBIENT, "" ) );
		put( "snow_fox", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FOX_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE0MzYzNzdlYjRjNGI0ZTM5ZmIwZTFlZDg4OTlmYjYxZWUxODE0YTkxNjliOGQwODcyOWVmMDFkYzg1ZDFiYSJ9fX0=" ) );
		put( "red_fox", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FOX_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlMDA0MzExMWJjNTcwOTA4NTYyNTkxNTU1NzFjNzkwNmU3MDcwNDZkZjA0MWI4YjU3MjcwNGM0NTFmY2Q4MiJ9fX0=" ) );
		
		put( "frog", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FROG_AMBIENT, "" ) );
		put( "cold_frog", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FROG_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY4Nzc4OTNlOTIwZmY1ZGZhNGI1ZmJkMTRkYWJlZTJlNjMwOGE2Zjk3YzNhMTliMDhlMjQxYTI5ZWI5YTVjMyJ9fX0=" ) );
		put( "temperate_frog", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FROG_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTUwZDEwNzNkNDFmMTkzNDA1ZDk1YjFkOTQxZjlmZTFhN2ZmMDgwZTM4MTU1ZDdiYjc4MGJiYmQ4ZTg2ZjcwZCJ9fX0=" ) );
		put( "warm_frog", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_FROG_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDViMGRhNDM5NzViODNjMzMyMjc4OGRkYTMxNzUwNjMzMzg0M2FlYmU1NTEyNzg3Y2IyZTNkNzY5ZWQyYjM4MiJ9fX0=" ) );
		
		put( "ghast", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_GHAST_WARN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUzZGUzMWEyZDAwNDFhNmVmNzViZjdhNmM4NDY4NDY0ZGIxYWFhNjIwMWViYjFhNjAxM2VkYjIyNDVjNzYwNyJ9fX0=" ) );
		put( "glow_squid", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_GLOW_SQUID_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGIyZTliNjU4MWZlZDQ4YTk5ZTAzMjMwOTFhZDVjM2MzMjZjZGEyMDA3M2UyOGE5MDJhMDM3M2Y3MzgyYjU5ZiJ9fX0=" ) );
		
		put( "goat", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_GOAT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc0NzNlMDU1ZGY2ZTdmZDk4NjY0ZTlmZGI2MzY3NWYwODgxMDYzMDVkNzQ0MDI0YTQxYmIzNTg5MThhMTQyYiJ9fX0=" ) );
		put( "screaming_goat", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_GOAT_SCREAMING_PREPARE_RAM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmRhNDg1YWMyMzUxMjQyMDg5MWE1YWUxZThkZTk4OWYwOTFkODQ4ZDE1YTkwNjhkYTQ3MjBkMzE2ZmM0MzMwZiJ9fX0=" ) );
		
		put( "guardian", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_GUARDIAN_FLOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiYTM0NDE2NjcwNDU0YjFhMjA0OTZmODBiOTM5ODUyOWY0OTAwM2ZjNjEzZWI5MzAyNDhlYTliNWQxYTM5MSJ9fX0=" ) );
		put( "hoglin", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_HOGLIN_ANGRY, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM0YTdmNTdmYzAzYjEzYWEyZjlkODNjZGQ0ODIyYjkzNjc5MzA5NmRhZjUxZTc4MDI1YmJkMjQxZWQ2ZjY4ZCJ9fX0=" ) );
		
		put( "horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "" ) );
		put( "white_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdiYzYxNjA5NzMwZjJjYjAxMDI2OGZhYjA4MjFiZDQ3MzUyNjk5NzUwYTE1MDU5OWYyMWMzZmM0ZTkyNTkxYSJ9fX0=" ) );
		put( "creamy_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMGQ1NGNjMDcxMjY3ZDZiZmQ1ZjUyM2Y4Yzg5ZGNmZGM1ZTgwNWZhYmJiNzYwMTBjYjNiZWZhNDY1YWE5NCJ9fX0=" ) );
		put( "chestnut_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmM4NzIwZDFmNTUyNjkzYjQwYTlhMzNhZmE0MWNlZjA2YWZkMTQyODMzYmVkOWZhNWI4ODdlODhmMDVmNDlmYSJ9fX0=" ) );
		put( "brown_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjc3MTgwMDc3MGNiNGU4MTRhM2Q5MTE4NmZjZDc5NWVjODJlMDYxMDJmZjdjMWVlNGU1YzM4MDEwMmEwYzcwZiJ9fX0=" ) );
		put( "black_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjcyM2ZhNWJlNmFjMjI5MmE3MjIzMGY1ZmQ3YWI2NjM0OTNiZDhmN2U2NDgxNjQyNGRjNWJmMjRmMTMzODkwYyJ9fX0=" ) );
		put( "gray_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI1OTg2MTAyMTgxMDgzZmIzMTdiYzU3MTJmNzEwNGRhYTVhM2U4ODkyNjRkZmViYjkxNTlmNmUwOGJhYzkwYyJ9fX0=" ) );
		put( "dark_brown_horse", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YyMzQxYWFhMGM4MmMyMmJiYzIwNzA2M2UzMTkyOTEwOTdjNTM5YWRhZDlhYTkxM2ViODAwMWIxMWFhNTlkYSJ9fX0=" ) );
		
		put( "husk", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_HUSK_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMzODMxOGJjOTFhMzZjZDVhYjZhYTg4NWM5YTRlZTJiZGFjZGFhNWM2NmIyYTk5ZGZiMGE1NjA5ODNmMjQ4MCJ9fX0=" ) );
		put( "illusioner", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM4MjcwMWM2N2Q2YzU0YzkwNzU1ODg5MWRjMTc2MjI1MTEyNTE4NzcxZTA2MWM1ZDhiZDkxODQ3OWU2YmRkOCJ9fX0=" ) );
		put( "iron_golem", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_IRON_GOLEM_HURT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU3YzA3MTlmYWJlMTE2ZGNlNjA1MTk5YmNhZGM2OWE1Mzg4NjA4NjRlZjE1NzA2OTgzZmY2NjI4MjJkOWZlMyJ9fX0=" ) );
		
		put( "llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "" ) );
		put( "creamy_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ2N2ZkNGJmZjI5MzI2OWNiOTA4OTc0ZGNhODNjMzM0ODVlNDM1ZWQ1YThlMWRiZDY1MjFjNjE2ODcxNDAifX19" ) );
		put( "white_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODAyNzdlNmIzZDlmNzgxOWVmYzdkYTRiNDI3NDVmN2FiOWE2M2JhOGYzNmQ2Yjg0YTdhMjUwYzZkMWEzNThlYiJ9fX0=" ) );
		put( "brown_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiMWVjZmY3N2ZmZTNiNTAzYzMwYTU0OGViMjNhMWEwOGZhMjZmZDY3Y2RmZjM4OTg1NWQ3NDkyMTM2OCJ9fX0=" ) );
		put( "gray_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2YyNGU1NmZkOWZmZDcxMzNkYTZkMWYzZTJmNDU1OTUyYjFkYTQ2MjY4NmY3NTNjNTk3ZWU4MjI5OWEifX19" ) );
		
		put( "magma_cube", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_MAGMA_CUBE_SQUISH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjgxNzE4ZDQ5ODQ4NDdhNGFkM2VjMDgxYTRlYmZmZDE4Mzc0MzIzOWFlY2FiNjAzMjIxMzhhNzI2MDk4MTJjMyJ9fX0=" ) );
		
		put( "mooshroom", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_COW_AMBIENT, "" ) );
		put( "brown_mooshroom", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_COW_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U2NDY2MzAyYTVhYjQzOThiNGU0NzczNDk4MDhlNWQ5NDAyZWEzYWQ4ZmM0MmUyNDQ2ZTRiZWQwYTVlZDVlIn19fQ==" ) );
		put( "red_mooshroom", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_COW_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE4MDYwNmU4MmM2NDJmMTQxNTg3NzMzZTMxODBhZTU3ZjY0NjQ0MmM5ZmZmZDRlNTk5NzQ1N2UzNDMxMWEyOSJ9fX0=" ) );
		
		put( "mule", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_MULE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjMjI0YTEwMzFiZTQzNGQyNWFlMTg4NWJmNGZmNDAwYzk4OTRjNjliZmVmNTZhNDkzNTRjNTYyNWMwYzA5YyJ9fX0=" ) );
		put( "ocelot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_OCELOT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE3NWNjNDNlYThhZTIwMTY4YTFmMTcwODEwYjRkYTRkOWI0ZWJkM2M5OTc2ZTlmYzIyZTlmOTk1YzNjYmMzYyJ9fX0=" ) );
		
		put( "panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlkZjQ3ZTAxNWQ1YzFjNjhkNzJiZTExYmI2NTYzODBmYzZkYjUzM2FhYjM4OTQxYTkxYjFkM2Q1ZTM5NjQ5NyJ9fX0=" ) );
		put( "aggressive_panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTU0NmU0MzZkMTY2YjE3ZjA1MjFiZDg1MzhlYTEzY2Q2ZWUzYjVkZjEwMmViMzJlM2U0MjVjYjI4NWQ0NDA2MyJ9fX0=" ) );
		put( "lazy_panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTg3ZjFmNWRiMmUyNGRmNGRhYWVkNDY4NWQ2YWVlNWRlYjdjZGQwMjk2MzBmMDA3OWMxZjhlMWY5NzQxYWNmZCJ9fX0=" ) );
		put( "playful_panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGNhZGQ0YmYzYzRjYWNlOTE2NjgwZTFmZWY5MGI1ZDE2YWQ2NjQzOTUxNzI1NjY4YmE2YjQ5OTZiNjljYTE0MCJ9fX0=" ) );
		put( "worried_panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI4NmZkMWJmOGNiY2UyM2JjMDhmYjkwNjkxNzE3NjExYWRkYzg1YWI4MjNiNzcxNGFlYzk4YTU2NjBlZmYxNSJ9fX0=" ) );
		put( "brown_panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ1ZjZkNjEyNjcyODY3MWI0NGMxYzc3NWY5OTYxNzQyNGUzMzYxMWI1ZDMxYWQyYWNmZjI4MDRlYjk2ZWIwNiJ9fX0=" ) );
		put( "weak_panda", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PANDA_SNEEZE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M1NmEzNTVmYmUwZTJmYmQyOGU4NWM0ZDgxNWZmYTVkMWY5ZDVmODc5OGRiYzI1OWZmODhjNGFkZGIyMDJhZSJ9fX0=" ) );
		
		put( "parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBhM2Q0N2Y1NGU3MWE1OGJmOGY1N2M1MjUzZmIyZDIxM2Y0ZjU1YmI3OTM0YTE5MTA0YmZiOTRlZGM3NmVhYSJ9fX0=" ) );
		put( "red_parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBhM2Q0N2Y1NGU3MWE1OGJmOGY1N2M1MjUzZmIyZDIxM2Y0ZjU1YmI3OTM0YTE5MTA0YmZiOTRlZGM3NmVhYSJ9fX0=" ) );
		put( "blue_parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk0YmQzZmNmNGQ0NjM1NGVkZThmZWY3MzEyNmRiY2FiNTJiMzAxYTFjOGMyM2I2Y2RmYzEyZDYxMmI2MWJlYSJ9fX0=" ) );
		put( "green_parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmExZGMzMzExNTIzMmY4MDA4MjVjYWM5ZTNkOWVkMDNmYzE4YWU1NTNjMjViODA1OTUxMzAwMGM1OWUzNTRmZSJ9fX0=" ) );
		put( "light_blue_parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI2OGNlMzdiZTg1MDdlZDY3ZTNkNDBiNjE3ZTJkNzJmNjZmOWQyMGIxMDZlZmIwOGU2YmEwNDFmOWI5ZWYxMCJ9fX0=" ) );
		put( "yellow_blue_parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI2OGNlMzdiZTg1MDdlZDY3ZTNkNDBiNjE3ZTJkNzJmNjZmOWQyMGIxMDZlZmIwOGU2YmEwNDFmOWI5ZWYxMCJ9fX0=" ) );
		put( "gray_parrot", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PARROT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiZTcyM2FhMTczOTNkOTlkYWRkYzExOWM5OGIyYzc5YzU0YjM1ZGViZTA1YzcxMzhlZGViOGQwMjU2ZGM0NiJ9fX0=" ) );
		
		put( "phantom", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PHANTOM_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U5NTE1M2VjMjMyODRiMjgzZjAwZDE5ZDI5NzU2ZjI0NDMxM2EwNjFiNzBhYzAzYjk3ZDIzNmVlNTdiZDk4MiJ9fX0=" ) );
		put( "pig", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_PIG_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFlZTc2ODFhZGYwMDA2N2YwNGJmNDI2MTFjOTc2NDEwNzVhNDRhZTJiMWMwMzgxZDVhYzZiMzI0NjIxMWJmZSJ9fX0=" ) );
		put( "piglin", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_PIGLIN_AMBIENT, "" ) );
		put( "piglin_brute", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_PIGLIN_BRUTE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQ4ODc5OWM4M2VjYjI5NDUyY2ViYTg5YzNjMDA5OTIxOTI3NGNlNWIyYmZiOGFkMGIzZWE0YzY1ZmFjNDYzMCJ9fX0=" ) );
		put( "pillager", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIyNWYwYjQ5YzUyOTUwNDhhNDA5YzljNjAxY2NhNzlhYThlYjUyYWZmNWUyMDMzZWJiODY1ZjQzNjdlZjQzZSJ9fX0=" ) );
		put( "polar_bear", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_POLAR_BEAR_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q4NzAyOTExZTYxNmMwZDMyZmJlNzc4ZDE5NWYyMWVjY2U5MDI1YmNiZDA5MTUxZTNkOTdhZjMxOTJhYTdlYyJ9fX0=" ) );
		put( "pufferfish", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTI3MzNkNWRhNTljODJlYWYzMTBiMzgyYWZmNDBiZDUxM2M0NDM1NGRiYmFiZmUxNGIwNjZhNTU2ODEwYTdmOSJ9fX0=" ) );
		
		put( "rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "" ) );
		put( "toast_rabbit", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFhNTdjM2QwYTliMTBlMTNmNjZkZjc0MjAwY2I4YTZkNDg0YzY3MjIyNjgxMmQ3NGUyNWY2YzAyNzQxMDYxNiJ9fX0=" ) );
		put( "brown_rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZkNGY4NmNmNzQ3M2ZiYWU5M2IxZTA5MDQ4OWI2NGMwYmUxMjZjN2JiMTZmZmM4OGMwMDI0NDdkNWM3Mjc5NSJ9fX0=" ) );
		put( "white_rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU0MmQ3MTYwOTg3MTQ4YTVkOGUyMGU0NjliZDliM2MyYTM5NDZjN2ZiNTkyM2Y1NWI5YmVhZTk5MTg1ZiJ9fX0=" ) );
		put( "black_rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJiNDI1ZmYyYTIzNmFiMTljYzkzOTcxOTVkYjQwZjhmMTg1YjE5MWM0MGJmNDRiMjZlOTVlYWM5ZmI1ZWZhMyJ9fX0=" ) );
		put( "black_and_white_rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzVmNzJhMjE5NWViZjQxMTdjNTA1NmNmZTJiNzM1N2VjNWJmODMyZWRlMTg1NmE3NzczZWU0MmEwZDBmYjNmMCJ9fX0=" ) );
		put( "gold_rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY3YjcyMjY1NmZkZWVjMzk5NzRkMzM5NWM1ZTE4YjQ3YzVlMjM3YmNlNWJiY2VkOWI3NTUzYWExNGI1NDU4NyJ9fX0=" ) );
		put( "salt_and_pepper_rabbit", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTIzODUxOWZmMzk4MTViMTZjNDA2MjgyM2U0MzE2MWZmYWFjOTY4OTRmZTA4OGIwMThlNmEyNGMyNmUxODFlYyJ9fX0=" ) );
		put( "killer_rabbit", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_RABBIT_ATTACK, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFkZDc2NzkyOWVmMmZkMmQ0M2U4NmU4NzQ0YzRiMGQ4MTA4NTM0NzEyMDFmMmRmYTE4Zjk2YTY3ZGU1NmUyZiJ9fX0=" ) );
		
		put( "ravager", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_RAVAGER_ROAR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI0ZGIyOTg2MTQwZTI1MWUzMmU3MGVkMDhjOGEwODE3MjAzMTNjZTI1NzYzMmJlMWVmOTRhMDczNzM5NGRiIn19fQ==" ) );
		put( "salmon", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SALMON_FLOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzkxZDllNjliNzk1ZGE0ZWFhY2ZjZjczNTBkZmU4YWUzNjdmZWQ4MzM1NTY3MDZlMDQwMzM5ZGQ3ZmUwMjQwYSJ9fX0=" ) );
		put( "shulker", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_SHULKER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI5ZTZhZjZiODE5ZjNkOTBlNjdjZTJlNzA1OWZiZWYzMWRhMmFhOTUzZDM1ZTM0NTRmMTAyMWZhOTEyZWZkZSJ9fX0=" ) );
		
		put( "sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "" ) );
		put( "black_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMzMzVlODA2NWM3YjVkZmVhNThkM2RmNzQ3NGYzOTZhZjRmYTBhMmJhNTJhM2M5YjdmYmE2ODMxOTI3MWM5MSJ9fX0=" ) );
		put( "blue_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQwZTI3N2RhNmMzOThiNzQ5YTMyZjlkMDgwZjFjZjRjNGVmM2YxZjIwZGQ5ZTVmNDIyNTA5ZTdmZjU5M2MwIn19fQ==" ) );
		put( "brown_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEyOGQwODZiYzgxNjY5ZmMyMjU1YmIyMmNhZGM2NmEwZjVlZDcwODg1ZTg0YzMyZDM3YzFiNDg0ZGIzNTkwMSJ9fX0=" ) );
		put( "cyan_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ0MmZjYmNhZjlkNDhmNzNmZmIwYzNjMzZmMzRiNDY0MzI5NWY2ZGFhNmNjNzRhYjlkMjQyZWQ1YWE1NjM2In19fQ==" ) );
		put( "gray_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhZmVjZjA2MDNiMmRjZDc5ODRkMjUyNTg2MDY5ODk1ZGI5YWE3OGUxODQxYmQ1NTRiMTk1MDhkY2Y5NjdhMSJ9fX0=" ) );
		put( "green_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVhODg3ZWFlNGIwNzYzNmU5ZTJmOTA2NjA5YjAwYWI4ZDliODZiNzQ3MjhiODE5ZmY2ZjM3NjU4M2VhMTM5In19fQ==" ) );
		put( "jeb_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjMzMzI2NzY1YTE5MGViZjkwZDU0ODZkNzFmMjBlMjU5N2U0YmVlMmEzOTFmZWNiYmQ4MGRlYmZlMWY4MmQ3OCJ9fX0=" ) );
		put( "light_blue_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWJmMjNhZjg3MTljNDM3YjNlZTg0MDE5YmEzYzllNjljYTg1NGQzYThhZmQ1Y2JhNmQ5Njk2YzA1M2I0ODYxNCJ9fX0=" ) );
		put( "light_gray_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQyZTJlOTNhMTQyYmZkNDNmMjQwZDM3ZGU4ZjliMDk3NmU3NmU2NWIyMjY1MTkwODI1OWU0NmRiNzcwZSJ9fX0=" ) );
		put( "lime_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJlYWQwMzQyYWU4OWI4ZGZkM2Q3MTFhNjBhZGQ2NWUyYzJiZmVhOGQwYmQyNzRhNzU4N2RlZWQ3YTMxODkyZSJ9fX0=" ) );
		put( "magenta_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThlMWYwNWYwZGFjY2E2M2E3MzE4NzRmOTBhNjkzZmZlMjFmZjgzMmUyYjFlMWQwN2I2NWM4NzY0NTI2ZjA4OSJ9fX0=" ) );
		put( "orange_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY4NGQwNGZhODBhYTU5ZGExNDUzNWRlYWQzODgzZDA5N2ZiYmE0MDA2MjU2NTlmNTI1OTk2NDgwNmJhNjZmMCJ9fX0=" ) );
		put( "pink_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2M2U4YTkzZDI4N2E4NGU2NDAzMDlhZTgzY2ExZGUwYTBiMjU3NTA1YTIwZWM1NWIzMzQ5ZDQwYTQ0ODU0In19fQ==" ) );
		put( "purple_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0OWQwODI5MWRhZTQ1YTI0NjczNjE5NjAyZjQzNWI1N2Y0Y2Q0ZTllOThkMmUwZmJlYzRmMTgxNDQ3ODFkMyJ9fX0=" ) );
		put( "red_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ3OGUwNTcxNThkZTZmNDVlMjU0MWNkMTc3ODhlNjQwY2NiNTk3MjNkZTU5YzI1NGU4MmFiNTcxMWYzZmMyNyJ9fX0=" ) );
		put( "white_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRmZTdjYzQ2ZDc0OWIxNTMyNjFjMWRjMTFhYmJmMmEzMTA4ZWExYmEwYjI2NTAyODBlZWQxNTkyZGNmYzc1YiJ9fX0=" ) );
		put( "yellow_sheep", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SHEEP_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRiMjhmMDM1NzM1OTA2ZjgyZmZjNGRiYTk5YzlmMGI1NTI0MGU0MjZjZDFjNTI1YTlhYTc3MTgwZWVjNDkzNCJ9fX0=" ) );
		
		put( "silverfish", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_SILVERFISH_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI1ZTlmYWUzNzE2NjRkZTFhODAwYzg0ZDAyNTEyNGFiYjhmMTUxMTE4MDdjOGJjMWFiOTEyNmFhY2JkNGY5NSJ9fX0=" ) );
		put( "skeleton", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_SKELETON_AMBIENT, "" ) );
		put( "skeleton_horse", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUyMjY3MDViZDJhOWU3YmI4ZDZiMGY0ZGFhOTY5YjllMTJkNGFlNWM2NmRhNjkzYmI1ZjRhNGExZTZhYTI5NiJ9fX0=" ) );
		put( "slime", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzA2NDI0ZWM3YTE5NmIxNWY5YWQ1NzMzYTM2YTZkMWYyZTZhMGQ0MmZmY2UxZTE1MDhmOTBmMzEyYWM0Y2FlZCJ9fX0=" ) );
		put( "sniffer", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_SNIFFER_SCENTING, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg0YTdlN2ZlMTk3YjdlNzQxOWI1MWQ0NmNjMjMzNTUxYjllYzg5OWRlMWFmZTdmNjUzZTRmOGZiMjZhNjg2ZSJ9fX0=" ) );
		put( "snow_golem", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_SNOW_GOLEM_HURT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FhM2UxN2VmMWIyOWE0Yjg3ZmE0M2RlZTFkYjEyYzQxZmQzOWFhMzg3ZmExM2FmMmEwNzliNWIzNzhmZGU4YiJ9fX0=" ) );
		put( "spider", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_SPIDER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUyOGU2NjI5YjZlZDFkYTk0ZDRhODE4NzYxNjEyYzM2ZmIzYTY4MTNjNGI2M2ZiOWZlYTUwNzY0MTVmM2YwYyJ9fX0=" ) );
		put( "squid", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_SQUID_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWI3ZDlhNGYzNmNmZTMxZmQ1OWQ4YzkwMGU0MTlhMTM1MTQ0MTA1ZTdhOTgxY2FhNWExNjhkY2ZmMzI1YiJ9fX0=" ) );
		put( "stray", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_STRAY_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTkyYjU1OTcwODVlMzVkYjUzZDliZGEwMDhjYWU3MmIyZjAwY2Q3ZDRjZDhkYzY5ZmYxNzRhNTViNjg5ZTZlIn19fQ==" ) );
		
		put( "strider", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_STRIDER_HAPPY, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM0MGZhZDFjMTFkZTllNjQyMmI0MDU0MjZlOWI5NzkwN2YzNWJjZTM0NWUzNzU4NjA0ZDNlN2JlN2RmODg0In19fQ==" ) );
		put( "cold_strider", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_STRIDER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcxMzA4NWE1NzUyN2U0NTQ1OWMzOGZhYTdiYjkxY2FiYjM4MWRmMzFjZjJiZjc5ZDY3YTA3MTU2YjZjMjMwOSJ9fX0=" ) );
		
		put( "tadpole", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_TADPOLE_FLOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2RhZjE2NTNiNWY1OWI1ZWM1YTNmNzk2MDljYjQyMzM1NzlmZWYwN2U2OTNiNjE3NDllMDkwMDE0OWVkZjU2MyJ9fX0=" ) );
		
		put( "trader_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "" ) );
		put( "creamy_trader_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg5YTJlYjE3NzA1ZmU3MTU0YWIwNDFlNWM3NmEwOGQ0MTU0NmEzMWJhMjBlYTMwNjBlM2VjOGVkYzEwNDEyYyJ9fX0=" ) );
		put( "white_trader_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA4N2E1NTZkNGZmYTk1ZWNkMjg0NGYzNTBkYzQzZTI1NGU1ZDUzNWZhNTk2ZjU0MGQ3ZTc3ZmE2N2RmNDY5NiJ9fX0=" ) );
		put( "brown_trader_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQyNDc4MGIzYzVjNTM1MWNmNDlmYjViZjQxZmNiMjg5NDkxZGY2YzQzMDY4M2M4NGQ3ODQ2MTg4ZGI0Zjg0ZCJ9fX0=" ) );
		put( "gray_trader_llama", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_LLAMA_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmU0ZDhhMGJjMTVmMjM5OTIxZWZkOGJlMzQ4MGJhNzdhOThlZTdkOWNlMDA3MjhjMGQ3MzNmMGEyZDYxNGQxNiJ9fX0=" ) );
		
		put( "tropical_fish", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_TROPICAL_FISH_FLOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRhMGM4NGRjM2MwOTBkZjdiYWZjNDM2N2E5ZmM2Yzg1MjBkYTJmNzNlZmZmYjgwZTkzNGQxMTg5ZWFkYWM0MSJ9fX0=" ) );
		put( "turtle", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_TURTLE_AMBIENT_LAND, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0OTMxMjAwYWQ0NjBiNjUwYTE5MGU4ZDQxMjI3YzM5OTlmYmViOTMzYjUxY2E0OWZkOWU1OTIwZDFmOGU3ZCJ9fX0=" ) );
		
		put( "vex", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_VEX_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk1MzhmMjgzMGM0ZGVhNjk5NmVkNzQ0Nzg1NTA0ZTMyZTBlMjBkODY2M2VkYWI2YjAyMjJmMmMwMjIwNzdiZCJ9fX0=" ) );
		put( "angry_vex", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_VEX_CHARGE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE0ZTUxOGUxNmU0YjVjMTE0YWNiZDljNjFjZDE4MjkyZGE5ZWY2MDU1MGE0ZmNhZTI3ZDM5YWUyOTNlNDc3YSJ9fX0=" ) );
		
		put( "villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=" ) );
		put( "armorer_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVmNjI3ZjU2NmFjMGE3ODI4YmFkOTNlOWU0Yjk2NDNkOTlhOTI4YTEzZDVmOTc3YmY0NDFlNDBkYjEzMzZiZiJ9fX0=" ) );
		put( "butcher_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFiYWQ2NDE4NWUwNGJmMWRhZmUzZGE4NDkzM2QwMjU0NWVhNGE2MzIyMWExMGQwZjA3NzU5MTc5MTEyYmRjMiJ9fX0=" ) );
		put( "cartographer_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNhZWNmYmU4MDFjZjMyYjVkMWIwYjFmNjY4MDA0OTY2NjE1ODY3OGM1M2Y0YTY1MWZjODNlMGRmOWQzNzM4YiJ9fX0=" ) );
		put( "cleric_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWI5ZTU4MmUyZjliODlkNTU2ZTc5YzQ2OTdmNzA2YjFkZDQ5MjllY2FlM2MwN2VlOTBiZjFkNWJlMzE5YmY2ZiJ9fX0=" ) );
		put( "farmer_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_FARMER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkyNzJkMDNjZGE2MjkwZTRkOTI1YTdlODUwYTc0NWU3MTFmZTU3NjBmNmYwNmY5M2Q5MmI4ZjhjNzM5ZGIwNyJ9fX0=" ) );
		put( "fisherman_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE4OWZiNGFjZDE1ZDczZmYyYTU4YTg4ZGYwNDY2YWQ5ZjRjMTU0YTIwMDhlNWM2MjY1ZDVjMmYwN2QzOTM3NiJ9fX0=" ) );
		put( "fletcher_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2MTFmMTJlMThjZTQ0YTU3MjM4ZWVmMWNhZTAzY2Q5ZjczMGE3YTQ1ZTBlYzI0OGYxNGNlODRlOWM0ODA1NiJ9fX0=" ) );
		put( "leatherworker_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=" ) );
		put( "librarian_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjYWE1NzRiYWJiNDBlZTBmYTgzZjJmZDVlYTIwY2ZmMzFmZmEyNzJmZTExMzU4OGNlZWU0Njk2ODIxMjhlNyJ9fX0=" ) );
		put( "mason_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_MASON, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=" ) );
		put( "nitwit_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_NO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=" ) );
		put( "shepherd_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiZjRlOTE1NGFjOTI3MTk0MWM3MzNlYWNjNjJkYzlmYzBhNmRjMWI1ZDY3Yzc4Y2E5OGFmYjVjYjFiZTliMiJ9fX0=" ) );
		put( "toolsmith_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=" ) );
		put( "weaponsmith_villager", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3NmZmYTQxMGJiZTdmYTcwOTA5OTY1YTEyNWY0YTRlOWE0ZmIxY2UxYjhiM2MzNGJmYjczYWFmZmQ0Y2U0MyJ9fX0=" ) );
		
		put( "vindicator", new HeadDrop( headDropFeature.EPIC_DROP_CHANCE, SoundEvents.ENTITY_VINDICATOR_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhYmFmZGUyN2VlMTJiMDk4NjUwNDdhZmY2ZjE4M2ZkYjY0ZTA0ZGFlMWMwMGNjYmRlMDRhZDkzZGNjNmM5NSJ9fX0=" ) );
		put( "wandering_trader", new HeadDrop( headDropFeature.COMMON_DROP_CHANCE, SoundEvents.ENTITY_WANDERING_TRADER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=" ) );
		put( "warden", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_WARDEN_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJmMzg3OWI3MzcxMjc0ODVlYjM1ZGRlZTc0OGQwNmNmOTE0YjE5M2Q5Nzc1M2FlMzRlOTIyMzA4NDI4MzFmYiJ9fX0=" ) );
		put( "witch", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_WITCH_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTUyMGYxMmM2M2M3OTEyMTg2YzRiZTRlMzBjMzNjNWFjYWVjMGRiMGI2YWJkODM2ZDUxN2Q3NGE2MjI3NWQ0YiJ9fX0=" ) );
		
		put( "wither", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_WITHER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRhMTA4MjhmNjNiN2VjZGVmZDc2N2IzMjQ1ZmJkYWExM2MzZWMwYzZiMTM3NzRmMWVlOGQzMDdjMDM0YzM4MyJ9fX0=" ) );
		put( "wither_projectile", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_WITHER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjM3YzU4MTRhOTJmOGVjMGY2YWU5OTMzYWJlOTU0MmUxNjUxOTA3NjhlNzYwNDc4NTQzYWViZWVkNDAyN2MyNyJ9fX0=" ) );
		put( "blue_wither_projectile", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_WITHER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2ODJiMDYyMDNiOWRlNGMyODU0MTA3MWEyNmNkYzM0MGRkMjVkNGMzNzJiNzAyM2VjMmY0MTIwMjFkNjJmNyJ9fX0=" ) );
		
		put( "wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "" ) );
		put( "ashen_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzIzODRjNWNmMTg5NDhiODNhODk1NDhkYmE1YTk5NDVlZGVlZmM1ZTk2NTRjNWQ2ZDM4YWUxMGE1ZDUwMmU3NSJ9fX0=" ) );
		put( "angry_ashen_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NiYzMwNjZkMzFjNDM5MDM1MDM4ZmQ2ODc1ZDVkYmVlYzM5NjhjMWI4MDA2ZmZiZmI1ZjY3NGQ3NmM4OWNkZSJ9fX0=" ) );
		put( "black_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVhNjZhNDJiMjVmODIyYTdlMTZhMjE4NzUyOGQxYTJlMjk0YTAxZDlmODUwNjcxYjk0Yzk1NzQyYmI0OTE2ZSJ9fX0=" ) );
		put( "angry_black_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVlZDQ3ZGVkMjcwOGIxM2Q5MmViNTBmYjY4ZThjMWUxMWIzOWEwY2Q0NWIzOTM3MmVlYWQ4NzJjNDllZWFlYiJ9fX0=" ) );
		put( "chestnut_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I5YjBkNDg0NDIyMDRmZjZmZDM5ZmEwNzQxNjcxMThlOWMwNjZjZGUzODg4OTc3ZDBmNjAzNmUxZDhhNjllZSJ9fX0=" ) );
		put( "angry_chestnut_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg0YjI4ZjIzMmIxNGE1OWI2Y2I3NzU3MzIzOTc0ZWE1MDJiMWJjYjk4NGRlYTMwMDkzZWMyMWVkMmFkZTMxMiJ9fX0=" ) );
		put( "pale_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVlMzNjMmRjMDdkNzZiNGYwM2U2NjQyN2EwOGNiYTJlODE3OWQwNzVhZTY0YjljZTE1MGFhNDIwOWM1YWYzOSJ9fX0=" ) );
		put( "angry_pale_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg4N2E0Mjc4NzkwMGU2NzE2ZmE0NjJmYmFkOGRlYjU1MjZiOTQzOTg3OTc0MTRmMDNmNjAxM2VmODg1YTFkYiJ9fX0=" ) );
		put( "rusty_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0NTVmNjA0OGE2ODA5OGNkMjc2MzRlMzE0NmM4MWM4MjY5YWVlZmNmMGFmZjkxY2M5NzZlZmEwYmFhMTE0NiJ9fX0=" ) );
		put( "angry_rusty_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFmMWMzMmU1MjU4ZjNkOGY4ZDE4MWZiMzBkZjYxZTA2OTNlNTVkNTM4YTEzZWVhYmRmNjMwMGYzODA4M2FkYyJ9fX0=" ) );
		put( "snowy_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVjYWRhYjUwYWE4ZDQxZmE5YjM2OWEyZjg0Zjk3NDU2YmU3OTAwYjIyMGVjZTNiOTVlOGEwMDk2ODY2MGQ1In19fQ==" ) );
		put( "angry_snowy_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJiN2MyODZjMjMwODI2YjI5ZTdmZDM3ZjI1NzNiOTAxNWM0MjJiYzM4ZmViMTRkOWEzMTdjNjg1NWFkYTNmNiJ9fX0=" ) );
		put( "spotty_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTczYjlhNjQzMWFmMjZiY2IzMTgyNmViNmZkOWY0YjM1Yjk0N2JhNTg4MmM2ZTRhYTkzNTg4NjMzZjdiOGQ5ZCJ9fX0=" ) );
		put( "angry_spotty_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc4NmI3MzkzNDhhYTg1MDJlYTE4NWRmYjE0YmY1YWIwMWUyOWUwODJkMWZlYjg2MTNiM2ZlOTNlMGRlYmQ4ZSJ9fX0=" ) );
		put( "striped_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2ZjNTJmYjNjZGZjNmFlYjAwZTY3YzFiN2E1OWQ4ZDMyMGRmNDQ2NTZjN2FmNjgyNGIxM2NhNjA3OTJhYTdkNyJ9fX0=" ) );
		put( "angry_striped_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ2MGMyNTQ4OGIwNjcyNzY2OWE2OTE1ZDFkYWRhYTlhN2QyODMxYjQ2MGJlZTMwZTVkYTQwNzg3NDcwNTAwMSJ9fX0=" ) );
		put( "woods_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQxMmFiMTc2NDdiNjljOTQyMTc2OTU3MmFjNjc0ZGUxOTkxMjRjMjg0YjllZDFmNjVhMjg1YzM4Y2QyYTUwNCJ9fX0=" ) );
		put( "angry_woods_wolf", new HeadDrop( headDropFeature.UNCOMMON_DROP_CHANCE, SoundEvents.ENTITY_WOLF_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMwNTgzZGJhOGVhNjE0MzA1ZGIwMTBiYWJkYzViYjQ0ZTlhMjAwMzMxMWIzOTlkODk2NWU3NzJkZDAxOTFmYiJ9fX0=" ) );
		
		put( "zoglin", new HeadDrop( headDropFeature.LEGENDARY_DROP_CHANCE, SoundEvents.ENTITY_ZOGLIN_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUzNDkzYTk1NmJmZDc1ODhlZDFhOGVhODU4NzU5NjY3NjU5ZDU4MTAwY2JlY2Q2ZDk2Y2NjMGNhOWIzNjkyMyJ9fX0=" ) );
		put( "zombie", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_AMBIENT, "" ) );
		put( "zombie_horse", new HeadDrop( headDropFeature.ALWAYS_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjYxOGZmYmUxY2ZhMjA1OGZlODBhMDY1ZjcwYzEyOGMyMjVhMWUwYmM5ZGVhZjhiMzhiMDM5NTQ0M2Y0MDkwOSJ9fX0=" ) );
		
		put( "zombie_villager", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=" ) );
		put( "zombie_armorer", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2NzllMDM0NzY3ZDUxODY2MGQ5NDE2ZGM1ZWFmMzE5ZDY5NzY4MmFjNDBjODg2ZTNjMmJjOGRmYTFkZTFkIn19fQ==" ) );
		put( "zombie_butcher", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNjZThkNmNlNDEyNGNlYzNlODRhODUyZTcwZjUwMjkzZjI0NGRkYzllZTg1NzhmN2Q2ZDg5MjllMTZiYWQ2OSJ9fX0=" ) );
		put( "zombie_cartographer", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTYwODAwYjAxMDEyZTk2M2U3YzIwYzhiYTE0YjcwYTAyNjRkMTQ2YTg1MGRlZmZiY2E3YmZlNTEyZjRjYjIzZCJ9fX0=" ) );
		put( "zombie_cleric", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ODU3OGJlMGUxMjE3MjczNGE3ODI0MmRhYjE0OTY0YWJjODVhYjliNTk2MzYxZjdjNWRhZjhmMTRhMGZlYiJ9fX0=" ) );
		put( "zombie_farmer", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc3ZDQxNWY5YmFhNGZhNGI1ZTA1OGY1YjgxYmY3ZjAwM2IwYTJjOTBhNDgzMWU1M2E3ZGJjMDk4NDFjNTUxMSJ9fX0=" ) );
		put( "zombie_fisherman", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkwNWQ1M2ZlNGZhZWIwYjMxNWE2ODc4YzlhYjgxYjRiZTUyYzMxY2Q0NzhjMDI3ZjBkN2VjZTlmNmRhODkxNCJ9fX0=" ) );
		put( "zombie_fletcher", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVhMjZhYzBlMjU0OThhZGFkYTRlY2VhNThiYjRlNzZkYTMyZDVjYTJkZTMwN2VmZTVlNDIxOGZiN2M1ZWY4OSJ9fX0=" ) );
		put( "zombie_leatherworker", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=" ) );
		put( "zombie_librarian", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIyMTFhMWY0MDljY2E0MjQ5YzcwZDIwY2E4MDM5OWZhNDg0NGVhNDE3NDU4YmU5ODhjYzIxZWI0Nzk3Mzc1ZSJ9fX0=" ) );
		put( "zombie_mason", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=" ) );
		put( "zombie_nitwit", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=" ) );
		put( "zombie_shepherd", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkxMzkxYmVmM2E0NmVmMjY3ZDNiNzE3MTA4NmJhNGM4ZDE3ZjJhNmIwZjgzZmEyYWMzMGVmZTkxNGI3YzI0OSJ9fX0=" ) );
		put( "zombie_toolsmith", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=" ) );
		put( "zombie_weaponsmith", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3MDg5NGI1Y2MzMDVkODdhYTA4YzNiNGIwODU4N2RiNjhmZjI5ZTdhM2VmMzU0Y2FkNmFiY2E1MGU1NTI4YiJ9fX0=" ) );
		
		put( "zombified_piglin", new HeadDrop( headDropFeature.RARE_DROP_CHANCE, SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRmMDMxMjhiMDAyYTcwNzA4ZDY4MjVlZDZjZjU0ZGRmNjk0YjM3NjZkNzhkNTY0OTAzMGIxY2I4YjM0YzZmYSJ9fX0=" ) );
	}};
	
	public static List<String> VARIED = List.of(
			"axolotl",
			"bee",
			"cat",
			"cow",
			"creeper",
			"fox",
			"frog",
			"goat",
			"horse",
			"llama",
			"mooshroom",
			"panda",
			"parrot",
			"rabbit",
			"sheep",
			"strider",
			"trader_llama",
			"vex",
			"villager",
			"wither",
			"wolf",
			"zombie_villager"
	);
	
	public static List<String> VANILLA_HEADS = List.of(
			"creeper",
			"dragon",
			"piglin",
			"skeleton",
			"zombie"
	);
	
	public static List<String> CREATIVE = List.of(
			"illusioner",
			"zombie_horse"
	);
	
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	
	public static void execute() {
		registerHeadDrop();
	}
	
	private static void registerHeadDrop() {
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
			if( entity.isPlayer() && MathUtil.hasChance( Headhunters.CONFIG.playerHeadDropChance ) ) {
				if( null != damageSource.getAttacker() ) {
					if (damageSource.getAttacker().isPlayer()) {
						entity.dropStack( getPlayerHeadStack((PlayerEntity) entity) );
					} // if
				} // if
			} else if( MOB_DROPS.containsKey( entity.getType().getUntranslatedName() ) ) {
				HeadDrop drop = MOB_DROPS.get( entity.getType().getUntranslatedName() );
				
				if( null != drop && null != damageSource.getAttacker() ) {
					if( damageSource.getAttacker().isPlayer() ) {
						World world = entity.getWorld();
						Entity source = damageSource.getSource();
						
						if( null != source ) {
							ItemStack weapon = damageSource.getWeaponStack();
							
							float dropChance = drop.DROP_CHANCE;
							
							if( null != weapon ) {
								ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(weapon);
								DynamicRegistryManager drm = world.getRegistryManager();
								
								if( null != drm ) {
									Registry<Enchantment> registry = drm.get(RegistryKeys.ENCHANTMENT);
									Enchantment lootingEnchantment = registry.get(Enchantments.LOOTING);
									RegistryEntry<Enchantment> lootingEntry = registry.getEntry(lootingEnchantment);
									
									int enchantmentLevel = enchantments.getLevel( lootingEntry );
									
									if( enchantmentLevel > 0 ) {
										dropChance = dropChance * (1 + ( Headhunters.CONFIG.lootingBonusPerLevel * enchantmentLevel ));
									} // if
								} // if
							} // if
							
							if (MathUtil.hasChance(dropChance)) {
								ItemStack headStack = buildHeadStack(entity, damageSource.getAttacker().getUuid(), drop.TEXTURE, drop.NOTE_BLOCK_SOUND, drop.DROP_CHANCE);
								
								if (!headStack.isEmpty()) {
									entity.dropStack(headStack);
								} // if
							} // if
						} // if
					} // if
				} // if
			} // if
			
			return true;
		});
	}
	
	public static ItemStack getPlayerHeadStack( PlayerEntity player ) {
		return getPlayerHeadStack( player.getServer(), player.getUuid() );
	}
	
	public static ItemStack getPlayerHeadStack( MinecraftServer server, UUID uuid ) {
		ItemStack headStack = new ItemStack( Blocks.PLAYER_HEAD, 1 );
		headStack.set(DataComponentTypes.MAX_STACK_SIZE, 64);
//		headStack.set(DataComponentTypes.NOTE_BLOCK_SOUND, SoundEvents.ENTITY_PLAYER_BURP.getId());
		
		if( null != server ) {
			MinecraftSessionService sessionService = server.getSessionService();
			ProfileResult profile = sessionService.fetchProfile(uuid, false);
			
			if( null != profile ) {
				ProfileComponent profileComponent = new ProfileComponent(profile.profile());
				
				headStack.set(DataComponentTypes.PROFILE, profileComponent );
			} // if
		} // if
		
		return headStack;
	}
	
	public static ItemStack buildHeadStack(Entity entity, UUID attackerUuid, @NotNull String texture, SoundEvent sound, float dropChance ) {
		String entityName = Text.translatable( entity.getType().getTranslationKey() ).getString().toLowerCase();
		String entityType = entityName.replace(" ","_");
		
		if( VARIED.contains( entity.getType().getUntranslatedName() ) ) {
			String variant = getHeadVariant( entity );
			
			if( MOB_DROPS.containsKey( variant ) ) {
				HeadDrop drop = MOB_DROPS.get(variant);
				
				return createHeadStack(entityType, variant, DEFAULT_UUID, drop.TEXTURE, drop.NOTE_BLOCK_SOUND, dropChance);
			} // if
		} // if
		
		return createHeadStack( entityType, entityName, DEFAULT_UUID, texture, sound, dropChance );
	}
	
	public static ItemStack buildHeadStack(String entityType, @NotNull String texture, SoundEvent sound, float dropChance ) {
		String entityName = formatEntityName( entityType );
		
		if( entityType.contains("_") ) {
			int lastUnderscore = entityType.lastIndexOf('_');
			
			if (lastUnderscore != -1) {
				entityType = entityType.substring(lastUnderscore + 1);
				
				if(entityType.equals("projectile")) {
					entityType = "wither";
				} // if
			} // if
		} // if
		
		return createHeadStack( entityType, entityName, DEFAULT_UUID, texture, sound, dropChance );
	}
	
	public static ItemStack createHeadStack(String entityType, String entityName, UUID attackerUuid, @NotNull String texture, SoundEvent sound, float dropChance ) {
		Headhunters.LOGGER.info( entityName + " Head" );
		
		if( VANILLA_HEADS.contains( entityName ) ) {
			return getVanillaMobHead( entityName );
		} // if
		
		ItemStack headStack = new ItemStack(Items.PLAYER_HEAD, 1);
		headStack.set(DataComponentTypes.MAX_STACK_SIZE, 64);
		headStack.set(DataComponentTypes.NOTE_BLOCK_SOUND, sound.getId());
		
		Rarity rarity = Rarity.COMMON;
		
		if( UNCOMMON_DROP_CHANCE == dropChance ) {
			rarity = Rarity.UNCOMMON;
		} else if( RARE_DROP_CHANCE == dropChance ) {
			rarity = Rarity.RARE;
		} else if( EPIC_DROP_CHANCE == dropChance ) {
			rarity = Rarity.EPIC;
		} else if( LEGENDARY_DROP_CHANCE == dropChance ) {
			rarity = Rarity.EPIC;
		} else if( ALWAYS_DROP_CHANCE == dropChance ) {
			rarity = Rarity.EPIC;
		} // if, else if
		
		headStack.set(DataComponentTypes.RARITY, rarity);
		
		if( !texture.isEmpty() ) {
			GameProfile gameProfile = new GameProfile( attackerUuid, entityType );
			
			Property property = new Property("textures", texture);
			gameProfile.getProperties().put("textures", property);

			ProfileComponent profile = new ProfileComponent(gameProfile);

			headStack.set(DataComponentTypes.PROFILE, profile);
		} // if
		
		headStack.set(DataComponentTypes.ITEM_NAME, Text.of( formatEntityName( entityName ) + " Head" ));
		
		return headStack;
	}
	
	public static ItemStack getVanillaMobHead( String entityName ) {
		Block block = switch (entityName) {
			case "creeper" -> Blocks.CREEPER_HEAD;
			case "dragon" -> Blocks.DRAGON_HEAD;
			case "piglin" -> Blocks.PIGLIN_HEAD;
			case "skeleton" -> Blocks.SKELETON_SKULL;
			case "zombie" -> Blocks.ZOMBIE_HEAD;
			default -> Blocks.PLAYER_HEAD;
		};
		
		return new ItemStack( block, 1);
	}
	
	public static String getHeadVariant( Entity entity ) {
		String entityName = getEntityName( entity );
		
		return switch (entityName) {
			case "axolotl" -> getAxolotlVariant((AxolotlEntity) entity);
			case "bee" -> getBeeVariant((BeeEntity) entity);
			case "cat" -> getCatVariant((CatEntity) entity);
			case "creeper" -> getCreeperVariant((CreeperEntity) entity);
			case "fox" -> getFoxVariant((FoxEntity) entity);
			case "frog" -> getFrogVariant((FrogEntity) entity);
			case "goat" -> getGoatVariant((GoatEntity) entity);
			case "horse" -> getHorseVariant((HorseEntity) entity);
			case "llama" -> getLlamaVariant((LlamaEntity) entity);
			case "mooshroom" -> getMooshroomVariant((MooshroomEntity) entity);
			case "panda" -> getPandaVariant((PandaEntity) entity);
			case "parrot" -> getParrotVariant((ParrotEntity) entity);
			case "rabbit" -> getRabbitVariant((RabbitEntity) entity);
			case "sheep" -> getSheepVariant((SheepEntity) entity);
			case "strider" -> getStriderVariant((StriderEntity) entity);
			case "trader_llama" -> getLlamaVariant((TraderLlamaEntity) entity);
			case "vex" -> getVexVariant((VexEntity) entity);
			case "villager" -> getVillagerVariant((VillagerEntity) entity);
			case "wither" -> getWitherVariant((WitherEntity) entity);
			case "wolf" -> getWolfVariant((WolfEntity) entity);
			case "zombie_villager" -> getZombieVillagerVariant((ZombieVillagerEntity) entity);
			default -> entityName;
		};
	}
	
	public static String getEntityName( Entity entity ) {
		return entity.getType().getUntranslatedName();
	}
	
	public static String formatEntityId( String id, String entityName ) {
		id = id
				.toLowerCase()
				.replace("minecraft:","");
		
		return id + "_" + entityName;
	}
	
	public static String formatEntityName( String entityName ) {
		entityName = entityName
				.toLowerCase()
				.replace("_"," ");
		
		return StringUtil.capitalizeAll( entityName );
	}
	
	public static String getAxolotlVariant(@NotNull AxolotlEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getBeeVariant(@NotNull BeeEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( entity.hasAngerTime() ) {
			if( entity.hasNectar() ) {
				return formatEntityId( "angry_pollinated", entityName );
			} // if
			
			return formatEntityId( "angry", entityName );
		} else if( entity.hasNectar() ) {
			return formatEntityId( "pollinated", entityName );
		} // if, else if
		
		return entityName;
	}
	
	public static String getCatVariant(@NotNull CatEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getTexture().getPath().isEmpty() ) {
			return formatEntityId( entity.getTexture().getPath().replace("textures/entity/cat/","").replace(".png",""), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getCreeperVariant(@NotNull CreeperEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( entity.getDataTracker().get( CHARGED ) ) {
			return formatEntityId( "charged", entityName );
		} // if
		
		return entityName;
	}
	
	public static String getFoxVariant(@NotNull FoxEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getFrogVariant(@NotNull FrogEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().value().texture().getPath().isEmpty() ) {
			return formatEntityId( entity.getVariant().value().texture().getPath().replace("textures/entity/frog/","").replace(".png",""), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getGoatVariant(@NotNull GoatEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.isScreaming() ) {
			return formatEntityId( "screaming", entityName );
		} // if
		
		return entityName;
	}
	
	public static String getHorseVariant(@NotNull HorseEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getLlamaVariant(@NotNull LlamaEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getMooshroomVariant(@NotNull MooshroomEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getPandaVariant(@NotNull PandaEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getMainGene().toString().isEmpty() ) {
			return formatEntityId( entity.getMainGene().toString(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getParrotVariant(@NotNull ParrotEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getRabbitVariant(@NotNull RabbitEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( entity.hasCustomName() && Text.of("Toast") == entity.getDisplayName() ) {
			return formatEntityId( "toast", entityName );
		} // if
		
		if( entity.getVariant().getId() == 99 ) {
			return formatEntityId( "killer", entityName );
		} // if
		
		if( !entity.getVariant().name().isEmpty() ) {
			return formatEntityId( entity.getVariant().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getSheepVariant(@NotNull SheepEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( entity.hasCustomName() && entity.getName().contains(Text.of("jeb_")) ) {
			return formatEntityId( "jeb", entityName );
		} // if
		
		if( !entity.getColor().name().isEmpty() ) {
			return formatEntityId( entity.getColor().name(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getStriderVariant(@NotNull StriderEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( entity.isCold() ) {
			return formatEntityId( "cold", entityName );
		} // if
		
		return entityName;
	}
	
	public static String getVexVariant(@NotNull VexEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( entity.isCharging() ) {
			return formatEntityId( "angry", entityName );
		} // if
		
		return entityName;
	}
	
	public static String getVillagerVariant(@NotNull VillagerEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVillagerData().getProfession().id().equals("none") ) {
			return formatEntityId( entity.getVillagerData().getProfession().id(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getWitherVariant(@NotNull WitherEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( MathUtil.hasChance( EPIC_DROP_CHANCE ) ) {
			return formatEntityId( "wither_projectile", entityName );
		} else if( MathUtil.hasChance( LEGENDARY_DROP_CHANCE ) ) {
			return formatEntityId( "blue_wither_projectile", entityName );
		} // if, else if
		
		return entityName;
	}
	
	public static String getWolfVariant(@NotNull WolfEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVariant().getIdAsString().isEmpty() ) {
			if( entity.hasAngerTime() ) {
				return formatEntityId( "angry_" + entity.getVariant().getIdAsString(), entityName );
			} // if
			
			return formatEntityId( entity.getVariant().getIdAsString(), entityName );
		} // if
		
		return entityName;
	}
	
	public static String getZombieVillagerVariant(@NotNull ZombieVillagerEntity entity ) {
		String entityName = getEntityName( entity );
		
		if( !entity.getVillagerData().getProfession().id().equals("none") ) {
			return formatEntityId( entityName, entity.getVillagerData().getProfession().id() );
		} // if
		
		return entityName;
	}
	
}
