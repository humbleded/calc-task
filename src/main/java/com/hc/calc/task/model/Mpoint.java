package com.hc.calc.task.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Mpoint implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -4827693154631243582L;

    private Long id;

    private String mpointType;

    private Long siteId;

    private String siteName;

    private String categoryName;

    private Long tenantId;

    private String mpointId;

    private String mpointName;

    private Long categoryId;

    private String datasource;

    private String datype;

    private String unit;

    private Long numtail;

    private String enumvalue;

    private BigDecimal upperRange;

    private BigDecimal lowerRange;

    private String slope;

    private String increase;

    private String coefficient;

    private BigDecimal cardinality;

    private BigDecimal magnification;

    private String remarks;

    private Date createTime;

    private Long createUserid;

    private Date updateTime;

    private Long updateUserid;

    private String updateUserName;

    private Long deleteFlag;

    private String point;

    private Long equipment;

    private String readWriteSet;

    private String roleId;

    private String pushSet;

    private String deadZone;

    private Integer cycle;

    private String curveYaxisLowerRange;

    private String curveYaxisUpperRange;

    private String pointsControl;

    private Long startPoint;

    private Long stopPoint;

    private Integer storageCycle;

    public Integer getStorageCycle() {
        return storageCycle;
    }

    public void setStorageCycle(Integer storageCycle) {
        this.storageCycle = storageCycle;
    }

    public Long getEquipment() {
        return equipment;
    }

    public String getMpointType() {
        return mpointType;
    }

    public void setMpointType(String mpointType) {
        this.mpointType = mpointType;
    }

    public void setEquipment(Long equipment) {
        this.equipment = equipment;
    }

    public String getReadWriteSet() {
        return readWriteSet;
    }

    public void setReadWriteSet(String readWriteSet) {
        this.readWriteSet = readWriteSet;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPushSet() {
        return pushSet;
    }

    public void setPushSet(String pushSet) {
        this.pushSet = pushSet;
    }

    public String getDeadZone() {
        return deadZone;
    }

    public void setDeadZone(String deadZone) {
        this.deadZone = deadZone;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }


    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMpointId() {
        return mpointId;
    }

    public void setMpointId(String mpointId) {
        this.mpointId = mpointId == null ? null : mpointId.trim();
    }

    public String getMpointName() {
        return mpointName;
    }

    public void setMpointName(String mpointName) {
        this.mpointName = mpointName == null ? null : mpointName.trim();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource == null ? null : datasource.trim();
    }

    public String getDatype() {
        return datype;
    }

    public void setDatype(String datype) {
        this.datype = datype == null ? null : datype.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public Long getNumtail() {
        return numtail;
    }

    public void setNumtail(Long numtail) {
        this.numtail = numtail;
    }

    public String getEnumvalue() {
        return enumvalue;
    }

    public void setEnumvalue(String enumvalue) {
        this.enumvalue = enumvalue == null ? null : enumvalue.trim();
    }

    public BigDecimal getUpperRange() {
        return upperRange;
    }

    public void setUpperRange(BigDecimal upperRange) {
        this.upperRange = upperRange;
    }

    public BigDecimal getLowerRange() {
        return lowerRange;
    }

    public void setLowerRange(BigDecimal lowerRange) {
        this.lowerRange = lowerRange;
    }


    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getIncrease() {
        return increase;
    }

    public void setIncrease(String increase) {
        this.increase = increase;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public BigDecimal getCardinality() {
        return cardinality;
    }

    public void setCardinality(BigDecimal cardinality) {
        this.cardinality = cardinality;
    }

    public BigDecimal getMagnification() {
        return magnification;
    }

    public void setMagnification(BigDecimal magnification) {
        this.magnification = magnification;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUserid() {
        return createUserid;
    }

    public void setCreateUserid(Long createUserid) {
        this.createUserid = createUserid;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUserid() {
        return updateUserid;
    }

    public void setUpdateUserid(Long updateUserid) {
        this.updateUserid = updateUserid;
    }

    public Long getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Long deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }


    public String getCurveYaxisLowerRange() {
        return curveYaxisLowerRange;
    }

    public void setCurveYaxisLowerRange(String curveYaxisLowerRange) {
        this.curveYaxisLowerRange = curveYaxisLowerRange;
    }

    public String getCurveYaxisUpperRange() {
        return curveYaxisUpperRange;
    }

    public void setCurveYaxisUpperRange(String curveYaxisUpperRange) {
        this.curveYaxisUpperRange = curveYaxisUpperRange;
    }

    public String getPointsControl() {
        return pointsControl;
    }

    public void setPointsControl(String pointsControl) {
        this.pointsControl = pointsControl;
    }

    public Long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Long startPoint) {
        this.startPoint = startPoint;
    }

    public Long getStopPoint() {
        return stopPoint;
    }

    public void setStopPoint(Long stopPoint) {
        this.stopPoint = stopPoint;
    }

    @Override
    public String toString() {
        return "Mpoint{" +
            "id=" + id +
            ", mpointType='" + mpointType + '\'' +
            ", siteId=" + siteId +
            ", siteName='" + siteName + '\'' +
            ", categoryName='" + categoryName + '\'' +
            ", tenantId=" + tenantId +
            ", mpointId='" + mpointId + '\'' +
            ", mpointName='" + mpointName + '\'' +
            ", categoryId=" + categoryId +
            ", datasource='" + datasource + '\'' +
            ", datype='" + datype + '\'' +
            ", unit='" + unit + '\'' +
            ", numtail=" + numtail +
            ", enumvalue='" + enumvalue + '\'' +
            ", upperRange=" + upperRange +
            ", lowerRange=" + lowerRange +
            ", slope='" + slope + '\'' +
            ", increase='" + increase + '\'' +
            ", coefficient='" + coefficient + '\'' +
            ", cardinality=" + cardinality +
            ", magnification=" + magnification +
            ", remarks='" + remarks + '\'' +
            ", createTime=" + createTime +
            ", createUserid=" + createUserid +
            ", updateTime=" + updateTime +
            ", updateUserid=" + updateUserid +
            ", updateUserName='" + updateUserName + '\'' +
            ", deleteFlag=" + deleteFlag +
            ", point='" + point + '\'' +
            ", equipment=" + equipment +
            ", readWriteSet='" + readWriteSet + '\'' +
            ", roleId='" + roleId + '\'' +
            ", pushSet='" + pushSet + '\'' +
            ", deadZone='" + deadZone + '\'' +
            ", cycle=" + cycle +
            ", curveYaxisLowerRange='" + curveYaxisLowerRange + '\'' +
            ", curveYaxisUpperRange='" + curveYaxisUpperRange + '\'' +
            ", pointsControl='" + pointsControl + '\'' +
            ", startPoint=" + startPoint +
            ", stopPoint=" + stopPoint +
            '}';
    }
}
