package com.bx.scorecounter;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test() {
        File file = new File("test/inner", "more/test.t");


        System.out.println(file.getName());
        System.out.println(file.getPath());
    }
}