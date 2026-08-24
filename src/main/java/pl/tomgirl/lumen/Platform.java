package pl.tomgirl.lumen;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

public enum Platform {
    UNIX(),
    MACOS() {
        @Override
        protected String[] getProcessArguments(String uri) {
            return new String[]{"open", uri};
        }
    },
    WINDOWS() {
        @Override
        protected String[] getProcessArguments(String uri) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", uri};
        }
    },
    UNKNOWN();

    public static final Platform CURRENT = getPlatform();

    public void open(String uri) { // TODO: Validate URI
        try {
            Process process = Runtime.getRuntime().exec(this.getProcessArguments(uri));
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
        } catch (IOException e) {
            Lumen.LOG.error("Couldn't open uri '{}'", uri, e);
        }
    }

    protected String[] getProcessArguments(String uri) {
        try {
            var parsed = new URI(uri);
            if ("file".equals(parsed.getScheme())) {
                uri = uri.replace("file:", "file://");
            }
        } catch (Exception ignored) {}

        return new String[]{"xdg-open", uri};
    }

    private static Platform getPlatform() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        return os.contains("win") ? Platform.WINDOWS
            : os.contains("mac") ? Platform.MACOS
            : os.contains("linux") || os.contains("unix") ? Platform.UNIX
            : Platform.UNKNOWN;
    }
}
