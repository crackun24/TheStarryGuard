package xyz.starrylandserver.thestarryguard.Event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.starrylandserver.thestarryguard.DataType.*;
import xyz.starrylandserver.thestarryguard.TgMain;
import xyz.starrylandserver.thestarryguard.Tool;

import java.util.HashMap;

public class RightClickEvent {
    TgMain main;

    @SubscribeEvent
    void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        String block_id;//方块的id
        BlockPos pos = event.getPos();//右键方块的位置
        Direction direction = event.getFace();//获取玩家相对于方块的朝向

        BlockPos place_pos = pos.offset(direction.getNormal());//获取玩家放置方块的坐标

        ActionType action_type;//右键的行为
        String str_dimension = event.getLevel().dimension().location().toString();//获取维度的字符

        Player mc_player = event.getEntity();
        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getStringUUID());//构建接口的玩家对象

        if (event.getItemStack().getItem() instanceof BlockItem && event.getLevel().isEmptyBlock(place_pos)) {
            pos = place_pos;//计算新的方块的位置
            block_id = event.getItemStack().getDescriptionId();//获取玩家手中的方块的ID这个就是放置的方块的ID
            action_type = ActionType.BLOCK_PLACE_ACTION;//设置为方块的放置事件
        } else if (event.getItemStack().getItem() instanceof FlintAndSteelItem)//玩家使用打火石的事件
        {
            action_type = ActionType.FIRE_BLOCK_ACTION;//设置为点燃方块的事件
            block_id = event.getLevel().getBlockState(event.getPos()).getBlock().getDescriptionId();//获取点击的方块的ID
        } else if (event.getItemStack().getItem() instanceof BucketItem)//判断是否为桶物品
        {
            action_type = ActionType.BUKKIT_USE_ACTION;//设置为使用桶的事件
            block_id = event.getItemStack().getItem().getDescriptionId();//获取手中拿着的物品的ID
        } else {
            block_id = event.getLevel().getBlockState(event.getPos()).getBlock().getDescriptionId();//获取点击的方块的ID
            action_type = ActionType.RIGHT_CLICK_BLOCK_ACTION;
        }

        HashMap<String, String> temp_data_slot = new HashMap<>();//临时的数据插槽
        temp_data_slot.put("name", block_id);
        Target target = new Target(TargetType.BLOCK, temp_data_slot);

        Action action = new Action(action_type, player, pos.getX(), pos.getY(), pos.getZ(),
                str_dimension, target, Tool.GetCurrentTime());

        this.main.onStorage(action);
    }

    public RightClickEvent(TgMain tg_main) {
        this.main = tg_main;
    }
}
