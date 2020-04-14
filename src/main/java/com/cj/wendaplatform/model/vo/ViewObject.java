package com.cj.wendaplatform.model.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @date 2019/7/16
 * vo类
 */
public class ViewObject {

    private Map<String, Object> objs = new HashMap<>();

    public Object getObjs(String key) {
        return objs.get(key);
    }

    public void set(String key, Object value) {
        objs.put(key, value);
    }
}
