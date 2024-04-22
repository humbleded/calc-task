package com.hc.calc.task.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Metric implements Serializable, Comparable<Metric>{

	private static final long serialVersionUID = 1L;
	private String name;
    private Double value;
    private Long time;
    private Map<String, String> tags;

    public Metric() {
        // Metric默认有一个QoD=U的tag
        this.tags = new HashMap<>();
        this.tags.put("QoD", "U");
    }

    public String getName() {
        return name;
    }

    public Metric setName(String name) {
        this.name = name;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public Metric setValue(Double value) {
        this.value = value;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public Metric setTime(Long time) {
        this.time = time;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Metric setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", time=" + time +
                ", tags=" + tags +
                '}';
    }

    /**
     * 转换为ExceptionData所需的JSON格式，在出现异常时将测点信息存入MongoDB以待补录
     *
     * @return
     */
    public String toExceptionDataJson() {
        return " {\"name\" : \"" + name + "\",\"time\" : \"" + time + "\",\"value\" : \"" + value + "\"\n" +
                "}";
    }

    /**
     * 将该对象中的所有属性拷贝到目标对象中
     * @param dest
     */
    public void copyTo(Metric dest){
        dest.setName(name);
        dest.setTime(time);
        dest.setValue(value);
        dest.getTags().putAll(tags);
    }

	@Override
	public int compareTo(Metric arg0) {
		return this.time.compareTo((arg0.getTime()));
	}
}
