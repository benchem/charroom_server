package team.benchem.communication;

public interface ServicePortal {

    void registerHandler(String handlerKey, MessageHandler handler);

    void unRegisterHandler(String handlerKey);

    interface MessageHandler{
        Response onMessageReceive(Request request);
    }
}
