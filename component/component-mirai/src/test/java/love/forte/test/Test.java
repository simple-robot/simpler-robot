package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.message.results.GroupMemberList;
import love.forte.simbot.api.message.results.SimpleGroupInfo;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test implements SimbotProcess {
    public static void main(String[] args) throws InterruptedException {
        SimbotApp.run(Test.class, args);
    }

    @Override
    public void pre(@NotNull Configuration config) {
    }

    @Override
    public void post(@NotNull SimbotContext context) {
        Getter getter = context.getBotManager().getDefaultBot().getSender().GETTER;

        for (SimpleGroupInfo group : getter.getGroupList()) {
            System.out.println("群：" + group.getGroupName() + "("+ group.getGroupCode() +")");
            GroupMemberList memberList = getter.getGroupMemberList(group);
            for (GroupMemberInfo member : memberList) {
                System.out.print("\t");
                System.out.print("成员：" + member.getAccountRemarkOrNickname() + "("+ member.getAccountCode() +")");
                String title = member.getAccountTitle();
                if (title != null) {
                    System.out.print(" ["+ title +"]");
                }
                System.out.println();
            }
        }

    }
}
