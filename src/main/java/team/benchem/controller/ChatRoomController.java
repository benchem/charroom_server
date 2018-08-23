package team.benchem.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.benchem.service.ChatRoom;

/**
 * @ClassName ChatRoomController
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 16:03
 * @Version 1.0
 **/
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    @Autowired
    ChatRoom chatRoom;

    @PostMapping("/login")
    public void login(@RequestBody JSONObject postData){
        chatRoom.login(
                postData.getString("nickName"),
                postData.getString("clientTag")
        );
    }

    @PostMapping("/logout")
    public void logout(@RequestBody JSONObject postData){
        chatRoom.logout(
                postData.getString("nickName")
        );
    }

    @PostMapping("/say")
    public void say(@RequestBody JSONObject postData){
        chatRoom.say(
                postData.getString("form"),
                postData.getString("to"),
                postData.getString("message")
        );
    }
}
