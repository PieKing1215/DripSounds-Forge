package me.pieking1215.waterdripsound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("waterdripsound")
public class WaterDripSound {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "waterdripsound");
    public static final RegistryObject<SoundEvent> LAVA_DRIP_SOUND = SOUNDS.register(
            "lavadrip", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("waterdripsound", "lavadrip")));

    public WaterDripSound(){

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WaterDripSoundConfig.spec);
            WaterDripSoundConfig.registerClothConfig();
        });
    }



}
