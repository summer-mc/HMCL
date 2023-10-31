package summerworks.launcher.hmcl;

import javafx.scene.image.Image;
import org.jackhuang.hmcl.task.FileDownloadTask;
import org.jackhuang.hmcl.task.GetTask;
import org.jackhuang.hmcl.util.Logging;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

public class HMCLAddon {

    public static final boolean SHOW_CUSTOM_ANNOUNCEMNTS;
    public static final boolean SHOW_ACCOUNT_WARNING;
    public static final Path LOCAL_DIRECTORY;
    public static Optional<Image> CUSTOM_BG_IMAGE = Optional.empty();

    public static String CUSTOM_ANNOUNCEMENTS_TITLE;
    public static String CUSTOM_ANNOUNCEMENTS_TEXT;
    public static String CUSTOM_HELP_LINK;

    static {
        SHOW_CUSTOM_ANNOUNCEMNTS = true;
        SHOW_ACCOUNT_WARNING = false;
        LOCAL_DIRECTORY = new File(".hmcl").toPath();

        CUSTOM_ANNOUNCEMENTS_TITLE = "欢迎进入服务器";
        CUSTOM_ANNOUNCEMENTS_TEXT = "暂无新公告";
        CUSTOM_HELP_LINK = "https://www.baidu.com/s?wd=%E6%AC%A1%E5%85%83%E8%BE%B9%E5%A2%83";
    }

    public static void onlineInit() {
        URI uri = URI.create("https://dimension-edge-root-1252256770.cos.ap-guangzhou.myqcloud.com/launcher-addon.properties");

        try {
            GetTask task = new GetTask(uri.toURL());
            String response = task.run();

            Properties props = new Properties();
            props.load(new CharArrayReader(response.toCharArray()));

            CUSTOM_ANNOUNCEMENTS_TITLE = props.getProperty("ui.announcements.title", CUSTOM_ANNOUNCEMENTS_TITLE);
            CUSTOM_ANNOUNCEMENTS_TEXT = props.getProperty("ui.announcements.text", CUSTOM_ANNOUNCEMENTS_TEXT);
            CUSTOM_HELP_LINK = props.getProperty("ui.help.link", CUSTOM_HELP_LINK);

            String bgLink = props.getProperty("ui.background", "");

            if (!bgLink.isEmpty()) {
                CUSTOM_BG_IMAGE = Optional.of(new Image(bgLink, false));
            }
        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Failed to load launcher-addon.properties", e);
        }
    }

}
