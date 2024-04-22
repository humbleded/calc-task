package com.haocang.common.domain.mpoint;

import java.io.Serializable;
import java.util.Date;

/**
 * @author weihaifeng
 */
public class AutoMpointRTDataCacheDTO implements Serializable {

    private static final long serialVersionUID = 122562286737542497L;

    private Date datadt;

    private String value;

    public Date getDatadt() {
        return datadt;
    }

    public void setDatadt(Date datadt) {
        this.datadt = datadt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AutoMpointRTDataCacheDTO{" +
                "datadt=" + datadt +
                ", value=" + value +
                '}';
    }
}