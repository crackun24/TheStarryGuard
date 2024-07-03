package xyz.starrylandserver.thestarryguard.Mixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.Events.PlayerAttackPlayerEvent;
import xyz.starrylandserver.thestarryguard.Events.PlayerKillPlayerEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "damage")
    private void onAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        if (!(source.getAttacker() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity attacker = (PlayerEntity) source.getAttacker();//获取攻击this玩家的玩家
        World world = attacker.getWorld();

        PlayerEntity be_attacked = (PlayerEntity) (Object) this;//获取当前的玩家

        if (be_attacked.getHealth() - amount <= 0)//击杀玩家的事件
        {
            ActionResult result = PlayerKillPlayerEvent.EVENT.invoker().interact(world, attacker, be_attacked);
        } else {//攻击玩家的事件
            ActionResult result = PlayerAttackPlayerEvent.EVENT.invoker().interact(world, attacker, be_attacked);
        }
    }
}
