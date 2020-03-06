package com.jieyun.mongo.model.request;

import lombok.Data;

/**
 * @CalssName countReq
 * @Desc mongodb 统计请求实体
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/9
 * @Version 1.0
 **/
@Data
public class CountReq {

    /**
     * 开始时间(yyyy.MM.dd)
     */
    private String startTime;
    /**
     * 结束时间(yyyy.MM.dd)
     */
    private String endTime;

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;
}
