package com.lym.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * (Search)实体类
 *
 * @author makejava
 * @since 2020-05-22 10:20:35
 */
@Data
public class Search implements Serializable {
    private static final long serialVersionUID = -10882607974819302L;
    /**
    * id
    */
    @Field
    private Integer id;
    /**
    * 介绍
    */
    @Field
    private String introduce;
    /**
    * 分类
    */
    @Field
    private String classification;
    /**
    * 价格
    */
    @Field
    private float  price;
    /**
    * 地址
    */
    @Field
    private String address;
    /**
    * 编号
    */
    @Field
    private String number;
    /**
    * 修改时间
    */
    private Date updatetime;


}