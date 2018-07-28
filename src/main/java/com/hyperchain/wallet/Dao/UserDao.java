package com.hyperchain.wallet.Dao;

import com.hyperchain.wallet.model.Asset;
import com.hyperchain.wallet.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Repository
public class UserDao {

    List<User> users = new ArrayList<>();

    public User FindUser(String phone){
        for (User user : this.users){
            if (user.phone.equals(phone)){
                return user;
            }
        }
        return null;
    }

    public User FindUserByAddr(String address){
        for (User user : this.users){
            if (user.accountAddr.equals(address)){
                return user;
            }
        }
        return null;
    }

    public String Save(User user) {
        if (user.id == 0){
            user.id = this.users.size()+1;
            this.users.add(user);
        }else{
            this.users.set(user.id-1,user);
        }
        return user.phone;
    }
}
