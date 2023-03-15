package me.pieking1215.waterdripsound.mixin.client;

import me.pieking1215.waterdripsound.WaterDripSound;
import me.pieking1215.waterdripsound.WaterDripSoundConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.particle.DripParticle$FallAndLandParticle")
public abstract class MixinFallAndLandDripParticle extends TextureSheetParticle {
    protected MixinFallAndLandDripParticle(ClientLevel clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/DripParticle$FallAndLandParticle;remove()V"), method = "postMoveUpdate")
    private void createParticle(CallbackInfo ci) {
        // if mod is enabled in the config
        if(WaterDripSoundConfig.GENERAL.enabled.get()){
            ClientLevel clientWorld = this.level;
            double x = this.x;
            double y = this.y;
            double z = this.z;
            DripParticle blp = (DripParticle)(TextureSheetParticle)this;
            Fluid particleFluid = ((DripParticleAccessor)blp).getType();
            FluidState belowFluid = clientWorld.getBlockState(new BlockPos((int)x, (int)y - 1, (int)z)).getFluidState();

            SoundEvent play = null;
            float volumeMod = 1f;
            float pitch = 1f;

            // if particle is lava, only play sound if landed on block or non-lava fluid (water)
            // if particle is water, only play sound if landed on block
            if (particleFluid == Fluids.LAVA && belowFluid.getType() != Fluids.LAVA) {
                play = WaterDripSoundConfig.GENERAL.useDripstoneSounds.get() ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : WaterDripSound.LAVA_DRIP_SOUND.get();
                if (!WaterDripSoundConfig.GENERAL.useDripstoneSounds.get()) {
                    pitch = 1f + (float)(Math.random() * 0.1f);
                    volumeMod = 0.5f;
                }
            } else if (particleFluid == Fluids.WATER && belowFluid.isEmpty()) {
                play = WaterDripSoundConfig.GENERAL.useDripstoneSounds.get() ? SoundEvents.POINTED_DRIPSTONE_DRIP_WATER : SoundEvents.BUBBLE_COLUMN_BUBBLE_POP;
            }

            if (play != null) {
                // play the sound
                float vol = Mth.clamp(WaterDripSoundConfig.GENERAL.volume.get().floatValue() * volumeMod, 0f, 1f);
                if(WaterDripSoundConfig.GENERAL.useDripstoneSounds.get()) {
                    vol *= Math.random() * 0.7 + 0.3; // same as vanilla dripstone drips
                }
                clientWorld.playLocalSound(x, y, z, play, WaterDripSoundConfig.GENERAL.soundCategory.get(), vol, pitch, false);
            }
        }
    }
}
