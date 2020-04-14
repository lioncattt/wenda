package com.cj.wendaplatform.async.handler;

import com.cj.wendaplatform.async.EventHandler;
import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventType;
import com.cj.wendaplatform.model.Message;
import com.cj.wendaplatform.model.User;
import com.cj.wendaplatform.service.MessageService;
import com.cj.wendaplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cj
 * @date 2019/8/6
 * 回答后发送邮件通知对方
 */
@Component
public class CommentHandler implements EventHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;


    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(12);//设置一个代理发送的userid
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        User user = userService.selectUserById(model.getActorId());

        message.setContent("用户" + user.getName()
                + "回答了你的问题, http://127.0.0.1/wenda/question/"
                + model.getExt("questionId") + "/0/50");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
