package cn.sparta1029.sayi.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPSSLTransport;

public class EmailUtil {
	final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	private static final String sendUserName = "1535996137@qq.com";// 发送邮件需要连接的服务器的用户名
	private static final String sendPassword = "qfixehvyjkcphcae";// 发送邮件需要连接的服务器的密码
	private static final String sendProtocol = "465";// 发送邮件使用的端口
	private static final String sendHostAddress = "smtp.qq.com";// 发送邮件使用的服务器的地址

	public void sendMail(String MailAddress,String userAccount,String userPassword) throws AddressException,
			MessagingException {

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");// 服务器需要认证
		properties.setProperty("mail.transport.protocol", sendProtocol);// 声明发送邮件使用的端口
		properties.setProperty("mail.host", sendHostAddress);// 发送邮件的服务器地址
		properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.port", "465");

		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sendUserName, sendPassword);
			}
		});
		session.setDebug(true);// 在后台打印发送邮件的实时信息

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sendUserName));
		message.setSubject("找回密码 From SaYir");// 设置主题
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(MailAddress));// 发送
		message.setContent("From SaYi：</span>用户：" + userAccount
				+ "  正在使用找回密码服务，其密码为：" +userPassword+"</span>", "text/html;charset=gbk");
		SMTPSSLTransport.send(message);// 发送邮件
	}

}
