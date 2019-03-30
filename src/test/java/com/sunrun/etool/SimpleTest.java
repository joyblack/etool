package com.sunrun.etool;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SimpleTest {
    @Test
    public void t1(){
        String nowContent = "新时代   ";
        nowContent = nowContent.replaceAll("^[\\s]*[\\u4E00-\\u9FA5]*[\\d]*[\\u4E00-\\u9FA5]*[\\s]*[']*", "");
        System.out.println(nowContent.isEmpty());;
        System.out.println(nowContent);
    }
}
