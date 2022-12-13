package com.mrzak34.thunderhack.util.captcha;


import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpHelper {

    public static HttpResponse download(HttpRequest request) throws Exception {

        BasicCookieStore cookieStore = new BasicCookieStore();

        if (request.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : request.getCookies().entrySet()) {

                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(getCookieDomain(request.getUrl()));

                cookieStore.addCookie(cookie);
            }
        }

        HttpClientBuilder httpClientBuilder;

        // if "https:" and don't need to check certificates
        if (!request.isValidateTLSCertificates() && request.getUrl().toLowerCase().charAt(4) == 's') {
            httpClientBuilder = HttpsClientBuilderGiver.INSTANCE.getHttpsClientBuilder();
        } else {
            httpClientBuilder = HttpClientBuilder.create();
        }

        if (request.getCookies() != null) {
            httpClientBuilder.setDefaultCookieStore(cookieStore);
        }

        if (request.getProxy() != null) {

            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(new HttpHost(
                    request.getProxy().get("host"),
                    Integer.parseInt(request.getProxy().get("port"))
            )));
        }

        HttpClient httpClient;

        if (request.isFollowRedirects()) {
            httpClient = httpClientBuilder.build();
        } else {
            httpClient = httpClientBuilder.disableRedirectHandling().build();
        }

        org.apache.http.HttpResponse response;
        HttpRequestBase apacheHttpRequest;

        if (request.getRawPost() == null) {
            apacheHttpRequest = new HttpGet(request.getUrl());
        } else {
            apacheHttpRequest = new HttpPost(request.getUrl());
            ((HttpPost) apacheHttpRequest).setEntity(new StringEntity(request.getRawPost(), "UTF-8"));
        }

        HttpClientContext context = HttpClientContext.create();

        apacheHttpRequest.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(request.getTimeout())
                .setConnectTimeout(request.getTimeout())
                .setSocketTimeout(request.getTimeout())
                .build());

        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            apacheHttpRequest.addHeader(header.getKey(), header.getValue());
        }

        response = httpClient.execute(apacheHttpRequest, context);
        String charset = "utf8";

        if (response.getHeaders("Content-Type").length != 0) {
            String[] charsetSplitted = response.getHeaders("Content-Type")[0].getValue().split("; charset=");

            if (charsetSplitted.length == 2) {
                charset = charsetSplitted[1];
            }
        }

        return new HttpResponse(
                InputOutput.INSTANCE.toString(response.getEntity().getContent(), charset, request.getMaxBodySize()),
                response,
                context
        );
    }

    private static String getCookieDomain(String url) {
        return "." + url.split("://")[1].split("/")[0];
    }

    // Костыль для получения HttpClient'а для HTTPS
    private enum HttpsClientBuilderGiver {
        INSTANCE;

        /**
         * Apache HttpClient which will work well with any (even invalid and expired) HTTPS
         * certificate.
         */
        public HttpClientBuilder getHttpsClientBuilder() throws NoSuchAlgorithmException, KeyManagementException {

            SSLContext sslcontext = SSLContext.getInstance("TLS"); // SSL and TLS - both work
//            SSLContext sslcontext = SSLContextexts.custom().useSSL().build(); // works, too

            sslcontext.init(new KeyManager[0], new TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
//            sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom()); // works, too

            SSLContext.setDefault(sslcontext);

            return HttpClients.custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(sslcontext)); //TODO Noop
        }

        private class HttpsTrustManager implements X509TrustManager {

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

        }
    }

    private enum InputOutput {
        INSTANCE;

        /**
         * The default buffer size to use.
         */
        private final int DEFAULT_BUFFER_SIZE = 1024 * 4;

        /**
         * Get the contents of an <code>InputStream</code> as a String using the specified character
         * encoding. <p> Character encoding names can be found at <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
         * <p> This method buffers the input internally, so there is no need to use a
         * <code>BufferedInputStream</code>.
         *
         * @param input    the <code>InputStream</code> to read from
         * @param encoding the encoding to use, null means platform default
         * @param bytesMax the amount of bytes you want to get, when exceeded, download will stop
         * @return the requested String
         * @throws NullPointerException if the input is null
         * @throws IOException          if an I/O jsonFieldParseError occurs
         */
        public String toString(InputStream input, String encoding, Integer bytesMax) throws IOException {

            StringWriter output = new StringWriter();
            InputStreamReader in = new InputStreamReader(input, encoding);

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            long count = 0;
            int n = 0;

            while (-1 != (n = in.read(buffer))) {

                output.write(buffer, 0, n);
                count += n;

                if (bytesMax > 0 && count >= bytesMax) {
                    break;
                }
            }

            return output.toString();
        }
    }
}