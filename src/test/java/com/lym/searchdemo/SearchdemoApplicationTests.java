package com.lym.searchdemo;

import com.lym.entity.Search;
import com.lym.service.SearchService;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class SearchdemoApplicationTests
{
    @Resource
    private SearchService searchService;

    /**
     * 读取文件中的数据
     * 以逗号拆分
     * 插入到数据库中
     */
    @Test
    void contextLoads() throws IOException, ParseException
    {
        String fileName = "hello140k.txt";
        BufferedReader buf = new BufferedReader(new FileReader(fileName));
        List<Search> searchList = new ArrayList<>();
        List<String> content = new ArrayList<>();
        String str = null;

        while ((str = buf.readLine()) != null) {
            content.add(str);  //一行内容
        }
        buf.close();
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < 10; i++) {
            String[] columnCon = content.get(i).split(",");
            Search searchRow = new Search();
            searchRow.setId(Integer.parseInt(columnCon[0]));
            searchRow.setIntroduce(columnCon[1]);
            searchRow.setClassification(columnCon[2]);
            searchRow.setPrice(Float.valueOf(columnCon[3]));
            searchRow.setAddress(columnCon[4]);
            searchRow.setNumber(columnCon[5]);
            searchRow.setUpdatetime(sf.parse(sf.format(date)));
//            searchService.insert(searchRow);
            System.out.println(searchRow);
        }


    }

    /**
     * 创建索引
     * 将数据添加到索引中
     *
     * @param analyzer 分词器对象
     * @param products 实体数据
     * @return Directory对象
     * @throws IOException
     */
    private static Directory createIndex(IKAnalyzer analyzer, List<Search> products) throws IOException
    {
        //创建内存索引
        Directory index = new RAMDirectory();
        //根据中文分词器创建配置对象
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //创建索引 writer
        IndexWriter writer = new IndexWriter(index, config);
        //遍历数据，把他们挨个放进索引里
        for (Search search : products) {
            addDoc(writer, search);
        }
        writer.close();
        return index;
    }

    /**
     * 创建文档对象
     * 将数据添加到文档对象中
     *
     * @param w
     * @param name
     * @throws IOException
     */
    private static void addDoc(IndexWriter w, String name) throws IOException
    {
        Document doc = new Document();
        doc.add(new TextField("name", name, Field.Store.YES));
        w.addDocument(doc);
    }

    /**
     * 将数据添加到文档对象中
     *
     * @param w
     * @param search
     * @throws IOException
     */
    private static void addDoc(IndexWriter w, Search search) throws IOException
    {
        Document doc = new Document();
        doc.add(new TextField("name", search.getIntroduce(), Field.Store.YES));
        doc.add(new TextField("id", String.valueOf(search.getId()), Field.Store.YES));
        w.addDocument(doc);
    }


    /**
     * 结果处理
     *
     * @param searcher
     * @param hits
     * @param query
     * @param analyzer
     * @throws Exception
     */
    private static void showSearchResults(IndexSearcher searcher, ScoreDoc[] hits, Query query, IKAnalyzer analyzer)
            throws Exception
    {
        System.out.println("找到 " + hits.length + " 个命中.");
        System.out.println("序号\t匹配度得分\t结果");
        //增加颜色
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        //每一个ScoreDoc[] hits 就是一个搜索结果，首先把他遍历出来
        for (int i = 0; i < hits.length; ++i) {
            ScoreDoc scoreDoc = hits[i];
            //然后获取当前结果的docid, 这个docid相当于就是这个数据在索引中的主键
            int docId = scoreDoc.doc;
            //再根据主键docid，通过搜索器从索引里把对应的Document取出来
            Document d = searcher.doc(docId);
            List<IndexableField> fields = d.getFields();
            System.out.print((i + 1));
            System.out.print("\t" + scoreDoc.score);
            for (IndexableField f : fields) {
                if ("name".equals(f.name())) {
                    TokenStream tokenStream = analyzer.tokenStream(f.name(), new StringReader(d.get(f.name())));
                    String fieldContent = highlighter.getBestFragment(tokenStream, d.get(f.name()));
                    System.out.print("\t" + fieldContent);
                }
                else {
                    System.out.print("\t" + d.get(f.name()));
                }
            }
            System.out.println("<br>");
        }
    }

    /**
     * 分页查询(方式一)
     *
     * @param query
     * @param searcher
     * @param pageNow  第几页
     * @param pageSize 每页显示记录数
     * @return
     * @throws IOException
     */

    private static ScoreDoc[] pageSearch1(Query query, IndexSearcher searcher, int pageNow, int pageSize) throws
            IOException
    {
        TopDocs topDocs = searcher.search(query, pageNow * pageSize);
        System.out.println("查询到的总条数\t" + topDocs.totalHits);
        ScoreDoc[] alllScores = topDocs.scoreDocs;
        List<ScoreDoc> hitScores = new ArrayList<>();
        int start = (pageNow - 1) * pageSize;
        int end = pageSize * pageNow;
        for (int i = start; i < end; i++) {
            hitScores.add(alllScores[i]);
        }
        ScoreDoc[] hits = hitScores.toArray(new ScoreDoc[]{ });
        return hits;
    }

    /**
     * 分页查询(方式二)
     *
     * @param query
     * @param searcher
     * @param pageNow  第几页
     * @param pageSize 每页显示的记录数
     * @return
     * @throws IOException
     */
    private static ScoreDoc[] pageSearch2(Query query, IndexSearcher searcher, int pageNow, int pageSize)
            throws IOException
    {
        //计算起始记录下标公式
        int start = (pageNow - 1) * pageSize;
        if (0 == start) {
            TopDocs topDocs = searcher.search(query, pageNow * pageSize);
            System.out.println("查询到的条数\t" + topDocs.totalHits);
            return topDocs.scoreDocs;
        }
        // 查询数据， 结束页面之前的数据都会查询到，但是只取本页的数据
        TopDocs topDocs = searcher.search(query, start);
        System.out.println("查询到的条数\t" + topDocs.totalHits);
        //获取到上一页最后一条
        ScoreDoc preScore = topDocs.scoreDocs[start - 1];
        //查询最后一条后的数据的一页数据
        topDocs = searcher.searchAfter(preScore, query, pageSize);
        System.out.println("根据分页条件取到的条数\t" + topDocs.scoreDocs.length);
        return topDocs.scoreDocs;

    }

    @Test
    void testOne() throws Exception
    {
        //1,准备中文分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        //2,创建索引
        List<Search> searches = searchService.queryAllByLimit(0, 2000);
        Directory index = createIndex(analyzer, searches);
        //3,查询器
        String keyword = "护眼";
        Query query = new QueryParser("name", analyzer).parse(keyword);
        //4,创建索引 reader
        IndexReader reader = DirectoryReader.open(index);
        //基于 reader 创建搜索器
        IndexSearcher searcher = new IndexSearcher(reader);
        //指定每页显示多少条数据
        int numPerPage = 10;
        System.out.println("当前一共" + searches.size() + "条数据");
        System.out.println("查询关键字:" + keyword);
        //执行搜索
        ScoreDoc[] hits = searcher.search(query, numPerPage).scoreDocs;
        //5,显示结果
        showSearchResults(searcher, hits, query, analyzer);
        //6,关闭查询
        reader.close();
    }

    @Test
    void testTwo() throws Exception
    {
        IKAnalyzer analyzer = new IKAnalyzer();
        TokenStream ts = analyzer.tokenStream("name", "护眼带光源");
        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(ts.reflectAsString(false));
        }
        /* 输出结果
        加载扩展词典：ext.dic
        加载扩展停止词典：stopword.dic
        term=护眼,bytes=......
        term=带,bytes=......
        term=光源,bytes=......
         */
    }

    /**
     * 测试分页查询
     */
    @Test
    void testThree() throws Exception
    {
        //1,准备中文分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        //2,创建索引
        List<Search> searches = searchService.queryAllByLimit(0, 2000);
        Directory index = createIndex(analyzer, searches);
        //3,查询器
        String keyword = "护眼";
        Query query = new QueryParser("name", analyzer).parse(keyword);
        //4,创建索引 reader
        IndexReader reader = DirectoryReader.open(index);
        //基于 reader 创建搜索器
        IndexSearcher searcher = new IndexSearcher(reader);
        //指定每页显示多少条数据
        int numPerPage = 10;
        System.out.println("当前一共" + searches.size() + "条数据");
        System.out.println("查询关键字:" + keyword);
        // 分页查询方式一
        //ScoreDoc[] hits = pageSearch1(query, searcher, 2, 5);
        //分页查询方式二
        ScoreDoc[] hits = pageSearch2(query, searcher, 3, 5);
        //5,显示结果
        showSearchResults(searcher, hits, query, analyzer);
        //6,关闭查询
        reader.close();
    }

    @Test
    void testFour() throws Exception
    {
        //1,准备中文分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        //2,创建索引
        List<Search> searches = searchService.queryAll();
        Directory index = createIndex(analyzer, searches);
        //3,删除索引
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);
        //删除id=5205的索引
        indexWriter.deleteDocuments(new Term("id", "5205"));
        indexWriter.commit();
        indexWriter.close();
        //删除之后再次查询
        String keyword = "光";
        // 4,查询
        Query query = new QueryParser("name", analyzer).parse(keyword);
        //5,创建索引 reader
        IndexReader reader = DirectoryReader.open(index);
        //基于 reader 创建搜索器
        IndexSearcher searcher = new IndexSearcher(reader);
        //指定每页显示多少条数据
        int numPerPage = 20;
        System.out.println("当前一共" + searches.size() + "条数据");
        System.out.println("查询关键字:" + keyword);
        //执行搜索
        ScoreDoc[] hits = searcher.search(query, numPerPage).scoreDocs;
        //6,显示结果
        showSearchResults(searcher, hits, query, analyzer);
        //7,关闭查询
        reader.close();
    }
}