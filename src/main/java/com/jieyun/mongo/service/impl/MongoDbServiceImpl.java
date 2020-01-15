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
    /**
     * 黑龙江
     */
    @Value("${hlj_spl.dbname}")
    private String hljDbName;
    @Value("${hlj_spl.colname}")
    private String hljColName;
    /**
     * 湖南
     */
    @Value("${hn_spl.dbname}")
    private String hnDbName;
    @Value("${hn_spl.colname}")
    private String hnColName;

    /**
     * 河南
     */
    @Value("${henan_spl.dbname}")
    private String henanDbName;
    @Value("${henan_spl.colname}")
    private String henanColName;

    /**
     * 吉林
     */
    @Value("${jl_spl.dbname}")
    private String jlDbName;
    @Value("${jl_jl_spl.colname}")
    private String jljlColName;
    @Value("${jl_ch_spl.colname}")
    private String jlchColName;

    /**
     * 江苏
     */
    @Value("${js_spl.dbname}")
    private String jsDbName;
    @Value("${js_spl.colname}")
    private String jsColName;

    /**
     * 内蒙古
     */
    @Value("${nmg_spl.dbname}")
    private String nmgDbName;
    @Value("${nmg_spl.colname}")
    private String nmgColName;
    /**
     * 宁夏
     */
    @Value("${nx_spl.dbname}")
    private String nxDbName;
    @Value("${nx_spl.colname}")
    private String nxColName;
    /**
     * 青海
     */
    @Value("${qh_spl.dbname}")
    private String qhDbName;
    @Value("${qh_spl.colname}")
    private String qhColName;
    /**
     * 西藏
     */
    @Value("${xizang_spl.dbname}")
    private String xizangDbName;
    @Value("${xizang_spl.colname}")
    private String xizangColName;

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
        Document sort = new Document("$sort", new Document("notice_time", 1));
        aggregateList.add(sort);

        MongoDatabase hljDb = mongoDbFactory.getDb(hljDbName);
        AggregateIterable<Document> hljAggregate = hljDb.getCollection(hljColName).aggregate(aggregateList);
        ;
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

        MongoDatabase henanDb = mongoDbFactory.getDb(henanDbName);
        AggregateIterable<Document> henanAggregate = henanDb.getCollection(henanColName).aggregate(aggregateList);
        List<NameAndCountDto> henanList = getNameAndCountDtos(henanAggregate);
        NameAndListDto henanListDto = new NameAndListDto();

        CityListDto henancityListDto = new CityListDto();
        henancityListDto.setCityName("河南");
        henancityListDto.setCityCounts(henanList);
        List<CityListDto> henanCityListDtos = new ArrayList<>();
        henanCityListDtos.add(henancityListDto);
        henanListDto.setName("河南省");
        henanListDto.setCounts(henanCityListDtos);
        lists.add(henanListDto);

        MongoDatabase hnDb = mongoDbFactory.getDb(hnDbName);
        AggregateIterable<Document> hnAggregate = hnDb.getCollection(hnColName).aggregate(aggregateList);
        List<NameAndCountDto> hnList = getNameAndCountDtos(hnAggregate);
        NameAndListDto hnListDto = new NameAndListDto();
        CityListDto hncityListDto = new CityListDto();
        hncityListDto.setCityCounts(hnList);
        hncityListDto.setCityName("湖南");
        List<CityListDto> hncityListDtos = new ArrayList<>();
        hncityListDtos.add(hncityListDto);
        hnListDto.setName("湖南省");
        hnListDto.setCounts(hncityListDtos);
        lists.add(hnListDto);

        MongoDatabase jlDb = mongoDbFactory.getDb(jlDbName);
        //吉林省吉林市
        AggregateIterable<Document> jljlaggregate = jlDb.getCollection(jljlColName).aggregate(aggregateList);
        List<NameAndCountDto> jljlList = getNameAndCountDtos(jljlaggregate);
        // 吉林省长春市
        AggregateIterable<Document> jlchaggregate = jlDb.getCollection(jlchColName).aggregate(aggregateList);
        List<NameAndCountDto> jlchList = getNameAndCountDtos(jlchaggregate);

        NameAndListDto jljlListDto = new NameAndListDto();
        jljlListDto.setName("吉林省");
        CityListDto jljlcityListDto = new CityListDto();
        jljlcityListDto.setCityCounts(jljlList);
        jljlcityListDto.setCityName("吉林市");

        CityListDto jlchcityListDto = new CityListDto();
        jlchcityListDto.setCityCounts(jlchList);
        jlchcityListDto.setCityName("长春市");

        List<CityListDto> jljlcityListDtos = new ArrayList<>();
        jljlcityListDtos.add(jljlcityListDto);
        jljlcityListDtos.add(jlchcityListDto);

        jljlListDto.setCounts(jljlcityListDtos);
        lists.add(jljlListDto);

        /**
         * 江苏
         */
        MongoDatabase jshDb = mongoDbFactory.getDb(jsDbName);
        AggregateIterable<Document> jsAggregate = jshDb.getCollection(jsColName).aggregate(aggregateList);
        List<NameAndCountDto> jsList = getNameAndCountDtos(jsAggregate);
        NameAndListDto jsListDto = new NameAndListDto();
        CityListDto jscityListDto = new CityListDto();
        jscityListDto.setCityCounts(jsList);
        jscityListDto.setCityName("江苏");
        List<CityListDto> jscityListDtos = new ArrayList<>();
        jscityListDtos.add(jscityListDto);
        jsListDto.setName("江苏省");
        jsListDto.setCounts(jscityListDtos);
        lists.add(jsListDto);

        /**
         * 内蒙古
         */
        MongoDatabase nmgDb = mongoDbFactory.getDb(nmgDbName);
        AggregateIterable<Document> nmgAggregate = nmgDb.getCollection(nmgColName).aggregate(aggregateList);

        List<NameAndCountDto> nmgList = getNameAndCountDtos(nmgAggregate);
        NameAndListDto nmgListDto = new NameAndListDto();
        CityListDto nmgcityListDto = new CityListDto();
        nmgcityListDto.setCityCounts(nmgList);
        nmgcityListDto.setCityName("内蒙");
        List<CityListDto> nmgcityListDtos = new ArrayList<>();
        nmgcityListDtos.add(nmgcityListDto);
        nmgListDto.setName("内蒙古自治区");
        nmgListDto.setCounts(nmgcityListDtos);
        lists.add(nmgListDto);

        /**
         * 西藏
         */
        MongoDatabase xizangDb = mongoDbFactory.getDb(xizangDbName);
        AggregateIterable<Document> xizangAggregate = xizangDb.getCollection(xizangColName).aggregate(aggregateList);
        ;
        List<NameAndCountDto> xizangList = getNameAndCountDtos(xizangAggregate);
        NameAndListDto xizangListDto = new NameAndListDto();
        CityListDto xizangcityListDto = new CityListDto();
        xizangcityListDto.setCityCounts(xizangList);
        xizangcityListDto.setCityName("西藏");
        List<CityListDto> xizangcityListDtos = new ArrayList<>();
        xizangcityListDtos.add(xizangcityListDto);
        xizangListDto.setName("西藏自治区");
        xizangListDto.setCounts(xizangcityListDtos);
        lists.add(xizangListDto);
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
