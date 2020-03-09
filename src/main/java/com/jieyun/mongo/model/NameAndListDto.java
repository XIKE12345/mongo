package com.jieyun.mongo.model;

import lombok.Data;

import java.util.List;

/**
 * @CalssName NameAndList
 * @Desc TODO
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/1/9
 * @Version 1.0
 **/
@Data
public class NameAndListDto {
    /**
     * 省
     */
    private String name;


    private List<CityListDto> counts;
}
