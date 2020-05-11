package com.cj.wendaplatform.util;

/**
 * @author cj
 * @date 2019/7/29
 * 自定义唯一不重复的like key
 */
public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE"; //前缀
    private static String BIZ_DISLIKE = "DISLIKE"; //前缀
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";//所有事件的统一key值
    //粉丝 key值前缀
    private static String BIZ_FOLLOWER = "FOLLOWER";
    //关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";

    //注册验证码key
    private static String REG_CAPTCHA = "REG_CAPTCHA";

    //*用于模糊查询
    private static String PATTERN = "*";

    public static String getRegCaptcha(String phoneNumber) {
        return REG_CAPTCHA + SPLIT + phoneNumber;
    }

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) +
                SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType)
                + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    //某个实体的粉丝的key
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType)
                + SPLIT + String.valueOf(entityId);
    }

    //每个用户对某个实体的关注key
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId)
                + SPLIT + String.valueOf(entityType);
    }

    public static String getLikeAndDisLikeKeyPrefix() {
        return PATTERN + BIZ_LIKE + SPLIT + PATTERN;
    }

    public static String getBizLike() {
        return BIZ_LIKE;
    }

    public static String getFollowKeyPrefix() {
        return PATTERN + BIZ_FOLLOWEE + SPLIT + PATTERN;
    }



    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }
}
