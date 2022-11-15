package org.example.stage1.part2;

import org.example.stage1.BaseSetTestSetup;
import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.jupiter.api.Test;

public class FineGrainedSetTest extends BaseSetTestSetup {

    public FineGrainedSetTest() {
        super(new FineGrainedSet<>());
    }

    @Test
    public void testLincheck() {
        LinChecker.check(this.getClass(), new StressOptions());
    }
}