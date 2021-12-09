package me.pieking1215.waterdripsound.mixin.client;

import me.pieking1215.waterdripsound.WaterDripSoundConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashParticle.Provider.class)
public class MixinWaterSplashParticle {

    @Inject(at = @At("HEAD"), method = "createParticle", cancellable = true)
    private void createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<Particle> callback) {
        // if mod is enabled in the config
        if(WaterDripSoundConfig.GENERAL.enabled.get()){
            // the splash when moving in water has speed, while drips and fishing splashes don't
            if(vx == 0 && vy == 0 && vz == 0) {
                // check that the block below isn't fluid since fishing splashes have water below
                if (clientWorld.getBlockState(new BlockPos(x, y - 1, z)).getFluidState().isEmpty()) {
                    // play the sound
                    float vol = Mth.clamp(WaterDripSoundConfig.GENERAL.volume.get().floatValue(), 0f, 1f);
                    if(WaterDripSoundConfig.GENERAL.useDripstoneSounds.get()) {
                        vol *= Math.random() * 0.7 + 0.3; // same as vanilla dripstone drips
                    }
                    clientWorld.playLocalSound(x, y, z, WaterDripSoundConfig.GENERAL.useDripstoneSounds.get() ? SoundEvents.POINTED_DRIPSTONE_DRIP_WATER : SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, WaterDripSoundConfig.GENERAL.soundCategory.get(), vol, 1f, false);
                }
            }
        }
    }
}
