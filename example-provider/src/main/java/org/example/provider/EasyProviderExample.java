package org.example.provider;


import org.example.common.service.UserService;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.server.HttpServer;
import org.example.rpc.server.VertxHttpServer;
import org.example.rpc.server.tcp.VertxTcpServer;
import org.example.rpc.utils.RpcApplication;

public class EasyProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        //serviceMetaInfo.getServicePort(rpcConfig.getServerPort());
        try{
            registry.register(serviceMetaInfo);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        //web服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
