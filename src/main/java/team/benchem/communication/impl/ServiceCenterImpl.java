package team.benchem.communication.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.springframework.stereotype.Service;
import team.benchem.communication.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ServiceCenterImpl
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 14:49
 * @Version 1.0
 **/
@Service
public class ServiceCenterImpl implements JsonServiceCenter, ServiceCenter {

    private final static int TIMEOUT_SECOND = 300;
    private final Object proxiesLock = new Object();
    private final Map<String, Date> clientExpiresTime = new HashMap<>();
    private final Map<String, JsonServicePortalPrx> clientServicePortalProxies = new HashMap<>();
    private final Object userClientTagLock = new Object();
    private final Map<String, String> userClientTags = new HashMap<>();

    @Override
    public void register(String clientTag, Identity identity, Current current) {
        synchronized (proxiesLock){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, TIMEOUT_SECOND);
            Date expiresTime = calendar.getTime();
            clientExpiresTime.put(clientTag, expiresTime);
            JsonServicePortalPrx dymCallback = JsonServicePortalPrx
                    .uncheckedCast(current.con.createProxy(identity))
                    .ice_twoway();
            clientServicePortalProxies.put(clientTag, dymCallback);
        }
    }

    @Override
    public void unRegister(String clientTag, Identity identity, Current current) {
        synchronized (proxiesLock){
            if(clientExpiresTime.containsKey(clientTag)){
                clientExpiresTime.remove(clientTag);
            }
            if(clientServicePortalProxies.containsKey(clientTag)){
                clientServicePortalProxies.remove(clientTag);
            }
        }
    }

    @Override
    public void cast(String requestBody, Current current) {
        Calendar calendar = Calendar.getInstance();
        Date timeNow = calendar.getTime();
        for(Map.Entry<String, Date> item : clientExpiresTime.entrySet()){
            if(item.getValue().before(timeNow)){
                continue;
            }

            if(clientServicePortalProxies.containsKey(item.getKey())){
                clientServicePortalProxies.get(item.getKey()).invoke(requestBody);
            }
        }
    }

    @Override
    public String forward(String clientTag, String requestBody, Current current) {
        if(!clientExpiresTime.containsKey(clientTag) || !clientServicePortalProxies.containsKey(clientTag)){
            Response response = new Response("ClientIsOffline", "客户端已离线");
            return JSON.toJSONString(response);
        }

        Calendar calendar = Calendar.getInstance();
        Date timeNow = calendar.getTime();
        if(clientExpiresTime.get(clientTag).before(timeNow)){
            synchronized (proxiesLock){
                clientExpiresTime.remove(clientTag);
                clientServicePortalProxies.remove(clientTag);
            }

            Response responseBody = new Response("ClientIsOffline", "客户端已离线");
            return JSON.toJSONString(responseBody);
        }

        return clientServicePortalProxies.get(clientTag).invoke(requestBody);
    }

    @Override
    public void ice_ping(Current current) {
        if(!current.ctx.containsKey("clientTag")){
            return;
        }

        String clientTag = current.ctx.get("clientTag");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, TIMEOUT_SECOND);
        Date expiresTime = calendar.getTime();
        synchronized (proxiesLock) {
            clientExpiresTime.put(clientTag, expiresTime);
        }
    }

    @Override
    public void login(String userName, String clientTag) {
        synchronized (userClientTagLock){
            userClientTags.put(userName, clientTag);
        }
    }

    @Override
    public void logout(String userName) {
        synchronized (userClientTags){
            if(userClientTags.containsKey(userName)){
                userClientTags.remove(userName);
            }
        }
    }

    @Override
    public void cast(Request request) {
        cast(JSON.toJSONString(request), null);
    }

    @Override
    public Response forward(String userName, Request request) {
        if(!userClientTags.containsKey(userName)){
            return new Response("UserIsNotOnline", "用户已掉线");
        }

        String clientTag = userClientTags.get(userName);
        String responseStr = forward(clientTag, JSON.toJSONString(request), null);
        return JSONObject.parseObject(responseStr).toJavaObject(Response.class);
    }
}
