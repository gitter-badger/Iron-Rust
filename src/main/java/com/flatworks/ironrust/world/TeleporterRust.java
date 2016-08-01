package com.flatworks.ironrust.world;

import java.util.HashMap;
import java.util.Random;

import com.flatworks.ironrust.IronRustMod;
import com.flatworks.ironrust.block.BlockRustPortal;
import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * @author sjx233
 */
public class TeleporterRust extends Teleporter {
    public static final HashMap<Entity, Boolean> inPortal = Maps.newHashMap();
    public static final HashMap<Entity, BlockPos> lastPortalPos = Maps.newHashMap();
    public static final HashMap<Entity, Vec3d> lastPortalVec = Maps.newHashMap();
    public static final HashMap<Entity, EnumFacing> teleportDirection = Maps.newHashMap();
    public static final HashMap<Entity, Integer> timeUntilPortal = Maps.newHashMap();
    public static final HashMap<Entity, Integer> portalCounter = Maps.newHashMap();
    
    private final WorldServer worldServerInstance;
    private final Random random;
    private final Long2ObjectMap<PortalPosition> destinationCoordinateCache =
            new Long2ObjectOpenHashMap<PortalPosition>(4096);
    
    public TeleporterRust(WorldServer worldIn) {
        super(worldIn);
        this.worldServerInstance = worldIn;
        this.random = new Random(worldIn.getSeed());
    }
    
    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {
        if (this.worldServerInstance.provider.getDimensionType().getId() != 1) {
            if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
                this.makePortal(entityIn);
                this.placeInExistingPortal(entityIn, rotationYaw);
            }
        }
    }
    
    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        double minDistance = -1d;
        int entityBlockX = MathHelper.floor_double(entityIn.posX);
        int entityBlockZ = MathHelper.floor_double(entityIn.posZ);
        boolean notCached = true;
        BlockPos portalPos = BlockPos.ORIGIN;
        long chunkIndex = ChunkPos.chunkXZ2Int(entityBlockX, entityBlockZ);
        
        if (this.destinationCoordinateCache.containsKey(chunkIndex)) {
            PortalPosition cachedPos = this.destinationCoordinateCache.get(chunkIndex);
            minDistance = 0d;
            portalPos = cachedPos;
            cachedPos.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
            notCached = false;
        } else {
            BlockPos entityPos = new BlockPos(entityIn);
            for (int offsetX = -128; offsetX <= 128; ++offsetX) {
                BlockPos tryPortalPos;
                for (int offsetZ = -128; offsetZ <= 128; ++offsetZ) {
                    for (BlockPos pos = entityPos.add(offsetX,
                            this.worldServerInstance.getActualHeight() - 1 - entityPos.getY(),
                            offsetZ); pos.getY() >= 0; pos = tryPortalPos) {
                        tryPortalPos = pos.down();
                        
                        if (this.worldServerInstance.getBlockState(pos)
                                .getBlock() == IronRustMod.RUST_PORTAL) {
                            for (tryPortalPos =
                                    pos.down(); this.worldServerInstance.getBlockState(tryPortalPos)
                                            .getBlock() == IronRustMod.RUST_PORTAL; tryPortalPos =
                                                    tryPortalPos.down()) {
                                pos = tryPortalPos;
                            }
                            double distance = pos.distanceSq(entityPos);
                            if (minDistance < 0d || distance < minDistance) {
                                minDistance = distance;
                                portalPos = pos;
                            }
                        }
                    }
                }
            }
        }
        
        if (minDistance >= 0d) {
            if (notCached) {
                this.destinationCoordinateCache.put(chunkIndex, new PortalPosition(portalPos,
                        this.worldServerInstance.getTotalWorldTime()));
            }
            
            PatternHelper patternHelper = ((BlockRustPortal) IronRustMod.RUST_PORTAL)
                    .createPatternHelper(this.worldServerInstance, portalPos);
            
            double tpPosX = portalPos.getX() + 0.5d;
            double tpPosY = patternHelper.getFrontTopLeft().getY() + 1
                    - lastPortalVec.get(entityIn).yCoord * patternHelper.getHeight();
            double tpPosZ = portalPos.getZ() + 0.5d;
            
            double sidePos = patternHelper.getForwards().getAxis() == EnumFacing.Axis.X
                    ? (double) patternHelper.getFrontTopLeft().getZ()
                    : (double) patternHelper.getFrontTopLeft().getX();
            
            if (patternHelper.getForwards().rotateY()
                    .getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                sidePos += 1d;
            }
            
            if (patternHelper.getForwards().getAxis() == EnumFacing.Axis.X) {
                tpPosZ = sidePos + (1d - lastPortalVec.get(entityIn).xCoord)
                        * patternHelper.getWidth()
                        * patternHelper.getForwards().rotateY().getAxisDirection().getOffset();
            } else {
                tpPosX = sidePos + (1d - lastPortalVec.get(entityIn).xCoord)
                        * patternHelper.getWidth()
                        * patternHelper.getForwards().rotateY().getAxisDirection().getOffset();
            }
            
            float xxDirection = 0f;
            float zzDirection = 0f;
            float xzDirection = 0f;
            float zxDirection = 0f;
            
            if (patternHelper.getForwards().getOpposite() == teleportDirection.get(entityIn)) {
                xxDirection = 1f;
                zzDirection = 1f;
            } else if (patternHelper.getForwards().getOpposite() == teleportDirection.get(entityIn)
                    .getOpposite()) {
                xxDirection = -1f;
                zzDirection = -1f;
            } else if (patternHelper.getForwards().getOpposite() == teleportDirection.get(entityIn)
                    .rotateY()) {
                xzDirection = 1f;
                zxDirection = -1f;
            } else {
                xzDirection = -1f;
                zxDirection = 1f;
            }
            
            double originalMotionX = entityIn.motionX;
            double originalMotionZ = entityIn.motionZ;
            entityIn.motionX = originalMotionX * xxDirection + originalMotionZ * zxDirection;
            entityIn.motionZ = originalMotionX * xzDirection + originalMotionZ * zzDirection;
            entityIn.rotationYaw = rotationYaw
                    - teleportDirection.get(entityIn).getOpposite().getHorizontalIndex() * 90
                    + patternHelper.getForwards().getHorizontalIndex() * 90;
            
            if (entityIn instanceof EntityPlayerMP) {
                ((EntityPlayerMP) entityIn).connection.setPlayerLocation(tpPosX, tpPosY, tpPosZ,
                        entityIn.rotationYaw, entityIn.rotationPitch);
            } else {
                entityIn.setLocationAndAngles(tpPosX, tpPosY, tpPosZ, entityIn.rotationYaw,
                        entityIn.rotationPitch);
            }
            
            return true;
        }
        return false;
    }
    
    @Override
    public boolean makePortal(Entity entityIn) {
        double d0 = -1d;
        int j = MathHelper.floor_double(entityIn.posX);
        int k = MathHelper.floor_double(entityIn.posY);
        int l = MathHelper.floor_double(entityIn.posZ);
        int i1 = j;
        int j1 = k;
        int k1 = l;
        int l1 = 0;
        int i2 = this.random.nextInt(4);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        
        for (int j2 = j - 16; j2 <= j + 16; ++j2) {
            double d1 = j2 + 0.5d - entityIn.posX;
            
            for (int l2 = l - 16; l2 <= l + 16; ++l2) {
                double d2 = l2 + 0.5d - entityIn.posZ;
                label146: for (int j3 =
                        this.worldServerInstance.getActualHeight() - 1; j3 >= 0; --j3) {
                    if (this.worldServerInstance
                            .isAirBlock(blockpos$mutableblockpos.setPos(j2, j3, l2))) {
                        while (j3 > 0 && this.worldServerInstance
                                .isAirBlock(blockpos$mutableblockpos.setPos(j2, j3 - 1, l2))) {
                            --j3;
                        }
                        
                        for (int k3 = i2; k3 < i2 + 4; ++k3) {
                            int l3 = k3 % 2;
                            int i4 = 1 - l3;
                            
                            if (k3 % 4 >= 2) {
                                l3 = -l3;
                                i4 = -i4;
                            }
                            
                            for (int j4 = 0; j4 < 3; ++j4) {
                                for (int k4 = 0; k4 < 4; ++k4) {
                                    for (int l4 = -1; l4 < 4; ++l4) {
                                        int i5 = j2 + (k4 - 1) * l3 + j4 * i4;
                                        int j5 = j3 + l4;
                                        int k5 = l2 + (k4 - 1) * i4 - j4 * l3;
                                        blockpos$mutableblockpos.setPos(i5, j5, k5);
                                        
                                        if (l4 < 0
                                                && !this.worldServerInstance
                                                        .getBlockState(blockpos$mutableblockpos)
                                                        .getMaterial().isSolid()
                                                || l4 >= 0 && !this.worldServerInstance
                                                        .isAirBlock(blockpos$mutableblockpos)) {
                                            continue label146;
                                        }
                                    }
                                }
                            }
                            
                            double d5 = j3 + 0.5d - entityIn.posY;
                            double d7 = d1 * d1 + d5 * d5 + d2 * d2;
                            
                            if (d0 < 0d || d7 < d0) {
                                d0 = d7;
                                i1 = j2;
                                j1 = j3;
                                k1 = l2;
                                l1 = k3 % 4;
                            }
                        }
                    }
                }
            }
        }
        
        if (d0 < 0d) {
            for (int l5 = j - 16; l5 <= j + 16; ++l5) {
                double d3 = l5 + 0.5d - entityIn.posX;
                
                for (int j6 = l - 16; j6 <= l + 16; ++j6) {
                    double d4 = j6 + 0.5d - entityIn.posZ;
                    label567:
                    
                    for (int i7 = this.worldServerInstance.getActualHeight() - 1; i7 >= 0; --i7) {
                        if (this.worldServerInstance
                                .isAirBlock(blockpos$mutableblockpos.setPos(l5, i7, j6))) {
                            while (i7 > 0 && this.worldServerInstance
                                    .isAirBlock(blockpos$mutableblockpos.setPos(l5, i7 - 1, j6))) {
                                --i7;
                            }
                            
                            for (int k7 = i2; k7 < i2 + 2; ++k7) {
                                int j8 = k7 % 2;
                                int j9 = 1 - j8;
                                
                                for (int j10 = 0; j10 < 4; ++j10) {
                                    for (int j11 = -1; j11 < 4; ++j11) {
                                        int j12 = l5 + (j10 - 1) * j8;
                                        int i13 = i7 + j11;
                                        int j13 = j6 + (j10 - 1) * j9;
                                        blockpos$mutableblockpos.setPos(j12, i13, j13);
                                        
                                        if (j11 < 0
                                                && !this.worldServerInstance
                                                        .getBlockState(blockpos$mutableblockpos)
                                                        .getMaterial().isSolid()
                                                || j11 >= 0 && !this.worldServerInstance
                                                        .isAirBlock(blockpos$mutableblockpos)) {
                                            continue label567;
                                        }
                                    }
                                }
                                
                                double d6 = i7 + 0.5d - entityIn.posY;
                                double d8 = d3 * d3 + d6 * d6 + d4 * d4;
                                
                                if (d0 < 0d || d8 < d0) {
                                    d0 = d8;
                                    i1 = l5;
                                    j1 = i7;
                                    k1 = j6;
                                    l1 = k7 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        int i6 = i1;
        int k2 = j1;
        int k6 = k1;
        int l6 = l1 % 2;
        int i3 = 1 - l6;
        
        if (l1 % 4 >= 2) {
            l6 = -l6;
            i3 = -i3;
        }
        
        if (d0 < 0d) {
            j1 = MathHelper.clamp_int(j1, 70, this.worldServerInstance.getActualHeight() - 10);
            k2 = j1;
            
            for (int j7 = -1; j7 <= 1; ++j7) {
                for (int l7 = 1; l7 < 3; ++l7) {
                    for (int k8 = -1; k8 < 3; ++k8) {
                        int k9 = i6 + (l7 - 1) * l6 + j7 * i3;
                        int k10 = k2 + k8;
                        int k11 = k6 + (l7 - 1) * i3 - j7 * l6;
                        boolean flag = k8 < 0;
                        this.worldServerInstance.setBlockState(new BlockPos(k9, k10, k11),
                                flag ? IronRustMod.RUST_BLOCK.getDefaultState()
                                        : Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        
        IBlockState iblockstate = IronRustMod.RUST_PORTAL.getDefaultState()
                .withProperty(BlockPortal.AXIS, l6 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
        
        for (int i8 = 0; i8 < 4; ++i8) {
            for (int l8 = 0; l8 < 4; ++l8) {
                for (int l9 = -1; l9 < 4; ++l9) {
                    int l10 = i6 + (l8 - 1) * l6;
                    int l11 = k2 + l9;
                    int k12 = k6 + (l8 - 1) * i3;
                    boolean flag1 = l8 == 0 || l8 == 3 || l9 == -1 || l9 == 3;
                    this.worldServerInstance.setBlockState(new BlockPos(l10, l11, k12),
                            flag1 ? IronRustMod.RUST_BLOCK.getDefaultState() : iblockstate, 2);
                }
            }
            
            for (int i9 = 0; i9 < 4; ++i9) {
                for (int i10 = -1; i10 < 4; ++i10) {
                    int i11 = i6 + (i9 - 1) * l6;
                    int i12 = k2 + i10;
                    int l12 = k6 + (i9 - 1) * i3;
                    BlockPos blockpos = new BlockPos(i11, i12, l12);
                    this.worldServerInstance.notifyNeighborsOfStateChange(blockpos,
                            this.worldServerInstance.getBlockState(blockpos).getBlock());
                }
            }
        }
        
        return true;
    }
    
    @Override
    public void removeStalePortalLocations(long worldTime) {
        if (worldTime % 100L == 0) {
            long i = worldTime - 300L;
            ObjectIterator<PortalPosition> it = this.destinationCoordinateCache.values().iterator();
            while (it.hasNext()) {
                PortalPosition pos = it.next();
                if (pos == null || pos.lastUpdateTime < i) {
                    it.remove();
                }
            }
        }
    }
}
