package com.jieyun.mongo.controller;

import com.jieyun.mongo.model.MongoDbDto;
import com.jieyun.mongo.model.NameAndListDto;
import com.jieyun.mongo.model.request.CountReq;
import com.jieyun.mongo.service.MongoDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @CalssName MongoDbController
 * @Desc TODO
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/8
 * @Version 1.0
 **/
@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/mongo")
public class MongoDbController {

    private final MongoDbService mongoDbService;

    @Autowired
    private MongoDbController(MongoDbService mongoDbService) {
        this.mongoDbService = mongoDbService;
    }

    /**
     * 从Mongo获取数据
     *
     * @return
     */
    @GetMapping("/data")
    public List<MongoDbDto> getDataToMongoDb() {
        List<MongoDbDto> dataByMongoDb = mongoDbService.getDataByMongoDb();
        return dataByMongoDb;
    }

    /**
     * 从Mongo获取数据
     *
     * @return
     */
    @PostMapping(value = "/count")
    public List<NameAndListDto> queryCountByTime(@RequestBody CountReq countReq) {
        List<NameAndListDto> nameAndList = mongoDbService.countQuery(countReq);
        return nameAndList;
    }
}
