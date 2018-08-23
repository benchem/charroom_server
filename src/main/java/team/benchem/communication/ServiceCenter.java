package team.benchem.communication;

public interface ServiceCenter {

    void login(String userName, String clientTag);

    void logout(String userName);

    void cast(Request request);

    Response forward(String userName, Request request);
}
