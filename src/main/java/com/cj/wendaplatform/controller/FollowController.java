package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventProducer;
import com.cj.wendaplatform.async.EventType;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cj
 * @date 2019/8/3
 *
 */
@Controller
public class FollowController extends  BaseController {

    private final FollowService followService;

    private final CommentService commentService;

    private final QuestionService questionService;

    private final UserService userService;

    private final HostHolder hostHolder;

    private final EventProducer eventProducer;

    @Autowired
    public FollowController(FollowService followService, CommentService commentService, QuestionService questionService,
                            UserService userService, HostHolder hostHolder, EventProducer eventProducer) {
        this.followService = followService;
        this.commentService = commentService;
        this.questionService = questionService;
        this.userService = userService;
        this.hostHolder = hostHolder;
        this.eventProducer = eventProducer;
    }


    /**
     * 关注用户功能
     * @param userId 对方id
     * @return
     */
    @PostMapping(path = {"/followers/user"})
    @ResponseBody
    public ResponseData followUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return ResponseData.create("successMsg", -1);
        }

        //true表示事务执行成功 false表示失败
        boolean ret = followService.follow(hostHolder.getUser().getId(),
                EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
            .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
            .setEntityType(EntityType.ENTITY_USER)
            .setEntityOwnerId(userId));
        //返回关注的人数
        /*return ResponseData.create("successMsg", String.valueOf(followService.getFolloweeCount(
                hostHolder.getUser().getId(), EntityType.ENTITY_USER
        )));*/
        return ResponseData.create(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(
                hostHolder.getUser().getId(), EntityType.ENTITY_USER
        )), "success");
    }

    @ResponseBody
    @PostMapping(path = {"/unfollowers/user"})
    public ResponseData unfollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return ResponseData.create("successMsg", -1);
        }
        //true表示事务执行成功 false表示失败
        boolean ret = followService.unfollow(hostHolder.getUser().getId(),
                EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        //返回关注的人数
       /* return ResponseData.create("successMsg", String.valueOf(followService.getFolloweeCount(
                hostHolder.getUser().getId(), EntityType.ENTITY_USER
        )));*/
       return ResponseData.create(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(
               hostHolder.getUser().getId(), EntityType.ENTITY_USER
       )), "success");
    }

    /**
     * 关注问题
     * @param questionId
     * @return 当前用户的头像名字，当前关注数量
     */
    @ResponseBody
    @PostMapping("/followers/question")
    public ResponseData followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return ResponseData.create("successMsg", -1);
        }
        Question q =  questionService.getById(questionId);
        if (q == null) {
            return ResponseData.create("successMsg", 1);
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(),
                EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
            .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
            .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return ResponseData.create(ret ? 0 : 1, info, "success");
    }

    /**
     * 不关注问题
     * @param questionId
     * @return
     */
    @ResponseBody
    @PostMapping("/unfollowers/question")
    public ResponseData unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return ResponseData.create("successMsg", -1);
        }
        Question q =  questionService.getById(questionId);
        if (q == null) {//问题不存在
            return ResponseData.create("successMsg", 1);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(),
                EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>();

        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return ResponseData.create(ret ? 0 : 1, info, "success");
    }

    /**
     * 查询某用户的粉丝列表
     * @param model
     * @param offset
     * @param userId 查询的用户
     * @param limit
     * @return
     */
    @GetMapping(path = {"/user/{uid}/followers/{offset}/{limit}"})
    public String followers(Model model, @PathVariable("offset") int offset,
                            @PathVariable("uid") int userId,
                            @PathVariable("limit") int limit) {
        //查询某用户所有粉丝的id
        List<Integer> followersids = followService.getFollowers(EntityType.ENTITY_USER,
                userId, offset, limit);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(),followersids));
        } else {
            //未注册用户
            model.addAttribute("followers", getUsersInfo(0, followersids));
        }
        //获取粉丝数量
        model.addAttribute("followCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        //查询当前用户
        model.addAttribute("curUser", userService.selectUserById(userId));
        return "followers";
    }

    /**
     * 查询某用户的关注列表
     * @param model
     * @param offset
     * @param userId
     * @param limit
     * @return
     */
    @GetMapping(path = {"/user/{uid}/followees/{offset}/{limit}"})
    public String followees(Model model, @PathVariable("offset") int offset,
                            @PathVariable("uid") int userId,
                            @PathVariable("limit") int limit) {
        //查询某用户所有关注的id
        List<Integer> followeesIds = followService.getFollowees(userId,
                EntityType.ENTITY_USER, offset, limit);
        if (hostHolder.getUser() != null) {
            //查询关注的用户列表
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(),
                    followeesIds));
        } else {
            //未注册用户
            model.addAttribute("followees", getUsersInfo(0, followeesIds));
        }
        //获取关注数量
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        //查询某用户
        model.addAttribute("curUser", userService.selectUserById(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer uid : userIds) {
            User user = userService.selectUserById(uid);
            if(user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);//列表内所有用户
            //列表内的用户的回答问题的数量
            vo.set("commentCount", commentService.getCommentCountByEntityTypeAndUserId(EntityType.ENTITY_QUESTION, uid));
            //列表内用户的粉丝数量
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            //列表内用户的关注人数
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) { //当前用户是否已注册
                //判断当前用户是否关注了列表中的用户
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else { //未注册用户
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
