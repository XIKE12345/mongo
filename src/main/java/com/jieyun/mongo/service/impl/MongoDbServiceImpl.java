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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @CalssName MongoDbServiceImpl
 * @Desc mongoDb统计数据
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
     * @param countReq 统计请求实体
     * @return lists
     */
    @Override
    public List<NameAndListDto> countQuery(CountReq countReq) {
        List<NameAndListDto> lists = new ArrayList<>();
        MongoDbFactory mongoDbFactory = mongoTemplate.getMongoDbFactory();
        List<Document> aggregateList = new ArrayList<>();
        Long startTime = 0L;
        Long endTime = 0L;
        try {
            startTime = MongoDbServiceImpl.dateToStamp(countReq.getStartTime() + " 00:00:00");
            endTime = MongoDbServiceImpl.dateToStamp(countReq.getEndTime() + " 23:59:59");
        } catch (Exception e) {
            log.error("时间转化异常");
        }

        // 根据条件查询
        if (!StringUtils.isEmpty(countReq.getStartTime()) && !StringUtils.isEmpty(countReq.getEndTime())) {
            Document sub_match = new Document();
            sub_match.put("time_stamp", new Document("$gt", startTime).append("$lt", endTime));
            // 查询
            Document match = new Document("$match", sub_match);
            aggregateList.add(match);
        }

        // 只查 notice_time、site、site_name、_id 这几个字段
        Document subProject = new Document();
        Map<Object, Object> map = new HashMap<>(16);
        map.put("_id", 1);
        map.put("time_stamp", 1);
        map.put("site", 1);
        map.put("site_name", 1);
        subProject.put("$project", map);
        aggregateList.add(subProject);

        // 分组条件
        Document groupDoc = new Document();

        // groupDoc.append("notice_time", new Document("$substr", Arrays.asList("$notice_time", 0, 10)));

        groupDoc.append("time_stamp", new Document("$dateToString", new Document("format", "%Y.%m.%d").append("date",
                new Document("$add", Arrays.asList(new Date(0), "$time_stamp")))));
        groupDoc.append("site", "$site");
        groupDoc.append("site_name", "$site_name");
        Document groupDocs = new Document();
        groupDocs.append("_id", groupDoc);
        groupDocs.append("totalCounts", new Document("$sum", 1));
        // 分组
        Document group = new Document("$group", groupDocs);
        aggregateList.add(group);

        Document sortDoc = new Document("$sort", new Document("site", -1));
        aggregateList.add(sortDoc);

        Document skipDoc = new Document("$skip", (countReq.getPageNum() - 1) * countReq.getPageSize());
        aggregateList.add(skipDoc);

        Document limitDoc = new Document("$limit", countReq.getPageSize());
        aggregateList.add(limitDoc);


        MongoDatabase hljDb = mongoDbFactory.getDb(ccgpDbName);
        AggregateIterable<Document> hljAggregate = hljDb.getCollection(ccgpColName).aggregate(aggregateList);

        List<NameAndCountDto> dtoList = getNameAndCountDtos(hljAggregate);
        NameAndListDto nameAndListDto = new NameAndListDto();
        CityListDto cityListDto = new CityListDto();
        cityListDto.setCityCounts(dtoList);
        cityListDto.setCityName("全国");
        cityListDto.setSum(dtoList.size());
        List<CityListDto> hljCityListDtos = new ArrayList<>();
        hljCityListDtos.add(cityListDto);
        nameAndListDto.setName("全国");
        nameAndListDto.setCounts(hljCityListDtos);

        lists.add(nameAndListDto);

        return lists;
    }

    private List<NameAndCountDto> getNameAndCountDtos(AggregateIterable<Document> aggregate1) {
        MongoCursor<Document> cursor = aggregate1.iterator();
        List<NameAndCountDto> list = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                NameAndCountDto nameAndCountDto = new NameAndCountDto();
                Document itemDoc = cursor.next();

                Object id = itemDoc.get("_id");
                nameAndCountDto.setDoc(id);
                int count = itemDoc.getInteger("totalCounts", 0);
                nameAndCountDto.setCount(count);
                list.add(nameAndCountDto);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private static Long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }
}
