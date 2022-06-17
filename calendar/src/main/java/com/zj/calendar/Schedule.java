package com.zj.calendar;

import java.io.Serializable;

/**
 * 事件标记服务，现在多类型的事务标记建议使用这个
 */
final class Schedule implements Serializable {

    private final String name;
    private final int schemeColor;
    private final Object obj;

    public Schedule(int schemeColor, String name, Object obj) {
        this.schemeColor = schemeColor;
        this.name = name;
        this.obj = obj;
    }

    public int getScheduleColor() {
        return schemeColor;
    }

    public String getName() {
        return name;
    }

    public Object getObj() {
        return obj;
    }
}