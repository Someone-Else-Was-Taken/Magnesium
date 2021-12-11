package me.jellysquid.mods.sodium.client.compat;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;

import java.util.Collection;

// Compatibility with Flywheel/Create Block Instancing
public class FlywheelCompat {
    public static void AvoidRender(Collection<BlockEntity> blockEntities) {
        if (SodiumClientMod.hasFlywheel) {
            InstancedRenderRegistry irr = InstancedRenderRegistry.getInstance();
            blockEntities.removeIf(irr::shouldSkipRender);
        }
    }
}