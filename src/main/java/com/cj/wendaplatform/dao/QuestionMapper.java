package com.cj.wendaplatform.dao;

import com.cj.wendaplatform.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/16
 * 对Question进行增删查改等业务
 */
@Mapper
public interface QuestionMapper {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    /**
     * 插入一个问题
     * @param question
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    /**
     * 查询最新的问题
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Question> selectLatestQuestions(@Param("userId") Integer userId,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id} "})
    Question getById(int id);

    /**
     * 更新Question中的commentCount评论数量
     * @param id  问题id
     * @param commentCount
     * @return
     */
    @Update({"update ", TABLE_NAME, "set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
}
