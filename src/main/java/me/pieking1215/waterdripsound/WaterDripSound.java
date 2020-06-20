package me.pieking1215.waterdripsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;

@Mod("waterdripsound")
public class WaterDripSound {
    private final Minecraft mc = Minecraft.getInstance();

    public WaterDripSound(){
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.spec);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load ev){
        if(Minecraft.getInstance().particles != null) {
            try{
                // get existing splash particle factory

                // do Map<ResourceLocation, IParticleFactory<?>> facts = Minecraft.getInstance().particles.factories;
                Map<ResourceLocation, IParticleFactory<?>> facts = ObfuscationReflectionHelper.getPrivateValue(ParticleManager.class, Minecraft.getInstance().particles, "field_178932_g");
                IParticleFactory pf = facts.get(ParticleTypes.SPLASH.getRegistryName());

                // check that it's the vanilla one
                if(pf instanceof SplashParticle.Factory){
                    // inject custom splash particle factory
                    Minecraft.getInstance().particles.registerFactory(ParticleTypes.SPLASH, SplashFactory2::new);
                    IParticleFactory npf = facts.get(ParticleTypes.SPLASH.getRegistryName());

                    // check that it worked
                    if(npf instanceof SplashFactory2){
                        // wrap the original factory to copy the sprite data
                        ((SplashFactory2) npf).wrap((SplashParticle.Factory)pf);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SplashFactory2 extends SplashParticle.Factory {
        public SplashFactory2(IAnimatedSprite p_i50679_1_) {
            super(p_i50679_1_);
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            // the splash particle is used in several ways, so there's some checks to tell if this is a splash from a drip.

            // if mod is enabled in the config
            if(Config.GENERAL.enabled.get()){
                // the splash when moving in water has speed, while drips and fishing splashes don't
                if(xSpeed == 0 && ySpeed == 0 && zSpeed == 0) {
                    // check that the block below isn't fluid since fishing splashes have water below
                    if (worldIn.getBlockState(new BlockPos(x, y - 1, z)).getFluidState().isEmpty()) {
                        // play the sound
                        float vol = MathHelper.clamp(Config.GENERAL.volume.get().floatValue(), 0f, 1f);
                        worldIn.playSound(x, y, z, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.AMBIENT, vol, 1f, false);
                    }
                }
            }

            // make the particle
            return super.makeParticle(typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        public void wrap(SplashParticle.Factory real){
            // do: this.spriteSet = real.spriteSet;
            IAnimatedSprite spr = ObfuscationReflectionHelper.getPrivateValue(SplashParticle.Factory.class, real, "field_217547_a");
            ObfuscationReflectionHelper.setPrivateValue(SplashParticle.Factory.class, this, spr, "field_217547_a");

        }
    }
}
