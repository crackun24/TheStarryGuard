package xyz.starrylandserver.thestarryguard.Command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguard.Operation.DataQuery;
import xyz.starrylandserver.thestarryguard.TgMain;

import java.util.UUID;

public class QueryPage {
    TgMain main;
    DataQuery dataQuery;
    String PERMISSION_NODE = "thestarryguard.query.querypage";

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

        int page = IntegerArgumentType.getInteger(dispatcher, "page");

        Player mc_player = dispatcher.getSource().getPlayer();

        if (mc_player == null)//判断获取的对象是否合法
        {
            return 1;
        }

        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getStringUUID());

        if (!hasPermission(mc_player)) {
            this.main.getAdapter().SendMsgToPlayer(player,this.main.getLang().getVal("no_permission"));
            return 1;
        }

        dataQuery.AddPageQuery(player, page);
        return 1;
    }


    public QueryPage(TgMain main) {
        this.main = main;
        this.dataQuery = main.getDataQuery();
    }
}
