package org.ddmj.entity;

import lombok.Getter;
import lombok.Setter;
import org.ddmj.localmsg.TaskStatus;


@Getter
@Setter
public class LocalMessageDO extends BaseDO {

    private String reqSnapshot;

    private String status;

    private Long nextRetryTime;

    private Integer retryTimes;

    private Integer maxRetryTimes;

    private String failReason;

    public LocalMessageDO() {
    }

    public LocalMessageDO(String reqSnapshot, Integer maxRetryTimes, Long nextRetryTime) {
        this.status = TaskStatus.INIT.name();
        this.reqSnapshot = reqSnapshot;
        this.maxRetryTimes = maxRetryTimes;
        this.nextRetryTime = nextRetryTime;
        this.retryTimes = 0;
    }

}
