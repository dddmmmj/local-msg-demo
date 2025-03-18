package org.ddmj.dao;

import org.apache.ibatis.annotations.Param;
import org.ddmj.entity.LocalMessageDO;

import java.util.List;


public interface LocalMessageDAO {

    void insert(LocalMessageDO localMessageDO);

    int updateById(LocalMessageDO localMessageDO);

    List<LocalMessageDO> loadWaitRetryRecords(
        @Param("status") List<String> status,
        @Param("nextRetryTime") Long nextRetryTime,
        @Param("retryIntervalMinutes") int retryIntervalMinutes
    );
}
