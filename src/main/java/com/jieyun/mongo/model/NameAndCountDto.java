package com.jieyun.mongo.model;

import lombok.Data;

/**
 * @CalssName NameAndCountDto
 * @Desc TODO
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/8
 * @Version 1.0
 **/
@Data
public class NameAndCountDto {

    /**
     * 总条数
     */
    private long count;

    /**
     * 时间
     */
    private String time;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站域名
     */
    private String siteUrl;

}
