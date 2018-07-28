package com.hyperchain.wallet.controller;

import com.hyperchain.wallet.Dao.UserDao;
import com.hyperchain.wallet.controller.Req.TokenReq;
import com.hyperchain.wallet.model.Result;
import com.hyperchain.wallet.model.User;
import com.hyperchain.wallet.util.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class TokenController {

    @Autowired
    UserDao userDao;

    @ResponseBody
    @RequestMapping(value="/token", method = RequestMethod.POST)
    public ResponseEntity<Object> Token(@RequestBody TokenReq tokenReq){
        System.out.println(tokenReq.phone+"========="+tokenReq.password);

        User user = userDao.FindUser(tokenReq.phone);
        if (user == null){
            return new ResponseEntity<>(new Result("failed","not find the user",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (user.password.equals(tokenReq.password)){
            String token = JwtHelper.createJavaWebToken(user.accountAddr,user.phone,user.password);
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Result("failed","password error",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
