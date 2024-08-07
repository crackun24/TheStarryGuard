package xyz.starrylandserver.thestarryguard.DataType;


public class QueryTask {//玩家请求的查询任务

    public enum QueryType {POINT, AREA}

    //查询的类型是区域还是一个点
    public QueryType queryType;//查询的类型
    public final int Max_PAGE_AMOUNT = 5;//每页显示的最大结果数量
    public final int MAX_AREA_QUERY_AREA_RADIUS = 3;//最大的区域搜索半径
    public int pageId;//结果的页数
    public int x;
    public int y;
    public int z;
    public String dimensionName;//维度的名字
    public TgPlayer player;

    public QueryTask(int x, int y, int z, String dimension_name, QueryType query_type, TgPlayer player, int pageId) {//构造函数
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensionName = dimension_name;
        this.queryType = query_type;
        this.pageId = pageId;
        this.player = player;
    }

}
