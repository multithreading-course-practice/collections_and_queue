package org.example.stage1.part5;

import org.example.stage1.BaseSetTestSetup;
import org.example.stage1.part2.FineGrainedSet;
import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.jupiter.api.Test;

public class NonblockingSetTest extends BaseSetTestSetup {

    public NonblockingSetTest() {
        super(new NonblockingSet<>());
    }

    @Test
    public void testLincheck() {
        LinChecker.check(this.getClass(), new StressOptions());
    }

}