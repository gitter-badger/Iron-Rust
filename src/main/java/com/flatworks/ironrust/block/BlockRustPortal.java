package com.flatworks.ironrust.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.flatworks.ironrust.IronRustMod;
import com.flatworks.ironrust.client.particle.ParticleRustPortal;
import com.flatworks.ironrust.entity.EntityRustyCow;
import com.flatworks.ironrust.world.TeleporterRust;
import com.google.common.cache.LoadingCache;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRustPortal extends BlockBreakable {
    public static final PropertyEnum<Axis> AXIS =
            PropertyEnum.create("axis", Axis.class, Axis.X, Axis.Z);
    protected static final AxisAlignedBB X_AABB =
            new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB Z_AABB =
            new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB Y_AABB =
            new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
    
    public BlockRustPortal() {
        super(Material.PORTAL, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.X));
        this.setTickRandomly(true);
        this.setHardness(-1.0F);
        this.setSoundType(SoundType.GLASS);
        this.setLightLevel(0.75F);
        this.setUnlocalizedName("portalRust");
    }
    
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case X:
                return X_AABB;
            case Z:
                return Z_AABB;
            default:
                return Y_AABB;
        }
    }
    
    @Override
    @Nullable
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn,
            BlockPos pos) {
        return NULL_AABB;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (worldIn.provider.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning")
                && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
            int i = pos.getY();
            BlockPos blockpos;
            
            for (blockpos = pos; !worldIn.getBlockState(blockpos).isFullyOpaque()
                    && blockpos.getY() > 0; blockpos = blockpos.down()) {
            }
            
            if (i > 0 && !worldIn.getBlockState(blockpos.up()).isNormalCube()) {
                double x = blockpos.getX() + 0.5d;
                double y = blockpos.getY() + 1.1d;
                double z = blockpos.getZ() + 0.5d;
                EntityRustyCow entity = new EntityRustyCow(worldIn);
                entity.setLocationAndAngles(x, y, z,
                        MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360f), 0f);
                entity.rotationYawHead = entity.rotationYaw;
                entity.renderYawOffset = entity.rotationYaw;
                entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), null);
                worldIn.spawnEntityInWorld(entity);
                TeleporterRust.timeUntilPortal.put(entity, entity.getPortalCooldown());
            }
        }
    }
    
    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public static boolean trySpawnPortal(World worldIn, BlockPos pos) {
        Size sizeX = new Size(worldIn, pos, Axis.X);
        
        if (sizeX.isValid() && sizeX.portalBlockCount == 0) {
            sizeX.placePortalBlocks();
            return true;
        }
        
        Size sizeZ = new Size(worldIn, pos, Axis.Z);
        
        if (sizeZ.isValid() && sizeZ.portalBlockCount == 0) {
            sizeZ.placePortalBlocks();
            return true;
        }
        return false;
    }
    
    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        Axis axis = state.getValue(AXIS);
        
        switch (axis) {
            case X:
                Size sizeX = new Size(worldIn, pos, Axis.X);
                
                if (!sizeX.isValid() || sizeX.portalBlockCount < sizeX.width * sizeX.height) {
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                break;
            case Z:
                Size sizeZ = new Size(worldIn, pos, Axis.Z);
                
                if (!sizeZ.isValid() || sizeZ.portalBlockCount < sizeZ.width * sizeZ.height) {
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                break;
            default:
                break;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess,
            BlockPos pos, EnumFacing side) {
        pos = pos.offset(side);
        Axis axis = null;
        
        if (blockState.getBlock() == this) {
            axis = blockState.getValue(AXIS);
            
            if (axis == null) {
                return false;
            }
            
            if (axis == Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
                return false;
            }
            
            if (axis == Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
                return false;
            }
        }
        
        boolean flag = blockAccess.getBlockState(pos.west()).getBlock() == this
                && blockAccess.getBlockState(pos.west(2)).getBlock() != this;
        boolean flag1 = blockAccess.getBlockState(pos.east()).getBlock() == this
                && blockAccess.getBlockState(pos.east(2)).getBlock() != this;
        boolean flag2 = blockAccess.getBlockState(pos.north()).getBlock() == this
                && blockAccess.getBlockState(pos.north(2)).getBlock() != this;
        boolean flag3 = blockAccess.getBlockState(pos.south()).getBlock() == this
                && blockAccess.getBlockState(pos.south(2)).getBlock() != this;
        boolean flag4 = flag || flag1 || axis == Axis.X;
        boolean flag5 = flag2 || flag3 || axis == Axis.Z;
        return flag4 && side == EnumFacing.WEST ? true : flag4 && side == EnumFacing.EAST ? true
                : flag5 && side == EnumFacing.NORTH ? true : flag5 && side == EnumFacing.SOUTH;
    }
    
    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
    
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state,
            Entity entityIn) {
        if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
            boolean inPortal = IronRustMod.get(TeleporterRust.inPortal, entityIn, false);
            BlockPos lastPortalPos =
                    IronRustMod.get(TeleporterRust.lastPortalPos, entityIn, BlockPos.ORIGIN);
            Vec3d lastPortalVec =
                    IronRustMod.get(TeleporterRust.lastPortalVec, entityIn, Vec3d.ZERO);
            EnumFacing teleportDirection =
                    IronRustMod.get(TeleporterRust.teleportDirection, entityIn, EnumFacing.DOWN);
            int timeUntilPortal = IronRustMod.get(TeleporterRust.timeUntilPortal, entityIn, 0);
            if (timeUntilPortal > 0) {
                timeUntilPortal = entityIn.getPortalCooldown();
            } else {
                if (!worldIn.isRemote && !pos.equals(lastPortalPos)) {
                    lastPortalPos = new BlockPos(pos);
                    PatternHelper pattern = ((BlockRustPortal) IronRustMod.RUST_PORTAL)
                            .createPatternHelper(entityIn.getEntityWorld(), lastPortalPos);
                    double d0 = pattern.getForwards().getAxis() == EnumFacing.Axis.X
                            ? (double) pattern.getFrontTopLeft().getZ()
                            : (double) pattern.getFrontTopLeft().getX();
                    double d1 = pattern.getForwards().getAxis() == EnumFacing.Axis.X ? entityIn.posZ
                            : entityIn.posX;
                    d1 = Math.abs(MathHelper.pct(d1 - (pattern.getForwards().rotateY()
                            .getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), d0,
                            d0 - pattern.getWidth()));
                    double d2 =
                            MathHelper.pct(entityIn.posY - 1.0D, pattern.getFrontTopLeft().getY(),
                                    pattern.getFrontTopLeft().getY() - pattern.getHeight());
                    lastPortalVec = new Vec3d(d1, d2, 0.0D);
                    teleportDirection = pattern.getForwards();
                }
                
                inPortal = true;
            }
            IronRustMod.put(TeleporterRust.inPortal, entityIn, inPortal);
            IronRustMod.put(TeleporterRust.lastPortalPos, entityIn, lastPortalPos);
            IronRustMod.put(TeleporterRust.lastPortalVec, entityIn, lastPortalVec);
            IronRustMod.put(TeleporterRust.teleportDirection, entityIn, teleportDirection);
            IronRustMod.put(TeleporterRust.timeUntilPortal, entityIn, timeUntilPortal);
        }
    }
    
    @Override
    @Nullable
    @Deprecated
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }
    
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, (meta & 1) == 1 ? Axis.Z : Axis.X);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return getMetaForAxis(state.getValue(AXIS));
    }
    
    public static int getMetaForAxis(Axis axis) {
        return axis == Axis.Z ? 1 : 0;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F,
                    rand.nextFloat() * 0.4F + 0.8F, false);
        }
        
        for (int i = 0; i < 4; ++i) {
            double x = pos.getX() + rand.nextFloat();
            double y = pos.getY() + rand.nextFloat();
            double z = pos.getZ() + rand.nextFloat();
            double vx = (rand.nextFloat() - 0.5D) * 0.5D;
            double vy = (rand.nextFloat() - 0.5D) * 0.5D;
            double vz = (rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            
            if (worldIn.getBlockState(pos.west()).getBlock() != this
                    && worldIn.getBlockState(pos.east()).getBlock() != this) {
                x = pos.getX() + 0.5D + 0.25D * j;
                vx = rand.nextFloat() * 2.0F * j;
            } else {
                z = pos.getZ() + 0.5D + 0.25D * j;
                vz = rand.nextFloat() * 2.0F * j;
            }
            
            Minecraft.getMinecraft().effectRenderer
                    .addEffect(new ParticleRustPortal(worldIn, x, y, z, vx, vy, vz));
        }
    }
    
    @Override
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case X:
                        return state.withProperty(AXIS, Axis.Z);
                    case Z:
                        return state.withProperty(AXIS, Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }
    
    public PatternHelper createPatternHelper(World worldIn, BlockPos p_181089_2_) {
        Axis axis = Axis.Z;
        Size size = new Size(worldIn, p_181089_2_, Axis.X);
        LoadingCache<BlockPos, BlockWorldState> loadingcache =
                BlockPattern.createLoadingCache(worldIn, true);
        
        if (!size.isValid()) {
            axis = Axis.X;
            size = new Size(worldIn, p_181089_2_, Axis.Z);
        }
        
        if (!size.isValid()) {
            return new PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1,
                    1, 1);
        }
        int[] aint = new int[AxisDirection.values().length];
        EnumFacing enumfacing = size.rightDir.rotateYCCW();
        BlockPos blockpos = size.bottomLeft.up(size.getHeight() - 1);
        
        for (AxisDirection enumfacing$axisdirection : AxisDirection.values()) {
            PatternHelper blockpattern$patternhelper = new PatternHelper(
                    enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos
                            : blockpos.offset(size.rightDir, size.getWidth() - 1),
                    EnumFacing.getFacingFromAxis(enumfacing$axisdirection, axis), EnumFacing.UP,
                    loadingcache, size.getWidth(), size.getHeight(), 1);
            
            for (int i = 0; i < size.getWidth(); ++i) {
                for (int j = 0; j < size.getHeight(); ++j) {
                    BlockWorldState blockworldstate =
                            blockpattern$patternhelper.translateOffset(i, j, 1);
                    
                    if (blockworldstate.getBlockState() != null
                            && blockworldstate.getBlockState().getMaterial() != Material.AIR) {
                        ++aint[enumfacing$axisdirection.ordinal()];
                    }
                }
            }
        }
        
        AxisDirection enumfacing$axisdirection1 = AxisDirection.POSITIVE;
        
        for (AxisDirection enumfacing$axisdirection2 : AxisDirection.values()) {
            if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1
                    .ordinal()]) {
                enumfacing$axisdirection1 = enumfacing$axisdirection2;
            }
        }
        
        return new PatternHelper(
                enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos
                        : blockpos.offset(size.rightDir, size.getWidth() - 1),
                EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, axis), EnumFacing.UP,
                loadingcache, size.getWidth(), size.getHeight(), 1);
    }
    
    public static class Size {
        public final World world;
        public final Axis axis;
        public final EnumFacing rightDir;
        public final EnumFacing leftDir;
        protected int portalBlockCount;
        protected BlockPos bottomLeft;
        protected int height;
        protected int width;
        
        public Size(World worldIn, BlockPos p_i45694_2_, Axis axis) {
            this.world = worldIn;
            this.axis = axis;
            
            if (axis == Axis.X) {
                this.leftDir = EnumFacing.EAST;
                this.rightDir = EnumFacing.WEST;
            } else {
                this.leftDir = EnumFacing.NORTH;
                this.rightDir = EnumFacing.SOUTH;
            }
            
            for (BlockPos blockpos = p_i45694_2_; p_i45694_2_.getY() > blockpos.getY() - 21
                    && p_i45694_2_.getY() > 0
                    && this.isEmptyBlock(
                            worldIn.getBlockState(p_i45694_2_.down()).getBlock()); p_i45694_2_ =
                                    p_i45694_2_.down()) {
            }
            
            int i = this.getDistanceUntilEdge(p_i45694_2_, this.leftDir) - 1;
            
            if (i >= 0) {
                this.bottomLeft = p_i45694_2_.offset(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                
                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }
            
            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }
        }
        
        protected int getDistanceUntilEdge(BlockPos p_180120_1_, EnumFacing p_180120_2_) {
            int i;
            
            for (i = 0; i < 22; ++i) {
                BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);
                
                if (!this.isEmptyBlock(this.world.getBlockState(blockpos).getBlock()) || this.world
                        .getBlockState(blockpos.down()).getBlock() != IronRustMod.RUST_BLOCK) {
                    break;
                }
            }
            
            Block block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
            return block == IronRustMod.RUST_BLOCK ? i : 0;
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        protected int calculatePortalHeight() {
            findTop: for (this.height = 0; this.height < 21; ++this.height) {
                for (int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    Block block = this.world.getBlockState(blockpos).getBlock();
                    
                    if (!this.isEmptyBlock(block)) {
                        break findTop;
                    }
                    
                    if (block == IronRustMod.RUST_PORTAL) {
                        ++this.portalBlockCount;
                    }
                    
                    if (i == 0) {
                        block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();
                        
                        if (block != IronRustMod.RUST_BLOCK) {
                            break findTop;
                        }
                    } else if (i == this.width - 1) {
                        block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();
                        
                        if (block != IronRustMod.RUST_BLOCK) {
                            break findTop;
                        }
                    }
                }
            }
            
            for (int j = 0; j < this.width; ++j) {
                if (this.world
                        .getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height))
                        .getBlock() != IronRustMod.RUST_BLOCK) {
                    this.height = 0;
                    break;
                }
            }
            
            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            }
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
        }
        
        public boolean isEmptyBlock(Block blockIn) {
            return blockIn.getDefaultState().getMaterial() == Material.AIR || blockIn == Blocks.FIRE
                    || blockIn == IronRustMod.RUST_PORTAL;
        }
        
        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21
                    && this.height >= 3 && this.height <= 21;
        }
        
        public void placePortalBlocks() {
            for (int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);
                for (int j = 0; j < this.height; ++j) {
                    this.world.setBlockState(blockpos.up(j),
                            IronRustMod.RUST_PORTAL.getDefaultState().withProperty(AXIS, this.axis),
                            2);
                }
            }
        }
    }
}
