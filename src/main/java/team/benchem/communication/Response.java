package team.benchem.communication;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;

/**
 * @ClassName Response
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 14:20
 * @Version 1.0
 **/
public class Response<T> {

    private String stateCode;

    private String message;

    private T result;

    public Response() {
    }

    public Response(String stateCode, String message) {
        this.stateCode = stateCode;
        this.message = message;
    }

    public Response(T result) {
        this.stateCode = "OK";
        this.message = "";
        this.result = result;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
