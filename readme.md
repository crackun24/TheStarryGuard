
## 使用方法
- 加入到mods文件夹中，启动游戏(仅服务端有效)
- **前置模组 luckperm**




### 记录的事件
- 玩家破坏方块事件
- 玩家放置方块事件
- 玩家点燃方块的事件
- 玩家攻击生物事件
- 玩家攻击玩家事件
- 玩家击杀生物事件
- 玩家击杀玩家事件
- 玩家右键方块事件

### **注意事项**
- <font color = "red">配置文件可能无法跨版本使用,如果提示配置文件错误,请删除后等待自动生成后重新配置</font>
- 请不要把数据库中带有map字段的表删除，否则会造成数据错误
- 语言文件请使用unicode的格式保存
- 测试版本，如果有问题请及时提交到issue中

支持本人的开发： [赞助链接](https://afdian.net/a/StarryLandServer)

### 配置文件
~~~
#data base type can be sqlite、 mysql
data_storage_type = sqlite #使用的数据库可以是mysql 或者 sqlite

mysql_host = host #mysql的地址
mysql_name = the_starry_guard #mysql 的数据库的名字
mysql_user = user #mysql 的用户名
mysql_pass = pass #mysql 的密码
mysql_port = port #mysql 的端口

lang = zh_cn #使用的语言 (位于/config/TheStarryGuard/lang/下的文件)
~~~