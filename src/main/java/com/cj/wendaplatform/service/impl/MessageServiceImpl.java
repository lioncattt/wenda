package com.cj.wendaplatform.service.impl;

import com.cj.wendaplatform.dao.MessageMapper;
import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.model.Message;
import com.cj.wendaplatform.service.MessageService;
import com.cj.wendaplatform.util.SensitiveUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/25
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveUtil sensitiveUtil;

    /**
     * 添加一条私信
     * @return
     * @throws BusinessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addMessage(Message message)  {

        //过滤私信内容
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveUtil.filter(message.getContent()));

        return messageMapper.addMessage(message) > 0 ? message.getId() : 0;
    }

    /**
     * 查询某个会话详情
     * @param conversationId 会话id
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Message> getConversationDetail(String conversationId, Integer offset, Integer limit) {
        return messageMapper.selectConversationDetail(conversationId, offset, limit);
    }

    /**
     * 查询某个用户的会话列表
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Message> getConversationList(Integer userId, Integer offset, Integer limit) {
        return messageMapper.selectConversationList(userId, offset, limit);
    }

    @Override
    public int getConversationUnreadCount(int userId, String conversationId) {
        return messageMapper.selectConversationUnreadCount(userId, conversationId);
    }

    /**
     * 如果未读消息条数>0, 点击会话详情后，则更新message为已读状态
     * @param userId
     * @param conversationId 会话id
     * @return
     */
    @Override
    public int updateHasReadStatus(int userId, String conversationId) {
        return messageMapper.updateHasRead(userId, conversationId);
    }
}
