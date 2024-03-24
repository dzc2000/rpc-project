package org.example.provider;


import org.example.common.service.UserService;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.server.HttpServer;
import org.example.rpc.server.VertxHttpServer;
import org.example.rpc.utils.RpcApplication;

public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
