package com.hc.calc.task.model;

import java.util.Date;

/**
 * @author Holger
 * @date 2018-5-7
 */
public class CalcDate {
	private Date start;

	private Date end;

	private Date save;

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getSave() {
		return save;
	}

	public void setSave(Date save) {
		this.save = save;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((save == null) ? 0 : save.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
		    return true;
		}
		if (obj == null){
		    return false;
		}
		if (getClass() != obj.getClass()){
		    return false;
		}
		CalcDate other = (CalcDate) obj;
		if (end == null) {
			if (other.end != null){
			    return false;
			}
		} else if (!end.equals(other.end)){
		    return false;
		}
		if (save == null) {
			if (other.save != null){
			    return false;
			}
		} else if (!save.equals(other.save)){
		    return false;
		}
		if (start == null) {
			if (other.start != null){
			    return false;
			}
		} else if (!start.equals(other.start)){
		    return false;
		}
		return true;
	}

}
