package com.cj.wendaplatform.error;

/**
 * @author cj
 * @date 2019/7/16
 * 定义一般错误格式
 */
public interface CommonError {
    public int getErrCode();//获取错误代码
    public String getErrMsg();//获取错误信息
    public CommonError setErrMsg(String errMsg);//设置错误信息
}
