package com.cj.wendaplatform.service;

import java.util.List;

/**
 * @author cj
 * @date 2019/8/2
 */
public interface FollowService {
    boolean follow(int userId, int entityType, int entityId);

    boolean unfollow(int userId, int entityType, int entityId);

    List<Integer> getFollowers(int entityType, int entityId,
                               int offset, int limit);

    List<Integer> getFollowees(int userId, int entityType, int offset, int count);

    long getFollowerCount(int entityType, int entityId);

    long getFolloweeCount(int userId, int entityType);

    boolean isFollower(int userId, int entityType, int entityId);
}
