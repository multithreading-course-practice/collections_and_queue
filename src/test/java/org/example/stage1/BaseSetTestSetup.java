package org.example.stage1;

import org.jetbrains.kotlinx.lincheck.annotations.Operation;
import org.jetbrains.kotlinx.lincheck.annotations.Param;
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen;

@Param(name = "int", gen = IntGen.class, conf = "1:100")
public abstract class BaseSetTestSetup {
    private final Set<Integer> set;

    protected BaseSetTestSetup(final Set<Integer> set) {
        this.set = set;
    }

    @Operation
    public boolean add(@Param(name = "int") Integer item) {
        return set.add(item);
    }

    @Operation
    public boolean remove(@Param(name = "int") Integer item){
        return set.remove(item);
    }

    @Operation
    public boolean contain(@Param(name = "int") Integer item){
        return set.contains(item);
    }
}
