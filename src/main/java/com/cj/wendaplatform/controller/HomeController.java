package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.model.EntityType;
import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.model.Question;
import com.cj.wendaplatform.model.User;
import com.cj.wendaplatform.model.vo.ViewObject;
import com.cj.wendaplatform.response.ResponseData;
import com.cj.wendaplatform.service.CommentService;
import com.cj.wendaplatform.service.FollowService;
import com.cj.wendaplatform.service.QuestionService;
import com.cj.wendaplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cj
 * @date 2019/7/16
 * 首页
 * 查询最新的回答
 * 跳转至首页
 */
@Controller
public class HomeController extends BaseController {

    private final QuestionService questionService;

    private final UserService userService;

    private final CommentService commentService;

    private final FollowService followService;

    private final HostHolder hostHolder;

    @Autowired
    public HomeController(QuestionService questionService, UserService userService, CommentService commentService, FollowService followService, HostHolder hostHolder) {
        this.questionService = questionService;
        this.userService = userService;
        this.commentService = commentService;
        this.followService = followService;
        this.hostHolder = hostHolder;
    }


    //查询所有的问题
    @GetMapping(path = {"", "/index"})
    public String index(Model model) throws BusinessException {
        model.addAttribute("resp", getQuestions(0, 0, 10));
        return "index";
    }

    //查询某个用户的主页 包括提过的问题 关注,粉丝等信息, 还可以关注用户
    @GetMapping(path = {"/user/profile/{userId}"})
    public String userIndex(Model model, @PathVariable("userId") Integer userId) throws BusinessException {
        //查询某用户的10个问题
       model.addAttribute("vos", getQuestions(userId, 0, 10));

       User user = userService.selectUserById(userId);
       ViewObject vo = new ViewObject();
       vo.set("user", user);//查询某用户
       vo.set("commentCount", commentService
               .getCommentCountByEntityTypeAndUserId(
                       EntityType.ENTITY_QUESTION, userId
               )); //查询某用户的回答问题数

       vo.set("followerCount", followService.getFollowerCount(
              EntityType.ENTITY_USER, userId
       ));//查询某用户的粉丝数量

        vo.set("followeeCount", followService.getFolloweeCount(
                userId, EntityType.ENTITY_USER
        ));//查询某用户的关注数量
        if (hostHolder.getUser() != null) {
            //判断当前用户是否是某用户的粉丝
            vo.set("followed", followService.isFollower(
                    hostHolder.getUser().getId(), EntityType.ENTITY_USER,
                    userId));

            //判断当前用户是否就是某用户 是本人的主页，就不显示关注该用户
            if (hostHolder.getUser().getId() == user.getId()) {
                vo.set("isCurUser", true);
            } else {
                vo.set("isCurUser", false);
            }

        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
       return "profile";
    }



    /**
     * 获取最新的问题和每个问题对应的用户
     * @param userId
     * @param Offset
     * @param limit
     * @return ResponseData集合中，每一个ResponseData都有一个question和对应的user
     */
    private List<ResponseData> getQuestions(Integer userId, Integer Offset, Integer limit) throws BusinessException {
        //查询出所有问题
        List<Question> questionList = questionService.selectLatestQuestion(userId, Offset, limit);
        List<ResponseData> crt = new ArrayList<>();//存储查询出来的结果
        if(questionList !=null && !questionList.isEmpty() ) {
            for (Question question : questionList) {
                ResponseData responseData = ResponseData.create("question", question);
                //根据问题中的userId找出对应的用户
                responseData.setData("user", userService.selectUserById(question.getUserId()));
                //查询当前问题粉丝数量
                responseData.setData("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION,
                        question.getId()));
                crt.add(responseData);
            }

        }
        return crt;
        //throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "查询问题为空");

    }



}
