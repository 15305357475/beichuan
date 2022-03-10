package org.fh.entity.fhoa;

import org.fh.entity.Page;

/**
 * 说明：登记用户白名单 作者：f-sci 授权：bsic
 */
public class Registered {
    private Integer ID; // 主键
    private String CARD; // 身份证
    private String NAME; // 姓名
    private String PHONE; // 电话
    private String REGISTERED; // 状态
    private String LABORRELATION;// 签约公司
    private String SERVICESDEPARTMENT;// 当前用工部门
    private String EMPLOYEETYPE;// 劳动关系：正式、派遣
    private String USERNO;        // 员工编号
    private String EMPLOYER;    // 最初签约用工部门
    private String WAYS;    // 用工方式：直签、转包
    private String MAJOR;    // 专业
    private String STATION;    // 岗位
    private String GENDER;    // 性别
    private String JOB;    // 工种
    private String PHOTO;    // 人像照片
    private String TEAM;    // 班组
    private String ONJOB;    // 0为在岗，1为不在岗
    private String PHOTO_URL;// http地址上的照片
    private String ALREADY_DOWN;    // 1:已经下载照片；0：未下载照片
    private Page page; // 分页对象

    public String getPHOTO_URL() {
        return PHOTO_URL;
    }

    public void setPHOTO_URL(String PHOTO_URL) {
        this.PHOTO_URL = PHOTO_URL;
    }

    public String getEMPLOYER() {
        return EMPLOYER;
    }

    public void setEMPLOYER(String EMPLOYER) {
        this.EMPLOYER = EMPLOYER;
    }

    public String getWAYS() {
        return WAYS;
    }

    public void setWAYS(String WAYS) {
        this.WAYS = WAYS;
    }

    public String getMAJOR() {
        return MAJOR;
    }

    public void setMAJOR(String MAJOR) {
        this.MAJOR = MAJOR;
    }

    public String getSTATION() {
        return STATION;
    }

    public void setSTATION(String STATION) {
        this.STATION = STATION;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getJOB() {
        return JOB;
    }

    public void setJOB(String JOB) {
        this.JOB = JOB;
    }

    public String getPHOTO() {
        return PHOTO;
    }

    public void setPHOTO(String PHOTO) {
        this.PHOTO = PHOTO;
    }

    public String getTEAM() {
        return TEAM;
    }

    public void setTEAM(String TEAM) {
        this.TEAM = TEAM;
    }

    public String getONJOB() {
        return ONJOB;
    }

    public void setONJOB(String ONJOB) {
        this.ONJOB = ONJOB;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer iD) {
        this.ID = iD;
    }

    public String getCARD() {
        return CARD;
    }

    public void setCARD(String cARD) {
        this.CARD = cARD;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        this.NAME = nAME;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String pHONE) {
        this.PHONE = pHONE;
    }

    public String getREGISTERED() {
        return REGISTERED;
    }

    public void setREGISTERED(String rEGISTERED) {
        this.REGISTERED = rEGISTERED;
    }

    public String getLaborRelation() {
        return LABORRELATION;
    }

    public void setLaborRelation(String laborRelation) {
        this.LABORRELATION = laborRelation;
    }

    public String getServicesDepartment() {
        return SERVICESDEPARTMENT;
    }

    public void setServicesDepartment(String servicesDepartment) {
        this.SERVICESDEPARTMENT = servicesDepartment;
    }

    public String getEmployeeType() {
        return EMPLOYEETYPE;
    }

    public void setEmployeeType(String employeeType) {
        this.EMPLOYEETYPE = employeeType;
    }

    public String getUserNo() {
        return USERNO;
    }

    public void setUserNo(String userNo) {
        this.USERNO = userNo;
    }

    public String getALREADY_DOWN() {
        return ALREADY_DOWN;
    }

    public void setALREADY_DOWN(String ALREADY_DOWN) {
        this.ALREADY_DOWN = ALREADY_DOWN;
    }

    public Page getPage() {
        if (page == null)
            page = new Page();
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
