package me.pieking1215.waterdripsound.mixin.client;

import me.pieking1215.waterdripsound.WaterDripSound;
import me.pieking1215.waterdripsound.WaterDripSoundConfig;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DripParticle.LandingLavaFactory.class)
public class MixinLandingLavaParticle {

    @Inject(at = @At("HEAD"), method = "makeParticle", cancellable = true)
    private void createParticle(BasicParticleType defaultParticleType, World clientWorld, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<Particle> callback) {
        // if mod is enabled in the config
        if(WaterDripSoundConfig.GENERAL.enabled.get()){
            // only play sound if landed on block or non-lava fluid (water)
            if (clientWorld.getBlockState(new BlockPos(x, y - 1, z)).getFluidState().getFluid() != Fluids.LAVA) {
                // play the sound
                float vol = MathHelper.clamp(WaterDripSoundConfig.GENERAL.volume.get().floatValue() * 0.5f, 0f, 1f);
                if(WaterDripSound.LAVA_DRIP_SOUND.isPresent()) clientWorld.playSound(x, y, z, WaterDripSound.LAVA_DRIP_SOUND.get(), SoundCategory.AMBIENT, vol, 1f + (float)(Math.random() * 0.1f), false);
            }
        }
    }
}
