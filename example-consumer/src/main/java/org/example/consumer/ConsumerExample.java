package org.example.consumer;

import org.example.common.model.User;
import org.example.common.service.UserService;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.proxy.ServiceProxyFactory;
import org.example.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("dzc");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

        long number = userService.getNumber();
        System.out.println(number);


    }
}
