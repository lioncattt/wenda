package com.cj.wendaplatform.dao;

import com.cj.wendaplatform.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author cj
 * @date 2019/7/16
 * 对用户进行增删查改
 */
@Mapper
public interface UserMapper {
    String TABLE_NAME = "user";
    String INSERT_FIELDS = " name, password, salt, head_url, phone ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 插入一个用户
     * @param user
     * @return 当前用户id
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{name}, #{password} , #{salt} ,#{headUrl}, #{phone}) "})
    int addUser(User user);

    /**
     * 根据id查询某用户
     * @param id
     * @return User
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            "where id = #{id}"})
    User selectById(Integer id);

    /**
     * 更新某个用户，返回对应记录的主键id
     * @param user
     * @return userId
     */
    @Update({"update ", TABLE_NAME, "set password = #{password} where id = #{id} " })
    int updatePassword(User user);

    /**
     * 通过用户名字查询某个用户
     * @param name
     * @return User
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            "where name = #{name}"})
    User selectByName(String name);


    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            "where phone = #{phone}"})
    User selectByPhone(String phone);
}
