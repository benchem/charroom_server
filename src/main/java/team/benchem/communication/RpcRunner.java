package team.benchem.communication;

import com.zeroc.Ice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @ClassName RpcRunner
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-23 15:31
 * @Version 1.0
 **/
@Component
public class RpcRunner implements ApplicationRunner, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RpcRunner.class);
    static Communicator communicator;

    @Autowired
    JsonServiceCenter serviceCenter;

    @Autowired
    JsonServicePortal servicePortal;

    @Value("${server.rpcport:8000}")
    int rcpPort;

    private void initializationCommunicator(){
        Properties props = Util.createProperties();
        props.setProperty("Ice.ThreadPool.Server.Size", "256");
        props.setProperty("Ice.ThreadPool.Server.SizeMax", "1000");
        props.setProperty("Ice.ThreadPool.Server.SizeWarn", "900");
        props.setProperty("Ice.ThreadPool.Client.Size", "20");
        props.setProperty("Ice.ThreadPool.Client.SizeMax", "100");
        props.setProperty("Ice.ThreadPool.Client.SizeWarn", "80");
        props.setProperty("Ice.MessageSizeMax", "5242880");

        InitializationData initData = new InitializationData();
        initData.properties = props;
        communicator = Util.initialize(initData);
    }

    static class RpcShutdownHook extends Thread{

        private final Communicator _communicator;

        RpcShutdownHook(Communicator communicator){
            _communicator = communicator;
        }

        @Override
        public void run() {
            _communicator.destroy();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        initializationCommunicator();
        Runtime.getRuntime().addShutdownHook(new RpcShutdownHook(communicator));

        String endpointStr = String.format("default -h 0.0.0.0 -p %s", rcpPort);
        ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("LonntecRPC", endpointStr);
        adapter.add(serviceCenter, Util.stringToIdentity("serviceCenter"));
        adapter.add(servicePortal, Util.stringToIdentity("servicePortal"));
        adapter.activate();

        logger.info("ZeroC Ice started on endpoints: {}", endpointStr);
        communicator.waitForShutdown();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
