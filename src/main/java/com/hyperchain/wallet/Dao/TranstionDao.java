package com.hyperchain.wallet.Dao;

import com.hyperchain.wallet.model.Asset;
import com.hyperchain.wallet.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TranstionDao {
    List<Transaction> transtions = new ArrayList<>();


    public List<Transaction> FindTranstionByTypeAndFrom(String type, String from){
        List<Transaction> transtions = new ArrayList<>();
        for (Transaction transaction : this.transtions){
            if (transaction.type.equals(type) && transaction.from.equals(from)){
                transtions.add(transaction);
            }
        }
        return transtions;
    }

    public List<Transaction>  FindTranstionByTypeAndTo(String type,String to){
        List<Transaction> transtions = new ArrayList<>();
        for (Transaction transaction : this.transtions){
            if (transaction.type.equals(type) && transaction.to.equals(to)){
                transtions.add(transaction);
            }
        }
        return transtions;
    }

    public List<Transaction>  FindTranstionByFromOrTo(String address){
        List<Transaction> transtions = new ArrayList<>();
        for (Transaction transaction : this.transtions){
            if (transaction.from.equals(address) || transaction.to.equals(address)){
                transtions.add(transaction);
            }
        }
        return transtions;
    }

    public int Save(Transaction transaction) {
        if (transaction.id == 0){
            transaction.id = this.transtions.size()+1;
            this.transtions.add(transaction);
        }else{
            this.transtions.set(transaction.id-1,transaction);
        }
        return transaction.id;
    }
}
