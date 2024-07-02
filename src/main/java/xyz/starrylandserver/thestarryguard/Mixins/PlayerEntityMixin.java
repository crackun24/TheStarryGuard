package xyz.starrylandserver.thestarryguard.Mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.Events.PlayerKillEntityEvent;
import xyz.starrylandserver.thestarryguard.Events.PlayerKillPlayerEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(at = @At(value = "HEAD"), method = "onKilledOther")
    private void onKill(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        try {
            PlayerEntity killer = (PlayerEntity) (Object) this;
            if (other instanceof PlayerEntity)//判断是否为玩家实体
            {
                PlayerEntity death_player = (PlayerEntity) (Object) other;//获得击杀其它玩家的对象
                PlayerKillPlayerEvent.EVENT.invoker().interact(world,killer,death_player);
                return;
            }
            PlayerKillEntityEvent.EVENT.invoker().interact(world, killer, other);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
