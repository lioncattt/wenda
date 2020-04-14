package com.cj.wendaplatform.service;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.model.Comment;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/24
 */
public interface CommentService {

    int addComment(Comment comment) throws BusinessException;

    List<Comment> getCommentsByEntity(int entityId, int entityType, int offset, int limit);

    int getCommentCount(int entityId, int entityType);

    void deleteComment(int entityId, int entityType);

    Comment getCommentById(int commentId);

    int getCommentCountByEntityTypeAndUserId(int entityType, int userId);
}
