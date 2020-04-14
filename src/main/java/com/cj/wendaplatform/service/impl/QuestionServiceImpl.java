package com.cj.wendaplatform.service.impl;

import com.cj.wendaplatform.dao.QuestionMapper;
import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.model.Question;
import com.cj.wendaplatform.service.QuestionService;
import com.cj.wendaplatform.util.SensitiveUtil;
import com.cj.wendaplatform.validator.ValidationResult;
import com.cj.wendaplatform.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

/**
 * @author cj
 * @date 2019/7/16
 */
@Service
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;

    private final ValidatorImpl validator;

    private final HostHolder hostHolder;

    @Autowired
    private SensitiveUtil sensitiveUtil;

    @Autowired
    public QuestionServiceImpl(QuestionMapper questionMapper, ValidatorImpl validator, HostHolder hostHolder) {
        this.questionMapper = questionMapper;
        this.validator = validator;
        this.hostHolder = hostHolder;
    }

    @Override
    public List<Question> selectLatestQuestion(Integer userId, Integer offset, Integer limit) {
        return questionMapper.selectLatestQuestions(userId, offset,limit);
    }

    /**
     * 插入一个问题
     * @param question
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addQuestion(Question question) throws BusinessException {
        if(question == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //校验user中字段是否合法
        ValidationResult result = validator.validate(question);

        if(result.isHasErrors()) { //校验结果为有错误
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        //=========插入问题
        question.setUserId(hostHolder.getUser().getId());
        question.setCreatedDate(new Date());
        //转义html，js代码
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));

        //敏感词过滤
        question.setTitle(sensitiveUtil.filter(question.getTitle()));

        if(StringUtils.isNotBlank(question.getContent())) {
            question.setContent(HtmlUtils.htmlEscape(question.getContent()));
            question.setContent(sensitiveUtil.filter(question.getContent()));
        }
        question.setCommentCount(0);//设置回答数量
        //返回questionId
        return questionMapper.addQuestion(question) > 0 ? question.getId() : 0;
    }

    /**
     * 更新问题的评论数量
     * @param id
     * @param count
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CachePut(value = "question", key = "'question:id:' + #id")
    public int updateCommentCount(int id, int count) {
        return questionMapper.updateCommentCount(id, count);
    }

    /**
     * 根据id查询某个问题详情
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "question", key = "'question:id:' + #id")
    public Question getById(int id) {
        return questionMapper.getById(id);
    }
}
