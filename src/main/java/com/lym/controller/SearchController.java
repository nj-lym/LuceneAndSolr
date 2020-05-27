package com.lym.controller;

import com.lym.entity.Search;
import com.lym.service.SearchService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Search)表控制层
 *
 * @author makejava
 * @since 2020-05-22 10:35:31
 */
@RestController
@RequestMapping("search")
public class SearchController {
    /**
     * 服务对象
     */
    @Resource
    private SearchService searchService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Search selectOne(Integer id) {
        System.out.println("请求成功");
        return this.searchService.queryById(id);
    }


}