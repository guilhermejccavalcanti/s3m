package br.ufpe.cin.mergers.handlers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import br.ufpe.cin.mergers.handlers.renaming.*;

@RunWith(Suite.class)
@SuiteClasses({ SafelyMergeSimilarRenamingHandlerTest.class, MergeSimilarRenamingHandlerTest.class, KeepSimilarMethodsRenamingHandlerTest.class,
                NoExtraFalsePositivesRenamingHandlerTest.class })
public class MethodAndConstructorRenamingAndDeletionHandlerTest {}