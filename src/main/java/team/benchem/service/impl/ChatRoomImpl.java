package team.benchem.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.benchem.communication.Request;
import team.benchem.communication.Response;
import team.benchem.communication.ServiceCenter;
import team.benchem.communication.ServicePortal;
import team.benchem.service.ChatRoom;

/**
 * @ClassName ChatRoomImpl
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 15:40
 * @Version 1.0
 **/
@Service
public class ChatRoomImpl implements ChatRoom {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    ServicePortal servicePortal;

    @Override
    public void login(String nickName, String clientTag) {
        serviceCenter.login(nickName, clientTag);

        JSONObject args = new JSONObject();
        args.put("nickName", nickName);
        Request request = new Request("online", args);
        serviceCenter.cast(request);
    }

    @Override
    public void logout(String nickName) {
        serviceCenter.logout(nickName);

        JSONObject args = new JSONObject();
        args.put("nickName", nickName);
        Request request = new Request("offline", args);
        serviceCenter.cast(request);
    }

    @Override
    public void say(String form, String to, String message) {
        JSONObject args = new JSONObject();
        args.put("form", form);
        args.put("to", to);
        args.put("message", message);

        Request request = new Request("say", args);

        if(to == null || to.length() == 0){
            serviceCenter.cast(request);
        } else {
            serviceCenter.forward(to, request);
        }
    }
}
