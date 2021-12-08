package me.pieking1215.waterdripsound;

import me.shedaniel.clothconfig2.forge.api.ConfigBuilder;
import me.shedaniel.clothconfig2.forge.api.ConfigCategory;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.gui.entries.BooleanListEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
//        public final ForgeConfigSpec.ConfigValue<Boolean> useDripstoneSounds;
        public final ForgeConfigSpec.ConfigValue<SoundCategory> soundCategory;

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
//            useDripstoneSounds = builder
//                    .comment("If enabled, uses the Dripstone water/lava drip sounds added in 1.17. If not, uses sounds from older versions of the mod. [false/true|default:true]")
//                    .translation("useDripstoneSounds.waterdripsound.config")
//                    .define("useDripstoneSounds", true);
            soundCategory = builder
                    .comment("Sound category [default:AMBIENT]")
                    .translation("soundCategory.waterdripsound.config")
                    .define("soundCategory", SoundCategory.AMBIENT);
            builder.pop();
        }
    }

    static void registerClothConfig() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslationTextComponent("config.waterdripsound.general"));
            builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/mossy_stone_bricks.png"));
            builder.transparentBackground();

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(new TranslationTextComponent("key.waterdripsound.category"));
            general.addEntry(eb.startBooleanToggle(new TranslationTextComponent("config.waterdripsound.enable"), GENERAL.enabled.get()).setDefaultValue(true).setSaveConsumer(GENERAL.enabled::set).build());
            general.addEntry(eb.startIntSlider(new TranslationTextComponent("config.waterdripsound.volume"), (int)(GENERAL.volume.get() * 100), 0, 100).setDefaultValue(30).setTextGetter(integer -> new StringTextComponent("Volume: " + integer + "%")).setSaveConsumer(integer -> GENERAL.volume.set(integer / 100.0)).build());
            general.addEntry(eb.startIntSlider(new TranslationTextComponent("config.waterdripsound.dripChance"), GENERAL.dripChance.get(), 1, 100).setDefaultValue(10).setTextGetter(integer -> new StringTextComponent("One in " + integer)).setSaveConsumer(GENERAL.dripChance::set).build());

            BooleanListEntry ble = eb.startBooleanToggle(new TranslationTextComponent("config.waterdripsound.useDripstoneSounds"), false).setDefaultValue(false).setYesNoTextSupplier(aBoolean -> new StringTextComponent(TextFormatting.GRAY + "Unavailable")).setTooltip(new TranslationTextComponent("tooltip.config.waterdripsound.useDripstoneSounds.cannot")).build();
            ble.setEditable(false);
            general.addEntry(ble);
            
            general.addEntry(eb.startEnumSelector(new TranslationTextComponent("config.waterdripsound.soundCategory"), SoundCategory.class, GENERAL.soundCategory.get()).setDefaultValue(SoundCategory.AMBIENT).setEnumNameProvider(anEnum -> new TranslationTextComponent("soundCategory." + ((SoundCategory)anEnum).getName())).setSaveConsumer(GENERAL.soundCategory::set).build());

            return builder.setSavingRunnable(spec::save).build();
        });
    }

}