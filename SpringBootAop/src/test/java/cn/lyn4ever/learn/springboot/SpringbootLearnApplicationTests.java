package cn.lyn4ever.learn.springboot;

import cn.lyn4ever.learn.springboot.teacher.HighTeacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SpringbootLearnApplication.class)
@RunWith(SpringRunner.class)
public class SpringbootLearnApplicationTests {

    @Autowired
    HighTeacher highTeacher;

	@Test
	public void contextLoads() {
        highTeacher.setAge(12);
        highTeacher.teach("大家好，我们大家的体育老师，我们开始上体育课");
	}
}
