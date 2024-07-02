package xyz.starrylandserver.thestarryguard.Mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starrylandserver.thestarryguard.DataType.Action;
import xyz.starrylandserver.thestarryguard.DataType.ActionType;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguard.Events.BlockBreakEvent;
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

        PlayerEntity mc_player = (PlayerEntity) source.getAttacker();
        World world = mc_player.getWorld();

        if (this.getHealth() - amount <= 0)//击杀实体的事件
        {
            ActionResult result = PlayerKillEntityEvent.EVENT.invoker().interact(mc_player.getWorld(), mc_player, this);
        }

    }
}
