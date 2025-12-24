package tobinio.denseflowers.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tobinio.denseflowers.OffsetGenerator;

import java.util.List;

/**
 * Created: 30.07.24
 *
 * @author Tobias Frischmann
 */
@Mixin(SectionCompiler.class)
public class SectionBuilderMixin {
    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Inject(
            method = {"compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"
            )}
    )
    private void denseflowers$render(SectionPos sectionPos, RenderChunkRegion renderRegion, VertexSorting vertexSorter, SectionBufferBuilderPack allocatorStorage, List adapter_injected_4, CallbackInfoReturnable cir, @Local net.minecraft.world.level.block.state.BlockState blockState, @Local(ordinal = 2) BlockPos blockPos, @Local PoseStack matrixStack, @Local BufferBuilder bufferBuilder, @Local RandomSource random) {
        if (blockState.getBlock() instanceof net.minecraft.world.level.block.FlowerBlock) {
            matrixStack.pushPose();
            Vec3 realOffset = blockState.getOffset(renderRegion, blockPos);
            matrixStack.translate(-realOffset.x(), -realOffset.y(), -realOffset.z());

            for(Vec3 flowerOffset : OffsetGenerator.getFlowerOffsets(blockState, renderRegion, blockPos)) {
                matrixStack.pushPose();
                matrixStack.translate(flowerOffset.x(), flowerOffset.y(), flowerOffset.z());
                this.blockRenderer.renderBatched(blockState, blockPos, renderRegion, matrixStack, bufferBuilder, true, random);
                matrixStack.popPose();
            }

            matrixStack.popPose();
        }

    }
}
