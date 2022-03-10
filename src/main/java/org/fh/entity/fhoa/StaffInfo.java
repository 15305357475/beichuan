package org.fh.entity.fhoa;

import org.fh.entity.Page;

/**
 * 说明：人员信息核验，人员基本信息实体 
 * 作者：f-sci 
 * 授权：bsic
 */
public class StaffInfo {
	private String ID; // 主键
	private String CARD; // 身份证
	private String NAME; // 姓名
	private String PHONE; // 电话
	private String REGISTERED; // 状态
	private String LABORRELATION;// 签约公司
	private String SERVICESDEPARTMENT;// 服务部门
	private String EMPLOYEETYPE;// 员工类型
	private String USERNO; // 员工编号
	private String TYPEOFWORK; // 工种
	private String TEAM; // 班组
	private String STATION; // 工位号
	private String CERTIFICATIONS;// 职业资格证书及有效期
	private String PHOTO;// 用户照片
	private String CERTIFICATIONS_IMGS;// 职业资格证书照片
	private String STATUS; // 状态，0：离职；1：在职
	private Page page; // 分页对象

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getCARD() {
		return CARD;
	}

	public void setCARD(String cARD) {
		CARD = cARD;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getREGISTERED() {
		return REGISTERED;
	}

	public void setREGISTERED(String rEGISTERED) {
		REGISTERED = rEGISTERED;
	}

	public String getLABORRELATION() {
		return LABORRELATION;
	}

	public void setLABORRELATION(String lABORRELATION) {
		LABORRELATION = lABORRELATION;
	}

	public String getSERVICESDEPARTMENT() {
		return SERVICESDEPARTMENT;
	}

	public void setSERVICESDEPARTMENT(String sERVICESDEPARTMENT) {
		SERVICESDEPARTMENT = sERVICESDEPARTMENT;
	}

	public String getEMPLOYEETYPE() {
		return EMPLOYEETYPE;
	}

	public void setEMPLOYEETYPE(String eMPLOYEETYPE) {
		EMPLOYEETYPE = eMPLOYEETYPE;
	}

	public String getUSERNO() {
		return USERNO;
	}

	public void setUSERNO(String uSERNO) {
		USERNO = uSERNO;
	}

	public String getTYPEOFWORK() {
		return TYPEOFWORK;
	}

	public void setTYPEOFWORK(String tYPEOFWORK) {
		TYPEOFWORK = tYPEOFWORK;
	}

	public String getTEAM() {
		return TEAM;
	}

	public void setTEAM(String tEAM) {
		TEAM = tEAM;
	}

	public String getSTATION() {
		return STATION;
	}

	public void setSTATION(String sTATION) {
		STATION = sTATION;
	}

	public String getCERTIFICATIONS() {
		return CERTIFICATIONS;
	}

	public void setCERTIFICATIONS(String cERTIFICATIONS) {
		CERTIFICATIONS = cERTIFICATIONS;
	}

	public String getPHOTO() {
		return PHOTO;
	}

	public void setPHOTO(String pHOTO) {
		PHOTO = pHOTO;
	}

	public String getCERTIFICATIONS_IMGS() {
		return CERTIFICATIONS_IMGS;
	}

	public void setCERTIFICATIONS_IMGS(String cERTIFICATIONS_IMGS) {
		CERTIFICATIONS_IMGS = cERTIFICATIONS_IMGS;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
