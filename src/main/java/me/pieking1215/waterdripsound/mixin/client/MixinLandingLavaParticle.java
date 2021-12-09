package me.pieking1215.waterdripsound.mixin.client;

import me.pieking1215.waterdripsound.WaterDripSound;
import me.pieking1215.waterdripsound.WaterDripSoundConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DripParticle.LavaLandProvider.class)
public class MixinLandingLavaParticle {

    @Inject(at = @At("HEAD"), method = "createParticle", cancellable = true)
    private void createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<Particle> callback) {
        // if mod is enabled in the config
        if(WaterDripSoundConfig.GENERAL.enabled.get()){
            // only play sound if landed on block or non-lava fluid (water)
            if (clientWorld.getBlockState(new BlockPos(x, y - 1, z)).getFluidState().getType() != Fluids.LAVA) {
                // play the sound
                float vol = Mth.clamp(WaterDripSoundConfig.GENERAL.volume.get().floatValue() * 0.5f, 0f, 1f);
                if(WaterDripSoundConfig.GENERAL.useDripstoneSounds.get()) {
                    vol *= Math.random() * 0.7 + 0.3; // same as vanilla dripstone drips
                }
                if(WaterDripSound.LAVA_DRIP_SOUND.isPresent() || WaterDripSoundConfig.GENERAL.useDripstoneSounds.get()) {
                    clientWorld.playLocalSound(x, y, z, WaterDripSoundConfig.GENERAL.useDripstoneSounds.get() ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : WaterDripSound.LAVA_DRIP_SOUND.get(), WaterDripSoundConfig.GENERAL.soundCategory.get(), vol, 1f + (float)(Math.random() * 0.1f), false);
                }
            }
        }
    }
}
