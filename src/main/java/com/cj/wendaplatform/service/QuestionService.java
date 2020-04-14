package com.cj.wendaplatform.service;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.model.Question;


import java.util.List;

/**
 * @author cj
 * @date 2019/7/16
 */
public interface QuestionService {

    List<Question> selectLatestQuestion(Integer userId, Integer offset, Integer limit);

    int addQuestion(Question question) throws BusinessException;

    int updateCommentCount(int id, int count);

    Question getById(int id);
}
