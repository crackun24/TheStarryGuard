package xyz.starrylandserver.thestarryguard.Mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.Events.PlayerAttackEntityEvent;
import xyz.starrylandserver.thestarryguard.Events.PlayerKillEntityEvent;

@Mixin(LivingEntity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract float getHealth();

    @Inject(at = @At(value = "HEAD"), method = "damage")
    void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source.getAttacker() instanceof PlayerEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity) {//如果判断是攻击的玩家则无需处理,由玩家实体的 Mixin 负责
            return;
        }

        PlayerEntity mc_player = (PlayerEntity) source.getAttacker();
        World world;

        //#if MC>=12000
        world = mc_player.getWorld();
        //#else
        //$$ world = mc_player.world;
        //#endif

        if (this.getHealth() - amount <= 0)//击杀实体的事件
        {
            ActionResult result;
            //#if MC>=12000
             result = PlayerKillEntityEvent.EVENT.invoker().interact(mc_player.getWorld(), mc_player, entity);
            //#else
            //$$ result = PlayerKillEntityEvent.EVENT.invoker().interact(mc_player.world, mc_player, entity);
            //#endif

        } else {//攻击实体的事件
            ActionResult result;
            //#if MC>11701
            result = PlayerAttackEntityEvent.EVENT.invoker().interact(mc_player.getWorld(), mc_player, entity);
            //#else
            //$$ result = PlayerAttackEntityEvent.EVENT.invoker().interact(mc_player.world, mc_player, entity);
            //#endif

        }

    }
}
