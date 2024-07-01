package xyz.starrylandserver.thestarryguardforge.DataType;

import java.util.HashMap;

public class Target {
    public TargetType targetType;//目标的类型
    public HashMap<String,String>targetDataSlot;//目标的数据插槽

    public Target(TargetType target_type,HashMap<String,String> target_data_slot)//构造函数
    {
       this.targetType = target_type;
       this.targetDataSlot = target_data_slot;
    }
}
