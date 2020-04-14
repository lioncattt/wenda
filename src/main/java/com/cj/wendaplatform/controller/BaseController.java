package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @date 2019/7/16
 * 全局异常拦截处理
 */

public class BaseController {
    //定义exceptionHandler拦截exception
    //使用例子(已知错误直接抛出): if(user == null) throw new BusinessException(EmBusinessError.USER_NOT_EXIST)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex) {
        Map<String, Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException) { //当前异常是否为自定义异常
            BusinessException businessException = (BusinessException) ex;
            responseData.put("errCode", businessException.getErrCode());
            responseData.put("errMsg", businessException.getErrMsg());

        } else { //未知错误 如空指针错误
            ex.printStackTrace();
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EmBusinessError.UNKNOWN_ERROR.getErrMsg());

        }
        ResponseData responseData2 = new ResponseData();
        responseData2.setData(responseData);
        responseData2.setStatus("fail");
        return responseData2;
    }
}
