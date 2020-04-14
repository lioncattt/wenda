package com.cj.wendaplatform.model;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * @author cj
 * @date 2019/7/25
 * 消息实体类
 * 制定一个conversationId： 规定插入时较小的id排在前面，较大的在后面
 * 让会话双方拥有共同的conversationId 查询某一个会话直接根据该id选出
 */
public class Message {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    @NotBlank(message = "消息不能为空")
    private String content;
    private Date createdDate;
    private Integer hasRead;//是否已读 0未读
    private String conversationId;//会话Id

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public String getConversationId() { //格式化传进来的conversationId,
        // 确保conversationId按该规则存入数据库
       if(fromId < toId) {
           return String.format("%d_%d", fromId, toId);
       }
       return String.format("%d_%d", toId, fromId);
    }


}
