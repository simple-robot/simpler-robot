/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Testj.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

import love.forte.simbot.http.template.GetHttpRequest;
import love.forte.simbot.http.template.HttpRequest;
import love.forte.simbot.http.template.HttpResponse;
import love.forte.simbot.http.template.spring.RestTemplateHttpTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author ForteScarlet
 */
public class Testj {

    static final String url = "http://shibe.online/api/shibes?count=60&urls=true&httpsUrls=false";

    public static void main(String[] args) {
        RestTemplateHttpTemplate temp = new RestTemplateHttpTemplate(new RestTemplate());

        HttpRequest<String[]> request = new GetHttpRequest<>(url, String[].class);

        List<HttpResponse<?>> resps = temp.requestAll(true, request, request, request, request, request);

        for (HttpResponse<?> resp : resps) {
            for (String s : ((String[]) resp.getBody())) {
                System.out.println(s);
            }
        }

    }


}
