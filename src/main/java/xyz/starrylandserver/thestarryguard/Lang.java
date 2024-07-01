package xyz.starrylandserver.thestarryguard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Lang {//语言文件类
    private Properties prop;
    private String langFilePath;//语言文件的路径
    private static final String DEFAULT_LANG_FILE = "initializing_database = 初始化数据库\n" +
            "no_permission = §4你没有权限操作\n" +
            "database_connected = 已连接到数据库\n" +
            "illegal_page = §4不合法的页数\n" +
            "no_data = §8没有数据\n" +
            "point_query_enable = 已启用方块查询\n" +
            "point_query_disable = 已关闭方块查询\n" +
            "ret_msg_tittle = §8---§4TheStarryGuard查询结果§8----§1共§3%s§1条记录§8----§1第 §3%s§8/§3%s §1页§8-----§f\n" +
            "ret_msg_foot  = §f使用§8/tg page<页数>§f可以查询指定的页数.\n" +
            "attack_entity = 攻击了\n" +
            "attack_player = 攻击了玩家\n" +
            "kill_entity = 击杀了\n" +
            "kill_player = 击杀了玩家\n" +
            "fire_block = 点燃了\n" +
            "block_place = 放置了\n" +
            "block_break = 破坏了\n" +
            "bukkit_use = 使用了桶\n" +
            "right_click_block = 右键了";

    private Lang() {
    }

    public synchronized void ReloadConfig() throws IOException {//重新加载语言文件
        File lang_file = new File(this.langFilePath);
        Reader reader = new InputStreamReader(new FileInputStream(lang_file), StandardCharsets.UTF_8);//以UTF-8的字符集读取配置文件数据
        prop.load(reader);
    }

    public synchronized static void genDefaultConf(String file_path) throws IOException//生成默认的配置文件
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_path), StandardCharsets.UTF_8));
        writer.write(DEFAULT_LANG_FILE);//写入配置文件的默认信息
        writer.close();//关闭输出流
    }

    public synchronized static Lang LoadLang(String file_path) throws IOException {
        Lang temp = new Lang();
        temp.prop = new Properties();//创建一个properties文件的解析对象
        temp.langFilePath = file_path;//设置配置文件的路径
        temp.ReloadConfig();
        return temp;
    }

    public synchronized String getVal(String key) {
        return this.prop.getProperty(key);//获取语言
    }
}
