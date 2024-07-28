package xyz.starrylandserver.thestarryguard.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.starrylandserver.thestarryguard.Adapter.TgAdapter;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguard.Lang;
import xyz.starrylandserver.thestarryguard.Operation.DataQuery;
import xyz.starrylandserver.thestarryguard.TgMain;

//#if MC>=11904
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//#else
//$$ import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//#endif

import java.util.UUID;

public class QueryPoint {
    private TgMain main;
    private String PERMISSION_NODE = "thestarryguard.query.querypoint";
    private DataQuery query;
    private TgAdapter adapter;
    private Lang lang;

    private boolean hasPermission(ServerPlayerEntity player) {//是否有权限
        if (player.hasPermissionLevel(4))//判断玩家是否有op权限
        {
            return true;
        }

        LuckPerms lp = LuckPermsProvider.get();
        UserManager userManager = lp.getUserManager();
        User user = userManager.getUser(UUID.fromString(player.getUuidAsString()));

        if (user == null) {
            throw new RuntimeException("Could not find the user.");
        }

        return user.getCachedData().getPermissionData(QueryOptions.defaultContextualOptions()).
                checkPermission(PERMISSION_NODE).asBoolean();//检查是否有对应的权限

    }

    private void onRegCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tg")
                .then(CommandManager.literal("point")
                        .requires(source -> {
                            if (source.getEntity() instanceof ServerPlayerEntity) {
                                ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
                                return hasPermission(player);
                            }
                            return false;
                        }).executes(this::executeCommand)));
    }

    private int executeCommand(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return 1;
        }

        ServerPlayerEntity mc_player;
        try {
            mc_player = context.getSource().getPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());//构造玩家

        if (main == null) {
            throw new RuntimeException("Service is not inited.");
        }


        if (query.IsPlayerEnablePointQuery(player))//如果玩家启用了点查询
        {
            query.DisablePlayerPointQuery(player);//关闭点查询
            adapter.SendMsgToPlayer(player, this.lang.getVal("point_query_disable"));
        } else {
            query.EnablePlayerPointQuery(player);//启用点查询
            adapter.SendMsgToPlayer(player, this.lang.getVal("point_query_enable"));
        }

        return 1;

    }

    public void RegQueryPointCommand() {
        //#if MC>=11904
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            onRegCommands(dispatcher);
        }));
        //#else
        //$$ CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
        //$$   onRegCommands(dispatcher);
        //$$ }));
        //#endif
    }

    public void setTgMain(TgMain main)//设置实体的main对象
    {
        this.main = main;
        this.adapter = main.getAdapter();
        this.lang = main.getLang();
        this.query = main.getDataQuery();
    }

    public QueryPoint() {

    }
}
