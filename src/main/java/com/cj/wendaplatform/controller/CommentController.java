package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventProducer;
import com.cj.wendaplatform.async.EventType;
import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.Comment;
import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.response.ResponseData;
import com.cj.wendaplatform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cj
 * @date 2019/7/24
 */
@Controller
@RequestMapping("/comment")
public class CommentController extends BaseController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加回答
     * @param comment
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseData addComment(Comment comment) throws BusinessException {
        try {
            if (hostHolder.getUser() == null) { //未登录跳转
                return ResponseData.create("errMsg", 1, "toLogin");
            }
            commentService.addComment(comment);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                .setEntityId(comment.getEntityId()));
            return ResponseData.create("successMsg", 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.COMMENT_INSERT_ERROR);
        }


    }
}
