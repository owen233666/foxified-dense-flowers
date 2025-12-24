package tobinio.denseflowers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tobinio.denseflowers.mixin.AbstractBlockAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 31.07.24
 *
 * @author Tobias Frischmann
 * 移植到NeoForge版本
 */
public class OffsetGenerator {

    public static List<Vec3> getFlowerOffsets(BlockState blockState, BlockGetter level, BlockPos flowerPos) {
        // 使用Minecraft原生的Vec3类
        var baseOffset = blockState.getOffset(level, flowerPos);
        AbstractBlockAccessor flower = (AbstractBlockAccessor) blockState.getBlock();

        var allLocations = new ArrayList<Vec3>();
        allLocations.add(new Vec3(baseOffset.x(), baseOffset.y(), baseOffset.z()));

        var locations = new ArrayList<Vec3>();

        outer:
        for (int i = 0; i < getNumberOfSurroundingFlowers(level, flowerPos); i++) {
            var offset = blockState.getOffset(level, flowerPos.offset((i + 1) * 5, 0, (i + 1) * 3));
            var newLocation = new Vec3(offset.x(), offset.y(), offset.z())
                    .scale(0.45 / flower.callGetMaxHorizontalOffset());

            for (Vec3 location : allLocations) {
                double rotation = Math.atan2(newLocation.x() - location.x(), newLocation.z() - location.z());

                if (location.distanceTo(newLocation) <= 0.4 || is45Degrees(rotation)) {
                    continue outer;
                }
            }

            allLocations.add(newLocation);
            locations.add(newLocation);
        }

        return locations;
    }

    private static int getNumberOfSurroundingFlowers(BlockGetter level, BlockPos flowerPos) {
        var count = 0;
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        for (Direction dir : directions) {
            if (level.getBlockState(flowerPos.offset(dir.getNormal()))
                    .getBlock() instanceof FlowerBlock) {
                count++;
            }
        }

        return count;
    }

    public static boolean is45Degrees(double angle) {
        double[] targetAngles = {Math.PI / 4, 3 * Math.PI / 4, 5 * Math.PI / 4, 7 * Math.PI / 4};

        double normalizedAngle = angle % (2 * Math.PI);
        if (normalizedAngle < 0) {
            normalizedAngle += 2 * Math.PI;
        }

        for (double targetAngle : targetAngles) {
            //check + rounding error
            if (Math.abs(normalizedAngle - targetAngle) < 0.01) {
                return true;
            }
        }

        return false;
    }
}