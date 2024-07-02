package xyz.starrylandserver.thestarryguard.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.util.math.BlockPos;
import xyz.starrylandserver.thestarryguard.DataType.QueryTask;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguard.TgMain;

import java.util.UUID;

public class QueryNear {
    private TgMain main;
    private String PERMISSION_NODE = "thestarryguard.query.querynear";//权限节点

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
                .requires(source -> {
                    if (source.getEntity() instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
                        return hasPermission(player);
                    }
                    return false;
                })
                .then(CommandManager.literal("near")
                        .executes(this::executeCommand)));
    }

    private int executeCommand(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return 1;
        }

        ServerPlayerEntity mc_player = source.getPlayer();
        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());//构造玩家

        BlockPos location = mc_player.getBlockPos();//获取玩家的位置
        String dimension_name = mc_player.getWorld().getRegistryKey().getValue().toUnderscoreSeparatedString();//获取世界的id

        QueryTask task = new QueryTask(location.getX(), location.getY(), location.getZ(), dimension_name,
                QueryTask.QueryType.AREA, player, 1);

        if(main == null)
        {
            throw new RuntimeException("Service is not inited.");
        }
        this.main.onQuery(task);
        return 1;
    }

    public void RegQueryAreaCommand() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            onRegCommands(dispatcher);
        }));
    }

    public void setTgMain(TgMain main)//设置实体的main对象
    {
       this.main = main;
    }

    public QueryNear() {

    }
}
