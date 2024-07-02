package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockPlaceEvent {
    Event<BlockPlaceEvent> EVENT = EventFactory.createArrayBacked(BlockPlaceEvent.class,
            (listeners) -> (world, pos, state, placer, itemStack) -> {
                for (BlockPlaceEvent listener : listeners) {
                    ActionResult result = listener.interact(world, pos, state, placer, itemStack);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack);
}
