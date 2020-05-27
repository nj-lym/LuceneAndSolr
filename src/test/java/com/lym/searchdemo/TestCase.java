package com.lym.searchdemo;

import com.lym.entity.Search;
import com.lym.util.SearchUtil;
import com.lym.util.SolrUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @Description
 * @Auther lym
 * @Date 2020-05-27 10:51
 * @Version 1.0
 */
@SpringBootTest
public class TestCase
{
    @Test
    void testOne() throws IOException, SolrServerException
    {
        List<Search> searches = SearchUtil.fileList("hello140k.txt");
        SolrUtil.batchSaveOrUpdate(searches);
    }

    @Test
    void testTwo() throws IOException, SolrServerException
    {
        //查询
        QueryResponse queryResponse = SolrUtil.query("introduce:手机", 0, 10);
        SolrDocumentList documents = queryResponse.getResults();
        System.out.println("累计找到的条数：" + documents.getNumFound());
        if (!documents.isEmpty()) {

            Collection<String> fieldNames = documents.get(0).getFieldNames();
            for (String fieldName : fieldNames) {
                System.out.print(fieldName + "\t");
            }
            System.out.println();
        }
        for (SolrDocument solrDocument : documents) {
            Collection<String> fieldNames = solrDocument.getFieldNames();
            for (String fieldName : fieldNames) {
                System.out.print(solrDocument.get(fieldName) + "\t");
            }
            System.out.println();

        }
    }

    @Test
    void testThree() throws IOException, SolrServerException
    {
        //高亮查询查询
        SolrUtil.queryHighlight("introduce:手机");
    }

    @Test
    void testFour() throws IOException, SolrServerException
    {
        String keyword = "introduce:鞭";
        System.out.println("修改之前");
        query(keyword);

        Search search = new Search();
        search.setId(51173);
        search.setIntroduce("修改后的神鞭");
        SolrUtil.saveOrUpdate(search);
        System.out.println("修改之后");
        query(keyword);

        SolrUtil.deleteById("51173");
        System.out.println("删除之后");
        query(keyword);
    }

    private static void query(String keyword) throws SolrServerException, IOException
    {
        QueryResponse queryResponse = SolrUtil.query(keyword, 0, 10);
        SolrDocumentList documents = queryResponse.getResults();
        System.out.println("累计找到的条数：" + documents.getNumFound());
        if (!documents.isEmpty()) {

            Collection<String> fieldNames = documents.get(0).getFieldNames();
            for (String fieldName : fieldNames) {
                System.out.print(fieldName + "\t");
            }
            System.out.println();
        }

        for (SolrDocument solrDocument : documents) {

            Collection<String> fieldNames = solrDocument.getFieldNames();

            for (String fieldName : fieldNames) {
                System.out.print(solrDocument.get(fieldName) + "\t");

            }
            System.out.println();

        }
    }
}
