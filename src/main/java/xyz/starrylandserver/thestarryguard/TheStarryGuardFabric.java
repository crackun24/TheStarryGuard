package xyz.starrylandserver.thestarryguard;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import xyz.starrylandserver.thestarryguard.Adapter.FabricAdapter;
import xyz.starrylandserver.thestarryguard.Commands.CommandsMgr;
import xyz.starrylandserver.thestarryguard.Events.EventMgr;

public class TheStarryGuardFabric implements ModInitializer {
    FabricAdapter adapter;
    EventMgr eventMgr;//事件管理类
    TgMain main;
    CommandsMgr mgr;//命令管理对象
    MinecraftServer mc_server;

    void regServerStartedEvent() {
        mgr = new CommandsMgr();
        mgr.RegAllCommands();//先注册

        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            this.mc_server = server;
            this.adapter = new FabricAdapter(this.mc_server);
            this.main = TgMain.getInstance(adapter);//获取服务主对象的实例

            main.start();
            mgr.setTgMain(main);//后设置对象

            this.eventMgr = new EventMgr(this.main);
            eventMgr.RegAllEvent();//注册事件
        }));
    }

    void regServerStoppedEvent()//注册服务器关闭的事件
    {
       ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
           this.adapter.ShutDownServer();
       }));
    }

    @Override
    public void onInitialize() {
        regServerStartedEvent(); //注册服务器开启的事件
        regServerStoppedEvent();
    }
}
