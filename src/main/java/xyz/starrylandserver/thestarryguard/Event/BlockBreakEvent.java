package xyz.starrylandserver.thestarryguardforge.Event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.starrylandserver.thestarryguardforge.DataType.*;
import xyz.starrylandserver.thestarryguardforge.Operation.DataQuery;
import xyz.starrylandserver.thestarryguardforge.TgMain;
import xyz.starrylandserver.thestarryguardforge.Tool;

import java.util.HashMap;


public class BlockBreakEvent {
    TgMain main;//服务的主类
    DataQuery dataQuery;//查询类

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent break_event) {
        Player mc_player = break_event.getPlayer();//获取玩家实例
        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getStringUUID());
        BlockState block_state = break_event.getState();//获取方块状态
        BlockPos pos = break_event.getPos();//获取事件发生的位置

        String dimension_str = mc_player.level().dimension().location().toString();//获取维度的名字

        if (this.dataQuery.IsPlayerEnablePointQuery(player))//判断玩家是否启用了点查询
        {
            QueryTask task = new QueryTask(pos.getX(), pos.getY(), pos.getZ(), dimension_str, QueryTask.QueryType.POINT, player, 1);
            this.dataQuery.AddQueryTask(task);//添加到查询列表

            break_event.setCanceled(true);//取消破坏事件
            return;//无需记录
        }

        HashMap<String, String> temp_data_slot = new HashMap<>();//临时的数据插槽
        temp_data_slot.put("name", block_state.getBlock().getDescriptionId());
        Target target = new Target(TargetType.BLOCK, temp_data_slot);//构造一个目标

        Action action = new Action(ActionType.BLOCK_BREAK_ACTION, player,
                pos.getX(), pos.getY(), pos.getZ(), dimension_str, target, Tool.GetCurrentTime());
        this.main.onStorage(action);
    }

    public BlockBreakEvent(TgMain tg_main) {
        this.main = tg_main;
        this.dataQuery = this.main.getDataQuery();
    }
}
