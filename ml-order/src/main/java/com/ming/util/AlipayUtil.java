package com.ming.util;  
import com.alipay.easysdk.kernel.Config;  
  
/** @author Ming */  
public class AlipayUtil {  
  
    /** 应用ID */  
    private static final String APPID = "9021000153697736";  
    /** 异步通知接口（下单成功后支付宝回调） */  
    private static final String NOTIFY_URL = "http://6e3689ca.r31.cpolar.top/api/v1/order/prePayNotify";  
    /** 支付宝公钥 */  
    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlbTLkXthxH15BQhhA0We3qmVP2jvKyy01CuU8GcZr+4Yj4GAiyJuKIdwKu866PfaQyncJSwV63vL4EzUp3BhrKXP3q/naG//yGnvkhE+ets5BsY1/9VE6yotCgFOfJ1gVjwOkoVczDekvWvrDXBWvssYybzT2j9+e0TSa4APzWtQuF0ykftDrWHkI09XfQg2sj0hFadpN/r7ccI/No44WR8GVfI9bGKk1VDaUNRuhN3Zuf4RI2SIYB647LBgZ2A4EGUESJkG83DAu1idM/Uj/WB4zYpQFj4eoxsprwGxor1c5oxhUeS61hBeBCzTdQMGPjWkpHfIwUAo8rICDENhvQIDAQAB";  
    /** 应用私钥 */  
    private static final String MERCHANT_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCsFBkdJN/PhTZ9luW1I1irbl4vETE+WPbyGGRbJ2Kxi++cvwEZgqmjZMuYITOe7ZwnFdG1NWJqY5yYPA7S8vZO3Z4wultss1PHZPkCWDm+GMBSuBkAa6IPr4GDnKreSbSm65cGt4muGXWd0gdLyGhjxfY85yhadv9CjQseCJDbbKPIMWp2dF4hvwDZ9LH9D0CCnQkSKr6qJE2/Fi9BHD/jhbYencnPDk6d9NinnVv9CrERQ062egF3TFqz1wmhSwKACOcrcOSwabnjhwpygzZCcoP/12XT053WlWCS55ugc0RbyHch3FnuoohKECUjk669zXHebIGc3EV3+yMHfCHlAgMBAAECggEAe+cY0jBb6xu7SaDek5f4369fp7pUX9VFMrq3gB8S5E6jYJTzoL/BkAIiys6VUck7V7VRQ45F598DwWX1MpV5Hv5aSjHVZKaXG4qr9bE7GpLN4IHolrHphivUMMUdD2O47TvTiiWbwD1awCGb2UhbSgQfsmirWMA4Ol4+dAis7SDVQDmgjq9fxbDpD1ZjMIyrGGNuEfIhNgEWvuRwk15SunXZGOOM4V39d+ii74xpaOiBiFQ5ZbCB1Oat0GOAwfu+7DRjONm06jz8DcGxuVUMEN3RJNMATSeC4Or/Y8OlpO0jZ9e7u31HiZ5wIHtZjvUA0dloQF1Dw029vQZ0qsFvQQKBgQDYul8MaSFLEmNW3ctB0Ht3lgbS7oMJ+MCRlrkLe8rQVYjF0ZT4MMgU7hDUKhpg0ftIPYVQvH+Mf3mQhAJ408TyuHgGPtZ6G147fJVqaj0msTS+bcajSKrj+BWkPEVxddiJ0zdFb5un4SWLoWbcgQl78vGElQ+XjJClhTfoOGUKUQKBgQDLQoPvG/eJO3WlSR5PBFR863HVnD8GOXPEZ1z/AxZu2b60nzr9i3z/OPPb/0PGVn+iSLvDj4TQJ5Dk2a4CaQ+YuRGULlUx16498ZpE74l1cZp2kBszzkXPK7asRVgDgTtcY2ORGrzu4jla3UEFRARjAVBeT+cWk/wAf4d/LdIlVQKBgGDtSSEFy4wFl0P8QbEXioB7KmYfoZ9ODuGN5QhLvvLZrie6icNOHc2ugvxAMDfo8VbnnL30755oTHfjci+TlltvQM6aP9Wwc75rA2/qP5sUWaBo/BN+pl76TsN66RvLNqK9QdTeC2FTgjUmZBht5U334ygGekiu5un/4HcM8bNhAoGAAopDQdK0pjdjAfzG7y/bqm/6zTOVqgs/wh2UkO9F8X6xHmq6/v4mQr5AdehbvXfJQorsJcZ+X2ePLPz3arpLYpo1eQeXMvdCCy4gwmnH6vrPdlnYrS4Pu0YNk1uOfQS5bC8lyGNwJwAWvWb0cA9oiqAyk+sltSeGR69QTyuQa70CgYEAjmqBFYPBm4Lj1VPJ/CLqg+E4brnqs5UNLwI5ggPhW32MdbAnclUWblJwlZFaxuQKc3qqNqT+h6ZNaLh+nnQjX+bv0RsRQ/4RLX+I6bqKDekO5oTOtSs1s4V2EOdOQtWfODQM/hZFPz3EBMpezp3CEjGVJviWUAOzOovVoYdTJ3k=";  
    /** 单例的Alipay配置对象 */  
    private static volatile Config config;  
    /** 单例对外方法 */  
    public static Config getConfig() {  
        if (config == null) {  
            synchronized (AlipayUtil.class) {  
                if (config == null) {  
                    config = new Config();  
                    config.protocol = "https";  
                    config.gatewayHost = "openapi-sandbox.dl.alipaydev.com";  
                    config.signType = "RSA2";  
                    config.ignoreSSL = true;  
                    config.appId = APPID;  
                    config.alipayPublicKey = ALIPAY_PUBLIC_KEY;  
                    config.merchantPrivateKey = MERCHANT_PRIVATE_KEY;  
                    config.notifyUrl = NOTIFY_URL;  
                }  
            }  
        }  
        return config;  
    }  
}