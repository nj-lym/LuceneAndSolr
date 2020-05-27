package com.lym.util;

import com.lym.entity.Search;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Auther lym
 * @Date 2020-05-27 9:35
 * @Version 1.0
 */

public class SearchUtil
{

    public static List<Search> fileList(String fileName) throws IOException
    {
        File file = new File(fileName);
        List<String> lines = FileUtils.readLines(file, "utf-8");
        List<Search> searchList = new ArrayList<>();
        for (String line : lines) {
            Search search = add(line);
            searchList.add(search);
        }
        return searchList;
    }


    public static Search add(String lineStr)
    {
        String[] columnCon = lineStr.split(",");
        Search search = new Search();
        search.setId(Integer.parseInt(columnCon[0]));
        search.setIntroduce(columnCon[1]);
        search.setClassification(columnCon[2]);
        search.setPrice(Float.valueOf(columnCon[3]));
        search.setAddress(columnCon[4]);
        search.setNumber(columnCon[5]);
        return search;
    }
}
