package com.thestarryguard.thestarryguard.Command;

import com.thestarryguard.thestarryguard.CommandMgr;
import com.thestarryguard.thestarryguard.Operation.DataQuery;
import com.thestarryguard.thestarryguard.DataType.QueryTask;
import com.thestarryguard.thestarryguard.Lang;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class QueryNear {//查询区域的命令
    private DataQuery mDataQuery;
    private Lang mLang;

    public void RegQueryAreaCommand(int permission_level) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal(CommandMgr.COMMAND_PREFIX).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(permission_level))
                        .then(literal("near")
                                .executes(context -> {
                                    // 在这里执行您的操作
                                    try {
                                        ServerPlayerEntity player = context.getSource().getPlayer();
                                        if (player == null) {//判断玩家是否正常获取
                                            return 1;
                                        }

                                        String player_name = player.getName().getString();//获取玩家的名字
                                        if (player_name.isEmpty())//判断是否可以正常的获取玩家的名字
                                        {
                                            return 1;
                                        }

                                        BlockPos location = player.getBlockPos();//获取玩家的位置
                                        String dimension_name = player.getWorld().getRegistryKey().getValue().toUnderscoreSeparatedString();//获取世界的id

                                        QueryTask task = new QueryTask(location.getX(), location.getY(), location.getZ(), dimension_name,
                                                QueryTask.QueryType.AREA, player_name,1);

                                        this.mDataQuery.AddQueryTask(task);//加入查询的任务

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        context.getSource().sendMessage(Text.literal("§cInternal error."));
                                    }

                                    return 1;
                                })
                        )
        ));
    }

    public QueryNear(DataQuery data_query, Lang lang) {
        this.mDataQuery = data_query;
        this.mLang = lang;
    }

}
