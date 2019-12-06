package com.itheima.task;

/**
 * @author zhengws
 * @create 2019/4/3 21:36
 */
public class Task {
    private Integer id;
    private String name;

    public Task(Integer id, String name) {
        this.id = id;
        this.name = name;
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
}
