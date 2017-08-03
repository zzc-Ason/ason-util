package com.zzc.ason.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * author : Ason
 * createTime : 2017 年 08 月 02 日
 */
public class DoneTest {

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User("tom", 22);
    }

    @Test
    public void acquireValueByFieldNameTest() throws Exception {
        String value = Done.acquireValueByFieldName(user, "name");
        Assert.assertEquals("tom", value);
        String birthday = Done.acquireValueByFieldName(user, "birthday");
        PrintOut.object(birthday);
    }
}

class User {
    private String name;
    private Integer age;
    private Timestamp birthday;

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public User(String name, Integer age, Timestamp birthday) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }
}