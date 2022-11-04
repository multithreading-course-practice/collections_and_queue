package org.example.stage1.part3;

import org.example.stage1.BaseSetTestSetup;
import org.example.stage1.part2.FineGrainedSet;
import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.jupiter.api.Test;

public class OptimisticSetTest extends BaseSetTestSetup {

    public OptimisticSetTest() {
        super(new OptimisticSet<>());
    }

    @Test
    public void testLincheck() {
        LinChecker.check(this.getClass(), new StressOptions());
    }
}