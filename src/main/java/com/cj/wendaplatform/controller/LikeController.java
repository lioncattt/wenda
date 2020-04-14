package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventProducer;
import com.cj.wendaplatform.async.EventType;
import com.cj.wendaplatform.model.Comment;
import com.cj.wendaplatform.model.EntityType;
import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.response.ResponseData;
import com.cj.wendaplatform.service.CommentService;
import com.cj.wendaplatform.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cj
 * @date 2019/7/29
 * 点赞功能
 * 1. 进入到问题详情页面，可以看到回答的点赞次数，点赞的状态(是否已被当前用户赞过或踩过)
 * 2. 点击赞或踩后，异步返回当前最新的点赞次数
 */
@Controller
public class LikeController extends BaseController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 用户点击赞 插入一个kv
     * 给对方发送站内信
     * @param commentId
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public ResponseData like(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) { //未登录
            return ResponseData.create("successMsg", -1);
        }

        Comment comment = commentService.getCommentById(commentId);
        if (hostHolder.getUser().getId() != comment.getUserId()) {
            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(hostHolder.getUser().getId()) //事件发起者
                    .setEntityType(EntityType.ENTITY_COMMENT) //关联的实体类型
                    .setEntityId(commentId) //关联的实体的id
                    .setEntityOwnerId(comment.getUserId()) //事件接收者
                    .setExt("questionId", String.valueOf(comment.getEntityId())));
        }



        //返回跟新后的点赞次数
           long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,
                   commentId);
        return ResponseData.create("successMsg", likeCount);
    }

    @PostMapping("/dislike")
    @ResponseBody
    public ResponseData dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return ResponseData.create("successMsg", -1);
        }
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return ResponseData.create("successMsg", likeCount);
    }

}
