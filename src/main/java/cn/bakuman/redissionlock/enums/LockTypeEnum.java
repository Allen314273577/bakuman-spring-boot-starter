package cn.bakuman.redissionlock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * lock
 * @author 梦叶
 */
@AllArgsConstructor
@Getter
public enum LockTypeEnum {
    LOCK,
    TRY_LOCK;
}
