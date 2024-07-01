package xyz.starrylandserver.thestarryguardforge.DataType;

public enum TargetType {
    PLAYER("player"),ENTITY("entity"),BLOCK("block");

    private final String targetName;

    public String getDisplayName()
    {
        return this.name();
    }

    public static TargetType fromString(String str_target)//从目标的字符串获取枚举
    {
        for(TargetType type :TargetType.values())
        {
            if(type.targetName.equalsIgnoreCase(str_target))
            {
                return type;//返回类型
            }
        }
        throw new RuntimeException("Could not parse the string target.");
    }

    TargetType(String str_type)
    {
        this.targetName = str_type;
    }
}
