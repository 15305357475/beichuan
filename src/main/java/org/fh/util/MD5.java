package org.fh.util;

import java.security.MessageDigest;

/**
 * 说明：MD5处理
 * 作者：f-sci
 * 授权：bsic
 */
public class MD5 {

	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return str;
	}
	/*
	 * public static void main(String[] args) { System.out.println(md5("fsci"));
	 * System.out.println(md5("123456mj1")); }
	 */
}
