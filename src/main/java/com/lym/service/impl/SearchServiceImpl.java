package com.lym.service.impl;

import com.lym.entity.Search;
import com.lym.dao.SearchDao;
import com.lym.service.SearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Search)表服务实现类
 *
 * @author makejava
 * @since 2020-05-22 11:07:41
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService
{
    @Resource
    private SearchDao searchDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Search queryById(Integer id)
    {
        return this.searchDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Search> queryAllByLimit(int offset, int limit)
    {
        return this.searchDao.queryAllByLimit(offset, limit);
    }

    @Override
    public List<Search> queryAll()
    {
        return this.searchDao.findAll();
    }

    /**
     * 新增数据
     *
     * @param search 实例对象
     * @return 实例对象
     */
    @Override
    public Search insert(Search search)
    {
        this.searchDao.insert(search);
        return search;
    }

    /**
     * 修改数据
     *
     * @param search 实例对象
     * @return 实例对象
     */
    @Override
    public Search update(Search search)
    {
        this.searchDao.update(search);
        return this.queryById(search.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id)
    {
        return this.searchDao.deleteById(id) > 0;
    }

}