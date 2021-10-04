package love.forte.simbot.component.kaiheila.api


public sealed interface KhlSender {

    public interface Sender : KhlSender, love.forte.simbot.api.sender.Sender
    public interface Setter : KhlSender, love.forte.simbot.api.sender.Setter
    public interface Getter : KhlSender, love.forte.simbot.api.sender.Getter

}




