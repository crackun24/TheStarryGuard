package xyz.starrylandserver.thestarryguard.Adapter;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.starrylandserver.thestarryguard.DataType.SendMsg.SendMsg;
import xyz.starrylandserver.thestarryguard.DataType.SendMsg.SendMsgType;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;

//#if MC<11904
//$$ import net.minecraft.text.TranslatableText;
//#else
import net.minecraft.text.TranslatableTextContent;
//#endif

import java.util.List;
import java.util.UUID;

public class FabricAdapter implements TgAdapter {
    Logger LOGGER;
    MinecraftServer server;

    @Override
    public String GetConfigFilePath() {
        return FabricLoader.getInstance().getConfigDir().toString();
    }

    @Override
    public String GetLangFilePath() {
        return FabricLoader.getInstance().getConfigDir().toString() + "/TheStarryGuard/lang/";
    }

    @Override
    public synchronized void LOGGER_INFO(String msg) {
        LOGGER.info(msg);
    }

    @Override
    public synchronized void LOGGER_WARN(String msg) {
        LOGGER.warn(msg);
    }

    @Override
    public synchronized void LOGGER_ERROR(String msg) {
        LOGGER.error(msg);
    }

    @Override
    public synchronized void LOGGER_DEBUG(String msg) {
        LOGGER.debug(msg);
    }

    @Override
    public synchronized void SendMsgToPlayer(TgPlayer player, String msg) {
        PlayerEntity mc_player = this.server.getPlayerManager().getPlayer(UUID.fromString(player.UUID));
        //#if MC>=11904
        mc_player.sendMessage(Text.of(msg));//发送消息给玩家
        //#else
        //$$ mc_player.sendMessage(Text.of(msg), false);//发送消息给玩家
        //#endif
    }

    @Override
    public synchronized void SendMsgWithTransToPlayer(TgPlayer player, List<SendMsg> msgs) {
        PlayerEntity mc_player = this.server.getPlayerManager().getPlayer(UUID.fromString(player.UUID));
        //#if MC>11904
        Text text = Text.empty();//发送给玩家的消息
        //#else
        //$$ Text text = Text.of("");//发送给玩家的消息
        //#endif


        for (var msg : msgs)//拼接消息
        {
            Text temp_msg;
            if (msg.type == SendMsgType.PLAIN)//是普通的消息
            {
                temp_msg = Text.of(msg.data);
            } else {//是带有翻译的消息
                //#if MC>=11904
                temp_msg = Text.translatable(msg.data);
                //#else
                //$$ temp_msg = new TranslatableText(msg.data);
                //#endif

            }

            text = text.copy().append(temp_msg);
        }

        //#if MC>11904
        mc_player.sendMessage(text);
        //#else
        //$$ mc_player.sendMessage(text, false);
        //#endif
    }

    @Override
    public void ShutDownServer() {
        this.server.close();
    }

    public FabricAdapter(MinecraftServer server) {
        this.LOGGER = LogManager.getLogger();
        this.server = server;
    }

    public static String GetWorldName(World world)//获取世界的名字
    {
        String world_name;
        //#if MC<11701
        //$$ world_name = world.getRegistryKey().getValue().getNamespace();
        //#else
        world_name = world.getRegistryKey().getValue().toUnderscoreSeparatedString();
        //#endif

        return world_name;
    }
}
