package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.registry.Registries;
import xyz.starrylandserver.thestarryguard.DataType.*;
import xyz.starrylandserver.thestarryguard.Operation.DataQuery;
import xyz.starrylandserver.thestarryguard.Operation.DataStorage;
import xyz.starrylandserver.thestarryguard.TgMain;
import xyz.starrylandserver.thestarryguard.Tool;

import java.util.HashMap;

public class EventMgr {
    DataQuery dataQuery;//数据查询类
    TgMain main;
    DataStorage dataStorage;//数据储存类

    void regBlockBreakEvent() {
        PlayerBlockBreakEvents.BEFORE.register((world, mc_player, pos, state, blockEntity) -> {
            TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());

            if (this.dataQuery.IsPlayerEnablePointQuery(player)) {//判断玩家是否启用了点查询

                String dimension_name = world.getRegistryKey().getValue().toUnderscoreSeparatedString();//获取世界的名字
                QueryTask task = new QueryTask(pos.getX(), pos.getY(), pos.getZ(),
                        dimension_name, QueryTask.QueryType.POINT, player, 1);
                //创建一个新的查询任务,默认显示第一页的内容,因为是点击,所以为点查询
                this.dataQuery.AddQueryTask(task);//添加查询任务

                return false;
            } else {//玩家没有启用方块查询
                String block_id = state.getBlock().getTranslationKey();
                String dimension_name = world.getRegistryKey().getValue().toUnderscoreSeparatedString();//获取世界的id

                HashMap<String, String> data_slot_temp = new HashMap<>();
                data_slot_temp.put("name", block_id);

                Target target = new Target(TargetType.BLOCK, data_slot_temp);

                Action action = new Action(ActionType.BLOCK_BREAK_ACTION, player,
                        pos.getX(), pos.getY(), pos.getZ(), dimension_name, target, Tool.GetCurrentTime());

                this.main.onStorage(action);//插入玩家破坏方块的行为对象
            }

            return true;
        });//方块破坏事件
    }

    void regBlockPlaceEvent() {//方块的放置事件
        PlayerBlockBreakEvents.BEFORE.register((world, mc_player, pos, state, blockEntity) -> {
            TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());

            String block_id = state.getBlock().getTranslationKey();
            String dimension_name = world.getRegistryKey().getValue().toUnderscoreSeparatedString();//获取世界的id

            HashMap<String, String> data_slot_temp = new HashMap<>();
            data_slot_temp.put("name", block_id);

            Target target = new Target(TargetType.BLOCK, data_slot_temp);

            Action action = new Action(ActionType.BLOCK_PLACE_ACTION, player,
                    pos.getX(), pos.getY(), pos.getZ(), dimension_name, target, Tool.GetCurrentTime());

            this.main.onStorage(action);//插入玩家破坏方块的行为对象
            return true;
        });//方块破坏事件
    }

    public void RegAllEvent()//注册所有的事件
    {
        regBlockBreakEvent();
        regBlockPlaceEvent();

    }
    public EventMgr(TgMain main)
    {
        this.dataQuery = main.getDataQuery();
        this.main = main;
    }
}
