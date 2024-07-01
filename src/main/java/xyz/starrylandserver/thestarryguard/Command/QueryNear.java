package xyz.starrylandserver.thestarryguardforge.Command;

import com.mojang.brigadier.context.CommandContext;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import xyz.starrylandserver.thestarryguardforge.DataType.QueryTask;
import xyz.starrylandserver.thestarryguardforge.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguardforge.TgMain;

import java.util.UUID;

public class QueryNear {
    TgMain main;

    String PERMISSION_NODE = "thestarryguard.query.querynear";

    public boolean hasPermission(Player player) {//是否有权限
        if (player.hasPermissions(4))//判断玩家是否有op权限
        {
            return true;
        }

        LuckPerms lp = LuckPermsProvider.get();
        UserManager userManager = lp.getUserManager();
        User user = userManager.getUser(UUID.fromString(player.getStringUUID()));

        if (user == null) {
            throw new RuntimeException("Could not find the user.");
        }

        return user.getCachedData().getPermissionData(QueryOptions.defaultContextualOptions()).
                checkPermission(PERMISSION_NODE).asBoolean();//检查是否有对应的权限

    }

    public int onCommandExec(CommandContext<CommandSourceStack> dispatcher) {
        Player mc_player = dispatcher.getSource().getPlayer();
        if (mc_player == null) {
            return 1;
        }

        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getStringUUID());
        if (!hasPermission(mc_player)) {
            main.getAdapter().SendMsgToPlayer(player, main.getLang().getVal("no_permission"));
            return 1;
        }

        BlockPos pos = mc_player.getOnPos();
        String dimension_str = mc_player.level().dimension().location().toString();//获取维度的名字

        QueryTask task = new QueryTask(pos.getX(), pos.getY(), pos.getZ(), dimension_str,
                QueryTask.QueryType.AREA, player, 1);

        this.main.getDataQuery().AddQueryTask(task);

        return 1;
    }

    public QueryNear(TgMain main) {
        this.main = main;
    }
}
