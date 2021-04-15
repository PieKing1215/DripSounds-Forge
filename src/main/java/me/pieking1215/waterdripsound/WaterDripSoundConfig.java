package me.pieking1215.waterdripsound;

import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

public class WaterDripSoundConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Boolean> enabled;
        public final ForgeConfigSpec.ConfigValue<Double> volume;
        public final ForgeConfigSpec.ConfigValue<Integer> dripChance;

        General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            enabled = builder
                    .comment("Enables/Disables the whole Mod [false/true|default:true]")
                    .translation("enable.waterdripsound.config")
                    .define("enableMod", true);
            volume = builder
                    .comment("Volume of water/lava drips [0.0-1.0|default:0.3]")
                    .translation("volume.waterdripsound.config")
                    .define("volume", 0.3);
            dripChance = builder
                    .comment("Chance of a drip forming each tick (one in X so lower is faster) [1-100|default:10]")
                    .translation("dripChance.waterdripsound.config")
                    .define("dripChance", 10);
            builder.pop();
        }
    }

    static void registerClothConfig() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("config.waterdripsound.general");
            builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/mossy_stone_bricks.png"));
            builder.transparentBackground();

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory("key.waterdripsound.category");
            general.addEntry(eb.startBooleanToggle("config.waterdripsound.enable", GENERAL.enabled.get()).setDefaultValue(true).setSaveConsumer(GENERAL.enabled::set).build());
            general.addEntry(eb.startIntSlider("config.waterdripsound.volume", (int)(GENERAL.volume.get() * 100), 0, 100).setDefaultValue(30).setTextGetter(integer -> "Volume: " + integer + "%").setSaveConsumer(integer -> GENERAL.volume.set(integer / 100.0)).build());
            general.addEntry(eb.startIntSlider("config.waterdripsound.dripChance", GENERAL.dripChance.get(), 1, 100).setDefaultValue(10).setTextGetter(integer -> "One in " + integer).setSaveConsumer(GENERAL.dripChance::set).build());

            return builder.setSavingRunnable(spec::save).build();
        });
    }

}