package xyz.starrylandserver.thestarryguardforge.Operation;

import xyz.starrylandserver.thestarryguardforge.Adapter.TgAdapter;
import xyz.starrylandserver.thestarryguardforge.DataBaseStorage.DataBase;
import xyz.starrylandserver.thestarryguardforge.DataType.Action;
import xyz.starrylandserver.thestarryguardforge.DataType.QueryTask;
import xyz.starrylandserver.thestarryguardforge.DataType.SendMsg.SendMsg;
import xyz.starrylandserver.thestarryguardforge.DataType.SendMsg.SendMsgType;
import xyz.starrylandserver.thestarryguardforge.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguardforge.Lang;
import xyz.starrylandserver.thestarryguardforge.Tool;

import java.util.*;

public class DataQuery extends Thread {//数据查询类
    private HashMap<TgPlayer, Byte> mPointQueryPlayer;//启用了点方块查询的玩家的哈希表,第二个数值无用
    private HashMap<TgPlayer, QueryTask> mPlayerLastTask;//玩家上一次查询的任务对照,方便进行翻页的操作
    private Queue<QueryTask> mQueryTask;//查询任务的玩家
    private Boolean mCloseState;//主线程的关闭状态
    private Lang mLang;//语言文件
    private TgAdapter adapter;//接口文件

    private DataQuery()//构造函数
    {

    }

    private DataBase mDataBase;

    private synchronized void DoTask() throws Exception//完成任务列表中的任务
    {
        if (this.mQueryTask.isEmpty())//如果任务队列为空,则直接返回
        {
            return;
        }

        QueryTask task = this.mQueryTask.poll();//弹出位于第一个的任务
        ArrayList<Action> action_list;//玩家的行为列表
        int total_page;//总共的页数
        int total_entries;//总共的条目数

        if (task.pageId <= 0) {
            this.adapter.SendMsgToPlayer(task.player, this.mLang.getVal("illegal_page"));//发送错误消息给玩家
            return;
        }

        switch (task.queryType)//判断查询的类型
        {
            case POINT:
                total_entries = this.mDataBase.GetPointActionCount(task);//获取符合要求的行为的数量
                total_page = total_entries % task.Max_PAGE_AMOUNT == 0 ? total_entries / task.Max_PAGE_AMOUNT : total_entries / task.Max_PAGE_AMOUNT + 1;

                if (total_entries == 0) {
                    this.adapter.SendMsgToPlayer(task.player, this.mLang.getVal("no_data"));
                    return;
                }
                if (task.pageId > total_page) {//判断玩家查询的页数是否大于最大的页数
                    this.adapter.SendMsgToPlayer(task.player, this.mLang.getVal("illegal_page"));//发送错误消息给玩家
                    return;
                }

                action_list = this.mDataBase.GetPointAction(task);//获取点的所有行为
                break;

            case AREA:
                total_entries = this.mDataBase.GetAreaActionCount(task);//获取符合要求的行为的数量
                total_page = total_entries % task.Max_PAGE_AMOUNT == 0 ? total_entries / task.Max_PAGE_AMOUNT : total_entries / task.Max_PAGE_AMOUNT + 1;

                if (total_entries == 0) {
                    this.adapter.SendMsgToPlayer(task.player, this.mLang.getVal("no_data"));//发送错误消息给玩家
                    return;
                }

                if (task.pageId > total_page) {//判断玩家查询的页数是否大于最大的页数
                    this.adapter.SendMsgToPlayer(task.player, this.mLang.getVal("illegal_page"));//发送错误消息给玩家
                    return;
                }

                action_list = this.mDataBase.GetAreaAction(task);//获取点的所有行为
                break;
            default://如果是未知的类型
                throw new IllegalStateException("Unexpected value: " + task.queryType);
        }

        List<SendMsg> send_msg_chain = new ArrayList<>();//要发送的消息

        String tittle = String.format(this.mLang.getVal("ret_msg_tittle") + "\n",
                total_entries, total_entries == 0 ? 0 : task.pageId,
                total_page);//发送给玩家的消息

        SendMsg tittle_msg = new SendMsg(tittle, SendMsgType.PLAIN);
        send_msg_chain.add(tittle_msg);

        long time = Tool.GetCurrentTime();

        for (Action action : action_list)//遍历返回的结果集
        {
            long delta_time = time - action.time;//获取时间差
            String time_msg = Tool.GetDateLengthDes(delta_time) + " ago";

            String target_id = action.target.targetDataSlot.get("name");//目标的id

            String entry = String.format("%-20s %-20s %-20s", action.player.name, time_msg, action.actionType.getTransName(mLang));

            SendMsg sendMsg = new SendMsg(entry, SendMsgType.PLAIN);
            SendMsg sendMsg_target = new SendMsg(target_id, SendMsgType.TRANSLATE_KEY);
            SendMsg sendMsg_endline = new SendMsg("\n", SendMsgType.PLAIN);

            send_msg_chain.add(sendMsg);
            send_msg_chain.add(sendMsg_target);
            send_msg_chain.add(sendMsg_endline);
        }

        SendMsg foot = new SendMsg(this.mLang.getVal("ret_msg_foot"), SendMsgType.TRANSLATE_KEY);
        send_msg_chain.add(foot);

        this.mPlayerLastTask.put(task.player, task);//放入玩家与上一个任务的映射中
        this.adapter.SendMsgWithTransToPlayer(task.player, send_msg_chain);//默认发送第一页的内容
    }

    private synchronized boolean getCloseState() {
        return this.mCloseState;
    }

    @Override
    public void run()//启动线程
    {
        while (!getCloseState())//主线程未关闭则一直运行查询线程
        {
            try {
                sleep(1000);
                DoTask();//完成任务
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            DoTask();//完成最后的任务
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized Boolean IsPlayerEnablePointQuery(TgPlayer player)//判断玩家是否启用了点方块查询的指令
    {
        return this.mPointQueryPlayer.containsKey(player);//判断是否含有这个键,如果有则直接返回true
    }

    public synchronized void EnablePlayerPointQuery(TgPlayer player)//启用玩家的点查询
    {
        this.mPointQueryPlayer.put(player, null);//插入到查询列表中
    }

    public synchronized void DisablePlayerPointQuery(TgPlayer player)//关闭玩家的点查询
    {
        this.mPointQueryPlayer.remove(player);//删除在查询列表中的玩家
    }

    public synchronized void AddQueryTask(QueryTask query_task)//添加查询任务到队列中
    {
        this.mQueryTask.add(query_task);//将查询任务添加进队列中
    }

    public synchronized void CloseDataQuery()//关闭查询线程
    {
        this.mCloseState = true;//设置关闭状态成立
    }

    public synchronized void AddPageQuery(TgPlayer player, int page)//有页数的查询 用于 tg p 页数,之前一定有查询过
    {
        if (!this.mPlayerLastTask.containsKey(player)) //判断是否有这个玩家的上一次请求
        {
            this.adapter.SendMsgToPlayer(player, this.mLang.getVal("illegal_page"));
            return;
        }
        QueryTask task = this.mPlayerLastTask.get(player);//获取玩家的上一次的任务
        task.pageId = page;//改写原来的页数

        this.mQueryTask.add(task);//将改写后的任务添加进队列中
    }

    static public DataQuery GetDataQuery(DataBase data_base, TgAdapter adapter, Lang lang) {//创建一个data_query对象
        DataQuery temp = new DataQuery();
        temp.mDataBase = data_base;//设置使用的数据库
        temp.mPointQueryPlayer = new HashMap<>();//初始化哈希表
        temp.mQueryTask = new LinkedList<>();//初始化查询任务的队列
        temp.mPlayerLastTask = new HashMap<>();//初始化玩家的上一个哈希表
        temp.mCloseState = false;
        temp.mLang = lang;
        temp.adapter = adapter;
        return temp;//返回构造好的对象
    }
}
