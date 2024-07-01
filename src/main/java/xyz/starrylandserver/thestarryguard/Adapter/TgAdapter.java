package xyz.starrylandserver.thestarryguardforge.Adapter;

import xyz.starrylandserver.thestarryguardforge.DataType.SendMsg.SendMsg;
import xyz.starrylandserver.thestarryguardforge.DataType.TgPlayer;

import java.util.List;

public interface TgAdapter {//不同的模组加载器的适配接口
   String GetConfigFilePath();//获取配置文件夹的路径
   String GetLangFilePath();//获取语言文件的路径
   void LOGGER_INFO(String string);//记录器的输出接口
   void LOGGER_WARN(String string);
   void LOGGER_ERROR(String string);
   void LOGGER_DEBUG(String string);
   void SendMsgToPlayer(TgPlayer player, String msg);//发送消息给玩家
   void SendMsgWithTransToPlayer(TgPlayer player, List<SendMsg> msgs);//发送带有翻译消息的消息给玩家
   void ShutDownServer();//关闭服务
}
