package xyz.starrylandserver.thestarryguard;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import xyz.starrylandserver.thestarryguard.Adapter.FabricAdapter;
import xyz.starrylandserver.thestarryguard.Commands.CommandsMgr;
import xyz.starrylandserver.thestarryguard.Events.EventMgr;

public class TheStarryGuardFabric implements ModInitializer {
    FabricAdapter adapter;
    EventMgr eventMgr;//事件管理类
    TgMain main;
    MinecraftServer mc_server;
    CommandsMgr commandsMgr;

    void regServerStartedEvent() {
        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            this.mc_server = server;
            this.adapter = new FabricAdapter(this.mc_server);
            this.main = TgMain.getInstance(adapter);//获取服务主对象的实例
            main.start();

            this.eventMgr = new EventMgr(this.main);
            eventMgr.RegAllEvent();//注册事件

            this.commandsMgr.setTgMain(main);//设置为有效的对象
        }));
    }

    void regServerStoppedEvent()//注册服务器关闭的事件
    {
       ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
            this.main.CloseService();
       }));
    }

    @Override
    public void onInitialize() {
        this.commandsMgr = new CommandsMgr();
        commandsMgr.RegAllCommands();

        regServerStartedEvent(); //注册服务器开启的事件
        regServerStoppedEvent();
    }
}
