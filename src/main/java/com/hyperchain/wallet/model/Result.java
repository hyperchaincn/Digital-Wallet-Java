package com.hyperchain.wallet.model;

import com.alibaba.fastjson.annotation.JSONField;

public class Result {

    @JSONField(name="status")
    public String status;

    @JSONField(name="msg")
    public String msg;

    @JSONField(name="data")
    public Object data;

    public Result(String status,String msg, Object data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
