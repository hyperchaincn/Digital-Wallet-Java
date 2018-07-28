package com.hyperchain.wallet.model;

import java.util.Date;
import java.util.Map;

public class Transaction {

    public static String TRANSTYPE_TRANSACTION = "交易";
    public  static String TRANSTYPE_ISSUE= "发行";


   public String hash;

    public String from;

    public int amount;

    public String to;

    public String assetAddr;

    public  Date createTime;

    public String type;

    public int id;

    public Map<String,Object> other_party;
}
