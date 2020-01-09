package com.jieyun.mongo.utils;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

/**
 * @CalssName DocumentUtils
 * @Desc MongoDb的Document 转化为Bean
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/8
 * @Version 1.0
 **/
public class DocumentUtils {

    public static <T> T toBean(BasicDBObject dbObject, Class<T> clzss) {

        String realJson = dbObject.toJson(JsonWriterSettings.builder().build());

        T obj = JSON.parseObject(realJson, clzss);

        return obj;

    }

    /**
     * document To Bean
     *
     * @param document
     * @param clzss
     * @param <T>
     * @return
     */
    public static <T> T documentToBean(Document document, Class<T> clzss) {

        String realJson = document.toJson(JsonWriterSettings.builder().build());

        T obj = JSON.parseObject(realJson, clzss);

        return obj;

    }

    public static <T> BasicDBObject toDbObject(T object) {

        String json = JSON.toJSONString(object);

        BasicDBObject basicDBObject = BasicDBObject.parse(json);

        return basicDBObject;

    }

    public static <T> Document toDocument(T object) {

        String json = JSON.toJSONString(object);

        Document document = Document.parse(json);

        return document;

    }
}
