package com.mrzak34.thunderhack.util.captcha;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpRequest {
    private String url;
    private String postRaw;
    private Integer timeout = 60_000; // milliseconds
    private Integer maxBodySize = 0; // 0 = unlimited, in bytes
    private boolean followRedirects = true; // does not work now due to moving from JSOUP to ApacheHttpClient
    private boolean validateTLSCertificates = false;
    private Map<String, String> proxy = null; //new HashMap<String, String>() {{put("host", "192.168.0.168"); put("port", "8888");}};
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<String, String>()
    {{
        put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        put("Accept-Encoding", "gzip, deflate, sdch");
        put("Accept-Language", "ru-RU,en;q=0.8,ru;q=0.6");
    }};

    private boolean noCache = false;
    private Set<Integer> acceptedHttpCodes = new HashSet<Integer>() {{
        add(200);
    }};

    private String urlCuttedForHash;
    private String[] urlChangingParts = {
            "session_id",
            "sessionid",
            "timestamp",
    };

    public HttpRequest(String url) {
        this.url = url;
    }

    public boolean isValidateTLSCertificates() {
        return validateTLSCertificates;
    }

    public String getUrl() {
        return url;
    }

    public String getRawPost() {
        return postRaw;
    }

    public Map<String, String> getProxy() {
        return proxy;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getReferer() {

        if (headers.get("Referer") != null) {
            return headers.get("Referer");
        }

        return null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Set<Integer> getAcceptedHttpCodes() {
        return acceptedHttpCodes;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public Integer getMaxBodySize() {
        return maxBodySize;
    }

    public String getUrlWithoutChangingParts(String url) throws Exception {

        String newUrl = url = url.toLowerCase();

        for (String partToRemove : urlChangingParts) {

            String[] splitted = newUrl.split(partToRemove);

            if (splitted.length == 1) {
                continue;
            }

            String firstPiece = splitted[0];
            String secondPiece = splitted[1];

            if (splitted.length > 2) {

                String[] splitted2 = new String[splitted.length - 1];
                System.arraycopy(splitted, 1, splitted2, 0, splitted2.length);

                secondPiece = String.join(partToRemove, splitted2);
            }

            Integer breakpointPos = secondPiece.length();

            if (secondPiece.contains("?")) {
                breakpointPos = secondPiece.indexOf("?");
            } else if (secondPiece.contains("&")) {
                breakpointPos = secondPiece.indexOf("&");
            }

            newUrl = firstPiece + secondPiece.substring(breakpointPos);
        }

        if (newUrl.equals(url)) {
            return newUrl;
        } else {
            return getUrlWithoutChangingParts(newUrl);
        }
    }

    public void setRawPost(String post) {
        this.postRaw = post;
    }

    public void addToPost(String key, String value) throws UnsupportedEncodingException {
        if (postRaw == null) {
            postRaw = "";
        } else {
            postRaw += "&";
        }

        postRaw += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
        addHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setMaxBodySize(Integer maxBodySize) {
        this.maxBodySize = maxBodySize;
    }

    public void setReferer(String referer) {
        headers.put("Referer", referer);
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public void setValidateTLSCertificates(boolean validateTLSCertificates) {
        this.validateTLSCertificates = validateTLSCertificates;
    }

    public void setProxy(String proxyHost, Integer proxyPort) {
        this.proxy = new HashMap<>();
        this.proxy.put("host", proxyHost);
        this.proxy.put("port", String.valueOf(proxyPort));
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void addAcceptedHttpCode(Integer httpCode) {
        acceptedHttpCodes.add(httpCode);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }
}