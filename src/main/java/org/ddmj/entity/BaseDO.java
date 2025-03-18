package org.ddmj.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.Date;

public class BaseDO implements Serializable {

    public static final long serialVersionUID = -6897241552144L;

    private Long id;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
