package com.cj.wendaplatform.async.handler;

import com.cj.wendaplatform.async.EventHandler;
import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventType;
import com.cj.wendaplatform.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author cj
 * @date 2019/8/8
 * 添加问题处理器: 添加问题时建立solr索引
 */
@Component
public class AddQuestionHandler implements EventHandler {

    @Autowired
    private SearchService searchService;


    @Override
    public void doHandle(EventModel model) {
        try {
           searchService.indexQuestion(model.getEntityId(),
                   model.getExt("title"), model.getExt("content"));
        } catch (Exception e) {
            System.out.println("增加题目索引失败");
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
