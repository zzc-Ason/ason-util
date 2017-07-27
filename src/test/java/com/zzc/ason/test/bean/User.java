package com.zzc.ason.test.bean;

/**
 * author : Ason
 * createTime : 2017 年 07 月 27 日
 */
public class User {

    private Integer id;
    public String name;
    private String gender;

    public User() {
    }

    public User(Integer id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + '\'' + ", gender='" + gender + '\'' + '}';
    }
}
