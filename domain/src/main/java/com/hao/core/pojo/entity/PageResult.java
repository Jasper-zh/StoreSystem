package com.hao.core.pojo.entity;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public List getRows() {
        return rows;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    private Long total;
    private List rows;

}
