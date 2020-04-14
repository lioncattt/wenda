package com.cj.wendaplatform.controller.error;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author cj
 * @date 2019/8/9
 */
@Controller
public class HttpErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";


    @RequestMapping(value = ERROR_PATH)
    public String handleError() {
        return "errorpage/404";
    }



    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
