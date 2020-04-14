package com.cj.wendaplatform.service;

import com.cj.wendaplatform.model.Question;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

/**
 * @author cj
 * @date 2019/8/8
 */

public interface SearchService {

    List<Question> searchQuestion(String keyword, int offset, int count,
                                  String hlPre, String hlPos) throws IOException, SolrServerException;


    boolean indexQuestion(int qid, String title, String content)
            throws IOException, SolrServerException;

}
