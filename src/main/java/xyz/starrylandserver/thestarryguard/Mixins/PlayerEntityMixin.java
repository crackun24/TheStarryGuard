package xyz.starrylandserver.thestarryguard.Mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.Events.PlayerAttackEntityEvent;
import xyz.starrylandserver.thestarryguard.Events.PlayerAttackPlayerEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(at = @At(value = "HEAD"), method = "applyDamage")
    private void onAttack(DamageSource source, float amount, CallbackInfo ci) {

        PlayerEntity attacker = (PlayerEntity) source.getAttacker();
        World world = attacker.getWorld();//获取实体所在的世界

        if (attacker instanceof PlayerEntity)//判断是否为玩家实体
        {
            PlayerEntity be_attacked_player = (PlayerEntity) (Object) this;//获得击杀其它玩家的对象
            PlayerAttackPlayerEvent.EVENT.invoker().interact(world, attacker, be_attacked_player);
            return;
        }

        PlayerAttackEntityEvent.EVENT.invoker().interact(world, attacker, this);//玩家攻击实体的事件
    }
}
