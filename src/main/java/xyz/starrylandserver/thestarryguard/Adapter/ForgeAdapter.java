package xyz.starrylandserver.thestarryguard.Adapter;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.starrylandserver.thestarryguard.DataType.SendMsg.SendMsg;
import xyz.starrylandserver.thestarryguard.DataType.SendMsg.SendMsgType;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class ForgeAdapter implements TgAdapter {
    private final Logger LOGGER = LogManager.getLogger();

    @Override
    public String GetConfigFilePath() {
        return FMLPaths.CONFIGDIR.get().toAbsolutePath().toString();
    }

    @Override
    public String GetLangFilePath() {
        String config_path = GetConfigFilePath();
        return config_path + "/TheStarryGuard/lang/";
    }

    @Override
    public synchronized void LOGGER_INFO(String msg) {
        this.LOGGER.info(msg);
    }

    @Override
    public synchronized void LOGGER_WARN(String msg) {
        this.LOGGER.warn(msg);
    }

    @Override
    public synchronized void LOGGER_ERROR(String msg) {
        this.LOGGER.error(msg);
    }

    @Override
    public synchronized void LOGGER_DEBUG(String msg) {
        this.LOGGER.debug(msg);
    }

    @Override
    public synchronized void SendMsgToPlayer(TgPlayer player, String msg) {
        Component message = Component.literal(msg);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();//获取服务器对象
        var mcPlayer = server.getPlayerList().getPlayer(UUID.fromString(player.UUID));
        mcPlayer.sendSystemMessage(message);//发送消息给玩家
    }

    @Override
    public void SendMsgWithTransToPlayer(TgPlayer player, List<SendMsg> msgs) {
        Component text = Component.empty();
        for (SendMsg msg : msgs)//遍历消息
        {
            Component temp_msg;
            if (msg.type == SendMsgType.PLAIN) {//只是普通的消息
                temp_msg = Component.literal(msg.data);
            } else if (msg.type == SendMsgType.TRANSLATE_KEY) {//可以翻译的消息
                temp_msg = Component.translatable(msg.data);
            }else{
                throw new RuntimeException("Unknown msg type");
            }
            text = text.copy().append(temp_msg);
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();//获取服务器对象
        var mcPlayer = server.getPlayerList().getPlayer(UUID.fromString(player.UUID));
        mcPlayer.sendSystemMessage(text);//发送消息给玩家
    }


    @Override
    public void ShutDownServer() {
        MinecraftServer serve = ServerLifecycleHooks.getCurrentServer();
        serve.close();//关闭服务
    }

    public ForgeAdapter() {

    }
}
