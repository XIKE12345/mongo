package com.jieyun.mongo.service;

import com.jieyun.mongo.model.MongoDbDto;
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
     * @return
     */
    List<MongoDbDto> getDataByMongoDb();

    List<NameAndListDto> countQuery(CountReq countReq);
}
