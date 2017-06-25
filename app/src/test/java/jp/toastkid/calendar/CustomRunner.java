package jp.toastkid.calendar;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

/**
 * @author toastkidjp
 * @see <a href="http://qiita.com/yuya_presto/items/d5cc27225a19e1971096">
 *     Android Studio＆Robolectricでテストを正しく＆高速に動かす</a>
 */
public class CustomRunner extends RobolectricTestRunner {

    /**
     * Initialize.
     * @param testClass
     * @throws InitializationError
     */
    public CustomRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        final String buildVariant = (BuildConfig.FLAVOR.isEmpty() ? "" : BuildConfig.FLAVOR + "/")
                        + BuildConfig.BUILD_TYPE;
        System.setProperty("android.package", BuildConfig.APPLICATION_ID);
        System.setProperty(
                "android.manifest",
                "build/intermediates/manifests/full/" + buildVariant + "/AndroidManifest.xml"
        );
        System.setProperty("android.resources", "build/intermediates/res/merged/");
        System.setProperty("android.assets", "build/intermediates/assets/" + buildVariant);
    }
}
