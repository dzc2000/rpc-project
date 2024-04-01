package org.example.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcConstant;
import org.example.rpc.loadbalancer.LoadBalancer;
import org.example.rpc.loadbalancer.LoadBalancerFactory;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.protocol.ProtocolConstant;
import org.example.rpc.protocol.ProtocolMessage;
import org.example.rpc.protocol.ProtocolMessageDecoder;
import org.example.rpc.protocol.ProtocolMessageEncoder;
import org.example.rpc.protocol.enums.MessageSerializer;
import org.example.rpc.protocol.enums.MessageType;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.serializer.JdkSerializer;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;
import org.example.rpc.server.tcp.VertxTcpClient;
import org.example.rpc.server.tcp.VertxTcpServer;
import org.example.rpc.utils.RpcApplication;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK 动态代理）
 *
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            //发送TCP请求
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceInfo);
            return rpcResponse.getData();

//            // 发送请求
//            // todo 注意，这里地址被硬编码了（需要使用注册中心和服务发现机制解决）
//            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}