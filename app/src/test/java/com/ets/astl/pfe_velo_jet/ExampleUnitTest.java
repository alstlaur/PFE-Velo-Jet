package com.ets.astl.pfe_velo_jet;

import com.ets.astl.pfe_velo_jet.entity.Path;
import com.ets.astl.pfe_velo_jet.managers.FileManager;

import org.junit.Test;

import java.io.Console;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void readFile() throws Exception {
        FileManager manager = FileManager.getInstance();

        Path path = new Path();
        path.setName("Bob");
        path.setDistance(5.32f);
        path.setDate(new Date());

        System.out.println(manager.getPaths().toString());

        manager.savePath(path);

        System.out.println(manager.getPaths().toString());
    }
}