package xyz.starrylandserver.thestarryguard.Commands;

import xyz.starrylandserver.thestarryguard.TgMain;

public class CommandsMgr {//命令管理类
    TgMain main;
    QueryNear queryNear;
    QueryPoint queryPoint;
    QueryPage queryPage;

    public void RegAllCommands() {//注册所有的指令
        this.queryNear = new QueryNear();
        this.queryPoint = new QueryPoint();
        this.queryPage = new QueryPage();

        this.queryNear.RegQueryAreaCommand();
        this.queryPoint.RegQueryPointCommand();
        this.queryPage.RegQueryPageCommand();
    }

    public void setTgMain(TgMain main)//设置main对象
    {
       this.main = main;
       this.queryNear.setTgMain(main);
       this.queryPoint.setTgMain(main);
       this.queryPage.setTgMain(main);
    }

    public CommandsMgr() {
    }
}
