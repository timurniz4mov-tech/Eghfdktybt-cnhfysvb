package ru.timurniz4mov.livingcivilizations.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import ru.timurniz4mov.livingcivilizations.entity.CivilianEntity;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;

public class CastleFeature extends Feature<NoneFeatureConfiguration> {
    public CastleFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin.below()).isSolid()) return false;

        // Каменная площадка и стены крепости.
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                put(level, origin, x, 0, z, Blocks.STONE_BRICKS.defaultBlockState());
            }
        }
        for (int y = 1; y <= 5; y++) {
            for (int x = -7; x <= 7; x++) {
                for (int z = -7; z <= 7; z++) {
                    if (Math.abs(x) != 7 && Math.abs(z) != 7) continue;
                    if (x == 0 && z == 7 && y <= 3) continue;
                    put(level, origin, x, y, z, Blocks.STONE_BRICKS.defaultBlockState());
                }
            }
        }
        // Четыре башни с высокими полноразмерными стенами.
        for (int cx : new int[]{-6, 6}) {
            for (int cz : new int[]{-6, 6}) {
                for (int y = 1; y <= 8; y++) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            if (Math.abs(x) == 1 || Math.abs(z) == 1) {
                                put(level, origin, cx + x, y, cz + z, y == 8 ? Blocks.STONE_BRICK_SLAB.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        // Главный зал внутри стен.
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                put(level, origin, x, 1, z, Blocks.POLISHED_ANDESITE.defaultBlockState());
                if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                    for (int y = 2; y <= 5; y++) put(level, origin, x, y, z, Blocks.STONE_BRICKS.defaultBlockState());
                }
            }
        }
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) put(level, origin, x, 6, z, Blocks.DARK_OAK_PLANKS.defaultBlockState());
        }
        put(level, origin, 0, 1, 0, Blocks.CHEST.defaultBlockState());
        put(level, origin, -2, 1, 0, Blocks.CRAFTING_TABLE.defaultBlockState());

        // Полноразмерная дорога от ворот наружу.
        for (int z = 8; z <= 21; z++) {
            put(level, origin, 0, 0, z, z % 5 == 0 ? Blocks.STONE_BRICKS.defaultBlockState() : Blocks.GRAVEL.defaultBlockState());
        }
        spawn(level, ModEntities.KING.get(), origin.offset(0, 1, 0));
        spawn(level, ModEntities.LORD.get(), origin.offset(2, 1, 0));
        spawn(level, ModEntities.GUARD.get(), origin.offset(-2, 1, 5));
        spawn(level, ModEntities.WARRIOR.get(), origin.offset(2, 1, 5));
        spawn(level, ModEntities.CARAVAN_TRADER.get(), origin.offset(0, 1, 4));
        return true;
    }

    private static void put(WorldGenLevel level, BlockPos origin, int x, int y, int z, BlockState state) {
        level.setBlock(origin.offset(x, y, z), state, 2);
    }

    private static void spawn(WorldGenLevel level, net.minecraft.world.entity.EntityType<CivilianEntity> type, BlockPos pos) {
        CivilianEntity entity = type.create(level.getLevel());
        if (entity == null) return;
        entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getRandom().nextFloat() * 360.0F, 0.0F);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
        level.addFreshEntity(entity);
    }
}
