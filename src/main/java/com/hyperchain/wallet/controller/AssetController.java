package com.hyperchain.wallet.controller;

import cn.qsnark.sdk.exception.TxException;
import cn.qsnark.sdk.rpc.returns.GetTxReciptReturn;
import com.hyperchain.wallet.Dao.AssetDao;
import com.hyperchain.wallet.Dao.TranstionDao;
import com.hyperchain.wallet.Dao.UserDao;
import com.hyperchain.wallet.controller.Req.AssetReq;
import com.hyperchain.wallet.controller.Req.PubAssetReq;
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
import java.util.Date;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class AssetController {

    @Autowired
    HyperchainService hyperchainService;

    @Autowired
    UserDao userDao;

    @Autowired
    AssetDao assetDao;

    @Autowired
    TranstionDao transtionDao;

    @RequestMapping(value = "/asset",method = RequestMethod.POST)
    public ResponseEntity<Result> CreateAsset(ServletRequest req, @RequestBody AssetReq assetReq) throws IOException, InterruptedException {

        String phone = (String)req.getAttribute("phone");
        User user = userDao.FindUser(phone);
        System.out.println("user:"+user);
        GetTxReciptReturn trr =  hyperchainService.newAsset(user.accountAddr);
        Asset asset = new Asset();
        asset.name = assetReq.name;
        asset.unit = assetReq.unit;
        asset.description = assetReq.description;
        asset.logo = "http://fanyi.baidu.com/static/translation/img/header/logo_cbfea26.png";
        asset.accountAddr = user.accountAddr;
        asset.createTime = new Date().toString();
        asset.type = Asset.ASSET_TYPE_ISSUE;
        asset.assetAddr = trr.getContract_address();
        assetDao.Save(asset);

        return new ResponseEntity<Result>(new Result("ok","create new asset ok",asset),HttpStatus.OK);
    }


    @RequestMapping(value = "/asset",method = RequestMethod.GET)
    public ResponseEntity<Result> GetAsset(ServletRequest req, @RequestParam(value = "type", required = false) String type,
                                             @RequestParam(value = "asset_addr",required = false) String assetAddr) throws IOException, InterruptedException {

        String phone = (String)req.getAttribute("phone");
        User user = userDao.FindUser(phone);

        if (assetAddr != null && assetAddr.length() > 0){
            List<Asset> assets = assetDao.FindAssetByAddr(assetAddr,user.accountAddr);
            return new ResponseEntity<Result>(new Result("ok","ok",assets),HttpStatus.OK);
        }
        String tp = "";
        if (type == null){
            type = "";
        }
        if (type.equals("issue")){
            tp = Asset.ASSET_TYPE_ISSUE;
        }
        if (type.equals("receive")){
            tp = Asset.ASSET_TYPE_RECEIVING;
        }


        List<Asset> assets = assetDao.FindAssetByType(tp,user.accountAddr);
        return new ResponseEntity<Result>(new Result("ok","ok",assets),HttpStatus.OK);
    }

    @RequestMapping(value = "/asset",method = RequestMethod.PUT)
    public ResponseEntity<Result> publishAsset(ServletRequest req, @RequestBody PubAssetReq pubAssetReq) throws IOException, InterruptedException, TxException {
        if (pubAssetReq.asset_addr == null || pubAssetReq.asset_addr.length() == 0 || pubAssetReq.amount == 0){
            return new ResponseEntity<>(new Result("failed","请求数据错误",null),HttpStatus.BAD_REQUEST);
        }
        String address = (String)req.getAttribute("address");
        List<Asset> assets = assetDao.FindAssetByAddr(pubAssetReq.asset_addr,address);
        if (assets == null || assets.size() == 0){
            return new ResponseEntity<>(new Result("failed","资产不存在",null),HttpStatus.BAD_REQUEST);
        }
        if (!assets.get(0).type.equals(Asset.ASSET_TYPE_ISSUE)){
            return new ResponseEntity<>(new Result("failed","没有发行权",null),HttpStatus.BAD_REQUEST);
        }

        GetTxReciptReturn tx = hyperchainService.issAsset(address,pubAssetReq.asset_addr,pubAssetReq.amount);
        if (tx == null && tx.getTxHash().length() == 0){
            return new ResponseEntity<>(new Result("failed","布资产失败，请重试",null),HttpStatus.OK);
        }
        Transaction transaction = new Transaction();
        transaction.from = address;
        transaction.to = address;
        transaction.amount = pubAssetReq.amount;
        transaction.createTime = new Date();
        transaction.assetAddr = pubAssetReq.asset_addr;
        transaction.type = Transaction.TRANSTYPE_ISSUE;
        transaction.hash = tx.getTxHash();

        transtionDao.Save(transaction);

        Asset asset = assets.get(0);
        asset.amount += pubAssetReq.amount;
        asset.balance += pubAssetReq.amount;
        assetDao.Save(asset);
        return new ResponseEntity<Result>(new Result("ok","发布成功",asset),HttpStatus.OK);

    }
}
