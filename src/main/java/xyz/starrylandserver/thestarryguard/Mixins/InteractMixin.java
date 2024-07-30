package xyz.starrylandserver.thestarryguard.Mixins;


import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.Events.RightClickEvent;

@Mixin(ServerPlayerInteractionManager.class)
public class InteractMixin {
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void onInteract(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = RightClickEvent.EVENT.invoker().interact(player, world, stack, hand, hitResult);
    }
}
