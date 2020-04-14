package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventProducer;
import com.cj.wendaplatform.async.EventType;
import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.*;
import com.cj.wendaplatform.model.vo.ViewObject;
import com.cj.wendaplatform.response.ResponseData;
import com.cj.wendaplatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cj
 * @date 2019/7/22
 * 1.增加一个问题
 * 2.查看问题详情，包括问题和用户，评论和用户，每个回答对应的点赞次数和点赞状态
 * 点赞状态指显示当前已登录用户赞过或踩过
 * 点赞关联的对象是comment, comment关联的对象是question
 */
@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController  {

   private final QuestionService questionService;


   private final UserService userService;

   private final HostHolder hostHolder;

   private final CommentService commentService;

   private final LikeService likeService;

   private final FollowService followService;

   private final EventProducer eventProducer;

    @Autowired
    public QuestionController(QuestionService questionService,
                              UserService userService, HostHolder hostHolder, CommentService commentService, LikeService likeService, FollowService followService, EventProducer eventProducer) {
        this.questionService = questionService;
        this.userService = userService;
        this.hostHolder = hostHolder;
        this.commentService = commentService;
        this.likeService = likeService;
        this.followService = followService;
        this.eventProducer = eventProducer;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseData addQuestion(Question question) throws BusinessException {
        try {
            if (hostHolder.getUser() == null) {
                return ResponseData.create("errMsg", 1, "toLogin");
            }
            int i = questionService.addQuestion(question);
            if (i > 0) {
                //创建问题索引
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                    .setActorId(question.getUserId()).setEntityId(question.getId())
                    .setExt("title", question.getTitle())
                    .setExt("content", question.getContent()));
                return ResponseData.create("successMsg", 1);
            }
            return ResponseData.create("errMsg", 0, "fail");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.QUESTION_INSERT_ERROR);
        }
    }

    /**
     * 首页点击查询某个问题详情
     * 查询关注当前问题的用户，显示在问题下方用户的信息
     * 查询回答时要查询每个回答对应的点赞状态和次数
     * @param model
     * @param qid
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping(path = {"/{qid}/{offset}/{limit}"})
    public String questionDetail(Model model, @PathVariable("qid") int qid,
                                 @PathVariable("offset") int offset,
                                 @PathVariable("limit") int limit) {
        Question question = questionService.getById(qid);
        model.addAttribute("question", question);
        /*查询当前问题的回答*/
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION,
                offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for(Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0); //未登录用户点赞状态为0
            } else { //liked=-1 已踩 liked=1 已赞 liked=0:未踩未赞
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(),
                        EntityType.ENTITY_COMMENT, comment.getId()));
            }
            //查询点赞次数
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("comment", comment);
            vo.set("user", userService.selectUserById(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        //获取关注当前问题的最新的20个用户的id
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 0, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.selectUserById(userId);
            if(u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        //显示关注了当前问题最新20个用户信息
        model.addAttribute("followUsers", followUsers);

        if (hostHolder.getUser() != null) { //判断当前用户是否已关注了此问题
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(),
                    EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }
        return "detail";
    }
}
