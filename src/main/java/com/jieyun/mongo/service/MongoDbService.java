package com.jieyun.mongo.service;

import com.jieyun.mongo.model.MongoDbDto;
import com.jieyun.mongo.model.NameAndCountDto;
import com.jieyun.mongo.model.NameAndListDto;
import com.jieyun.mongo.model.request.CountReq;

import java.util.List;

/**
 * @CalssName MongoDbService
 * @Desc TODO
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/8
 * @Version 1.0
 **/
public interface MongoDbService {
    /**
     * 从mongoDb获取数据
     *
     * @return list
     */
    List<MongoDbDto> getDataByMongoDb();

    /**
     * 统计每个网站每天的爬虫数量
     *
     * @param countReq 查询统计参数
     * @return list
     */
    List<NameAndListDto> countQuery(CountReq countReq);

    /**
     * 获取所有网站列表
     *
     * @param countReq 请求参数
     */
    void getAllSiteListFromMongo(CountReq countReq);

    /**
     * 查每天每个网站的条数
     *
     * @param countReq
     * @return
     */
    List<NameAndCountDto> getEveryDayCounts(CountReq countReq);

}
