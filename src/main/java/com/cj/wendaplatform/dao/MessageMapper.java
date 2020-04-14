package com.cj.wendaplatform.dao;

import com.cj.wendaplatform.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/25
 * 消息的增删查改
 */
@Mapper
public interface MessageMapper {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS =  " from_id, to_id, content, " +
            "has_read, conversation_id, created_date";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 插入一个消息
     * @param message
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId} , #{toId} , #{content} ,#{hasRead} ,#{conversationId} , #{createdDate} )"})
    int addMessage(Message message);

    /**
     * 查询双方对话的一则message
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    @Select({"select ", SELECT_FIELDS, "FROM ", TABLE_NAME,
            " WHERE conversation_id = #{conversationId} " +
                    "order by id desc limit #{offset}, #{limit}"})
    List<Message> selectConversationDetail(@Param("conversationId") String conversationId,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    /**
     * 查询某个用户的会话列表
     * 先按时间排倒序，注意要加上limit不然语句失效
     * 再分组，count 每组会话有多少条信息message
     * 注: 这里将count(id) as id 把id用来存放条数了
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversationList(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    /**
     * 查询每组会话有多少条是发给当前userId的消息并且是未读的
     * 即当前userId是toId，接收方
     * @param userId
     * @param conversationId
     * @return
     */
    @Select({"select count(id) from ", TABLE_NAME,
            " WHERE has_read=0 and to_id = #{userId} and conversation_id = #{conversationId} "})
    int selectConversationUnreadCount(@Param("userId") int userId,
                                      @Param("conversationId") String conversationId);

    /**
     * 设置接收方为当前用户的消息状态为已读
     * @param userId
     * @param conversationId
     * @return
     */
    @Update({"update ", TABLE_NAME, " set has_read = 1 " +
            " where to_id = #{userId} and conversation_id = #{conversationId} "})
    int updateHasRead(@Param("userId") int userId,
                      @Param("conversationId") String conversationId);
}
