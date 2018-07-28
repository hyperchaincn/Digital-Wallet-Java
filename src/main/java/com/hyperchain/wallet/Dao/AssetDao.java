package com.hyperchain.wallet.Dao;

import com.hyperchain.wallet.model.Asset;
import com.hyperchain.wallet.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class AssetDao {
    List<Asset> assets = new ArrayList<>();

    public Asset FindUser(int id){
        for (Asset asset : this.assets){
            if (id == asset.id){
                return asset;
            }
        }
        return null;
    }

    public List<Asset> FindAssetByAddr(String assetAddr,String accountAddr){
        List<Asset> assets = new ArrayList<>();
        for (Asset asset : this.assets){
            if (asset.assetAddr.equals(assetAddr) && asset.accountAddr.equals(accountAddr)){
                assets.add(asset);
            }
        }
        return assets;
    }

    public Asset FindOneAssetByAddr(String assetAddr,String accountAddr){
        for (Asset asset : this.assets){
            if (asset.assetAddr.equals(assetAddr) && asset.accountAddr.equals(accountAddr)){
                return asset;
            }
        }
        return null;
    }



    public Asset FindAssetByAddrAndType(String assetAddr,String type){
        for (Asset asset : this.assets){
            if (asset.assetAddr.equals(assetAddr) && asset.type.equals(type)){
                return asset;
            }
        }
        return null;
    }

    public Asset AssetByAddr(String assetAddr){
        for (Asset asset : this.assets){
            if (asset.assetAddr.equals(assetAddr)){
                return asset;
            }
        }
        return null;
    }

    public List<Asset>  FindAssetByType(String type,String accountAddr){
        List<Asset> assets = new ArrayList<>();
        for (Asset asset : this.assets){
            if ((type.equals("")|| asset.type.equals(type)) && asset.accountAddr.equals(accountAddr)){
                assets.add(asset);
            }
        }
        return assets;
    }

    public int Save(Asset asset) {
        if (asset.id == 0){
            asset.id = assets.size()+1;
            this.assets.add(asset);
        }else{
            this.assets.set(asset.id-1,asset);
        }
        return asset.id;
    }


}
