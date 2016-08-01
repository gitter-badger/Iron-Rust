package com.flatworks.ironrust.client.particle;

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
}
