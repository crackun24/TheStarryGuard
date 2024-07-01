package xyz.starrylandserver.thestarryguardforge.DataType;

public class TgPlayer {//玩家类
    public String UUID;
    public String name;

    @Override
    public int hashCode() {

        return this.UUID.hashCode();//使用uuid作为哈希值计算对象
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TgPlayer temp)) {
            return false;
        }
        return this.UUID.equals(temp.UUID);
    }


    public TgPlayer(String name, String uuid) {//构造函数
        this.name = name;
        this.UUID = uuid;
    }
}
