package cn.bakuman.redissionlock.config;


import cn.bakuman.redissionlock.annotation.BKMLock;
import cn.bakuman.redissionlock.aspect.LockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(BKMLock.class)
public class BakumanAutoConfiguration {

    @Bean
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnMissingBean
    public LockAspect lockAspect() {
        return new LockAspect();
    }
}
