package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockBreakEvent {//玩家破坏方块事件
    Event<BlockBreakEvent> EVENT = EventFactory.createArrayBacked(BlockBreakEvent.class,
            (listeners) -> (world, pos, state, placer) -> {
                for (BlockBreakEvent listener : listeners) {
                    ActionResult result = listener.interact(world, pos, state, placer);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(World world, BlockPos pos, BlockState state, LivingEntity placer);
}
