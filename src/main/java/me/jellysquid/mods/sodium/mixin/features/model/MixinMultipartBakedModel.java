package me.jellysquid.mods.sodium.mixin.features.model;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

@Mixin(MultiPartBakedModel.class)
public class MixinMultipartBakedModel {
    private final Map<BlockState, List<BakedModel>> stateCacheFast = new Reference2ReferenceOpenHashMap<>();

    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, BakedModel>> selectors;

    /**
     * @author JellySquid
     * @reason Avoid expensive allocations and replace bitfield indirection
     */
    @Overwrite(remap = false)
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random, IModelData modelData) {
        if (state == null) {
            return Collections.emptyList();
        }

        List<BakedModel> models;

        // FIXME: Synchronization-hack because getQuads must be thread-safe
            // Vanilla is actually affected by the exact same issue safety issue, but crashes seem rare in practice
        synchronized (this.stateCacheFast) {
            models = this.stateCacheFast.get(state);

            if (models == null) {
                models = new ArrayList<>(this.selectors.size());

                for (Pair<Predicate<BlockState>, BakedModel> pair : this.selectors) {
                    if ((pair.getLeft()).test(state)) {
                        models.add(pair.getRight());
                    }
                }

                this.stateCacheFast.put(state, models);
            }
        }

        List<BakedQuad> list = new ArrayList<>();

        long seed = random.nextLong();

        for (BakedModel model : models) {
            random.setSeed(seed);

            list.addAll(model.getQuads(state, face, random));
        }

        return list;
    }

}
