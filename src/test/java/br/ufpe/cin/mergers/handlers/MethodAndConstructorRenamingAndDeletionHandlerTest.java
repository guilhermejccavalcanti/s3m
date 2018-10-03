package br.ufpe.cin.mergers.handlers;

import br.ufpe.cin.mergers.handlers.singlerenaming.DefaultSingleRenamingHandlerTest;
import br.ufpe.cin.mergers.handlers.singlerenaming.KeepBothMethodsSingleRenamingHandlerTest;
import br.ufpe.cin.mergers.handlers.singlerenaming.MergeMethodsSingleRenamingHandlerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DefaultSingleRenamingHandlerTest.class,
        KeepBothMethodsSingleRenamingHandlerTest.class,
        MergeMethodsSingleRenamingHandlerTest.class,
})
public class MethodAndConstructorRenamingAndDeletionHandlerTest {
}
