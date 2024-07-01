package xyz.starrylandserver.thestarryguard;

import net.minecraftforge.fml.loading.FMLPaths;
import xyz.starrylandserver.thestarryguard.Adapter.TgAdapter;
import xyz.starrylandserver.thestarryguard.DataBaseStorage.DataBase;
import xyz.starrylandserver.thestarryguard.DataBaseStorage.Mysql;
import xyz.starrylandserver.thestarryguard.DataBaseStorage.Sqlite;
import xyz.starrylandserver.thestarryguard.DataType.Action;
import xyz.starrylandserver.thestarryguard.DataType.QueryTask;
import xyz.starrylandserver.thestarryguard.DataType.TgPlayer;
import xyz.starrylandserver.thestarryguard.Operation.DataQuery;
import xyz.starrylandserver.thestarryguard.Operation.DataStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

//模组的主处理类
public class TgMain {
    String modType = "TheStarryGuardForge";
    TgConfig config;//配置对象
    TgAdapter adapter;//不同加载器的适配器
    Lang lang;//语言类
    DataBase database;//数据库管理类
    DataStorage dataStorage;//数据存储类
    DataQuery dataQuery;//数据查询类
    Feedback feedback;//反馈

    private TgMain()//私有构造函数
    {
    }

    private void InitDatabase()//初始化数据库
    {
        adapter.LOGGER_INFO(this.lang.getVal("initializing_database"));
        switch (this.config.GetValue("data_storage_type")) {
            case "mysql" -> {
                this.database = Mysql.GetMysql(this.config);
            }
            case "sqlite" -> {
                String sqlite_file_path = this.adapter.GetConfigFilePath();
                sqlite_file_path += "/TheStarryGuard" + Sqlite.FILE_NAME;//构建存放数据库文件的路径
                this.database = Sqlite.GetSqlite(new File(sqlite_file_path));
            }
            default -> throw new RuntimeException("Could not confirm database type you need.");
        }
        adapter.LOGGER_INFO(this.lang.getVal("database_connected"));
        this.dataQuery = DataQuery.GetDataQuery(this.database, this.adapter, this.lang);
        this.dataStorage = DataStorage.GetDataStorage(this.database, this.adapter);

        this.dataQuery.start();//启用查询服务
        this.dataStorage.start();//启用存储服务

    }

    public void LoadLang() throws IOException {
        Path file_path = Paths.get(this.adapter.GetLangFilePath());
        if (!Files.exists(file_path)) {//判断语言文件夹是否存在
            Files.createDirectories(file_path);//不存在则直接创建
        }
        String lang = this.config.GetValue("lang");//获取使用的语言

        String str_file_path = file_path + "/" + lang + ".properties";

        if (!Files.exists(Paths.get(str_file_path)))//判断文件是否存在
        {
            if (Objects.equals(lang, "zh_cn"))//如果是使用的默认的语言
            {
                String default_file_path = file_path + "/zh_cn.properties";
                Lang.genDefaultConf(default_file_path);//默认语言不存在则直接生成默认的语言文件
            } else {
                throw new RuntimeException("Could not find the lang file.");
            }
        }

        this.lang = Lang.LoadLang(str_file_path);//加载语言文件
    }

    public void LoadConf() throws IOException//加载配置文件
    {
        String conf_path = FMLPaths.CONFIGDIR.get().toString();
        this.config = TgConfig.LoadConfig(conf_path);//加载配置文件
    }

    public void InitFeedback()
    {
        this.feedback = new Feedback(this.modType);
        feedback.start();
    }

    public void start()//启动服务
    {
        try {
            LoadConf();//加载配置文件
            LoadLang();//加载语言文件
            InitDatabase();//初始化数据库
            InitFeedback();//初始化反馈服务
        } catch (Exception e) {
            e.printStackTrace();
            adapter.ShutDownServer();//有异常直接关闭服务器
        }
    }

    public synchronized DataQuery getDataQuery() {
        return this.dataQuery;
    }

    public synchronized DataStorage dataStorage() {
        return this.dataStorage;
    }

    public synchronized void onStorage(Action action) {
        this.dataStorage.InsertAction(action);//插入事件到队列中
    }

    public synchronized void onQuery(QueryTask queryTask) {
        this.dataQuery.AddQueryTask(queryTask);
    }

    public synchronized void onPageQuery(TgPlayer player, int page)//查询的翻页
    {
        this.dataQuery.AddPageQuery(player, page);
    }

    public synchronized TgAdapter getAdapter() {
        return this.adapter;
    }

    public synchronized Lang getLang()
    {
        return this.lang;
    }

    public static TgMain getInstance(TgAdapter adapter)//获取实例
    {
        TgMain temp = new TgMain();
        temp.adapter = adapter;//设置适配器
        return temp;
    }

}
