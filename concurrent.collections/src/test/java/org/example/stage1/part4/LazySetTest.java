package org.example.stage1.part4;

import org.example.stage1.BaseSetTestSetup;
import org.example.stage1.Set;
import org.example.stage1.part2.FineGrainedSet;
import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.jupiter.api.Test;

public class LazySetTest extends BaseSetTestSetup {

    public LazySetTest() {
        super(new LazySet<>());
    }

    @Test
    public void testLincheck() {
        LinChecker.check(this.getClass(), new StressOptions());
    }

}