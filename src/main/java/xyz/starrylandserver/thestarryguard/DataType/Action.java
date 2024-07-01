package xyz.starrylandserver.thestarryguard.DataType;


public class Action {//玩家的行为类

    public TgPlayer player;//触发这个事件的玩家
    public int posX;
    public int posY;
    public int posZ;
    public String dimension;//玩家触发这个事件的维度
    public String actionData;//事件的数据
    public long time;//触发事件的时间
    public ActionType actionType;
    public Target target;//事件的目标


    public Action(ActionType action_type, TgPlayer player, int x, int y, int z, String dimension,Target target,long time) {
        this.player = player;
        this.posX = x;
        this.posY = y;
        this.posZ = z;//设置玩家影响的目标的坐标
        this.dimension = dimension;//设置玩家触发事件的维度
        this.actionType = action_type;
        this.target  = target;//设置目标
        this.time  = time;
    }
}
