package com.leon.infrastructure.gatewayimpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import org.springframework.stereotype.Service;

@Service
public class AlipayLoginGatewayImpl {

    private final AlipayClient alipayClient;

    public AlipayLoginGatewayImpl() throws AlipayApiException {
        alipayClient = new DefaultAlipayClient(getAlipayConfig());
    }


    public String getAlipayOpenId(String code) throws AlipayApiException {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(code);
        request.setGrantType("authorization_code");
        AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
        System.out.println(response.getOpenId());
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return response.getOpenId();
    }

    public static void main(String[] args) throws AlipayApiException {
        AlipayLoginGatewayImpl impl = new AlipayLoginGatewayImpl();
        impl.getAlipayOpenId("16d8bb1a1af64bfd8a09a76269edRX35");
    }

    private static AlipayConfig getAlipayConfig() {
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoJX6VMb3cMQeZVV92EjBAmRm9cxGF+6xNfQgvUkKTYf2dbxGboNY5kTBMYCKx4+AYbqsWrR6IZboKM99Zp2ul0DgGWh/tomfx8Q3uBS582fRWU2TBi2OkKdW6IP13OrhlD1oyalloA863DmMPyBVLbsMStZe/fdIY+CAjtrvFBoOeaFjDtYK8XtbwT8QN1IestV0g47Iji8Xe5t0kCStEPubPHidgXFG9IcsFJBpzNVuEPHDjCdsFVzhZeEguX4rGR3Liy2RBhIXDEnyO4/giff0emvEvQUs9NnxPu4SvawIGpmHzisn+EQEWmPfgJbDummanwCyFUt9IpHfWQ/V7QIDAQAB";
        String privateKey  = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCHlXc7OxHlYLHd6x6pfPCN4Zw5WLACUJFcfDNI6P5dtxgMdynitVLBGxYOSDgCHpHxQTWSKV3pxVFhaXwJR/+9HBjxzt1WZJsp728P5mmQk8Hq1UaH5Prnjg6CTc6nRk7y9YItZIekCCJ+HO1XNpt/ZzkSsaNFsgZ2KvQijRjgFM6wiCVrNbrppSOi+kZTaQmtUrQ1ow9WWliTagjSu7XDvmwxASzH/ml6y9E8djVNwyrlHp7wvOAXLmRbxB8j2YTC1eTnm2IHl8FRg2+oU2ErfJGroI3q1x89GDIUJ9I6I/FYvpXhtXnKbyWq9dxJZ39M/Tk5XCUrt4JppsH7IKz9AgMBAAECggEABTg41dvpTXZSgqOZk5bOv/eOhxql9ADcB750TvtBR6RT1a8EusiWPf7n83bOSS6aGbqT8GONTQGep2xjLQxLiSE6yJxZIwlOe+fO08LgD4PQtFSvmW20RxPN9TA/o2rugSX4c/zpx+PUSBye+nuMsvPrW4/hJD69RvFSYr4h1gaIsjt+dmRbZutC6CXdLF21QTjGL6jJ5raoFaQTZKjSIWUdck/h6iGPjqPs6osseBRSFYreXKCXOAa7Dp5dMcc+VoP9LfN1KwtkZVIlEFyEZQTzJYdF6fTzFEvY0mGs0xGvRpunmHsybXkKw1HD5WpHZ5Q+krbTzAW48+wbO9a3IQKBgQC61TQSn9psjI3lGk4EPykjrPvlbhCRuCbehemDHOuLIPavc+qEeytwtf/xdV68SrJpVx3FOo4Ds52tBLY1tANPwpns+d5bPBn0kEWNQNVDoDbet9tr8QG7qN6vhVUzPIzhPGruhGGD6CZfsbBdwW9vuu0OhxAaSAFY0mbOKj4MWQKBgQC5xzf/hvyOqldJjV0U5zXkSJSPN7H9ajGRxAYmdcQdBHeyPobBQm0hibGjqXRQMl7EaN8SYZCWuGV7pG44+N+hsixfoi7CJw8OIN6rdaOU0G/+ZRDIJ5vakwO4g9BeLfAdk3ScFoaAA2qJqnrYAZwMin0gWeELg3f3TKp59dMBRQKBgDhbgcawc5nU2CSox6neR5GcZ6uwhM0ACLGNIYBMY/zm56LLS43E/biVHex/JCGWQrUl8zQAUsBpGQIM7s3KTEluATKLuPg6a/Xq4yAV037/wC10tnqcC5LJAMtfY/D9rd1vdS4Xvrake5bYKcROSkgg0ctVEJVs7QMXDK3Z7jeZAoGAftPfieXYo3js8Dy+0BdsdjWKnh9ytsN5pb7CSB1bzkePg7MmAefcYuoCyO1Vh65rfgDuUTkkIZEvTvKZgPF+kn/1xTAOZeOOZqHQAhPkoiKOYokSpOJNhzGYd64dYT5RT4+sKydBmUE7yVIGXVLz+qfM9noFpfuRudWwCGTnMVUCgYEAgDxdc+/9VOW5JaV5/qnhhoyq2VIMqD9iqtshNDLNG1uCijH2s/ajI+3i2qPqb1qsgQF0bsA21sl0CFu/IBT1sigT5FSMNfalTummHsuKvNp8w7zo+9R/ZesRTsYNDH8mC4l0o5ZxT7LGOMobp9LtMmDTPbb7RIX5AMOrpfzHEF8=";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId("2021005138649306");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}