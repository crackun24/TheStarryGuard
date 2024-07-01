package xyz.starrylandserver.thestarryguardforge.Operation;

import xyz.starrylandserver.thestarryguardforge.Adapter.TgAdapter;
import xyz.starrylandserver.thestarryguardforge.DataBaseStorage.DataBase;
import xyz.starrylandserver.thestarryguardforge.DataType.Action;

import java.util.*;


public class DataStorage extends Thread {//数据储存类,同时启动线程,不定时向数据库同步数据
    private Boolean isClose;//判断服务器是否关闭
    private DataBase mDataBase;//数据库对象
    private DataBase.DataBaseStorageType mDbStorageType;//使用存储数据库的类型
    private TgAdapter mAdapter;//游戏操作接口
    private final Queue<Action> mActionList;

    private synchronized Boolean GetMainCloseState()//获取主线程是否关闭
    {
        return this.isClose;
    }

    private synchronized void PutActionToDb() throws Exception//将玩家的行为存入数据库
    {
        while (!mActionList.isEmpty()) {//判断队列是否为空
            Action action = this.mActionList.poll();//弹出数据
            this.mDataBase.WriteActionToDb(action); //写入数据到数据库中
        }
    }

    public synchronized void CloseDataStorage()//关闭线程
    {
        this.isClose = true;
    }

    private DataStorage() {//构造函数
        this.mActionList = new LinkedList<>();//实例化队列对象
        this.isClose = false;//设置未关闭的状态
    }


    public synchronized void InsertAction(Action action) {//插入数据
        this.mActionList.add(action);
    }

    public void run() {//数据库同步数据的线程
        try {
            this.mAdapter.LOGGER_INFO("Connecting to database.");
            this.mDataBase.ConnectToDataBase();//连接到数据库
        } catch (Exception e) {
            this.mAdapter.LOGGER_ERROR("Could not connect to dataBase.");
            e.printStackTrace();
        }

        while (!GetMainCloseState()) {//主线程未发送关闭信号时无限循环
            try {
                sleep(1000);
                PutActionToDb();//弹出玩家的行为
            } catch (Exception e) {
                this.mAdapter.LOGGER_ERROR(String.format("An error occurred when running the data: %s", e));
                e.printStackTrace();
            }
        }
        try {
            PutActionToDb();//关闭的时候同步一次
        } catch (Exception e) {
            this.mAdapter.LOGGER_ERROR(String.format("An error occurred when closing record thread: %s", e));
            e.printStackTrace();
        }
    }

    static public DataStorage GetDataStorage(DataBase data_base, TgAdapter adapter) {//构造一个数据储存对象的工厂方法
        DataStorage temp = new DataStorage();//示例化一个对象
        temp.mDataBase = data_base;//设置使用的数据库
        temp.mAdapter = adapter;
        return temp;//返回构造好的对象
    }
}
