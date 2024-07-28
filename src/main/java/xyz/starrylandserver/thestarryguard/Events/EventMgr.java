package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.starrylandserver.thestarryguard.Adapter.FabricAdapter;
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

                String dimension_name = FabricAdapter.GetWorldName(world);
                QueryTask task = new QueryTask(pos.getX(), pos.getY(), pos.getZ(),
                        dimension_name, QueryTask.QueryType.POINT, player, 1);
                //创建一个新的查询任务,默认显示第一页的内容,因为是点击,所以为点查询
                this.dataQuery.AddQueryTask(task);//添加查询任务

                return false;
            } else {//玩家没有启用方块查询
                String block_id = state.getBlock().getTranslationKey();

                String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

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
            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            HashMap<String, String> data_slot_temp = new HashMap<>();
            data_slot_temp.put("name", block_id);

            Target target = new Target(TargetType.BLOCK, data_slot_temp);

            Action action = new Action(ActionType.BLOCK_PLACE_ACTION, player,
                    pos.getX(), pos.getY(), pos.getZ(), dimension_name, target, Tool.GetCurrentTime());

            this.main.onStorage(action);//插入玩家破坏方块的行为对象
            return true;
        });//方块破坏事件
    }

    void regFireBlockEvent() {
        PlayerFireBlockEvent.EVENT.register(((world, mc_player, blockState, pos) -> {
            TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());
            String block_id = blockState.getBlock().getTranslationKey();

            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            HashMap<String, String> data_slot_temp = new HashMap<>();
            data_slot_temp.put("name", block_id);

            Target target = new Target(TargetType.BLOCK, data_slot_temp);

            Action action = new Action(ActionType.FIRE_BLOCK_ACTION, player, pos.getX(), pos.getY(), pos.getZ(),
                    dimension_name, target, Tool.GetCurrentTime());

            this.main.onStorage(action);

            return ActionResult.PASS;
        }));
    }



    void regBucketUsedEvent() {//使用桶的事件
        BucketUseEvent.EVENT.register(((world, user, hand, item) -> {
            String item_id = item.getTranslationKey();//获取手中拿着的物品

            TgPlayer player = new TgPlayer(user.getName().getString(), user.getUuidAsString());//构造新的玩家对象
            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            HitResult res = user.raycast(20.D, 0.F, false);

            if (res.getType() != HitResult.Type.BLOCK)//判断是否点击到了方块上面
            {
                return ActionResult.PASS;
            }

            Vec3d pos = res.getPos();

            HashMap<String, String> temp_data_slot = new HashMap<>();
            temp_data_slot.put("name", item_id);

            Target target = new Target(TargetType.BLOCK, temp_data_slot);

            Action action = new Action(ActionType.KILL_ENTITY_ACTION, player, (int) pos.getX(), (int) pos.getY(), (int) pos.getZ(),
                    dimension_name, target, Tool.GetCurrentTime());

            this.main.onStorage(action);//存储事件

            return ActionResult.PASS;
        }));
    }

    void regPlayerAttackPlayerEvent() {
        PlayerAttackPlayerEvent.EVENT.register(((world, attacker, target) -> {

            TgPlayer player = new TgPlayer(attacker.getName().getString(), attacker.getUuidAsString());//构造新的玩家对象

            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            BlockPos pos = target.getBlockPos();//获取被击杀的实体的位置

            HashMap<String, String> temp_data_slot = new HashMap<>();
            temp_data_slot.put("name", target.getName().getString());
            temp_data_slot.put("uuid", target.getUuidAsString());

            Target target_ = new Target(TargetType.PLAYER, temp_data_slot);

            Action action = new Action(ActionType.ATTACK_PLAYER_ACTION, player, pos.getX(), pos.getY(), pos.getZ(),
                    dimension_name, target_, Tool.GetCurrentTime());

            this.main.onStorage(action);//存储事件

            return ActionResult.PASS;
        }));
    }

    void regKillPlayerEvent()//注册击杀玩家事件
    {
        PlayerKillPlayerEvent.EVENT.register((world, killer, mc_player) -> {
            TgPlayer player = new TgPlayer(killer.getName().getString(), killer.getUuidAsString());

            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            BlockPos pos = mc_player.getBlockPos();//获取被击杀的人的位置

            HashMap<String, String> temp_data_slot = new HashMap<>();

            temp_data_slot.put("name", mc_player.getName().getString());
            temp_data_slot.put("uuid", mc_player.getUuidAsString());
            Target target = new Target(TargetType.PLAYER, temp_data_slot);

            Action action = new Action(ActionType.KILL_PLAYER_ACTION, player, pos.getX(), pos.getY(), pos.getZ(), dimension_name,
                    target, Tool.GetCurrentTime());

            this.main.onStorage(action);//存储事件
            return ActionResult.PASS;
        });
    }

    void regKillEntityEvent() {
        PlayerKillEntityEvent.EVENT.register(((world, killer, entity) -> {
            String entity_id = entity.getType().getTranslationKey();//获取实体的id
            TgPlayer player = new TgPlayer(killer.getName().getString(), killer.getUuidAsString());//构造新的玩家对象

            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            BlockPos pos = entity.getBlockPos();//获取被击杀的实体的位置

            HashMap<String, String> temp_data_slot = new HashMap<>();
            temp_data_slot.put("name", entity_id);

            Target target = new Target(TargetType.ENTITY, temp_data_slot);

            Action action = new Action(ActionType.KILL_ENTITY_ACTION, player, pos.getX(), pos.getY(), pos.getZ(),
                    dimension_name, target, Tool.GetCurrentTime());

            this.main.onStorage(action);//存储事件

            return ActionResult.PASS;
        }));
    }

    void regPlayerAttackEntityEvent() {
        PlayerAttackEntityEvent.EVENT.register(((world, attacker, target) -> {
            String entity_id = target.getType().getTranslationKey();

            TgPlayer player = new TgPlayer(attacker.getName().getString(), attacker.getUuidAsString());//构造新的玩家对象

            String dimension_name = FabricAdapter.GetWorldName(world);//获取世界的id

            BlockPos pos = target.getBlockPos();//获取被击杀的实体的位置

            HashMap<String, String> temp_data_slot = new HashMap<>();
            temp_data_slot.put("name", entity_id);

            Target target_ = new Target(TargetType.ENTITY, temp_data_slot);

            Action action = new Action(ActionType.ATTACK_ENTITY_ACTION, player, pos.getX(), pos.getY(), pos.getZ(),
                    dimension_name, target_, Tool.GetCurrentTime());

            this.main.onStorage(action);//存储事件

            return ActionResult.PASS;
        }));

    }

    public void RegAllEvent()//注册所有的事件
    {
        regBlockBreakEvent();
        regBlockPlaceEvent();
        regFireBlockEvent();
        regBucketUsedEvent();
        regPlayerAttackEntityEvent();
        regPlayerAttackPlayerEvent();
        regKillPlayerEvent();
        regKillEntityEvent();

    }

    public EventMgr(TgMain main) {
        this.dataQuery = main.getDataQuery();
        this.main = main;
    }
}
