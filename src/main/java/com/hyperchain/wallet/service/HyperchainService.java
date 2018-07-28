package com.hyperchain.wallet.service;

import cn.qsnark.sdk.exception.TxException;
import cn.qsnark.sdk.rpc.QsnarkAPI;
import cn.qsnark.sdk.rpc.function.FuncParamReal;
import cn.qsnark.sdk.rpc.returns.CreteAccountReturn;
import cn.qsnark.sdk.rpc.returns.GetTokenReturn;
import cn.qsnark.sdk.rpc.returns.GetTxReciptReturn;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HyperchainService {

    String TOKEN_ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"issue\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"to\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"getBalance\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"account\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Issue\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]";
    String TOKEN_ABI_TRANS = "{\"constant\":false,\"inputs\":[{\"name\":\"to\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"payable\":false,\"type\":\"function\"}";
    String TOKEN_SOURCE = "contract Token {     address issuer;     mapping (address => uint) balances;      event Issue(address account, uint amount);     event Transfer(address from, address to, uint amount);      function Token() {         issuer = msg.sender;     }      function issue(address account, uint amount) {         if (msg.sender != issuer) throw;         balances[account] += amount;     }      function transfer(address to, uint amount) {         if (balances[msg.sender] < amount) throw;          balances[msg.sender] -= amount;         balances[to] += amount;          Transfer(msg.sender, to, amount);     }      function getBalance(address account) constant returns (uint) {         return balances[account];     } }";
    String TOKEN_BIN = "0x6060604052341561000c57fe5b5b60008054600160a060020a03191633600160a060020a03161790555b5b6101ca806100396000396000f300606060405263ffffffff60e060020a600035041663867904b48114610037578063a9059cbb14610058578063f8b2cb4f14610079575bfe5b341561003f57fe5b610056600160a060020a03600435166024356100a7565b005b341561006057fe5b610056600160a060020a03600435166024356100e6565b005b341561008157fe5b610095600160a060020a036004351661017f565b60408051918252519081900360200190f35b60005433600160a060020a039081169116146100c35760006000fd5b600160a060020a03821660009081526001602052604090208054820190555b5050565b600160a060020a0333166000908152600160205260409020548190101561010d5760006000fd5b600160a060020a0333811660008181526001602090815260408083208054879003905593861680835291849020805486019055835192835282015280820183905290517fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9181900360600190a15b5050565b600160a060020a0381166000908152600160205260409020545b9190505600a165627a7a72305820299e9bb6a492d60cb690d97c76ac26d821ff6bba1b863ce1b8720e449789692c0029";
    String URL_GTOKEN = "https://api.hyperchain.cn/v1/token/gtoken";

    private String clientId = "c4bc5803-8f2c-43e6-9961-9ff3cbd1f733";
    private String clientSecert = "9f5qzgZval3O7YKqp44m4jM74m03IN9S";
    private String phone = "18702604793";
    private String passwd = "me123456";
    private GetTokenReturn token;
    private QsnarkAPI api;

    public HyperchainService() throws IOException {
        this.api = new QsnarkAPI();
        this.token = api.getAccess_Token(this.clientId,this.clientSecert, this.phone, this.passwd);
        System.out.println(this.token);
    }

    public CreteAccountReturn  CreateAccount() throws IOException {
        CreteAccountReturn car = this.api.createAccount(this.token.getAccess_token());
        return car;
    }

    public GetTxReciptReturn newAsset(String from) throws IOException, InterruptedException {
        GetTxReciptReturn tx = this.api.deploysyncContract(this.token.getAccess_token(),TOKEN_BIN,from);
        return tx;
    }

    public GetTxReciptReturn issAsset(String from,String address,int amount) throws TxException, InterruptedException, IOException {
        GetTxReciptReturn tx = this.api.invokesyncContract(this.token.getAccess_token(),false,from, address, TOKEN_ABI,"issue",new FuncParamReal("address",from),new FuncParamReal("uint256",amount));
        return tx;
    }

    public GetTxReciptReturn newTransaction(String from,String to,int amount,String address) throws TxException, InterruptedException, IOException {
        GetTxReciptReturn tx = this.api.invokesyncContract(this.token.getAccess_token(),false,from, address, TOKEN_ABI_TRANS,"transfer",new FuncParamReal("address",to),new FuncParamReal("uint256",amount));
        return tx;
    }

}
