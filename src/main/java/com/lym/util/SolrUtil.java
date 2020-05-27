package com.lym.util;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Auther lym
 * @Date 2020-05-27 9:58
 * @Version 1.0
 */

public class SolrUtil
{

    public static SolrClient client;
    private static String url;

    static {
        url = "http://localhost:8983/solr/demo01";
        client = new HttpSolrClient.Builder(url).build();
    }

    public static <T> boolean batchSaveOrUpdate(List<T> entities) throws IOException, SolrServerException
    {
        DocumentObjectBinder binder = new DocumentObjectBinder();
        int total = entities.size();
        int count = 0;
        for (T t : entities) {
            SolrInputDocument doc = binder.toSolrInputDocument(t);
            client.add(doc);
            System.out.printf("添加数据到索引中总共添加%d条记录,当前添加第%d条", total, ++count);

        }
        client.commit();
        return true;
    }

    /**
     * 分页查询
     *
     * @param keywords     查询关键字
     * @param startOfPage  起始页
     * @param numberOfPage 每页记录数
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static QueryResponse query(String keywords, int startOfPage, int numberOfPage) throws SolrServerException,
            IOException
    {
        SolrQuery query = new SolrQuery();
        query.setStart(startOfPage);
        query.setRows(numberOfPage);
        // 设置查询关键字
        query.setQuery(keywords);
        QueryResponse rsp = client.query(query);
        return rsp;
    }

    /**
     * 高亮显示关键字
     *
     * @param keywords
     * @throws SolrServerException
     * @throws IOException
     */
    public static void queryHighlight(String keywords) throws SolrServerException, IOException
    {
        SolrQuery q = new SolrQuery();
        //开始页数
        q.setStart(0);
        //每页显示条数
        q.setRows(10);
        // 设置查询关键字
        q.setQuery(keywords);
        // 开启高亮
        q.setHighlight(true);
        // 高亮字段
        q.addHighlightField("introduce");
        // 高亮单词的前缀
        q.setHighlightSimplePre("<span style='color:red'>");
        // 高亮单词的后缀
        q.setHighlightSimplePost("</span>");
        //摘要最长100个字符
        q.setHighlightFragsize(100);
        //查询
        QueryResponse query = client.query(q);
        //获取高亮字段introduce相应结果
        NamedList<Object> response = query.getResponse();
        NamedList<?> highlighting = (NamedList<?>) response.get("highlighting");
        for (int i = 0; i < highlighting.size(); i++) {
            System.out.println(highlighting.getName(i) + "：" + highlighting.getVal(i));
        }

        //获取查询结果
        SolrDocumentList results = query.getResults();
        for (SolrDocument result : results) {
            System.out.println(result.toString());
        }
    }

    /**
     * 增加索引或更新索引
     *
     * @param entity
     * @param <T>
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static <T> boolean saveOrUpdate(T entity) throws SolrServerException, IOException
    {
        DocumentObjectBinder binder = new DocumentObjectBinder();
        SolrInputDocument doc = binder.toSolrInputDocument(entity);
        client.add(doc);
        client.commit();
        return true;
    }

    /**
     * 根据id删除索引
     * @param id
     * @return
     */
    public static boolean deleteById(String id)
    {
        try {
            client.deleteById(id);
            client.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
