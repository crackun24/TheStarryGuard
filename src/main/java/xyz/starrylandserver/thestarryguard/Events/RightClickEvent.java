package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RightClickEvent {
    Event<RightClickEvent> EVENT = EventFactory.createArrayBacked(RightClickEvent.class,
            (listeners) -> (ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) -> {
                for (RightClickEvent listener : listeners) {
                    ActionResult result = listener.interact(player, world, stack, hand, hitResult);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult);
}
