package com.hyperchain.wallet.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class User {
    public int id;

    @JsonProperty(value="phone")
    public String phone;

    @JsonProperty(value="password")
    public String password;

    @JsonProperty(value="account_addr")
    public  String accountAddr;

    @JSONField
    public String privateKey;

    @JSONField
    public String createTime;

    @JSONField
    public ArrayList<Object> Assets;

    @JSONField
    public String key;

    @JSONField
    public String token;
}


