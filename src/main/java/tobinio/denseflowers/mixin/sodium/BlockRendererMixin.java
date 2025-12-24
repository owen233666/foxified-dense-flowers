package tobinio.denseflowers.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tobinio.denseflowers.util.OffsetStorage;

@Mixin(value = BlockRenderer.class, remap = false)
public class BlockRendererMixin {
    @Shadow
    @Final
    private Vector3f posOffset;

    @Inject (method = "renderModel", at = @At (value = "INVOKE", target = "Lorg/joml/Vector3f;add(FFF)Lorg/joml/Vector3f;"), remap = false)
    private void denseflowers$renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci,
                                          @Local Vec3 modelOffset) {

        Vec3 offset = OffsetStorage.offsets.get(pos);

        if (offset != null) {
            posOffset.sub((float) modelOffset.x(), (float) modelOffset.y(), (float) modelOffset.z());
            posOffset.add((float) offset.x(), (float) offset.y(), (float) offset.z());
        }
    }
}