package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.model.Message;
import com.cj.wendaplatform.model.User;
import com.cj.wendaplatform.model.vo.ViewObject;
import com.cj.wendaplatform.response.ResponseData;
import com.cj.wendaplatform.service.MessageService;
import com.cj.wendaplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cj
 * @date 2019/7/25
 * 1.发送私信 跳转至站内信页面
 * 2. 查询站内信(包括对方user和对方会话的最新一条消息 头像上的未读消息，会话的消息条数)
 * 点击某一个会话
 * 3. 查询会话详情
 */
@Controller
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 发送一条msg
     * @param toName
     * @param content
     * @return
     * @throws BusinessException
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseData addMessage(@RequestParam("toName") String toName,
                                   @RequestParam("content") String content) throws BusinessException {
        try {
            if (hostHolder.getUser() == null) {
                return ResponseData.create("errMsg", 1, "toLogin");
            }
            User user = userService.selectUserByName(toName);
            if(user == null) {
                //要发送的目标用户不存在
                return ResponseData.create("successMsg", -1);
            }
            if(user.getId() == hostHolder.getUser().getId()) {
                return ResponseData.create("successMsg", -2);//发送目标方为自己
            }

            Message message = new Message();
            message.setContent(content);
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setCreatedDate(new Date());
            message.setHasRead(0);//默认未读

            int i = messageService.addMessage(message);
             if (i == 0) {
                 return ResponseData.create("errMsg", 0);//插入失败
             }
            return ResponseData.create("successMsg", 1);//插入成功
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.MESSAGE_INSERT_ERROR);
        }

    }

    /**
     * 查看站内信列表
     * @param model
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping(path = {"/list/{offset}/{limit}"})
    public String conversationList(Model model,
                                   @PathVariable("offset")Integer offset,
                                   @PathVariable("limit") Integer limit) {

        int localUserId = hostHolder.getUser().getId();
        List<ViewObject> conversations = new ArrayList<>();
        List<Message> conversationList = messageService
                .getConversationList(localUserId, offset, limit);
        for (Message msg : conversationList) {
            /*System.out.println("消息数"+msg.getId());*/
            ViewObject vo = new ViewObject();
            vo.set("conversation", msg);//存入每一条对话
            //返回对方的id 判断当前用户是否是某一方(接收/发送)，返回对应的一方user
            int targetId = msg.getToId() == localUserId ? msg.getFromId():msg.getToId();

            //查询对方信息
            User user = userService.selectUserById(targetId);
            vo.set("target", user);
            //查询有多少条未读信息
            vo.set("unread", messageService.getConversationUnreadCount(localUserId, msg.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations", conversations);
        return "letter";
    }

    /**
     * 查看会话详情
     * 如果未读消息条数>0, 点击会话详情后，则更新message为已读状态
     * @param model
     * @param conversationId 当前会话id
     * @param readNum 当前用户已读消息条数
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping(path = {"/detail/{conversationId}/{readNum}/{offset}/{limit}"})
    public String conversationDetail(Model model,
                                     @PathVariable("conversationId") String conversationId,
                                     @PathVariable("readNum") Integer readNum,
                                     @PathVariable("offset") Integer offset,
                                     @PathVariable("limit") Integer limit) throws BusinessException {
        if(readNum > 0) {
            try {

                //返回更新成功条数
               int flag = messageService.updateHasReadStatus(
                       hostHolder.getUser().getId(), conversationId);
//                System.out.println("flag =" + flag);
            } catch (Exception e) {
                throw new BusinessException(EmBusinessError.MESSAGE_UPDATE_STATUS_ERROR);
            }
        }
        List<Message> messages = messageService
                .getConversationDetail(conversationId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : messages) {
            ViewObject vo = new ViewObject();
            vo.set("message", message);
           //查询发送方用户信息, 显示在页面上
            User user = userService.selectUserById(message.getFromId());
            vo.set("user", user);
            vos.add(vo);
        }
        model.addAttribute("vos",vos);
        return "letterDetail";

    }


}
