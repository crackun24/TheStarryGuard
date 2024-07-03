package xyz.starrylandserver.thestarryguard.Event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.starrylandserver.thestarryguard.DataType.*;
import xyz.starrylandserver.thestarryguard.TgMain;
import xyz.starrylandserver.thestarryguard.Tool;

import java.util.HashMap;

public class AttEntityEvent {
    TgMain main;

    @SubscribeEvent
    public void onAttackEntity(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player mc_player))//判断是否是玩家触发了事件
        {
            return;
        }

        LivingEntity be_attacked_entity = event.getEntity();
        BlockPos pos = be_attacked_entity.getOnPos();//获取位置

        ActionType action_type;//行为的类型
        TargetType target_type;//目标的类型
        HashMap<String, String> temp_target_data_slot = new HashMap<>();

        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getStringUUID());

        String dimension_str = mc_player.level().dimension().location().toString();//获取维度的名字

        if (be_attacked_entity instanceof Player be_attacked_player)//判断攻击的对象是否为玩家
        {
            //将攻击的实体转换为玩家类型
            target_type = TargetType.PLAYER;

            if (be_attacked_player.getHealth() - event.getAmount() <= 0)//判断是击杀玩家还是攻击玩家
            {
                action_type = ActionType.KILL_PLAYER_ACTION;//血量小于0则为击杀事件
            } else {
                action_type = ActionType.ATTACK_PLAYER_ACTION;
            }

            temp_target_data_slot.put("name", be_attacked_player.getName().getString());
            temp_target_data_slot.put("uuid", be_attacked_player.getStringUUID());
        } else {//攻击实体

            if (be_attacked_entity.getHealth() - event.getAmount() <= 0)//判断是击杀实体还是伤害实体
            {
                action_type = ActionType.KILL_ENTITY_ACTION;
            } else {
                action_type = ActionType.ATTACK_ENTITY_ACTION;
            }

            target_type = TargetType.ENTITY;
            temp_target_data_slot.put("name", be_attacked_entity.getType().getDescriptionId());
        }

        Target target = new Target(target_type, temp_target_data_slot);
        Action action = new Action(action_type, player, pos.getX(), pos.getY(), pos.getZ(),
                dimension_str, target, Tool.GetCurrentTime());
        this.main.onStorage(action);
    }

    public AttEntityEvent(TgMain tg_main) {
        this.main = tg_main;
    }
}
