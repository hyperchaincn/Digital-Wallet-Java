package com.hyperchain.wallet.controller;

import com.hyperchain.wallet.Dao.UserDao;
import com.hyperchain.wallet.model.Result;
import com.hyperchain.wallet.model.User;
import com.hyperchain.wallet.util.JwtHelper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class PasswordController {

    @Autowired
    UserDao userDao;

    @ResponseBody
    @RequestMapping(value="/password", method = RequestMethod.POST)
    public ResponseEntity<Object> Token(ServletRequest req, @RequestParam(value = "new_psw", required = true) String newpasswd){

        String phone = (String)req.getAttribute("phone");
        User user = userDao.FindUser(phone);
        if (user == null){
            return new ResponseEntity<>(new Result("failed","not find the user",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.password = newpasswd;
        userDao.Save(user);
        String token = JwtHelper.createJavaWebToken(user.accountAddr,user.phone,user.password);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("status","ok");
        map.put("msg","密码修改成功");
        map.put("token",token);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
}
