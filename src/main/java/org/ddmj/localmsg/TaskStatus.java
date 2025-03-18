package org.ddmj.localmsg;

import org.apache.commons.lang3.EnumUtils;


public enum TaskStatus {

    INIT,

    SUCCESS,

    FAIL,

    RETRY,

    ;

    public static TaskStatus of(String status) {
        return EnumUtils.getEnum(TaskStatus.class, status, INIT);
    }
}
