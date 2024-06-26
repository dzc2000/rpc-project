package org.example.rpc.loadbalancer;

import org.example.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    public static final LoadBalancer DEFAULT_LOAD_BALANCER = new PollLoadBalancer();

    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}