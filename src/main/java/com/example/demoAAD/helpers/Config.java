package com.example.demoAAD.helpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class Config {

    private static Properties props = instantiateProperties();
    private static final String[] REQUIRED = {
        "aad.authority", "aad.clientId", "aad.secret",
        "aad.signOutEndpoint", "aad.postSignOutFragment",
        "app.stateTTL", "app.homePage", "app.redirectEndpoint",
        "app.sessionParam", "app.protect.authenticated", "aad.scopes",
        "aad.objectId", "aad.mfa", "app.redirectSignOutEndpoint"};
    private static final List<String> REQ_PROPS = Arrays.asList(REQUIRED);

    private static Properties instantiateProperties() {
        final Properties props = new Properties();
        try {
            props.load(Config.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (final IOException ex) {
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
        return props;
    }

    public static final String AUTHORITY = Config.getProperty("aad.authority");
    public static final String CLIENT_ID = Config.getProperty("aad.clientId");
    public static final String OBJECT_ID = Config.getProperty("aad.objectId");
    public static final String MFA = Config.getProperty("aad.mfa");
    public static final String SECRET = Config.getProperty("aad.secret");
    public static final String SCOPES = Config.getProperty("aad.scopes");
    public static final String SIGN_OUT_ENDPOINT = Config.getProperty("aad.signOutEndpoint");
    public static final String POST_SIGN_OUT_FRAGMENT = Config.getProperty("aad.postSignOutFragment");
    public static final Long STATE_TTL = Long.parseLong(Config.getProperty("app.stateTTL"));
    public static final String HOME_PAGE = Config.getProperty("app.homePage");
    public static final String REDIRECT_ENDPOINT = Config.getProperty("app.redirectEndpoint");
    public static final String REDIRECT_ENDPOINT_SIGN_OUT = Config.getProperty("app.redirectSignOutEndpoint");
    public static final String REDIRECT_URI = HOME_PAGE + REDIRECT_ENDPOINT;
    public static final String REDIRECT_URI_SIG_OUT = HOME_PAGE + REDIRECT_ENDPOINT_SIGN_OUT;
    public static final String SESSION_PARAM = Config.getProperty("app.sessionParam");
    public static final String PROTECTED_ENDPOINTS = Config.getProperty("app.protect.authenticated");
    public static final String ROLES_PROTECTED_ENDPOINTS = Config.getProperty("app.protect.roles");
    public static final String ROLE_NAMES_AND_IDS = Config.getProperty("app.roles");
    public static final String GROUPS_PROTECTED_ENDPOINTS = Config.getProperty("app.protect.groups");
    public static final String GROUP_NAMES_AND_IDS = Config.getProperty("app.groups");
    public static final String GRAPH_URL = "https://graph.microsoft.com";

    public static String getProperty(final String key) {
        String prop = null;
        if (props != null) {
            prop = Config.props.getProperty(key);
            if (prop != null) {
                return prop;
            } else if (REQ_PROPS.contains(key)) {
                System.exit(1);
                return null;
            } else {
                return "";
            }
        } else {
            System.exit(1);
            return null;
        }
    }

}
