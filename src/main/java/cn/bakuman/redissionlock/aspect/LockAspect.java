package cn.bakuman.redissionlock.aspect;


import cn.bakuman.redissionlock.annotation.BKMLock;
import cn.bakuman.redissionlock.enums.LockTypeEnum;
import cn.bakuman.redissionlock.utils.SpELParserUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class LockAspect {
    @Autowired
    private RedissonClient redissonClient;


    @Pointcut("@annotation(cn.bakuman.redissionlock.annotation.BKMLock)")
    public void doLock() {
    }

    @Around("doLock()&&@annotation(bkmLock)")
    public Object doBefore(ProceedingJoinPoint joinPoint, BKMLock bkmLock) {
        log.info("加锁准备 | method:[{}] | args:[{}]", joinPoint.getSignature().toString(), JSON.toJSONString(joinPoint.getArgs()));
        String key = getPrefixKey(joinPoint, bkmLock);
        key = bkmLock.perfixKey() + key;
        log.info("key generate complete {}", key);
        if (LockTypeEnum.TRY_LOCK == bkmLock.lockType()) {
            return doTryLock(joinPoint, bkmLock, key);
        } else {
            return doLock(joinPoint, bkmLock, key);
        }
    }

    /**
     * 获取key
     *
     * @param joinPoint
     * @param bkmLock
     * @return
     */
    private String getPrefixKey(ProceedingJoinPoint joinPoint, BKMLock bkmLock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String parse = SpELParserUtils.parse(signature.getMethod(), joinPoint.getArgs(), bkmLock.suffixSpEl(), String.class);
        if (StrUtil.isBlank(parse)) {
            throw new RuntimeException(bkmLock.errMsg());
        }
        return parse;
    }


    private Object doLock(ProceedingJoinPoint joinPoint, BKMLock bkmLock, String key) {
        RLock lock = this.redissonClient.getLock(key);
        try {
            lock.lock(bkmLock.leaseTime(), bkmLock.timeUnit());
            log.info(bkmLock.note() + "已加[{}]锁", "lock");
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("doLock异常", e);
            throw new RuntimeException(bkmLock.errMsg());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    private Object doTryLock(ProceedingJoinPoint joinPoint, BKMLock bkmLock, String key) {
        RLock lock = this.redissonClient.getLock(key);

        try {
            if (!lock.tryLock(bkmLock.maxiTime(), bkmLock.timeUnit())) {
                throw  new RuntimeException(bkmLock.errMsg());
            }
            log.info(bkmLock.note() + "已加[{}]锁", "tryLock");
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("doTryLock异常", e);
            throw new RuntimeException(bkmLock.errMsg());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info(bkmLock.note() + "释放锁");
                lock.unlock();
            }
        }
    }

}
