/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.lovelycat.message;

import org.jetbrains.annotations.Nullable;

/**
 * @author ForteScarlet
 */
public class LovelyCatApiResult implements ApiResult<String> {

    private int code;
    private String result;
    private String data;

    @Override
    public String toString() {
        return "LovelyCatApiResult{" +
                "code=" + code +
                ", result='" + result + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Nullable
    @Override
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Nullable
    @Override
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
