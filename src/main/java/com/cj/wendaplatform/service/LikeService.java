package com.cj.wendaplatform.service;

import com.cj.wendaplatform.model.Like;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/29
 * 点赞模块
 */
public interface LikeService {

    long getLikeCount(int entityType, int entityId);

    int getLikeStatus(int userId, int entityType, int entityId);

    long like(int userId, int entityType, int entityId);

    long disLike(int userId, int entityType, int entityId);

    int synLikeAndDisLikeToDataBase(List<Like> likeList);


}
