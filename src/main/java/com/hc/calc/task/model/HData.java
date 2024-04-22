package com.hc.calc.task.model;

import java.util.Date;

/**
 * Data class
 *
 * @author lixingzhong
 * @date 2019/03/28
 */
public class HData implements Comparable<HData> {

	private Long dataDate;

	private String dataValue;

	private String tag;

    private String shift;

    private String shiftsType;

    private Date startDT;

    private Date endDT;

    public Date getStartDT() {
        return startDT;
    }

    public void setStartDT(Date startDT) {
        this.startDT = startDT;
    }

    public Date getEndDT() {
        return endDT;
    }

    public void setEndDT(Date endDT) {
        this.endDT = endDT;
    }

    public String getShiftsType() {
        return shiftsType;
    }

    public void setShiftsType(String shiftsType) {
        this.shiftsType = shiftsType;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Long getDataDate() {
		return dataDate;
	}

	public void setDataDate(Long dataDate) {
		this.dataDate = dataDate;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	@Override
	public String toString() {
		return "DataDetailDTO [dataDate=" + dataDate + ", dataValue=" + dataValue + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataDate == null) ? 0 : dataDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null)
		{return false;}
		if (getClass() != obj.getClass())
		{return false;}
		Data other = (Data) obj;
		if (dataDate == null) {
			if (other.dataDate != null)
			{return false;}
		} else if (!dataDate.equals(other.dataDate))
		{return false;}
		return true;
	}


	@Override
	public int compareTo(HData o) {
		return this.dataDate.compareTo(o.getDataDate());
	}
}
