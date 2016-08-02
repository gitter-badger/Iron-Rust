package com.flatworks.ironrust.world.gen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public class WorldGenAbandonedHut extends WorldGenerator {
    private static final BlockStateMatcher IS_VALID =
            BlockStateMatcher.forBlock(IronRustMod.RUST_GRASS);
    private static final ResourceLocation STRUCTURE_ID =
            new ResourceLocation("ironrust:abandonedhut/abandonedhut");
    
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (worldIn.isAirBlock(position) && position.getY() > 2) {
            position = position.down();
        }
        
        if (!IS_VALID.apply(worldIn.getBlockState(position))) {
            return false;
        }
        
        Random random = worldIn.getChunkFromChunkCoords(position.getX(), position.getZ())
                .getRandomWithSeed(987234911L);
        MinecraftServer server = worldIn.getMinecraftServer();
        Rotation[] rotations = Rotation.values();
        Rotation rotation = rotations[random.nextInt(rotations.length)];
        Template template = worldIn.getSaveHandler().getStructureTemplateManager()
                .getTemplate(server, STRUCTURE_ID);
        ChunkPos chunkpos = new ChunkPos(position);
        StructureBoundingBox structureBB = new StructureBoundingBox(chunkpos.getXStart(), 0,
                chunkpos.getZStart(), chunkpos.getXEnd(), 256, chunkpos.getZEnd());
        PlacementSettings settings = new PlacementSettings().setRotation(rotation)
                .setBoundingBox(structureBB).func_189950_a(random);
        BlockPos genPos = template.func_189961_a(position.up(), Mirror.NONE, rotation);
        settings.func_189946_a(0.9F);
        template.addBlocksToWorldChunk(worldIn, genPos, settings);
        Map<BlockPos, String> map = template.getDataBlocks(genPos, settings);
        for (Entry<BlockPos, String> entry : map.entrySet()) {
            if ("furnace".equals(entry.getValue())) {
                BlockPos dataBlockPos = entry.getKey();
                worldIn.setBlockState(dataBlockPos, Blocks.AIR.getDefaultState(), 3);
                TileEntity te = worldIn.getTileEntity(dataBlockPos.down());
                if (te instanceof TileEntityFurnace) {
                    ((TileEntityFurnace) te).setInventorySlotContents(1,
                            new ItemStack(Items.COAL, random.nextInt(8) + 1));
                }
            }
        }
        return true;
    }
}
