package com.example.demoAAD.helpers;

import java.util.Arrays;

public class BrowserHelper {

    private static String[] browsers = {"x-www-browser", "google-chrome",
        "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
        "kazehakase", "mozilla"};

    public static void openURL(String url) throws Exception {
        try {  //attempt to use Desktop library from JDK 1.6+
            Class<?> d = Class.forName("java.awt.Desktop");
            d.getDeclaredMethod("browse",
                    new Class<?>[]{java.net.URI.class}).invoke(
                            d.getDeclaredMethod("getDesktop").invoke(null),
                            new Object[]{java.net.URI.create(url)});
        } catch (Exception ignore) {  //library not available or failed
            String osName = System.getProperty("os.name");
            try {
                if (osName.startsWith("Mac OS")) {
                    Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                            "openURL", new Class<?>[]{String.class}).invoke(null,
                                    new Object[]{url});
                } else if (osName.startsWith("Windows")) {
                    Runtime.getRuntime().exec(
                            "rundll32 url.dll,FileProtocolHandler " + url);
                } else { //assume Unix or Linux
                    String browser = null;
                    for (String b : browsers) {
                        if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
                            Runtime.getRuntime().exec(new String[]{browser = b, url});
                        }
                    }
                    if (browser == null) {
                        throw new Exception(Arrays.toString(browsers));
                    }
                }
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }
}
