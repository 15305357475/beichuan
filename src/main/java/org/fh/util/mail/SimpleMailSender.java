package org.fh.util.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.fh.util.PathUtil;

/**
 * 说明：邮件发送器 作者：fsci 授权：bsic
 */
public class SimpleMailSender {

	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo 待发送的邮件的信息
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) throws Exception {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getInstance(pro, authenticator);
		// 根据session创建一个邮件消息
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address to = new InternetAddress(mailInfo.getToAddress());
		mailMessage.setRecipient(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// 设置邮件消息的主要内容
		String mailContent = mailInfo.getContent();
		mailMessage.setText(mailContent);
		// 发送邮件
		Transport.send(mailMessage);
		return true;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo 待发送的邮件信息
	 */
	public boolean sendHtmlMail(MailSenderInfo mailInfo) throws Exception {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getInstance(pro, authenticator);
		// 根据session创建一个邮件消息
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address to = new InternetAddress(mailInfo.getToAddress());
		// Message.RecipientType.TO属性表示接收者的类型为TO
		mailMessage.setRecipient(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart html = new MimeBodyPart();
		// 设置HTML内容
		html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
		mainPart.addBodyPart(html);
		// 将MiniMultipart对象设置为邮件内容
		mailMessage.setContent(mainPart);
		// 发送邮件
		Transport.send(mailMessage);
		return true;
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @param mailInfo 待发送的邮件信息
	 */
	public static boolean sendFileMail(MailSenderInfo mailInfo) throws Exception {
		// 获取连接属性
		Properties prop = mailInfo.getProperties();
		// Session session = Session.getInstance(prop);
		// 获取到邮箱会话,利用匿名内部类的方式,将发送者邮箱用户名和密码授权给jvm
		Session session = Session.getDefaultInstance(prop, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailInfo.getUserName(), mailInfo.getPassword());
			}
		});
		// 构造连接对象
		// Transport ts = null;
		// ts = session.getTransport();
		// ts.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(),
		// mailInfo.getPassword());
		// 构造邮件对象
		MimeMessage message = new MimeMessage(session);
		// 发件人
		message.setFrom(new InternetAddress(mailInfo.getFromAddress()));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailInfo.getToAddress())); // 邮件的收件人
		// 抄送人
		InternetAddress[] ccaddress = null;
		if (!mailInfo.getCcAddress().equals("")) {
			String[] ccArray = mailInfo.getCcAddress().split(";");
			if (ccArray.length > 0) {
				ccaddress = new InternetAddress[ccArray.length];
				for (int i = 0; i < ccArray.length; i++) {
					try {
						ccaddress[i] = new InternetAddress(ccArray[i]);
					} catch (Exception e) {
						System.out.println("异常类型：" + e);
					}
				}
			}
			message.setRecipients(Message.RecipientType.CC, ccaddress);
		}
		// 密送人
		// message.setRecipient(Message.RecipientType.BCC, new
		// InternetAddress(mailInfo.getBccAddress())); // 邮件的密送人
		// 发件时间
		message.setSentDate(new Date());
		// 标题
		message.setSubject(mailInfo.getSubject());
		// 正文--HTML部分
		// 构造MiniMultipart容器,包含MimeBodyPart类型的对象
		Multipart mainPartHTML = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart html = new MimeBodyPart();
		// 设置HTML内容
		html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
		mainPartHTML.addBodyPart(html);
		// 将MiniMultipart对象设置为邮件内容
		message.setContent(mainPartHTML);
		// 正文--附件部分
		String filesPaths = mailInfo.getAttachFilePath();
		String filesNames = mailInfo.getAttachFileNames();
		if (!filesPaths.equals("") && filesPaths != null) {
			String[] pathArray = filesPaths.split("\\$");
			String[] nameArray = filesNames.split("\\$");
			Multipart mainPartFiles = new MimeMultipart();
			int i = 0;
			// 添加邮件附件
			for (String filepath : pathArray) {
				MimeBodyPart oneFile = new MimeBodyPart();
				DataSource ds = new FileDataSource(PathUtil.getProjectpath() + filepath);
				oneFile.setDataHandler(new DataHandler(ds));
				oneFile.setFileName(nameArray[i]);
				i++;
				// oneFile.attachFile(filepath);
				mainPartFiles.addBodyPart(oneFile);
			}
			message.setContent(mainPartFiles);
			message.saveChanges();
		}
		//ts.sendMessage(message, message.getAllRecipients());
		//ts.close();
		Transport.send(message, message.getAllRecipients());
		return true;
	}

	/**
	 * @param SMTP    邮件服务器
	 * @param PORT    端口
	 * @param EMAIL   本邮箱账号
	 * @param PAW     本邮箱密码
	 * @param toEMAIL 对方箱账号
	 * @param TITLE   标题
	 * @param CONTENT 内容
	 * @param TYPE    1：文本格式;2：HTML格式
	 */
	public static void sendEmail(String SMTP, String PORT, String EMAIL, String PAW, String toEMAIL, String TITLE,
			String CONTENT, String TYPE) throws Exception {
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(SMTP);
		mailInfo.setMailServerPort(PORT);
		mailInfo.setValidate(true);
		mailInfo.setUserName(EMAIL);
		mailInfo.setPassword(PAW);
		mailInfo.setFromAddress(EMAIL);
		mailInfo.setToAddress(toEMAIL);
		mailInfo.setSubject(TITLE);
		mailInfo.setContent(CONTENT);
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		if ("1".equals(TYPE)) {
			sms.sendTextMail(mailInfo);
		} else {
			sms.sendHtmlMail(mailInfo);
		}

	}

	public static void main(String[] args) {
		try {
			SimpleMailSender.sendEmail("smtp.126.com", "25", "fhadmin@126.com", "Fh123456", "XX@qq.com", "你好",
					"<p>你好</p>", "2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
