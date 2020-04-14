package com.cj.wendaplatform.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * @author cj
 * @date 2019/7/16
 * 问题实体类
 */
public class Question {
    private Integer id;
    @NotEmpty(message = "标题不能为空")//size>0且不能有空格
    @Length(min=5,max=50,message = "标题长度要在5-50")//5-50
    private String title;
    private String content;
    private Integer userId;

    private Date createdDate;
    private Integer commentCount;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }



    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
