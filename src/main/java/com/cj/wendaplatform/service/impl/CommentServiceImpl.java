package com.cj.wendaplatform.service.impl;

import com.cj.wendaplatform.dao.CommentMapper;
import com.cj.wendaplatform.dao.QuestionMapper;
import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.Comment;
import com.cj.wendaplatform.model.EntityType;
import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.service.CommentService;
import com.cj.wendaplatform.util.SensitiveUtil;
import com.cj.wendaplatform.validator.ValidationResult;
import com.cj.wendaplatform.validator.ValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

/**
 * @author cj
 * @date 2019/7/24
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private SensitiveUtil sensitiveUtil;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    @Transactional
    public int addComment(Comment comment) throws BusinessException {
        if(comment == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //校验comment中字段是否合法
        ValidationResult result = validator.validate(comment);

        if(result.isHasErrors()) { //校验结果为有错误
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //过滤content
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveUtil.filter(comment.getContent()));


        comment.setUserId(hostHolder.getUser().getId());
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setStatus(0);//设置状态默认正常
        comment.setCreatedDate(new Date());
        //查询当前comment的数量
        int count = commentMapper.getCommentCount(comment.getEntityId(), comment.getEntityType());
        //更新question中的commentCount
        questionMapper.updateCommentCount(comment.getEntityId(), count);
        return commentMapper.addComment(comment) > 0 ? comment.getId() : 0;
    }

    @Override
    public List<Comment> getCommentsByEntity(int entityId, int entityType, int offset, int limit) {
        return commentMapper.selectByEntity(entityId, entityType, offset, limit);
    }

    @Override
    public int getCommentCount(int entityId, int entityType) {
        return commentMapper.getCommentCount(entityId, entityType);
    }

    @Override
    public void deleteComment(int entityId, int entityType) {
        commentMapper.updateStatus(entityId, entityType, 1);
    }

    @Override
    public Comment getCommentById(int commentId) {
        return commentMapper.getCommentById(commentId);
    }

    @Override
    public int getCommentCountByEntityTypeAndUserId(int entityType, int userId) {
        return commentMapper.getCommentCountByEntityTypeAndUserId(entityType, userId);
    }
}
