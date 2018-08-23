package team.benchem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import team.benchem.communication.Response;
import team.benchem.communication.ServiceCenter;
import team.benchem.communication.ServicePortal;

/**
 * @ClassName ServicePortalRunner
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 16:10
 * @Version 1.0
 **/
@Component
public class ServicePortalRunner implements ApplicationRunner, Ordered {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    ServicePortal servicePortal;

    @Override
    public void run(ApplicationArguments args) {
        servicePortal.registerHandler("online", request -> {
            serviceCenter.cast(request);
            return new Response("");
        });

        servicePortal.registerHandler("offline", request -> {
            serviceCenter.cast(request);
            return new Response("");
        });

        servicePortal.registerHandler("say", request -> {
            String to = request.getArgs().getString("to");
            if(to == null || to.length() == 0){
                serviceCenter.cast(request);
                return new Response("");
            } else {
                return serviceCenter.forward(to, request);
            }
        });
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
