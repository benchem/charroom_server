package team.benchem.service;

public interface ChatRoom {

    void login(String nickName, String clientTag);

    void logout(String nickName);

    void say(String form, String to, String message);
}
