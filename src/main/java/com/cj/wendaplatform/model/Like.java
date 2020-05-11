package com.cj.wendaplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
public class Like{
    /**
     * 主键
     */
    @NonNull
    private Integer id;

    /**
     * 关联的实体类型
     */
    @NonNull
    private Integer entityType;

    /**
     * 关联的实体id
     */
    @NonNull
    private Integer entityId;

    /**
     * 创建时间
     */
    @NonNull
    private Date createdDate;

    /**
     * 点赞点踩的用户id集合，用逗号隔开
     */
    @NonNull
    private String userId;

    /**
     * 点赞或点踩的个数
     */
    @NonNull
    private Integer likeCount;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 0：取消点赞 1：点赞
     */
    @NonNull
    private Integer state;
}

