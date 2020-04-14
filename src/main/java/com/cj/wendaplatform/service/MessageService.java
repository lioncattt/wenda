package com.cj.wendaplatform.service;

import com.cj.wendaplatform.model.Message;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/25
 */
public interface MessageService {

    int addMessage(Message message);

    List<Message> getConversationDetail(String conversationId, Integer offset, Integer limit);

    List<Message> getConversationList(Integer userId, Integer offset, Integer limit);

    int getConversationUnreadCount(int userId, String conversationId);

    int updateHasReadStatus(int userId, String conversationId);
}
