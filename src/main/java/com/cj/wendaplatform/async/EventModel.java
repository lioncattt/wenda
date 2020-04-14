package com.cj.wendaplatform.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @date 2019/8/1
 * 事件的抽象模型
 * 一个事件所要包含的属性
 *
 * 链式调用(set方法中返回this)：使得可重复调用当前的实例方法
 */
public class  EventModel {
    private EventType type;//该事件的类型
    private int actorId;//该事件的发起者
    private int entityType;//该事件关联的实体类型
    private int entityId;//该事件关联的实体id
    private int entityOwnerId;//接收事件的用户id

    //拓展字段 可自定义事件需要的额外的属性
    private Map<String, String> exts = new HashMap<>();

    public EventModel() { //提供默认的构造函数供spring创建当前对象

    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
