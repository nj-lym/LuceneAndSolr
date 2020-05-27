package com.lym.service;

import com.lym.entity.Search;

import java.util.List;

/**
 * (Search)表服务接口
 *
 * @author makejava
 * @since 2020-05-22 10:35:30
 */
public interface SearchService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Search queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Search> queryAllByLimit(int offset, int limit);

    /**
     * 查询全部数据
     * @return 对象列表
     */
    List<Search> queryAll();

    /**
     * 新增数据
     *
     * @param search 实例对象
     * @return 实例对象
     */
    Search insert(Search search);

    /**
     * 修改数据
     *
     * @param search 实例对象
     * @return 实例对象
     */
    Search update(Search search);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);


}