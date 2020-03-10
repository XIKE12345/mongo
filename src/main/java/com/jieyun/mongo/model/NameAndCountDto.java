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
    private Object count;
    private Object id;

    /**
     * 时间
     */
    private Object time;
    private Object timeStamp;

    /**
     * 网站名称
     */
    private Object siteName;

    /**
     * 网站Url
     */
    private Object siteUrl;
    /**
     *
     */
    private Object doc;

}
