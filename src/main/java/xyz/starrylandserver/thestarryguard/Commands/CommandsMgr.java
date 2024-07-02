package xyz.starrylandserver.thestarryguard.Commands;

import xyz.starrylandserver.thestarryguard.TgMain;

public class CommandsMgr {//命令管理类
    TgMain main;
    QueryNear queryNear;

    public void RegAllCommands() {//注册所有的指令
        this.queryNear = new QueryNear();
        this.queryNear.RegQueryAreaCommand();
    }

    public void setTgMain(TgMain main)//设置main对象
    {
       this.main = main;
       this.queryNear.setTgMain(main);
    }

    public CommandsMgr() {
    }
}
