package gay.lemmaeof.ducktor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ducktor implements ModInitializer {
	public static final String MODID = "ducktor";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final RegistryEntry<StatusEffect> REJUVENATION = Registry.registerReference(
			Registries.STATUS_EFFECT,
			Identifier.of(MODID, "rejuvenation"),
			new DucktorStatusEffect(StatusEffectCategory.BENEFICIAL, 0xC26D91)
	);

	public static final RegistryEntry<Potion> MAGNIFICENT_POTION = Registry.registerReference(
			Registries.POTION,
			Identifier.of(MODID, "magnificent"),
			new Potion()
	);

	public static final RegistryEntry<Potion> REJUVENATION_POTION = Registry.registerReference(
			Registries.POTION,
			Identifier.of(MODID, "rejuvenation"),
			new Potion("rejuvenation", new StatusEffectInstance(REJUVENATION, 1800, 1))
	);
	public static final RegistryEntry<Potion> LONG_REJUVENATION_POTION = Registry.registerReference(
			Registries.POTION,
			Identifier.of(MODID, "long_rejuvenation"),
			new Potion("rejuvenation", new StatusEffectInstance(REJUVENATION, 3600, 1))
	);
	public static final RegistryEntry<Potion> STRONG_REJUVENATION_POTION = Registry.registerReference(
			Registries.POTION,
			Identifier.of(MODID, "strong_rejuvenation"),
			new Potion("rejuvenation", new StatusEffectInstance(REJUVENATION, 900, 2))
	);

	public static final Item LOZENGE = Registry.register(
			Registries.ITEM,
			Identifier.of(MODID, "lozenge"),
			new Item(new Item.Settings().food(new FoodComponent.Builder()
					.nutrition(2)
					.saturationModifier(0.1F)
					.alwaysEdible()
					.statusEffect(new StatusEffectInstance(REJUVENATION, 450), 1f)
					.build()
			))
	);

	public static final ScentedCandleBlock SCENTED_CANDLE = Registry.register(
			Registries.BLOCK,
			Identifier.of(MODID, "scented_candle"),
			new ScentedCandleBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.PALE_PURPLE)
					.nonOpaque()
					.strength(0.1F)
					.sounds(BlockSoundGroup.CANDLE)
					.luminance(ScentedCandleBlock.STATE_TO_LUMINANCE)
					.pistonBehavior(PistonBehavior.DESTROY)
					.ticksRandomly()
			)
	);

	public static final WardingCandleBlock WARDING_CANDLE = Registry.register(
			Registries.BLOCK,
			Identifier.of(MODID, "warding_candle"),
			new WardingCandleBlock(AbstractBlock.Settings.create()
					.mapColor(MapColor.BRIGHT_RED)
					.nonOpaque()
					.strength(0.1F)
					.sounds(BlockSoundGroup.CANDLE)
					.luminance(ScentedCandleBlock.STATE_TO_LUMINANCE)
					.pistonBehavior(PistonBehavior.DESTROY)
					.ticksRandomly()
			)
	);

	@Override
	public void onInitialize() {
		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.WATER, Items.WARPED_FUNGUS, MAGNIFICENT_POTION);
			builder.registerPotionRecipe(MAGNIFICENT_POTION, Items.SWEET_BERRIES, REJUVENATION_POTION);
			builder.registerPotionRecipe(REJUVENATION_POTION, Items.REDSTONE, LONG_REJUVENATION_POTION);
			builder.registerPotionRecipe(REJUVENATION_POTION, Items.GLOWSTONE_DUST, STRONG_REJUVENATION_POTION);
		});
		Registry.register(Registries.ITEM, Identifier.of(MODID, "scented_candle"), new BlockItem(SCENTED_CANDLE, new Item.Settings()));
		Registry.register(Registries.ITEM, Identifier.of(MODID, "warding_candle"), new BlockItem(WARDING_CANDLE, new Item.Settings()));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.addAfter(Items.HONEY_BOTTLE, LOZENGE));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.addAfter(Items.PINK_CANDLE, SCENTED_CANDLE, WARDING_CANDLE));
	}
}
