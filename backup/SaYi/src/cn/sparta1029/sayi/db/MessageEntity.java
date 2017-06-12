package cn.sparta1029.sayi.db;

public class MessageEntity {
public String messageReceiver;
public String messageSender;
public String messageContent;
public String messageIsReaded;
public String messageTime;

public MessageEntity(String receiver,String sender, String message,String isReaded,String time) {
	this.messageReceiver=receiver;
	this.messageSender = sender;
	this.messageContent = message;
	this.messageIsReaded=isReaded;
	this.messageTime=time;
}
}