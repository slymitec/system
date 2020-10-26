package indi.sly.system.kernel.test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class T20200807 {
    public static void main(String[] args) {
//        List<String> list = new ArrayList<>();
//        list = Collections.unmodifiableList(list);
//        //list.add("ee");
//
//        System.out.println(list.getClass().getName());

        try {
            Robot robot = new Robot();

            int i = 0;

            while (true) {

                robot.mouseWheel(1);


                Thread.sleep(150);
                //robot.delay(100);

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
