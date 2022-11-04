package org.example.stage1.part1;

import org.example.stage1.BaseSetTestSetup;
import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.jupiter.api.Test;


public class CoarseGrainedSetTest extends BaseSetTestSetup {

    public CoarseGrainedSetTest() {
        super(new CoarseGrainedSet<>());
    }

    @Test
    public void testLincheck() {
        LinChecker.check(this.getClass(), new StressOptions());
    }

}