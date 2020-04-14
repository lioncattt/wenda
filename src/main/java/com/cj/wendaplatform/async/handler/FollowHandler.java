package com.cj.wendaplatform.async.handler;

import com.cj.wendaplatform.async.EventHandler;
import com.cj.wendaplatform.async.EventModel;
import com.cj.wendaplatform.async.EventType;
import com.cj.wendaplatform.model.EntityType;
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
 * @date 2019/8/2
 * 关注事件处理类(关注后发送邮件给对方)
 * 包括关注问题，关注用户等等
 */
@Component
public class FollowHandler implements EventHandler {

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

        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            if (model.getType() == EventType.FOLLOW) {
                message.setContent("用户" + user.getName()
                        + "关注了你的问题, http://127.0.0.1/wenda/question/"
                        + model.getEntityId());
            } else if (model.getType() == EventType.UNFOLLOW) {
                message.setContent("用户" + user.getName()
                        + "取消关注了你的问题, http://127.0.0.1/wenda/question/"
                        + model.getEntityId());
            }

        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            if (model.getType() == EventType.FOLLOW) {
                message.setContent("用户" + user.getName()
                        + "关注了你, http://127.0.0.1/wenda/user/profile/" + model.getActorId());
            } else if (model.getType() == EventType.UNFOLLOW) {
                message.setContent("用户" + user.getName()
                        + "取消关注了你, http://127.0.0.1/wenda/user/profile/" + model.getActorId());
            }


        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW, EventType.UNFOLLOW);
    }
}
