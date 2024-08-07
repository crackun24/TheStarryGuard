package xyz.starrylandserver.thestarryguard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TgConfig {//配置文件类
    static final String CONFIG_VERSION_USING  = "1.0";//使用的配置文件的版本
    static final String CONFIG_FILE_NAME = "TheStarryGuard/TheStarryGuard.properties";//配置文件的名称
    static final String DEFAULT_CONFIG_DATA = """
                        config_version = 1.0
                        #data base type can be sqlite、 mysql
                        data_storage_type = sqlite

                        mysql_host = host
                        mysql_name = the_starry_guard
                        mysql_user = user
                        mysql_pass = pass
                        mysql_port = port

                        query_op_only = true
                        rollback_op_only = true
                        lang = zh_cn
                        """;//默认的配置文件信息
    private Properties prop;//properties的配置文件读取对象
    private TgConfig() {//构造函数
    }

    private static void CreateDefaultConfigFile(final File file) throws IOException//创建默认的配置文件
    {
        file.createNewFile();//创建新的文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));//创建一个输出流
        writer.write(DEFAULT_CONFIG_DATA);//写入配置文件的默认信息
        writer.close();//关闭输出流
    }

    static public TgConfig LoadConfig(String file_path) throws IOException//加载配置文件
    {
        TgConfig temp_obj = new TgConfig();//配置文件对象
        File config_folder = new File(file_path + "/TheStarryGuard");//文件夹的路径


        if (!config_folder.exists())//如果文件夹不存在
        {
            config_folder.mkdir();//创建文件夹
        }

        File config_file = new File(file_path + "/" + CONFIG_FILE_NAME);//打开文件
        if (!config_file.exists())//判断文件是否存在
        {
            CreateDefaultConfigFile(config_file);//如果文件不存在,则直接创建一个新的文件,并且写入默认的配置文件信息
        }

        Reader reader = new InputStreamReader(new FileInputStream(config_file), StandardCharsets.UTF_8);//以UTF-8的字符集读取配置文件数据
        temp_obj.prop = new Properties();//创建一个properties文件的解析对象

        temp_obj.prop.load(reader);//解析配置文件

        if(!temp_obj.GetValue("config_version").equals(CONFIG_VERSION_USING))//判断是否使用的是正确的配置文件版本
        {
            throw new RuntimeException("Incorrect config version you are using.");
        }

        return temp_obj;//返回创建的对象
    }

    public synchronized String GetValue(final String key)//获取配置文件的条目的内容
    {
        return this.prop.getProperty(key);//返回指定Key的value
    }
}
