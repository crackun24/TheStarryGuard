package xyz.starrylandserver.thestarryguard.Adapter;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.starrylandserver.thestarryguard.DataType.SendMsg.SendMsg;
import xyz.starrylandserver.thestarryguard.DataType.SendMsg.SendMsgType;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;

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
        PlayerEntity mc_player = this.server.getPlayerManager().getPlayer(UUID.fromString(player.UUID));
        mc_player.sendMessage(Text.of(msg));//发送消息给玩家
    }

    @Override
    public synchronized void SendMsgWithTransToPlayer(TgPlayer player, List<SendMsg> msgs) {
        PlayerEntity mc_player = this.server.getPlayerManager().getPlayer(UUID.fromString(player.UUID));

        Text text = Text.empty();//发送给玩家的消息

        for(var msg : msgs)//拼接消息
        {
            Text temp_msg;
            if(msg.type == SendMsgType.PLAIN)//是普通的消息
            {
                temp_msg = Text.of(msg.data);
            }else{//是带有翻译的消息
                temp_msg = Text.translatable(msg.data) ;
            }

            text = text.copy().append(temp_msg);
        }
        mc_player.sendMessage(text);
    }

    @Override
    public void ShutDownServer() {
        this.server.close();;
    }

    public FabricAdapter(MinecraftServer server) {
        this.LOGGER = LogManager.getLogger();
        this.server = server;
    }
}
