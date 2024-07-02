package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public interface PlayerAttackPlayerEvent {
    Event<PlayerAttackPlayerEvent> EVENT = EventFactory.createArrayBacked(PlayerAttackPlayerEvent.class,
            (listeners) -> (world, attacker, target) -> {
                for (PlayerAttackPlayerEvent listener : listeners) {
                    ActionResult result = listener.interact(world, attacker,target);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(World world, PlayerEntity attacker, PlayerEntity target);
}
