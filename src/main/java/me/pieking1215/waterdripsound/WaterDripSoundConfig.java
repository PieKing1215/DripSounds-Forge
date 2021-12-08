package me.pieking1215.waterdripsound;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fmlclient.ConfigGuiHandler;

public class WaterDripSoundConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Boolean> enabled;
        public final ForgeConfigSpec.ConfigValue<Double> volume;
        public final ForgeConfigSpec.ConfigValue<Integer> dripChance;
//        public final ForgeConfigSpec.ConfigValue<Boolean> useDripstoneSounds;
        public final ForgeConfigSpec.ConfigValue<SoundSource> soundCategory;

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
                    .define("soundCategory", SoundSource.AMBIENT);
            builder.pop();
        }
    }

    static void registerClothConfig() {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableComponent("config.waterdripsound.general"));
            builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/mossy_stone_bricks.png"));
            builder.transparentBackground();

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(new TranslatableComponent("key.waterdripsound.category"));
            general.addEntry(eb.startBooleanToggle(new TranslatableComponent("config.waterdripsound.enable"), GENERAL.enabled.get()).setDefaultValue(true).setSaveConsumer(GENERAL.enabled::set).build());
            general.addEntry(eb.startIntSlider(new TranslatableComponent("config.waterdripsound.volume"), (int)(GENERAL.volume.get() * 100), 0, 100).setDefaultValue(30).setTextGetter(integer -> new TextComponent("Volume: " + integer + "%")).setSaveConsumer(integer -> GENERAL.volume.set(integer / 100.0)).build());
            general.addEntry(eb.startIntSlider(new TranslatableComponent("config.waterdripsound.dripChance"), GENERAL.dripChance.get(), 1, 100).setDefaultValue(10).setTextGetter(integer -> new TextComponent("One in " + integer)).setSaveConsumer(GENERAL.dripChance::set).build());

            BooleanListEntry ble = eb.startBooleanToggle(new TranslatableComponent("config.waterdripsound.useDripstoneSounds"), false).setDefaultValue(false).setYesNoTextSupplier(aBoolean -> new TextComponent(ChatFormatting.GRAY + "Unavailable")).setTooltip(new TranslatableComponent("tooltip.config.waterdripsound.useDripstoneSounds.cannot")).build();
            ble.setEditable(false);
            general.addEntry(ble);

            general.addEntry(eb.startEnumSelector(new TranslatableComponent("config.waterdripsound.soundCategory"), SoundSource.class, GENERAL.soundCategory.get()).setDefaultValue(SoundSource.AMBIENT).setEnumNameProvider(anEnum -> new TranslatableComponent("soundCategory." + ((SoundSource)anEnum).getName())).setSaveConsumer(GENERAL.soundCategory::set).build());

            return builder.setSavingRunnable(spec::save).build();
        }));
    }

}