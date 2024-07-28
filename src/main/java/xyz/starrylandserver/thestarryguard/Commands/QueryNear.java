package xyz.starrylandserver.thestarryguard.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import xyz.starrylandserver.thestarryguard.Adapter.FabricAdapter;
import xyz.starrylandserver.thestarryguard.DataType.QueryTask;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguard.TgMain;

//#if MC>=11904
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//#else
//$$ import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//#endif

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
                .then(CommandManager.literal("near").executes(this::executeCommand)
                        .requires(source -> {
                            if (source.getEntity() instanceof ServerPlayerEntity) {
                                ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
                                return hasPermission(player);
                            }
                            return false;
                        })));
    }

    private int executeCommand(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return 1;
        }
        ServerPlayerEntity mc_player;

        try {
            mc_player = source.getPlayer();
        } catch (Exception e) {
            return 1;
        }

        String dimension_name;

        //#if MC>=11802
        dimension_name = FabricAdapter.GetWorldName(mc_player.getWorld());
        //#else
        //$$ dimension_name = FabricAdapter.GetWorldName(mc_player.getServerWorld());
        //#endif

        TgPlayer player = new TgPlayer(mc_player.getName().getString(), mc_player.getUuidAsString());//构造玩家

        BlockPos location = mc_player.getBlockPos();//获取玩家的位置

        QueryTask task = new QueryTask(location.getX(), location.getY(), location.getZ(), dimension_name,
                QueryTask.QueryType.AREA, player, 1);

        if (main == null) {
            throw new RuntimeException("Service is not inited.");
        }
        this.main.onQuery(task);
        return 1;
    }

    public void RegQueryAreaCommand() {
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
    }

    public QueryNear() {

    }
}
