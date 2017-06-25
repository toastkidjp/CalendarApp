package jp.toastkid.calendar.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import jp.toastkid.calendar.CustomRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author toastkidjp
 */
@RunWith(CustomRunner.class)
public class DateTitleFactoryTest {

    @Test
    public void test() {
        String s1 = DateTitleFactory.makeDateTitle(RuntimeEnvironment.application, -1, 1);
        System.out.println(s1);
        assertTrue(s1.length() == 0);
        String s2 = DateTitleFactory.makeDateTitle(RuntimeEnvironment.application, 12, 1);
        System.out.println(s2);
        assertTrue(s2.length() == 0);
    }
}
