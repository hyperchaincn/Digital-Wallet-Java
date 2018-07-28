package com.hyperchain.wallet.controller;

import cn.qsnark.sdk.rpc.returns.CreteAccountReturn;
import com.hyperchain.wallet.Dao.UserDao;
import com.hyperchain.wallet.controller.Req.TokenReq;
import com.hyperchain.wallet.model.Result;
import com.hyperchain.wallet.model.User;
import com.hyperchain.wallet.service.HyperchainService;
import com.hyperchain.wallet.util.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.Date;

@RestController
@EnableAutoConfiguration
public class UserController {

    @Autowired
    UserDao userDao;

    @Autowired
    HyperchainService hyperchainService;

    @RequestMapping("/hello")
    public String index(){
        return "Hello World!";
    }

    @ResponseBody
    @RequestMapping(value="/user", method = RequestMethod.POST)
    public ResponseEntity<Result> UserLogin(@RequestBody TokenReq tokenReq){
        User user = userDao.FindUser(tokenReq.phone);
        if (user != null){
            return new ResponseEntity<>(new Result("failed","用户"+user.phone+"已被注册",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user = new User();
        user.phone = tokenReq.phone;
        user.password = tokenReq.password;
        user.createTime = new Date().toString();

        try {
            CreteAccountReturn acr = hyperchainService.CreateAccount();

            user.accountAddr = acr.getAddress();
            user.privateKey = "";
            String id = userDao.Save(user);
            String token = JwtHelper.createJavaWebToken(user.accountAddr,user.phone,user.password);

            user.token = token;

            return new ResponseEntity<>(new Result("ok","创建成功",user),HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new Result("failed","Something wrong with Server",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value="/user", method = RequestMethod.GET)
    public Object GetUser(ServletRequest req) {
        String phone = (String)req.getAttribute("phone");
        String password = (String)req.getAttribute("password");
        String address = (String)req.getAttribute("address");

        User user = new User();
        user.phone = phone;
        user.password = password;
        user.accountAddr = address;
        return new ResponseEntity<>(new Result("ok","成功",user),HttpStatus.OK);
    }
}
