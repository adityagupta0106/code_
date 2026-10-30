package com.serviceplus.metadata.departmentConfig.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bsa")
public class BsaRegistrationProperties {
	
	private String encryptionKey = "DD59B38895900606CDE51E5B70C7B61A";
    private String encryptionIv = "DD56B11CDC5CF165";
    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArTtl7ppgFVWwwh61t/8liSTAKBnVAhP7a9zaldxZgVwRB970/5uLMYoBmB8OGmRAkK3z8R3N2zCASlNl98L+i6IYKwIzoUX7RahNe5bYmqLnb1O79L/2GvF/vLp7uwn0GFBQG9nFTOSDvv2x+d1IKV5FHmY31kEAi2uXpz7h5GXImed92uXqzz2fzzYMnCViSNoM+CkNTT4wEjWkW6hssOPm9fJ97AfvDIGRWLhpHwExACnh1bhmNTvSZo7AW1ex81Hov+CrtkROxWjKx8wKz5hQur6xeMFxnGN2b/HyG+g4LEzBMJMMvburgZPta/qGPJqJMPxwm1l1Blq8sVfH0QIDAQAB";
    private String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtO2XummAVVbDCHrW3/yWJJMAoGdUCE/tr3NqV3FmBXBEH3vT/m4sxigGYHw4aZECQrfPxHc3bMIBKU2X3wv6LohgrAjOhRftFqE17ltiaoudvU7v0v/Ya8X+8unu7CfQYUFAb2cVM5IO+/bH53UgpXkUeZjfWQQCLa5enPuHkZciZ533a5erPPZ/PNgycJWJI2gz4KQ1NPjASNaRbqGyw4+b18n3sB+8MgZFYuGkfATEAKeHVuGY1O9JmjsBbV7HzUei/4Ku2RE7FaMrHzArPmFC6vrF4wXGcY3Zv8fIb6DgsTMEwkwy9u6uBk+1r+oY8mokw/HCbWXUGWryxV8fRAgMBAAECggEAAQOjfqgJncuA1egdOpHjJqziPw1JPKuArCKC+S2/R+eWWpTam4gkwpImiureoNuI8AWf2A19vj7Vrk5Kg6EH706HKRaWTQvvigOGb+SRbflXcFuUlzhVR7GRO5VeRwuTrUIfQjtXZe3CpMYiXGL6iqkIeOJuEkuAc946DbvriGZ6wc4SmUxDCyw4URD+KNHKmQaX8c/YOrvubSH66bZV05W/pB8vOP6L8nWL7Mer7JdlbuKaeODzcDLYqohz66soSZMSfDyU2IsEQOkfVB+UsTblug1Vk9ITSJe6jzjWnmnPU/kCzeI4JRihQgTof4ZQ9x7TU1YE5WaWBrHb3tNqAQKBgQDIpANz4c1Kw7DQi5kCSzHNsb8cUt8stS8a4rvOial6JIg6OMJx5WXnIwnVyTS9Rv4rfYF2utUbB1F7tSOI4lKj4nVoxxLeEsWaUlrLWwiwIUaIx2FPeP5hEoM0V5sw3mjUuDtG/0RAtLCZGI9MrItG4rpvtzhkZb2ueTbodOGRAQKBgQDdB2k4ZMYwgCIT2obD1ofDv9rrzNRUCZnb/YjXKEqf0mATPWGqhSYOJq/dwDoDPNGAcZuulkNKB/gthtFXG3caX5atuxFZaoqlxaSl2FAyFTvi4vdDa1QZEHNL+x871gXbkwJ1mr6XsDfoMbJroaprgvO8AiTTnGdwDsJyW2pm0QKBgQCTqvLHlQas4f3+Xt/MKl+LTiijxfJvSyJSHVGwHu1W8hA5GZEylm07gjQ43g8sx1VF2Ocx5Sv26pnz/Esc8cV0zLUw2mNugk5g1//u7x8j0jnBbUyXWyuNcjySh2+2tdpdxQILrNAk91eKNeMxwUP4deJVWnnsmlzSTnA1AeEKAQKBgFSg0svR4gSN+v5JDJKNYJw1qFfirGWk63FM6QkglzoMbdbQCMYmhwSiEXJBeLTmo3yfgzFcvVTqKj8cQKkujfLeKK5KUwE+humj/D+3zDTw5mYf6va4cJHMN6ke9L2PW9zPtABdiJ5BzZYs+Ri25K6XjEUvOPSBDSXdsNviXqIRAoGAISsSsexLnUeVuE4fkaYimo9OoTe9D0oU7Zq4owq/YbCphTBHWSEvynU6txyEON36LVlrUJ90g3EWX2tJOc6G7OYOomOBHKIS8UAioF4rWTrhs0xpFW4ChoodkwAsXdevZ48GhcOx3dfegQNmezCvdr07NOQZZubYy4oqpMPmNM=";
    private Registration registration = new Registration();
    private SupportDesk supportDesk = new SupportDesk();
    private Pdf pdf = new Pdf();
    private Mail mail = new Mail();
    private Esb esb = new Esb();

    public static class Esb {
        private boolean enabled = true;
        private String provider = "BSA";
        private String operation = "REGISTER";
        private String serviceName = "Integration";
        private String contextPath = "/esb";
        private String invokeUrl = "http://localhost:9090/esb/b/invoke";
        private String url = "";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getContextPath() {
            return contextPath;
        }

        public void setContextPath(String contextPath) {
            this.contextPath = contextPath;
        }

        public String getInvokeUrl() {
            return invokeUrl;
        }

        public void setInvokeUrl(String invokeUrl) {
            this.invokeUrl = invokeUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Registration {
        private String url = "http://localhost:9093/external/mock-bsa/api/v1/integration/register";
        private String apiKey = "MOCK_BSA_SECRET_KEY_2026";
        private String stagingUrl = "https://staging.bsa.gov.in/api/v1";
        private String prodUrl = "https://api.bsa.gov.in/api/v1";
        private String endpointVersion = "v1";
        private int connectTimeoutMs = 5000;
        private int readTimeoutMs = 10000;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getStagingUrl() {
            return stagingUrl;
        }

        public void setStagingUrl(String stagingUrl) {
            this.stagingUrl = stagingUrl;
        }

        public String getProdUrl() {
            return prodUrl;
        }

        public void setProdUrl(String prodUrl) {
            this.prodUrl = prodUrl;
        }

        public String getEndpointVersion() {
            return endpointVersion;
        }

        public void setEndpointVersion(String endpointVersion) {
            this.endpointVersion = endpointVersion;
        }

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }
    }

    public static class SupportDesk {
        private String email = "bsa-support@nic.in";

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Pdf {
        private boolean maskCredentials = false;

        public boolean isMaskCredentials() {
            return maskCredentials;
        }

        public void setMaskCredentials(boolean maskCredentials) {
            this.maskCredentials = maskCredentials;
        }
    }

    public static class Mail {
        private String senderUrl = "http://localhost:8096/spSendEmail";

        public String getSenderUrl() {
            return senderUrl;
        }

        public void setSenderUrl(String senderUrl) {
            this.senderUrl = senderUrl;
        }
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public SupportDesk getSupportDesk() {
        return supportDesk;
    }

    public void setSupportDesk(SupportDesk supportDesk) {
        this.supportDesk = supportDesk;
    }

    public Pdf getPdf() {
        return pdf;
    }

    public void setPdf(Pdf pdf) {
        this.pdf = pdf;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Esb getEsb() {
        return esb;
    }

    public void setEsb(Esb esb) {
        this.esb = esb;
    }

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getEncryptionIv() {
		return encryptionIv;
	}

	public void setEncryptionIv(String encryptionIv) {
		this.encryptionIv = encryptionIv;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
