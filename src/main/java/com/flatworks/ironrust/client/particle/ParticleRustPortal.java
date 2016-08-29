package com.flatworks.ironrust.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.world.World;

/**
 * @author sjx233
 */
public class ParticleRustPortal extends ParticlePortal {
    public ParticleRustPortal(World world, double posX, double posY, double posZ, double motionX,
            double motionY, double motionZ) {
        super(world, posX, posY, posZ, motionX, motionY, motionZ);
        float f = this.rand.nextFloat() * 0.6f + 0.4f;
        this.particleRed = f * 0.4f;
        this.particleGreen = f * 0.3f;
        this.particleBlue = f * 0.2f;
    }
    
    public static class Factory implements IParticleFactory {
        @Override
        public Particle getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn,
                double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... params) {
            return new ParticleRustPortal(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn,
                    zSpeedIn);
        }
    }
}
