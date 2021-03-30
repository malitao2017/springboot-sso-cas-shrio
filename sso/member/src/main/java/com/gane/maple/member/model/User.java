package com.gane.maple.member.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @Date 2020/4/9 20:09
 * @Created by 王弘博
 */
@Data
public class User implements Serializable {

    private String username;
    private String password;
}
