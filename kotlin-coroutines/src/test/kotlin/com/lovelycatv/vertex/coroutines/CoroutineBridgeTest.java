package com.lovelycatv.vertex.coroutines;

import com.lovelycatv.vertex.coroutines.jvm.CoroutineBridge;
import kotlin.test.junit5.JUnit5Asserter;
import kotlinx.coroutines.Dispatchers;
import org.junit.jupiter.api.Test;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-06-25 02:36
 */
public class CoroutineBridgeTest {
    @Test
    public void  awaitSuspendFunction() {
        String result1 = CoroutineBridge.awaitSuspendFunction(Dispatchers.getIO(), TestUtils.INSTANCE::noSuspendFunction);
        JUnit5Asserter.INSTANCE.assertEquals("NO_SUSPEND", "NO_SUSPEND", result1);

        long delayTimeMillis = 100;

        String result2 = CoroutineBridge.awaitSuspendFunction(Dispatchers.getIO(), c -> TestUtils.INSTANCE.delayFunction(delayTimeMillis, c));
        JUnit5Asserter.INSTANCE.assertEquals("NO_SUSPEND", "DELAYED_" + delayTimeMillis, result2);
    }
}
