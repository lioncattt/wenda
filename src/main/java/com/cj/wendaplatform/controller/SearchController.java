package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.model.EntityType;
import com.cj.wendaplatform.model.Question;
import com.cj.wendaplatform.model.vo.ViewObject;
import com.cj.wendaplatform.service.FollowService;
import com.cj.wendaplatform.service.QuestionService;
import com.cj.wendaplatform.service.SearchService;
import com.cj.wendaplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cj
 * @date 2019/8/8
 * 页面全局搜素问题
 */
@Controller
public class SearchController extends BaseController {

    private  final SearchService searchService;

    private  final FollowService followService;

    private  final UserService userService;

    private  final QuestionService questionService;

    @Autowired
    public SearchController(SearchService searchService, FollowService followService, UserService userService,
                            QuestionService questionService) {
       this.searchService = searchService;
       this.followService = followService;
       this.userService = userService;
       this.questionService = questionService;
    }

    @GetMapping(path = {"/search"})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            //通过solr查询出相应的问题
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count, "<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionService.getById(question.getId());
                ViewObject vo = new ViewObject();
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                vo.set("question", q);
                //获取问题关注数量
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userService.selectUserById(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);//回显关键词
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "result";
    }
}
