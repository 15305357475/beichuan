package org.fh.util.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.fh.entity.PageData;
import org.fh.service.system.FHlogService;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * 说明：邮件接收器 作者：fsci 授权：bsic
 */
public class SimpleMailReceiver {
	List<String> text = new ArrayList<String>();// 邮件纯文本内容
	List<String> textFile = new ArrayList<String>();// 邮件纯文本附件
	List<String> files = new ArrayList<String>();// 邮件附件列表
	List<String> html = new ArrayList<String>();// 邮件富文本列表
	List<String> img = new ArrayList<String>();// 邮件图片列表
	String emlPath = "";// 邮件归档路径
	String uuid;// 邮件本地存储唯一标识
	String year = DateUtil.getYear();// 年份
	String month = DateUtil.getDay().substring(5, 7);// 月份

	@Autowired
	private FHlogService FHLOG;

	/**
	 * 收取邮件
	 * 
	 * @param mailInfo pop3收取邮件信息
	 */
	public Message[] StoreMailPOP3(MailSenderInfo mailInfo) throws Exception {
		Message[] messages = null;
		// 创建一个有具体连接信息的Properties对象
		Properties prop = mailInfo.getPropertiesPOP3();
		// 1、创建session
		Session session = Session.getInstance(prop);
		// 2、通过session得到Store对象
		Store store = session.getStore();
		// 3、连上邮件服务器
		try {
			store.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(), mailInfo.getPassword());
			// 4、获得邮箱收件箱内的邮件
			Folder folder = store.getFolder("inbox");
			folder.open(Folder.READ_WRITE);
			// 获得邮件夹Folder内的所有邮件Message对象
			messages = folder.getMessages();
			// 5、关闭
			if (folder != null)
				folder.close(true);
			if (store != null)
				store.close();
		} catch (Exception e) {
			FHLOG.save(Jurisdiction.getUsername(), "尝试查新邮件，但是无法连接邮件服务器，异常信息为：" + e.getMessage()); // 记录日志
		}
		/*
		 * for (int i = 0; i < messages.length; i++) { String subject =
		 * messages[i].getSubject(); String from =
		 * (messages[i].getFrom()[0]).toString(); System.out.println("第 " + (i + 1) +
		 * "封邮件的主题：" + subject); System.out.println("第 " + (i + 1) + "封邮件的发件人地址：" +
		 * from); }
		 */
		return messages;
	}

	/**
	 * 收取邮件并解基本信息，构造Java bean
	 * 
	 * @param mailInfo IMAP适配器
	 * @param flag     标记：1:全部邮件；0：未读邮件
	 * @return List<PageData>
	 */
	public List<PageData> StoreMailIMAPRB(MailSenderInfo mailInfo, String flag) throws Exception {
		Message[] messages = null;
		List<PageData> beans = new ArrayList<PageData>();
		// try {
		Properties prop = mailInfo.getPropertiesIMAP();
		Session session = Session.getInstance(prop);
		IMAPStore store = (IMAPStore) session.getStore();
		store.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(), mailInfo.getPassword());
		IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
		folder.open(Folder.READ_WRITE);
		if (flag.equals("1")) {// 全部邮件
			messages = folder.getMessages();
		}
		if (flag.equals("0")) {// 仅未读邮件
			messages = folder.getMessages(folder.getMessageCount() - folder.getUnreadMessageCount() + 1,
					folder.getMessageCount());
		} else {// 错误参数
			return beans;
		}
		// 基本信息解析入库
		for (int i = 0; i < messages.length; i++) {
			if (!messages[i].getFolder().isOpen()) { // 判断是否open
				messages[i].getFolder().open(Folder.READ_WRITE);
			} // 如果close，就重新open
			MimeMessage msg = (MimeMessage) messages[i];// 获取一条
			String mailUID = Long.toString(folder.getUID(msg));
			InternetAddress address = getFrom(msg);
			PageData bean = new PageData();
			uuid = UuidUtil.get32UUID();
			// 构造bean
			bean.put("MAILINBOX_ID", uuid);
			bean.put("MAIL_FK", mailUID);
			bean.put("FROM_", address.getAddress());
			bean.put("TO_", getReceiveAddress(msg, RecipientType.TO));
			bean.put("CC_", getReceiveAddress(msg, RecipientType.CC));
			bean.put("BCC_", getReceiveAddress(msg, RecipientType.BCC));
			bean.put("SUBJECT", decodeText(msg.getSubject()));
			bean.put("SENT_TIME", msg.getSentDate());
			bean.put("REFESH_TIME", DateUtil.date2Str(new Date()));
			bean.put("READS_", "");
			bean.put("STATE", "1");
			bean.put("TAG", "IN");
			bean.put("ISDOWN", "0");// 未被下载
			// 加入列表
			beans.add(bean);
			// 标记为已读
			msg.setFlag(Flags.Flag.SEEN, true);
		}
		// 释放资源
		if (folder != null) {
			folder.close(true);
		}
		if (store != null) {
			store.close();
		}
		/*
		 * } catch (Exception e) { //System.out.println("异常：" + e.getMessage());
		 * FHLOG.save(Jurisdiction.getUsername(), "尝试查新邮件，但是无法连接邮件服务器，异常信息为：" +
		 * e.getMessage()); // 记录日志 }
		 */
		return beans;
	}

	/**
	 * 收取邮件并解基本信息，构造Java bean 拦截到垃圾箱的邮件
	 * 
	 * @param mailInfo IMAP适配器
	 * @param flag     标记：1:全部邮件；0：未读邮件
	 * @return List<PageData>
	 */
	public List<PageData> StoreMailIMAPRB_INTERCEPT(MailSenderInfo mailInfo, String flag) throws Exception {
		Message[] messages = null;
		List<PageData> beans = new ArrayList<PageData>();
		Properties prop = mailInfo.getPropertiesIMAP();
		Session session = Session.getInstance(prop);
		IMAPStore store = (IMAPStore) session.getStore();
		store.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(), mailInfo.getPassword());
		IMAPFolder folder = (IMAPFolder) store.getFolder("垃圾邮件"); // 垃圾箱
		folder.open(Folder.READ_WRITE);
		if (flag.equals("1")) {// 全部邮件
			messages = folder.getMessages();
		}
		if (flag.equals("0")) {// 仅未读邮件
			messages = folder.getMessages(folder.getMessageCount() - folder.getUnreadMessageCount() + 1,
					folder.getMessageCount());
		} else {// 错误参数
			return beans;
		}
		// 基本信息解析入库
		for (int i = 0; i < messages.length; i++) {
			if (!messages[i].getFolder().isOpen()) { // 判断是否open
				messages[i].getFolder().open(Folder.READ_WRITE);
			} // 如果close，就重新open
			MimeMessage msg = (MimeMessage) messages[i];// 获取一条
			String mailUID = Long.toString(folder.getUID(msg));
			InternetAddress address = getFrom(msg);
			PageData bean = new PageData();
			uuid = UuidUtil.get32UUID();
			// 构造bean
			bean.put("MAILINBOX_ID", uuid);
			bean.put("MAIL_FK", mailUID);
			bean.put("FROM_", address.getAddress());
			bean.put("TO_", getReceiveAddress(msg, RecipientType.TO));
			bean.put("CC_", getReceiveAddress(msg, RecipientType.CC));
			bean.put("BCC_", getReceiveAddress(msg, RecipientType.BCC));
			bean.put("SUBJECT", decodeText(msg.getSubject()));
			bean.put("SENT_TIME", msg.getSentDate());
			bean.put("REFESH_TIME", DateUtil.date2Str(new Date()));
			bean.put("READS_", "");
			bean.put("STATE", "1");
			bean.put("TAG", "IN");
			bean.put("ISDOWN", "0");// 未被下载
			bean.put("INTERCEPT", "1");// 垃圾邮件标记
			// 加入列表
			beans.add(bean);
			// 标记为已读
			msg.setFlag(Flags.Flag.SEEN, true);
		}
		// 释放资源
		if (folder != null) {
			folder.close(true);
		}
		if (store != null) {
			store.close();
		}
		return beans;
	}

	/**
	 * 收取邮件
	 * 
	 * @param mailInfo IMAP收取邮件信息
	 * @param flag     标记：1:全部邮件；0：未读邮件
	 * @return Message[]
	 */
	public Message[] StoreMailIMAPRM(MailSenderInfo mailInfo, String flag) throws Exception {
		Message[] messages = null;
		try {
			Properties prop = mailInfo.getPropertiesIMAP();
			Session session = Session.getInstance(prop);
			IMAPStore store = (IMAPStore) session.getStore();
			store.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(), mailInfo.getPassword());
			IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
			folder.open(Folder.READ_WRITE);
			if (flag.equals("1")) {// 全部邮件
				messages = folder.getMessages(0, folder.getMessageCount());
			}
			if (flag.equals("0")) {// 仅未读邮件
				messages = folder.getMessages(folder.getMessageCount() - folder.getUnreadMessageCount() + 1,
						folder.getMessageCount());
			} else {// 错误参数
				return messages;
			}
			// 基本信息解析入库
			for (int i = 0; i < messages.length; i++) {
				if (!messages[i].getFolder().isOpen()) { // 判断是否open
					messages[i].getFolder().open(Folder.READ_WRITE);
				} // 如果close，就重新open
					// 标记为已读
				MimeMessage msg = (MimeMessage) messages[i];
				msg.setFlag(Flags.Flag.SEEN, true);
			}
			// 释放资源
			if (folder != null)
				folder.close(true);
			if (store != null)
				store.close();
		} catch (Exception e) {
			FHLOG.save(Jurisdiction.getUsername(), "尝试查新邮件，但是无法连接邮件服务器，异常信息为：" + e.getMessage()); // 记录日志
		}
		return messages;
	}

	/**
	 * 收取并解析下载一封邮件
	 * 
	 * @param mailInfo     IMAP收取邮件信息
	 * @param UID          邮件唯一标识--服务端
	 * @param MAILINBOX_ID 邮件唯一标识--本地
	 * @return PageData 入库数据列表
	 * @throws Exception
	 */
	public PageData DownLoadOneMail(MailSenderInfo mailInfo, long UID, String MAILINBOX_ID,String INTERCEPT) throws Exception {
		Message message = null;
		uuid = MAILINBOX_ID;
		Properties prop = mailInfo.getPropertiesIMAP();
		Session session = Session.getInstance(prop);
		IMAPStore store = (IMAPStore) session.getStore();
		store.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(), mailInfo.getPassword());
		IMAPFolder folder;
		if(INTERCEPT.equals("1")) {// 垃圾邮件
			folder = (IMAPFolder) store.getFolder("垃圾邮件"); // 收件箱
		}else {
			folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
		}
		folder.open(Folder.READ_WRITE);
		message = folder.getMessageByUID(UID);
		// 如果close，就重新open
		if (!message.getFolder().isOpen()) { // 判断是否open
			message.getFolder().open(Folder.READ_WRITE);
		}
		// 解析邮件
		MimeMessage msg = (MimeMessage) message;
		System.out.println("是否包含附件：" + isContainAttachment(msg));
		// 解析邮件正文
		ParsingMailContent(msg);
		// 解析邮件附件
		ParsingMailAttachment(msg);
		// 释放资源
		if (folder != null) {
			folder.close(true);
		}
		if (store != null) {
			store.close();
		}
		PageData result = new PageData();
		if (text.size() > 0) {
			// String temp = text.toString().replaceAll("(?:\\[|null|\\]| +)", "");
			result.put("CONTENT", text.toString());
		}
		if (textFile.size() > 0) {
			// String temp = textFile.toString().replaceAll("(?:\\[|null|\\]| +)", "");
			String temp = listToString(textFile, '♬');
			result.put("TEXTFILE", temp);
		}
		if (html.size() > 0) {
			// String temp = html.toString().replaceAll("(?:\\[|null|\\]| +)", "");
			String temp = listToString(html, '♬');
			result.put("HTML", temp);
		}
		if (files.size() > 0) {
			// String temp = files.toString().replaceAll("(?:\\[|null|\\]| +)", "");
			String temp = listToString(files, '♬');
			result.put("FILES", temp);
		}
		if (img.size() > 0) {
			// String temp = img.toString().replaceAll("(?:\\[|null|\\]| +)", "");
			String temp = listToString(files, '♬');
			result.put("IMG", temp);
		}
		return result;
	}

	/**
	 * 下载指定邮件的邮件归档
	 * 
	 * @param mailInfo     IMAP收取邮件信息
	 * @param UID          邮件唯一标识--服务端
	 * @param MAILINBOX_ID 邮件唯一标识--本地
	 * @return String 归档地址
	 */
	public String DownLoadEmlFile(MailSenderInfo mailInfo, long UID, String MAILINBOX_ID) throws Exception {
		Message message = null;
		uuid = MAILINBOX_ID;
		try {
			Properties prop = mailInfo.getPropertiesIMAP();
			Session session = Session.getInstance(prop);
			IMAPStore store = (IMAPStore) session.getStore();
			store.connect(mailInfo.getMailServerHost(), mailInfo.getUserName(), mailInfo.getPassword());
			IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
			folder.open(Folder.READ_WRITE);
			message = folder.getMessageByUID(UID);
			// 如果close，就重新open
			if (!message.getFolder().isOpen()) { // 判断是否open
				message.getFolder().open(Folder.READ_WRITE);
			}
			// 解析邮件
			MimeMessage msg = (MimeMessage) message;
			// 生成归档
			String baseURI = PathUtil.getProjectpath() + Const.MAIL_FILES + year + "//" + month + "//" + uuid + "//";
			String shortURI = Const.MAIL_FILES + year + "//" + month + "//" + uuid + "//";
			long timestamp = System.currentTimeMillis();
			this.saveFile(msg.getInputStream(), baseURI + "archive_" + timestamp + ".eml");
			emlPath = shortURI + "archive_" + timestamp + ".eml";
			// 释放资源
			if (folder != null)
				folder.close(true);
			if (store != null)
				store.close();
		} catch (Exception e) {
			FHLOG.save(Jurisdiction.getUsername(), "尝试查新邮件，但是无法连接邮件服务器，异常信息为：" + e.getMessage()); // 记录日志
		}
		return emlPath;
	}

	/**
	 * 获得邮件发件人
	 * 
	 * @param msg 邮件内容
	 * @return 地址
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private InternetAddress getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
		Address[] froms = msg.getFrom();
		if (froms.length < 1)
			throw new MessagingException("没有发件人!");
		return (InternetAddress) froms[0];
	}

	/**
	 * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人 Message.RecipientType.TO 收件人
	 * Message.RecipientType.CC 抄送 Message.RecipientType.BCC 密送
	 * 
	 * @param msg  邮件内容
	 * @param type 收件人类型
	 * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
	 * @throws MessagingException
	 */
	private String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
		StringBuffer receiveAddress = new StringBuffer();
		Address[] addresss = null;
		if (type == null) {
			addresss = msg.getAllRecipients();
		} else {
			addresss = msg.getRecipients(type);
		}
		if (addresss == null || addresss.length < 1)
			// throw new MessagingException("没有收件人!");
			return "";
		for (Address address : addresss) {
			// 收件人地址格式："工务信息管理" <it@qbsic.com>
			InternetAddress internetAddress = (InternetAddress) address;
			String all_address = internetAddress.toUnicodeString();
			if (all_address.indexOf("<") == -1) {
				receiveAddress.append(all_address).append(",");
			} else {
				receiveAddress.append(all_address.substring(all_address.indexOf("<") + 1, all_address.indexOf(">")))
						.append(",");
			}
		}
		receiveAddress.deleteCharAt(receiveAddress.length() - 1); // 删除最后一个逗号
		return receiveAddress.toString();
	}

	/**
	 * 解析邮件正文： 文本、网页和图片
	 */
	public void ParsingMailContent(Part part) throws Exception {
		// 基础变量
		String baseURI = PathUtil.getProjectpath() + Const.MAIL_FILES + year + "//" + month + "//" + uuid + "//";
		String shortURI = Const.MAIL_FILES + year + "//" + month + "//" + uuid + "//";
		long timestamp = System.currentTimeMillis();
		// 邮件结构体
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		System.out.println("邮件结构体类型：" + contenttype + "[" + timestamp + "]");
		if (part.isMimeType("text/plain") && !conname) {
			text.add(part.getContent().toString());
			timestamp = System.currentTimeMillis();
			this.saveFile(part.getInputStream(), baseURI + "text_" + timestamp + ".txt");// 普通文本
			textFile.add(shortURI + "text_" + timestamp + ".txt");
		} else if (part.isMimeType("text/html") && !conname) {
			timestamp = System.currentTimeMillis();
			this.saveFile(part.getInputStream(), baseURI + "html_" + timestamp + ".html");// 普通网页
			html.add(shortURI + "html_" + timestamp + ".html");
		} else if (part.isMimeType("image/*")) {
			String name = part.getFileName();
			String fileName;
			timestamp = System.currentTimeMillis();
			try {
				// INLINE式图片
				if (name.startsWith("=?")) {// 是否以"=?"开始
					// 文件名格式：=?UTF-8?B?NzJ4NzIucG5n?=
					fileName = name.substring(name.lastIndexOf("?", name.lastIndexOf("?") - 1) + 1,
							name.lastIndexOf("?="));// 取倒数第二个?到?=之间的字符
					fileName = fileName + ".jpg";
				} else if (name.equals(null)) {
					fileName = ".jpg";
				} else {
					// 附件式图片
					fileName = name;
				}
			} catch (Exception e) {
				fileName = ".jpg";
			}
			this.saveFile(part.getInputStream(), baseURI + "img_" + timestamp + "_" + fileName);
			img.add(shortURI + "img_" + timestamp + "_" + fileName);
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				ParsingMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			ParsingMailContent((Part) part.getContent());
		} else {
			// Nothing to do
		}
	}

	/**
	 * 解析邮件附件
	 * 
	 * @param part 邮件中多个组合体中的其中一个组合体
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void ParsingMailAttachment(Part part) throws Exception, FileNotFoundException, IOException {
		String baseURI = PathUtil.getProjectpath() + Const.MAIL_FILES + year + "//" + month + "//" + uuid + "//";
		String shortURI = Const.MAIL_FILES + year + "//" + month + "//" + uuid + "//";
		long timestamp = System.currentTimeMillis();
		System.out.println("邮件结构体类型：" + part.getContentType() + "[" + timestamp + "]");
		if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent(); // 复杂体邮件
			int partCount = multipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String disp = bodyPart.getDisposition();
				if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
					timestamp = System.currentTimeMillis();
					InputStream is = bodyPart.getInputStream();
					this.saveFile(is, baseURI + "files_" + timestamp + "_" + decodeText(bodyPart.getFileName()));
					files.add(shortURI + "files_" + timestamp + "_" + decodeText(bodyPart.getFileName()));
				} else if (bodyPart.isMimeType("multipart/*")) {
					ParsingMailAttachment(bodyPart);
				} else {
					String contentType = bodyPart.getContentType();
					if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
						timestamp = System.currentTimeMillis();
						InputStream is = bodyPart.getInputStream();
						this.saveFile(is, baseURI + "files_" + timestamp + "_" + decodeText(bodyPart.getFileName()));
						System.out.println("附件文件名：" + bodyPart.getFileName());
						files.add(shortURI + "files_" + timestamp + "_" + decodeText(bodyPart.getFileName()));
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			ParsingMailAttachment((Part) part.getContent());
		}
	}

	/**
	 * 判断邮件中是否包含附件
	 * 
	 * @param msg 邮件内容
	 * @return 邮件中存在附件返回true，不存在返回false
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean isContainAttachment(Part part) throws MessagingException, IOException {
		boolean flag = false;
		if (part.isMimeType("multipart/*")) {
			MimeMultipart multipart = (MimeMultipart) part.getContent();
			int partCount = multipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String disp = bodyPart.getDisposition();
				if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
					flag = true;
				} else if (bodyPart.isMimeType("multipart/*")) {
					flag = isContainAttachment(bodyPart);
				} else {
					String contentType = bodyPart.getContentType();
					if (contentType.indexOf("application") != -1) {
						flag = true;
					}
					if (contentType.indexOf("name") != -1) {
						flag = true;
					}
				}
				if (flag)
					break;
			}
		} else if (part.isMimeType("message/rfc822")) {
			flag = isContainAttachment((Part) part.getContent());
		}
		return flag;
	}

	/**
	 * 读取输入流中的数据保存至指定目录
	 * 
	 * @param is       输入流
	 * @param fileName 文件名
	 * @param destDir  文件存储目录
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveFile(InputStream is, String destDir) throws FileNotFoundException, IOException {
		File file = new File(destDir);
		if (!file.exists()) {
			// 先得到文件的上级目录，并创建上级目录，在创建文件
			file.getParentFile().mkdirs();
			try {
				// 创建文件
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir)));
		int len = -1;
		while ((len = bis.read()) != -1) {
			bos.write(len);
			bos.flush();
		}
		bos.close();
		bis.close();
	}

	/**
	 * 文本解码
	 * 
	 * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
	 * @return 解码后的文本
	 * @throws UnsupportedEncodingException
	 */
	private String decodeText(String encodeText) throws UnsupportedEncodingException {
		if (encodeText == null || "".equals(encodeText)) {
			return "";
		} else {
			return MimeUtility.decodeText(encodeText);
		}
	}

	/**
	 * List转String
	 * 
	 * @param List      列表
	 * @param separator 分隔符
	 * @return String 字符串
	 * @throws UnsupportedEncodingException
	 */
	public String listToString(List<String> list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}
}