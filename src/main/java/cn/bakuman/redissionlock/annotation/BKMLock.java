package cn.bakuman.redissionlock.annotation;


import cn.bakuman.redissionlock.enums.LockTypeEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁增强
 *
 * @author 梦叶
 * @see <a href='https://blog.bakuman.cn'>https://blog.bakuman.cn</>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BKMLock {


    /**
     * 锁的前缀
     *
     * @return
     */
    String perfixKey() default "";


    /**
     * 锁后缀El表达式
     *
     * @return
     */
    String suffixSpEl();

    /**
     * 业务说明
     * @return
     */
    String note();

    /**
     * 自动释放时间
     *
     * @return
     */
    long leaseTime() default 5;

    /**
     * 最大等待时间
     *
     * @return
     */
    long maxiTime() default 2;

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 锁类型
     *
     * @see LockTypeEnum
     */
    LockTypeEnum lockType() default LockTypeEnum.TRY_LOCK;

    /**
     * 错误提示
     * @return
     */
    String errMsg() default "业务失败";


}
