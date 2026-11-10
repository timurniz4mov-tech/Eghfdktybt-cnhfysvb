package ru.timurniz4mov.livingcivilizations.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import ru.timurniz4mov.livingcivilizations.entity.BanditEntity;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;

/** Small overworld camp where bandits gather before ambushing travellers. */
public class BanditCampFeature extends Feature<NoneFeatureConfiguration> {
    public BanditCampFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = chunkCenter(level, context.origin());
        if (!level.getBlockState(origin.below()).isSolid()) return false;

        // Flatten a modest camp clearing.
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                put(level, origin, x, 0, z, (Math.abs(x) + Math.abs(z)) % 4 == 0
                        ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.DIRT.defaultBlockState());
            }
        }

        // Two rough tents built from full blocks.
        tent(level, origin, -2, -1, Blocks.BROWN_WOOL.defaultBlockState());
        tent(level, origin, 2, 1, Blocks.GRAY_WOOL.defaultBlockState());

        // Campfire, stolen supplies and a broken fence.
        put(level, origin, 0, 1, 0, Blocks.CAMPFIRE.defaultBlockState());
        put(level, origin, -1, 1, -3, Blocks.BARREL.defaultBlockState());
        put(level, origin, 1, 1, 3, Blocks.CHEST.defaultBlockState());
        for (int x = -4; x <= 4; x++) {
            if (x % 2 == 0) put(level, origin, x, 1, -4, Blocks.SPRUCE_FENCE.defaultBlockState());
        }
        put(level, origin, -4, 1, -4, Blocks.SPRUCE_LOG.defaultBlockState());
        put(level, origin, 4, 1, -4, Blocks.SPRUCE_LOG.defaultBlockState());

        spawn(level, origin.offset(-1, 1, 1));
        spawn(level, origin.offset(1, 1, -1));
        return true;
    }

    private static void tent(WorldGenLevel level, BlockPos origin, int centerX, int centerZ, BlockState wool) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                put(level, origin, centerX + x, 1, centerZ + z, Blocks.SPRUCE_PLANKS.defaultBlockState());
            }
        }
        for (int x = -1; x <= 1; x++) {
            put(level, origin, centerX + x, 2, centerZ - 1, wool);
            put(level, origin, centerX + x, 2, centerZ + 1, wool);
        }
        put(level, origin, centerX, 3, centerZ, wool);
    }

    private static BlockPos chunkCenter(WorldGenLevel level, BlockPos featureOrigin) {
        int centerX = (featureOrigin.getX() & ~15) + 8;
        int centerZ = (featureOrigin.getZ() & ~15) + 8;
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX, centerZ);
        return new BlockPos(centerX, surfaceY, centerZ);
    }

    private static void put(WorldGenLevel level, BlockPos origin, int x, int y, int z, BlockState state) {
        BlockPos target = origin.offset(x, y, z);
        // Custom features must not write into neighbouring chunks while they are being generated.
        if ((target.getX() >> 4) != (origin.getX() >> 4) || (target.getZ() >> 4) != (origin.getZ() >> 4)) return;
        level.setBlock(target, state, 2);
    }

    private static void spawn(WorldGenLevel level, BlockPos pos) {
        BanditEntity entity = ModEntities.BANDIT.get().create(level.getLevel());
        if (entity == null) return;
        entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
        level.addFreshEntity(entity);
    }
}
