package me.pieking1215.waterdripsound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("waterdripsound")
public class WaterDripSound {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "waterdripsound");
    public static final RegistryObject<SoundEvent> LAVA_DRIP_SOUND = SOUNDS.register(
            "lavadrip", () -> new SoundEvent(new ResourceLocation("waterdripsound", "lavadrip")));

    public WaterDripSound(){

        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WaterDripSoundConfig.spec);
            WaterDripSoundConfig.registerClothConfig();
        });
    }



}
