package xyz.starrylandserver.thestarryguard.Events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
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

    void regRightClickEvent() {
        RightClickEvent.EVENT.register(((mc_player, world, stack, hand, hitResult) -> {
            String block_id;//方块的id
            BlockPos pos = hitResult.getBlockPos();//右键方块的位置

            BlockPos place_pos = pos.offset(hitResult.getSide());//获取玩家放置方块的坐标

            ActionType action_type;//右键的行为
            String str_dimension = FabricAdapter.GetWorldName(world);//获取世界的id

            TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());//构建接口的玩家对象

            ShapeContext context = ShapeContext.of(mc_player);

            if (stack.getItem() instanceof BlockItem &&
                    world.canPlace(((BlockItem) stack.getItem()).getBlock().getDefaultState(), place_pos, context))
            {
                block_id = stack.getItem().getTranslationKey();//获取玩家手中的方块的ID这个就是放置的方块的ID
                action_type = ActionType.BLOCK_PLACE_ACTION;//设置为方块的放置事件

            } else if (stack.getItem() instanceof FlintAndSteelItem)//玩家使用打火石的事件
            {
                action_type = ActionType.FIRE_BLOCK_ACTION;//设置为点燃方块的事件
                block_id = world.getBlockState(place_pos).getBlock().getTranslationKey();//获取点击的方块的ID

            } else if (stack.getItem() instanceof BucketItem)//判断是否为桶物品
            {
                action_type = ActionType.BUKKIT_USE_ACTION;//设置为使用桶的事件
                block_id = stack.getItem().getTranslationKey();//获取手中拿着的物品的ID

            } else {
                block_id = world.getBlockState(place_pos).getBlock().getTranslationKey();//获取点击的方块的ID
                action_type = ActionType.RIGHT_CLICK_BLOCK_ACTION;

            }

            HashMap<String, String> temp_data_slot = new HashMap<>();//临时的数据插槽
            temp_data_slot.put("name", block_id);
            Target target = new Target(TargetType.BLOCK, temp_data_slot);

            Action action = new Action(action_type, player, pos.getX(), pos.getY(), pos.getZ(),
                    str_dimension, target, Tool.GetCurrentTime());

            this.main.onStorage(action);

            return ActionResult.PASS;
        }));
    }

    public void RegAllEvent()//注册所有的事件
    {
        regBlockBreakEvent();
        regPlayerAttackEntityEvent();
        regPlayerAttackPlayerEvent();
        regKillPlayerEvent();
        regKillEntityEvent();
        regRightClickEvent();
    }

    public EventMgr(TgMain main) {
        this.dataQuery = main.getDataQuery();
        this.main = main;
    }
}
