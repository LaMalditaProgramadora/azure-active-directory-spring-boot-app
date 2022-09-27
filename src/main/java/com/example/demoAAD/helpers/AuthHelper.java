package com.example.demoAAD.helpers;

import com.example.demoAAD.dto.AuthDto;
import com.microsoft.aad.msal4j.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class AuthHelper {

    public static ConfidentialClientApplication getConfidentialClientInstance() throws MalformedURLException {
        ConfidentialClientApplication confClientInstance = null;
        try {
            final IClientSecret secret = ClientCredentialFactory.createFromSecret(Config.SECRET);
            confClientInstance = ConfidentialClientApplication.builder(Config.CLIENT_ID, secret)
                    .authority(Config.AUTHORITY).build();
        } catch (final Exception ex) {
            throw ex;
        }
        return confClientInstance;
    }

    public static void signIn(IdentityContextAdapter contextAdapter) throws AuthException, IOException, URISyntaxException, Exception {
        authorize(contextAdapter);
    }

    public static void signOut(IdentityContextAdapter contextAdapter) throws IOException, Exception {
        redirectToSignOutEndpoint(contextAdapter);
    }

    public static void redirectToSignOutEndpoint(IdentityContextAdapter contextAdapter) throws IOException, Exception {
        contextAdapter.setContext(null);
        String redirect = String.format("%s%s%s%s", Config.AUTHORITY, Config.SIGN_OUT_ENDPOINT,
                Config.POST_SIGN_OUT_FRAGMENT, URLEncoder.encode(Config.REDIRECT_URI_SIG_OUT, "UTF-8"));
        BrowserHelper.openURL(redirect);
    }

    public static void authorize(IdentityContextAdapter contextAdapter) throws Exception {
        /*
        IdentityContextData context = contextAdapter.getContext();
        if (context.getAccount() != null) {
            logger.log(Level.INFO, "found account in session. trying to silently acquire token...");
            acquireTokenSilently(contextAdapter);
        } else {*/
        redirectToAuthorizationEndpoint(contextAdapter);
        /*}*/
    }

    public static void acquireTokenSilently(IdentityContextAdapter contextAdapter)
            throws AuthException {
        IdentityContextData context = contextAdapter.getContext();

        if (context.getAccount() == null) {
            String message = "Need to have account in session in order to authorize silently";
            throw new AuthException(message);
        }
        final SilentParameters parameters = SilentParameters.builder(Collections.singleton(Config.SCOPES), context.getAccount())
                .build();

        try {
            ConfidentialClientApplication client = getConfidentialClientInstance();
            client.tokenCache().deserialize(context.getTokenCache());
            IAuthenticationResult result = client.acquireTokenSilently(parameters).get();
            if (result != null) {
                context.setAuthResult(result, client.tokenCache().serialize());
            } else {
                throw new AuthException("Unexpected Null result when attempting to acquire token silently.");
            }
        } catch (final Exception ex) {
            String message = String.format("Failed to acquire token silently:%n %s", ex.getMessage());
            throw new AuthException(message);
        }
    }

    private static void redirectToAuthorizationEndpoint(IdentityContextAdapter contextAdapter) throws IOException, URISyntaxException, Exception {
        IdentityContextData context = contextAdapter.getContext();
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        context.setStateAndNonce(state, nonce);
        contextAdapter.setContext(context);

        final ConfidentialClientApplication client = getConfidentialClientInstance();
        AuthorizationRequestUrlParameters parameters = AuthorizationRequestUrlParameters
                .builder(Config.REDIRECT_URI, Collections.singleton(Config.SCOPES)).responseMode(ResponseMode.QUERY)
                .prompt(Prompt.SELECT_ACCOUNT).state(state).nonce(nonce).build();

        final String authorizeUrl = client.getAuthorizationRequestUrl(parameters).toString();
        BrowserHelper.openURL(authorizeUrl);
    }

    public static AuthDto processAADCallback(IdentityContextAdapter contextAdapter) throws AuthException {
        final IdentityContextData context = contextAdapter.getContext();

        try {
            validateState(contextAdapter);
            processErrorCodes(contextAdapter);
            String authCode = contextAdapter.getParameter("code");
            if (authCode == null) {
                throw new AuthException("Auth code is not in request!");
            }
            AuthorizationCodeParameters authParams = AuthorizationCodeParameters
                    .builder(authCode, new URI(Config.REDIRECT_URI)).scopes(Collections.singleton(Config.SCOPES))
                    .build();
            ConfidentialClientApplication client = AuthHelper.getConfidentialClientInstance();
            IAuthenticationResult result = client.acquireToken(authParams).get();
            context.setIdTokenClaims(result.idToken());
            AuthDto authDto = new AuthDto("", "", result.idToken(), result.accessToken(), null, null);

            validateNonce(context);
            context.setAuthResult(result, client.tokenCache().serialize());
            return authDto;
        } catch (final Exception ex) {
            contextAdapter.setContext(null);
            String message = String.format("Unable to exchange auth code for token:%n %s", ex.getMessage());
            throw new AuthException(message);
        }
    }

    private static void validateState(IdentityContextAdapter contextAdapter) throws AuthException {
        String requestState = contextAdapter.getParameter("state");
        IdentityContextData context = contextAdapter.getContext();
        String sessionState = context.getState();
        Date now = new Date();
        if (sessionState == null || requestState == null || !sessionState.equals(requestState)
                || context.getStateDate().before(new Date(now.getTime() - (Config.STATE_TTL * 1000)))) {
            throw new AuthException("ValidateState() indicates state param mismatch, null, empty or expired.");
        }
        context.setState(null);
    }

    private static void processErrorCodes(IdentityContextAdapter contextAdapter) throws AuthException {
        String error = contextAdapter.getParameter("error");
        String errorDescription = contextAdapter.getParameter("error_description");
        if (error != null || errorDescription != null) {
            throw new AuthException(String.format("Received an error from AAD. Error: %s %nErrorDescription: %s", error,
                    errorDescription));
        }
    }

    private static void validateNonce(IdentityContextData context) throws AuthException {
        String nonceClaim = (String) context.getIdTokenClaims().get("nonce");
        String sessionNonce = context.getNonce();
        if (sessionNonce == null || !sessionNonce.equals(nonceClaim)) {
            throw new AuthException("ValidateNonce() indicates that nonce validation failed.");
        }
        context.setNonce(null);
    }

}
