package com.hyperchain.wallet.controller;

import cn.qsnark.sdk.exception.TxException;
import cn.qsnark.sdk.rpc.returns.GetTxReciptReturn;
import com.hyperchain.wallet.Dao.AssetDao;
import com.hyperchain.wallet.Dao.TranstionDao;
import com.hyperchain.wallet.Dao.UserDao;
import com.hyperchain.wallet.controller.Req.TransReq;
import com.hyperchain.wallet.model.Asset;
import com.hyperchain.wallet.model.Result;
import com.hyperchain.wallet.model.Transaction;
import com.hyperchain.wallet.model.User;
import com.hyperchain.wallet.service.HyperchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
@EnableAutoConfiguration
public class TransationController {

    @Autowired
    TranstionDao transtionDao;

    @Autowired
    AssetDao assetDao;

    @Autowired
    UserDao userDao;

    @Autowired
    HyperchainService hyperchainService;

    @RequestMapping(value = "/transaction",method = RequestMethod.GET)
    public Object Transation(ServletRequest req, @RequestParam(value = "type", required = false) String type){
        String address = (String)req.getAttribute("address");
        List<Transaction> transactions = new ArrayList<>();
        if (type == ""){
            transactions = transtionDao.FindTranstionByFromOrTo(address);
        }
        if (type == "issue"){
            transactions = transtionDao.FindTranstionByTypeAndFrom(Transaction.TRANSTYPE_ISSUE,address);
        }

        if (type == "receive"){
            transactions = transtionDao.FindTranstionByTypeAndTo(Transaction.TRANSTYPE_TRANSACTION,address);
        }

        if (type == "transfer"){
            transactions = transtionDao.FindTranstionByTypeAndFrom(Transaction.TRANSTYPE_TRANSACTION,address);
        }

        List<Map<String,Object>> trList = new ArrayList<>();
        for (Transaction transaction: transactions){
            Asset asset = assetDao.AssetByAddr(transaction.assetAddr);
            if (asset == null) {
                continue;
            }
            String otherPartyAddr = address == transaction.from? transaction.to : transaction.from;
            User user = userDao.FindUser(otherPartyAddr);
            asset = assetDao.FindAssetByAddrAndType(transaction.assetAddr,Asset.ASSET_TYPE_ISSUE);
            User issuer = userDao.FindUserByAddr(asset.accountAddr);
            if (user == null) {
                continue;
            }
            Map<String,Object> m = new HashMap<>();
            m.put("status","ok");
            m.put("msg","请求成功");
            m.put("asset_name",asset.name);
            m.put("asset_addr",transaction.assetAddr);
            m.put("hash",transaction.hash);
            m.put("create_time",transaction.createTime);
            m.put("amount",transaction.amount);
            m.put("type",transaction.type == Transaction.TRANSTYPE_ISSUE? transaction.type: (transaction.to == address ? "转入":"转出"));
            Map<String,Object> im = new HashMap<String,Object>();
            im.put("account_addr",issuer.accountAddr);
            im.put("phone",issuer.phone);
            m.put("issuer",im);
            Map<String,Object> um = new HashMap<String,Object>();
            um.put("phone",user.phone);
            um.put("account_addr",user.accountAddr);
            m.put("other_party",user == null? "":um);
            trList.add(m);

            if(trList.size() == transactions.size()){
                return new ResponseEntity<Result>(new Result("ok","",trList),HttpStatus.OK);
            }
        }
        return new ResponseEntity<Result>(new Result("ok","",trList),HttpStatus.OK);
    }

    @RequestMapping(value = "/transaction",method = RequestMethod.POST)
    public Object PostTransation(ServletRequest req, @RequestBody TransReq transReq) throws InterruptedException, IOException, TxException {
        String address = (String)req.getAttribute("address");
        String privateKey = (String)req.getAttribute("private");

        if (transReq.to.equals(address)){
            return new ResponseEntity<Result>(new Result("failed","无法给自己转账",null),HttpStatus.FORBIDDEN);
        }

        Asset asset = assetDao.FindOneAssetByAddr(transReq.asset_addr,address);
        if (asset == null){
            return new ResponseEntity<Result>(new Result("failed","你还未拥有该资产",null),HttpStatus.FORBIDDEN);
        }
        if (asset.balance < transReq.amount){
            return new ResponseEntity<Result>(new Result("failed","余额不足",null),HttpStatus.FORBIDDEN);
        }

        Asset toAsset = assetDao.FindOneAssetByAddr(transReq.asset_addr,transReq.to);

        GetTxReciptReturn tx = hyperchainService.newTransaction(address,transReq.to, transReq.amount, transReq.asset_addr);
        if (tx == null || tx.getTxHash() == null || tx.getTxHash().length() == 0){
            return new ResponseEntity<Result>(new Result("failed","创建交易失败",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Transaction transaction = new Transaction();
        transaction.hash = tx.getTxHash();
        transaction.from = address;
        transaction.to = transReq.to;
        transaction.amount = transReq.amount;
        transaction.assetAddr = transReq.asset_addr;
        transaction.createTime = new Date();
        transaction.type = Transaction.TRANSTYPE_TRANSACTION;

        if (toAsset != null){
            asset.balance -= transReq.amount;
            toAsset.balance += transReq.amount;
            User touser = userDao.FindUserByAddr(transReq.to);
            if (touser == null){
                return new ResponseEntity<Result>(new Result("failed","收款方地址有误",null),HttpStatus.FORBIDDEN);
            }
            Map<String,Object> m = new HashMap<>();
            m.put("phone",touser.phone);
            m.put("account_addr",touser.accountAddr);
            transaction.other_party = m;
            transaction.type = "转出";
            assetDao.Save(asset);
            assetDao.Save(toAsset);
            transtionDao.Save(transaction);
            return new ResponseEntity<Result>(new Result("ok","ok",transaction),HttpStatus.OK);
        }else{
            Asset newAsset = new Asset();
            newAsset.name = asset.name;
            newAsset.amount = asset.amount;
            newAsset.balance = asset.balance;
            newAsset.unit = asset.unit;
            newAsset.description = asset.description;
            newAsset.logo = asset.logo;
            newAsset.type = asset.type;
            newAsset.accountAddr = transReq.to;
            asset.balance -= transReq.amount;

            User user = userDao.FindUserByAddr(transReq.to);
            if (user == null) {
                return new ResponseEntity<Result>(new Result("failed","收款方地址有误",null),HttpStatus.PRECONDITION_FAILED);
            }

            Map<String,Object> m = new HashMap<>();
            m.put("phone",user.phone);
            m.put("account_addr",user.accountAddr);
            transaction.other_party = m;

            assetDao.Save(newAsset);
            assetDao.Save(asset);
            transtionDao.Save(transaction);
            return new ResponseEntity<Result>(new Result("ok","ok",transaction),HttpStatus.OK);
        }
    }
}
