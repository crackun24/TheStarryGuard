package xyz.starrylandserver.thestarryguardforge.DataType.SendMsg;

public class SendMsg {//发送到客户端的消息
    public SendMsgType type;
    public String data;

    public SendMsg(String data, SendMsgType type) {
        this.data = data;
        this.type = type;
    }
}
