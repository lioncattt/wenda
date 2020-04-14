package com.cj.wendaplatform.dao;

import com.cj.wendaplatform.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/24
 * 评论mapper 增删查改
 *
 */
@Mapper
public interface CommentMapper {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Update({"update ", TABLE_NAME, " set status = #{status} " +
            " where entity_id = #{entityId} and entity_type = #{entityType}"})
    void updateStatus(@Param("entityId") int entityId,
                      @Param("entityType") int entityType,
                      @Param("status") int status);

    /**
     * 根据entityid和entity类型查询出对应的评论
     * @param entityId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
    "where entity_id = #{entityId} and entity_type = #{entityType} " +
            "order by id desc limit #{offset},#{limit}"})
    List<Comment> selectByEntity(@Param("entityId") int entityId,
                                 @Param("entityType") int entityType,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    //获取评论数量
    @Select({"select count(id) from ", TABLE_NAME, "where entity_id =#{entityId} " +
            "and entity_type = #{entityType} " })
    int getCommentCount(@Param("entityId") int entityId,
                        @Param("entityType") int entityType);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
                " WHERE id = #{commentId} "})
    Comment getCommentById(int commentId);

    //根据用户id和实体类型查询该问题在某实体下的评论数量
    @Select({"select count(id) from ", TABLE_NAME, "where entity_type =#{entityType} " +
            "and user_id = #{userId} " })
    int getCommentCountByEntityTypeAndUserId(@Param("entityType") int entityType,
                                             @Param("userId") int userId);


}
