package com.leon.common;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.leon.common.exception.BizException;
import org.slf4j.helpers.MessageFormatter;

public final class BizAssert {

    public static void isTrue(boolean expression, String errorCode, String pattern, Object... args) {
        if (!expression) {
            throw new BizException(errorCode, MessageFormatter.arrayFormat(pattern, args).getMessage());
        }
    }

    public static void isFalse(boolean expression, String errorCode, String pattern, Object... args) {
        isTrue(!expression, errorCode, pattern, args);
    }

    public static void isNotBlank(CharSequence cs, String errorCode, String pattern, Object... args) {
        isTrue(StringUtils.isNotBlank(cs), errorCode, pattern, args);
    }


    public static void isNotNull(Object obj, String errorCode, String pattern, Object... args) {
        isTrue(obj != null, errorCode, pattern, args);
    }

    public static void isNull(Object obj, String errorCode, String pattern, Object... args) {
        isTrue(obj == null, errorCode, pattern, args);
    }

}
