package com.cj.wendaplatform.response;

/**
 * @author cj
 * @date 2019/7/16
 * 定义统一的返回格式
 */
public class CommonReturnType {
    //表明对应请求返回处理结果 success 或 fail
    private String status;

    //若status = success, 则data内返回对应json数据
    //若status = fail, 则data内使用通用的错误码格式
    private Object data;


    //定义一个通用的创建方法 传一个参数默认就是成功
    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    //错误则需要自定义状态码
    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
