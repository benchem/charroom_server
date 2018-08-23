package team.benchem.communication.impl;

import com.alibaba.fastjson.JSONObject;
import com.zeroc.Ice.Current;
import org.springframework.stereotype.Service;
import team.benchem.communication.JsonServicePortal;
import team.benchem.communication.Request;
import team.benchem.communication.Response;
import team.benchem.communication.ServicePortal;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ServicePortalImpl
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 14:33
 * @Version 1.0
 **/
@Service
public class ServicePortalImpl implements JsonServicePortal, ServicePortal {

    private final Object messageHandlersLock = new Object();
    private final Map<String, MessageHandler> messageHandlers = new HashMap<>();

    @Override
    public String invoke(String requestBody, Current current) {
        Request request = JSONObject.parseObject(requestBody).toJavaObject(Request.class);
        if(messageHandlers.containsKey(request.getHandlerName())){
            MessageHandler handler = messageHandlers.get(request.getHandlerName());
            Response response = handler.onMessageReceive(request);
            return JSONObject.toJSONString(response);
        } else {
            Response response = new Response("HandlerNotFound",
                    String.format("%s messageHandler 未被定义",  request.getHandlerName())
            );
            return JSONObject.toJSONString(response);
        }
    }

    @Override
    public void registerHandler(String handlerKey, MessageHandler handler) {
        synchronized(messageHandlersLock) {
            messageHandlers.put(handlerKey, handler);
        }
    }

    @Override
    public void unRegisterHandler(String handlerKey) {
        synchronized (messageHandlersLock) {
            if(messageHandlers.containsKey(handlerKey)){
                messageHandlers.remove(handlerKey);
            }
        }
    }
}
