package cn.lyn4ever.learn.springboot.teacher;

import org.springframework.stereotype.Component;

/**
 * 教师类
 */
@Component
public class HighTeacher {
    private String name;
    private int age;

    public void teach(String content) {
        System.out.println("I am a teacher,and my age is " + age);
        System.out.println("开始上课");
        System.out.println(content);
        System.out.println("下课");
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
