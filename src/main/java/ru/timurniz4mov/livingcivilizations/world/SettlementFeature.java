package ru.timurniz4mov.livingcivilizations.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import ru.timurniz4mov.livingcivilizations.entity.CivilianEntity;
import ru.timurniz4mov.livingcivilizations.registry.ModEntities;

public class SettlementFeature extends Feature<NoneFeatureConfiguration> {
    public SettlementFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin.below()).isSolid()) return false;

        // Дом с полноценными блоками пола, стен и крыши.
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) put(level, origin, x, 0, z, Blocks.COBBLESTONE.defaultBlockState());
        }
        for (int y = 1; y <= 3; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (Math.abs(x) != 4 && Math.abs(z) != 4) continue;
                    if (x == 0 && z == 4 && y <= 2) continue;
                    BlockState wall = (Math.abs(x) == 4 && Math.abs(z) == 4)
                            ? Blocks.SPRUCE_LOG.defaultBlockState() : Blocks.SPRUCE_PLANKS.defaultBlockState();
                    put(level, origin, x, y, z, wall);
                }
            }
        }
        // Полноразмерная крыша без плит, чтобы не было провалов от воды.
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                put(level, origin, x, 4, z, Blocks.SPRUCE_PLANKS.defaultBlockState());
            }
        }
        BlockState doorLower = Blocks.SPRUCE_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState doorUpper = doorLower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        put(level, origin, 0, 1, 4, doorLower);
        put(level, origin, 0, 2, 4, doorUpper);
        for (int x : new int[]{-3, 3}) {
            put(level, origin, x, 2, 4, Blocks.GLASS_PANE.defaultBlockState());
            put(level, origin, x, 2, -4, Blocks.GLASS_PANE.defaultBlockState());
        }
        put(level, origin, -2, 1, -2, Blocks.BARREL.defaultBlockState());
        put(level, origin, 2, 1, -2, Blocks.CRAFTING_TABLE.defaultBlockState());
        put(level, origin, 0, 1, 0, Blocks.CHEST.defaultBlockState());
        // Кровать и колокол делают поселение распознаваемым как место отдыха.
        BlockState bedHead = Blocks.RED_BED.defaultBlockState()
                .setValue(BedBlock.FACING, Direction.SOUTH).setValue(BedBlock.PART, BedPart.HEAD);
        BlockState bedFoot = bedHead.setValue(BedBlock.PART, BedPart.FOOT);
        put(level, origin, -2, 1, 0, bedFoot);
        put(level, origin, -2, 1, 1, bedHead);
        put(level, origin, 2, 1, -3, Blocks.BELL.defaultBlockState());

        // Небольшая грядка рядом с домом.
        for (int x = 7; x <= 10; x++) {
            for (int z = -2; z <= 2; z++) {
                put(level, origin, x, 0, z, Blocks.FARMLAND.defaultBlockState());
                put(level, origin, x, 1, z, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7));
            }
        }
        put(level, origin, 8, 0, 3, Blocks.WATER.defaultBlockState());

        // Дорога — только полноразмерные блоки, не плиты и не тонкие дорожные блоки.
        for (int z = 5; z <= 17; z++) {
            put(level, origin, 0, 0, z, z % 4 == 0 ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.GRAVEL.defaultBlockState());
        }
        spawn(level, ModEntities.WORKER.get(), origin.offset(-2, 1, 1));
        spawn(level, ModEntities.FARMER.get(), origin.offset(2, 1, 1));
        spawn(level, ModEntities.TRADER.get(), origin.offset(1, 1, -2));
        spawn(level, ModEntities.GUARD.get(), origin.offset(0, 1, 6));
        spawn(level, ModEntities.TRAVELER.get(), origin.offset(0, 1, 8));
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
