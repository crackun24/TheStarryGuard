package xyz.starrylandserver.thestarryguard.Mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.Events.BlockBreakEvent;

@Mixin(Block.class)
public abstract class BlockMixin {

    //#if MC<1203
    //$$
    //$$ @Inject(at = @At(value = "HEAD"),method = "onBreak")
    //$$ public void PlayerBreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci)
    //$$ {
    //$$  ActionResult result = BlockBreakEvent.EVENT.invoker().interact(world,pos,state,player);
    //$$ }
    //#else
    @Inject(at = @At(value = "HEAD"), method = "onBreak")
    public void PlayerBreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        ActionResult result = BlockBreakEvent.EVENT.invoker().interact(world, pos, state, player);
    }
    //#endif
}
