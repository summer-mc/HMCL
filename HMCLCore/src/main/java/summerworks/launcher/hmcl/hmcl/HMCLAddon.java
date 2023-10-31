package summerworks.launcher.hmcl.hmcl;

import javafx.scene.image.Image;
import org.jackhuang.hmcl.game.Arguments;
import org.jackhuang.hmcl.task.FileDownloadTask;
import org.jackhuang.hmcl.task.GetTask;
import org.jackhuang.hmcl.util.Logging;

import java.io.CharArrayReader;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class HMCLAddon {

    public static final boolean SHOW_CUSTOM_ANNOUNCEMNTS;
    public static final boolean SHOW_ACCOUNT_WARNING;
    public static final Path LOCAL_DIRECTORY;
    public static Image CUSTOM_BG_IMAGE;

    public static String CUSTOM_ANNOUNCEMENTS_TITLE;
    public static String CUSTOM_ANNOUNCEMENTS_TEXT;
    public static String CUSTOM_HELP_LINK;

    private static String NIDE8_AUTH_URL = "https://auth.mc-user.com:233/{serverId}/";
    private static String NIDE8_AUTH_BASEURL = "https://auth.mc-user.com:233/";
    private static String NIDE8_SERVER_URL = "https://login.mc-user.com:233/{serverId}/";
    private static String NIDE8_REGISTER_URL = "https://login.mc-user.com:233/{serverId}/loginreg";
    private static String NIDE8_SERVER_JAR_URL = "https://login.mc-user.com:233/index/jar";

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
                CUSTOM_BG_IMAGE = new Image(bgLink, false);
            }

            NIDE8_AUTH_URL = props.getProperty("auth.nide8.links.auth", NIDE8_AUTH_URL);
            NIDE8_SERVER_URL = props.getProperty("auth.nide8.links.server", NIDE8_SERVER_URL);
            NIDE8_REGISTER_URL = props.getProperty("auth.nide8.links.register", NIDE8_REGISTER_URL);
            NIDE8_SERVER_JAR_URL = NIDE8_SERVER_URL.replaceAll("\\{serverId}/", "") + "index/jar";
            NIDE8_SERVER_JAR_URL = props.getProperty("auth.nide8.links.jar", NIDE8_SERVER_JAR_URL);

            NIDE8_AUTH_BASEURL = NIDE8_AUTH_URL.replaceAll("\\{serverId}/", "");

            checkNide8AuthJar();
        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Failed to load launcher-addon.properties", e);
        }
    }

    public static boolean isNide8AuthServer(String url) {
        String serverId = getNide8ServerId(url);

        if (serverId.length() != 32)
            return false;

        for (char c : serverId.toCharArray()) {
            if (c >= '0' && c <= '9')
                continue;
            if (c >= 'a' && c <= 'f')
                continue;
            if (c >= 'A' && c <= 'F')
                continue;
            return false;
        }

        checkNide8AuthJar();
        return true;
    }

    public static Arguments getNide8AuthArgs(String url) {
        checkNide8AuthJar();
        String serverId = getNide8ServerId(url);
        return new Arguments().addJVMArguments(
                "-javaagent:nide8auth.jar" + "=" + serverId,
                "-Dnide8auth.client=true");
    }

    public static Map<String, String> getNide8Links(String url) {
        String serverId = getNide8ServerId(url);
        Map<String, String> links = new HashMap<>();
        links.put("homepage", NIDE8_SERVER_URL.replaceAll("\\{serverId}", serverId));
        links.put("register", NIDE8_REGISTER_URL.replaceAll("\\{serverId}", serverId));
        return links;
    }

    private static String getNide8ServerId(String url)
    {
        if (!url.startsWith(NIDE8_AUTH_BASEURL))
            return "";
        return url.substring(NIDE8_AUTH_BASEURL.length(), url.endsWith("/") ? url.length() - 1 : url.length());
    }

    private static void checkNide8AuthJar()
    {
        File jar = new File(".minecraft", "nide8auth.jar");
        if (!jar.isFile()) {
            try {
                URI uri = URI.create(NIDE8_SERVER_JAR_URL);
                FileDownloadTask task = new FileDownloadTask(uri.toURL(), jar);
                task.run();
            } catch (Exception e) {
                throw new RuntimeException("Failed to download nide8auth.jar", e);
            }
        }
    }
}
