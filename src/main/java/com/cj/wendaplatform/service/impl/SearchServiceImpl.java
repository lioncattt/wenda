package com.cj.wendaplatform.service.impl;

import com.cj.wendaplatform.model.Question;
import com.cj.wendaplatform.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cj
 * @date 2019/8/8
 * 页面全局查询
 */
@Service
public class SearchServiceImpl implements SearchService {
    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/wenda";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();//连接solr
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    /**
     * 使用solr查询，将结果封装到questionList中
     * @param keyword 查询关键词
     * @param offset 查询的起始索引
     * @param count 查询行数
     * @param hlPre 高亮词前缀
     * @param hlPos 高亮词后缀
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<Question> searchQuestion(String keyword, int offset, int count,
                                         String hlPre, String hlPos) throws IOException, SolrServerException {
        List<Question> questionList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);//查询行数
        query.setStart(offset);//查询起始索引
        query.setHighlight(true);//设置关键词高亮
        query.setHighlightSimplePre(hlPre);//设置高亮关键词的前缀
        query.setHighlightSimplePost(hlPos);//设置高亮关键词的后缀
        //设置搜索出来的结果的内容(显示title 和 content)
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD);
        QueryResponse response = client.query(query);
        //将solr查询出来的结果封装到questionList中
        for (Map.Entry<String, Map<String, List<String>>> entry :
            response.getHighlighting().entrySet()) {
            Question q = new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    q.setContent(contentList.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (titleList.size() > 0) {
                    q.setTitle(titleList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }

    //为问题建立索引
    public boolean indexQuestion(int qid, String title, String content)
            throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }

}
