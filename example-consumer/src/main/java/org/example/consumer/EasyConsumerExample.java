package org.example.consumer;

import org.example.common.model.User;
import org.example.common.service.UserService;
import org.example.rpc.proxy.ServiceProxyFactory;

/**
 * 简单服务消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = new UserServiceProxy();
        //UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("dzc");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
