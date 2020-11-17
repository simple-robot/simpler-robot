/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     DingSecretUtil.java
 * Date  2020/8/9 下午4:55
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.utils;

import cn.hutool.core.codec.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 钉钉的加签操作
 * @author ForteScarlet <ForteScarlet></ForteScarlet>@163.com>
 * 2020/8/7
 */
public class DingSecretUtil {
    /**
     * 验证方法之二，加签
     * <br></br>
     * 加签后，把 timestamp和第一步得到的签名值拼接到URL中。 <br></br>
     * 例如: `https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&timestamp=XXX&sign=XXX`
     *
     * @see [https://ding-doc.dingtalk.com/doc./serverapi3/iydd5h](https://ding-doc.dingtalk.com/doc./serverapi3/iydd5h)
     *
     * @param timestamp 当前时间戳，单位是毫秒，与请求调用时间误差不能超过1小时
     * @param secret 密钥，机器人安全设置页面，加签一栏下面显示的SEC开头的字符串
     * @return sign
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */
    public static String secret(long timestamp, String secret) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(Base64.encode(signData), "UTF-8");
    }
}
