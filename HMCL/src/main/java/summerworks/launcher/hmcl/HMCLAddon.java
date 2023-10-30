package summerworks.launcher.hmcl;

import org.jackhuang.hmcl.task.FileDownloadTask;
import org.jackhuang.hmcl.task.GetTask;
import org.jackhuang.hmcl.util.Logging;

import java.io.CharArrayReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;

public class HMCLAddon {

    public static final boolean SHOW_CUSTOM_ANNOUNCEMNTS = true;

    public static String CUSTOM_ANNOUNCEMENTS_TITLE = "欢迎进入服务器";
    public static String CUSTOM_ANNOUNCEMENTS_TEXT = "";

    static {
        URI uri = URI.create("https://dimension-edge-root-1252256770.cos.ap-guangzhou.myqcloud.com/launcher-addon.properties");

        try {
            GetTask task = new GetTask(uri.toURL());
            String response = task.run();

            Properties props = new Properties();
            props.load(new CharArrayReader(response.toCharArray()));

            CUSTOM_ANNOUNCEMENTS_TITLE = props.getProperty("ui.announcements.title", CUSTOM_ANNOUNCEMENTS_TITLE);
            CUSTOM_ANNOUNCEMENTS_TEXT = props.getProperty("ui.announcements.text", CUSTOM_ANNOUNCEMENTS_TEXT);

        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Failed to load launcher-addon.properties", e);
        }
    }

}
