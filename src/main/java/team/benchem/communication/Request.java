package team.benchem.communication;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName Request
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 14:20
 * @Version 1.0
 **/
public class Request {

    private String handlerName;

    private JSONObject args;

    public Request() {
    }

    public Request(String handlerName, JSONObject args) {
        this.handlerName = handlerName;
        this.args = args;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public JSONObject getArgs() {
        return args;
    }
}
