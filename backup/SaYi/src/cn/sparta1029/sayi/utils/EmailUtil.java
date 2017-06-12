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

	private static final String sendUserName = "1535996137@qq.com";// �����ʼ���Ҫ���ӵķ��������û���
	private static final String sendPassword = "qfixehvyjkcphcae";// �����ʼ���Ҫ���ӵķ�����������
	private static final String sendProtocol = "465";// �����ʼ�ʹ�õĶ˿�
	private static final String sendHostAddress = "smtp.qq.com";// �����ʼ�ʹ�õķ������ĵ�ַ

	public void sendMail(String MailAddress,String userAccount,String userPassword) throws AddressException,
			MessagingException {

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");// ��������Ҫ��֤
		properties.setProperty("mail.transport.protocol", sendProtocol);// ���������ʼ�ʹ�õĶ˿�
		properties.setProperty("mail.host", sendHostAddress);// �����ʼ��ķ�������ַ
		properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.port", "465");

		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sendUserName, sendPassword);
			}
		});
		session.setDebug(true);// �ں�̨��ӡ�����ʼ���ʵʱ��Ϣ

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sendUserName));
		message.setSubject("�һ����� From SaYir");// ��������
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(MailAddress));// ����
		message.setContent("From SaYi��</span>�û���" + userAccount
				+ "  ����ʹ���һ��������������Ϊ��" +userPassword+"</span>", "text/html;charset=gbk");
		SMTPSSLTransport.send(message);// �����ʼ�
	}

}
