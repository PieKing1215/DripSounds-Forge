package me.pieking1215.waterdripsound.mixin.client;

import me.pieking1215.waterdripsound.WaterDripSoundConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashParticle.Factory.class)
public class MixinWaterSplashParticle {

    @Inject(at = @At("HEAD"), method = "makeParticle", cancellable = true)
    private void createParticle(BasicParticleType defaultParticleType, World clientWorld, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<Particle> callback) {
        // if mod is enabled in the config
        if(WaterDripSoundConfig.GENERAL.enabled.get()){
            // the splash when moving in water has speed, while drips and fishing splashes don't
            if(vx == 0 && vy == 0 && vz == 0) {
                // check that the block below isn't fluid since fishing splashes have water below
                if (clientWorld.getBlockState(new BlockPos(x, y - 1, z)).getFluidState().isEmpty()) {
                    // play the sound
                    float vol = MathHelper.clamp(WaterDripSoundConfig.GENERAL.volume.get().floatValue(), 0f, 1f);
                    clientWorld.playSound(x, y, z, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.AMBIENT, vol, 1f, false);
                }
            }
        }
    }
}
