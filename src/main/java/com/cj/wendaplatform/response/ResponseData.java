package com.cj.wendaplatform.response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @date 2019/7/16
 */
public class ResponseData {
    //表明对应请求返回处理结果 success 或 fail
    private String status;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    //若status = success, 则data内返回对应json数据
    //若status = fail, 则data内使用通用的错误码格式
    private Map<String, Object> data = new HashMap<String, Object>();



    //定义一个通用的创建方法 传一个参数默认就是成功
    public static ResponseData create(String key, Object obj) {
        return ResponseData.create(key, obj, "success");
    }

    public static ResponseData create(int code, String msg, String status) {
        ResponseData type = new ResponseData();
        type.setStatus(status);
        type.setData("successMsg", code);
        type.setData("msg", msg);
        return type;
    }

    public static ResponseData create(int code, Map<String, Object> map, String status) {
        ResponseData type = new ResponseData();
        type.setStatus(status);
        type.setData(map);
        type.setData("successMsg", code);
        return type;
    }

    //错误则需要自定义状态码
    public static ResponseData create(String key, Object obj, String status) {
        ResponseData type = new ResponseData();
        type.setStatus(status);
        type.setData(key, obj);
        return type;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }


}
