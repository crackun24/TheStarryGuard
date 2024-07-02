package xyz.starrylandserver.thestarryguard;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import xyz.starrylandserver.thestarryguard.Adapter.FabricAdapter;
import xyz.starrylandserver.thestarryguard.Events.EventMgr;

public class TheStarryGuardFabric implements ModInitializer {
    FabricAdapter adapter;
    EventMgr eventMgr;//事件管理类
    TgMain main;
    MinecraftServer mc_server;

    void regServerStartedEvent() {
        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            this.mc_server = server;
            this.adapter = new FabricAdapter(this.mc_server);
            this.main = TgMain.getInstance(adapter);//获取服务主对象的实例
            main.start();

            this.eventMgr = new EventMgr(this.main);
            eventMgr.RegAllEvent();//注册事件
        }));
    }

    void regServerStoppedEvent()//注册服务器关闭的事件
    {
       ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
           this.main.getDataQuery().CloseDataQuery();
           this.main.dataStorage().CloseDataStorage();
           this.main.feedback.close();

           System.out.println("closed");//test
       }));
    }

    @Override
    public void onInitialize() {
        regServerStartedEvent(); //注册服务器开启的事件
        regServerStoppedEvent();
    }
}
