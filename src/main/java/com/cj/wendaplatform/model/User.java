package com.cj.wendaplatform.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * @author cj
 * @date 2019/7/16
 */
public class User {
    private Integer id;

    @NotEmpty(message = "用户名不能为空")//size>0且不能有空格
    @Length(min=3,max=12,message = "用户名长度3-12位")//用户名长度3-12
    private String name;

    @NotEmpty(message = "密码不能为空")//size>0
    @Length(min=5,max=10,message = "密码长度为5到10位")//密码长度为5到10
    private String password;
    private String salt;
    private String headUrl;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;//手机号

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
}
