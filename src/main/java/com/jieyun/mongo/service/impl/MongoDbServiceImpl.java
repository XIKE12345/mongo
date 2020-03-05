package com.jieyun.mongo.service.impl;

import com.jieyun.mongo.model.CityListDto;
import com.jieyun.mongo.model.MongoDbDto;
import com.jieyun.mongo.model.NameAndCountDto;
import com.jieyun.mongo.model.NameAndListDto;
import com.jieyun.mongo.model.request.CountReq;
import com.jieyun.mongo.service.MongoDbService;
import com.jieyun.mongo.utils.DocumentUtils;
import com.mongodb.client.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @CalssName MongoDbServiceImpl
 * @Desc TODO
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/8
 * @Version 1.0
 **/
@Service
@Slf4j
public class MongoDbServiceImpl implements MongoDbService {

    @Value("${db_ccgp.dbname}")
    private String ccgpDbName;
    @Value("${db_ccgp.colname}")
    private String ccgpColName;


    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoDbServiceImpl() {
    }

    @Override
    public List<MongoDbDto> getDataByMongoDb() {
        long l = System.currentTimeMillis();
        MongoCollection<Document> documentMongoCollection = mongoTemplate.getCollection("ccgp_search_raw");
        long countDocuments = documentMongoCollection.countDocuments();
        long l2 = System.currentTimeMillis();
        log.info("{}", l2 - l);
        FindIterable<Document> documents = documentMongoCollection.find();
        long pageSize = 1000;
        long pageCount = countDocuments / pageSize + 1;
        long page = 1;
        MongoCursor<Document> iterator = documents.iterator();
        long l1 = System.currentTimeMillis();
        List<MongoDbDto> dbObjects = new ArrayList<>();
        do {
            Document document = iterator.next();
            MongoDbDto mongoDbDto = DocumentUtils.documentToBean(document, MongoDbDto.class);
            dbObjects.add(mongoDbDto);
            page++;
        } while (page <= pageCount);
        long l3 = System.currentTimeMillis();
        log.info("+++++++++++++++{}", l3 - l1);
        return dbObjects;
    }

    /**
     * 统计MongoDb中每天每个省的条数
     *
     * @param countReq
     * @return
     */
    @Override
    public List<NameAndListDto> countQuery(CountReq countReq) {
        List<NameAndListDto> lists = new ArrayList<>();
        MongoDbFactory mongoDbFactory = mongoTemplate.getMongoDbFactory();
        List<Document> aggregateList = new ArrayList<Document>();
        // 根据条件查询
        if (!StringUtils.isEmpty(countReq.getStartTime()) && !StringUtils.isEmpty(countReq.getEndTime())) {
            Document sub_match = new Document();
            sub_match.put("notice_time", new Document("$gt", countReq.getStartTime()).append("$lt", countReq.getEndTime()));
            // 查询
            Document match = new Document("$match", sub_match);
            aggregateList.add(match);
        }

        // 根据条件分组
        Document sub_group = new Document();
        sub_group.put("_id", new Document("$substr", Arrays.asList("$notice_time", 0, 10)));
        sub_group.put("count", new Document("$sum", 1));

        // 分组
        Document group = new Document("$group", sub_group);
        aggregateList.add(group);

        // 排序（对_id字段进行升序排列）
        Document sort = new Document("$sort", new Document("_id", 1));
        aggregateList.add(sort);

        MongoDatabase hljDb = mongoDbFactory.getDb(ccgpDbName);
        AggregateIterable<Document> hljAggregate = hljDb.getCollection(ccgpColName).aggregate(aggregateList);

        List<NameAndCountDto> hljList = getNameAndCountDtos(hljAggregate);
        NameAndListDto hljListDto = new NameAndListDto();
        CityListDto hljcityListDto = new CityListDto();
        hljcityListDto.setCityCounts(hljList);
        hljcityListDto.setCityName("黑龙江");
        List<CityListDto> hljCityListDtos = new ArrayList<>();
        hljCityListDtos.add(hljcityListDto);
        hljListDto.setName("黑龙江省");
        hljListDto.setCounts(hljCityListDtos);
        lists.add(hljListDto);

        return lists;
    }

    private List<NameAndCountDto> getNameAndCountDtos(AggregateIterable<Document> aggregate1) {
        MongoCursor<Document> cursor = aggregate1.iterator();
        List<NameAndCountDto> list = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                NameAndCountDto nameAndCountDto = new NameAndCountDto();
                Document item_doc = cursor.next();
                String time = (String) item_doc.get("_id");
                int count = item_doc.getInteger("count", 0);
                nameAndCountDto.setCount(count);
                nameAndCountDto.setTime(time);
                list.add(nameAndCountDto);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
