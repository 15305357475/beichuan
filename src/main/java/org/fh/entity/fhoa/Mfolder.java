package org.fh.entity.fhoa;

import java.util.List;

 /** 
 * 说明：文件管理实体类
 * 作者：fsci
 * 授权：bsic
 */
public class Mfolder{ 
	
	private String MFOLDER_ID;	//主键
	private String NAME;					//名称
	private String PARENT_ID;				//父类ID
	private String target;
	private Mfolder mfolder;
	private List<Mfolder> subMfolder;
	private boolean hasMfolder = false;
	private String treeurl;
	
	private String FILEPATH;			//路径
	public String getFFILEPATH() {
		return FILEPATH;
	}
	public void setFFILEPATH(String FILEPATH) {
		this.FILEPATH = FILEPATH;
	}
	private String CTIME;			//上传时间
	public String getFCTIME() {
		return CTIME;
	}
	public void setFCTIME(String CTIME) {
		this.CTIME = CTIME;
	}
	private String UNAME;			//上传者
	public String getFUNAME() {
		return UNAME;
	}
	public void setFUNAME(String UNAME) {
		this.UNAME = UNAME;
	}
	private String MASTER;			//所属人
	public String getFMASTER() {
		return MASTER;
	}
	public void setFMASTER(String MASTER) {
		this.MASTER = MASTER;
	}
	private String FILESIZE;			//文件大小
	public String getFFILESIZE() {
		return FILESIZE;
	}
	public void setFFILESIZE(String FILESIZE) {
		this.FILESIZE = FILESIZE;
	}
	private String SHARE;			//是否共享
	public String getFSHARE() {
		return SHARE;
	}
	public void setFSHARE(String SHARE) {
		this.SHARE = SHARE;
	}
	private String REMARKS;			//备注说明
	public String getFREMARKS() {
		return REMARKS;
	}
	public void setFREMARKS(String REMARKS) {
		this.REMARKS = REMARKS;
	}

	public String getMFOLDER_ID() {
		return MFOLDER_ID;
	}
	public void setMFOLDER_ID(String MFOLDER_ID) {
		this.MFOLDER_ID = MFOLDER_ID;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String NAME) {
		this.NAME = NAME;
	}
	public String getPARENT_ID() {
		return PARENT_ID;
	}
	public void setPARENT_ID(String PARENT_ID) {
		this.PARENT_ID = PARENT_ID;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Mfolder getMfolder() {
		return mfolder;
	}
	public void setMfolder(Mfolder mfolder) {
		this.mfolder = mfolder;
	}
	public List<Mfolder> getSubMfolder() {
		return subMfolder;
	}
	public void setSubMfolder(List<Mfolder> subMfolder) {
		this.subMfolder = subMfolder;
	}
	public boolean isHasMfolder() {
		return hasMfolder;
	}
	public void setHasMfolder(boolean hasMfolder) {
		this.hasMfolder = hasMfolder;
	}
	public String getTreeurl() {
		return treeurl;
	}
	public void setTreeurl(String treeurl) {
		this.treeurl = treeurl;
	}
	
}
