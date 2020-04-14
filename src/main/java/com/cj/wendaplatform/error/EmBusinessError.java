package com.cj.wendaplatform.error;

/**
 * @author cj
 * @date 2019/7/16
 * 构建自定义错误类型
 */
public enum EmBusinessError implements CommonError {
    //通用错误类型10001 一般是校验某个参数是否为空
    PARAMETER_VALIDATION_ERROR(10001, "传入参数不合法"),

    //未知错误
    UNKNOWN_ERROR(10002, "未知错误"),

    //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_PASSWORD_ERROR(20002, "用户密码错误"),
    USER_INSERT_TICKET_ERROR(20003, "生成用户对应ticket失败"),
    USER_CAPTCHA_ERROR(20004, "输入验证码有误或已过期"),
    USER_CAPTCHA_NOT_EXIST(20005, "验证码为空"),

    //30000开头为数据库异常
    DATABASE_ERROR(30001, "数据库异常"),

    //40000开头为阿里云短信通道异常
    ALIYUN_SERVER_ERROR(40001, "阿里云服务器异常"),

    //50000开头为问题模块异常
    QUESTION_INSERT_ERROR(50001, "问题插入失败"),

    //60000开头为评论模块异常
    COMMENT_INSERT_ERROR(60001, "评论插入失败或更新评论数失败"),

    //70000开头为私信模块异常
    MESSAGE_INSERT_ERROR(70001, "插入message失败"),
    MESSAGE_UPDATE_STATUS_ERROR(70002, "更新message为已读状态失败"),


    ;



    private int errCode;
    private String errMsg;

    private EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
