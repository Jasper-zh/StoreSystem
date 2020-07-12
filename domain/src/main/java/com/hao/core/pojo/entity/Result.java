package com.hao.core.pojo.entity;

import java.io.Serializable;

public class Result implements Serializable {
    private Boolean success;
    private String msg;

    public Boolean getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Result(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }
}
