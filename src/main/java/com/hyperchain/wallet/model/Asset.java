package com.hyperchain.wallet.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Asset {

    public static String ASSET_TYPE_RECEIVING = "转入";
    public static String ASSET_TYPE_ISSUE = "发行";

    public String name;

    public int amount;

    public int balance;

    public String unit;

    public String description;

    public String logo;

    @JsonProperty(value = "account_addr")
   public String accountAddr;

    @JsonProperty(value = "create_time")
    public String createTime;

    @JsonProperty(value = "asset_addr")
    public String assetAddr;

    public ArrayList<Object> holder;

    public String type;

    public int id;
}
