import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';


function createData(name, v2, v3, remark) {
    return {name, v2, v3, remark};
}

// 基础部分
const rowsNormal = [
    createData('事件总类型', 'love.forte.simbot.api.message.events.MsgGet', 'love.forte.simbot.event.Event'),
    createData('监听函数定义注解', 'love.forte.simboot.annotation.Listener', '仅存在于boot模块下。'),
    createData(
        '事件监听注解',
        'love.forte.simbot.annotation.Listen\nlove.forte.simbot.annotation.Listens',
        'love.forte.simboot.annotation.Listen\nlove.forte.simboot.annotation.Listens',
        '仅存在于boot模块下，且不再强求此注解。'),
    createData(
        '事件过滤注解',
        'love.forte.simbot.annotation.Filter\nlove.forte.simbot.annotation.Filters',
        'love.forte.simboot.annotation.Filter\nlove.forte.simboot.annotation.Filters',
        '仅存在于boot模块下。'
    ),
    createData('监听函数', 'love.forte.simbot.listener.ListenerFunction', 'love.forte.simbot.event.EventListener'),
    createData('监听过滤器', 'love.forte.simbot.filter.ListenerFilter', 'love.forte.simbot.event.EventFilter'),
    createData('监听拦截器', 'love.forte.simbot.listener.ListenerInterceptor', 'love.forte.simbot.event.EventInterceptor'),
    createData('Bot', 'love.forte.simbot.bot.Bot', 'love.forte.simbot.Bot'),
    createData('Bot管理器', 'love.forte.simbot.bot.BotManager', 'love.forte.simbot.BotManager', '由 OriginBotManager 管理。'),
    createData('多组件', 'love.forte.simbot.Component', '2.x对多组件协同的支持并不友好。'),
    createData('用户类型', 'love.forte.simbot.api.message.containers.AccountInfo', 'love.forte.simbot.definition.User'),
    createData('好友', 'love.forte.simbot.api.message.results.FriendInfo', 'love.forte.simbot.definition.Friend'),
    createData('群聊', 'love.forte.simbot.api.message.containers.GroupInfo', 'love.forte.simbot.definition.Group'),
    createData('群成员', 'love.forte.simbot.api.message.results.GroupMemberInfo', 'love.forte.simbot.definition.Member'),
    createData('频道', 'love.forte.simbot.definition.Guild', 'v2中，以群聊（group）模拟频道概念。'),
    createData('子频道', 'love.forte.simbot.definition.Channel'),
    createData('消息体', '猫猫码字符串/猫猫码对象', 'Message对象各类实现', 'v3中Message具有序列化特性，猫猫码是否存在将不影响使用。'),
    createData('送信器', 'love.forte.simbot.api.sender.MsgSender', 'v3中的api操作都会整合到 definition 中，不再有独立的“送信器”概念。'),
    createData('Getter', 'love.forte.simbot.api.sender.Getter'),
    createData('Setter', 'love.forte.simbot.api.sender.Setter'),
    createData('Sender', 'love.forte.simbot.api.sender.Sender'),
    createData('事件上下文', 'love.forte.simbot.listener.ListenerContext', 'love.forte.simbot.event.EventProcessingContext', 'v2不区分事件和函数上下文。'),
    createData('监听函数上下文', 'love.forte.simbot.listener.ListenerContext', 'love.forte.simbot.event.EventListenerProcessingContext'),
    createData('瞬时作用域', 'love.forte.simbot.listener.MapScopeContext', 'love.forte.simbot.event.ScopeContext'),
    createData('全局作用域', 'love.forte.simbot.listener.MapScopeContext', 'love.forte.simbot.event.ScopeContext'),
    createData('持续会话作用域', 'love.forte.simbot.listener.ContinuousSessionScopeContext', 'love.forte.simbot.event.ContinuousSessionContext', 'v3中的 持续会话 在用法上与v2完全不同。'),
    createData('事件响应体', 'love.forte.simbot.listener.ListenResult', 'love.forte.simbot.event.EventResult'),
    createData('事件响应处理器', 'love.forte.simbot.processor.ListenResultProcessor', 'v3的响应体处理可以直接借助拦截器实现。'),
    createData('日志API', 'slf4j', 'slf4j', '这倒是没变化。'),
];

var strs = ''
for (let ele of rowsNormal) {
    strs += `| ${ele.name || ''} | ${ele.v2 || ''} | ${ele.v3 || ''} | ${ele.remark || ''} |\n`
}

console.log(strs)

// 启动部分
const rowsStarter = [
    createData('事件总类型', 'love.forte.simbot.api.message.events.MsgGet', 'love.forte.simbot.event.Event'),
];


export default function TableData() {
    return (
        <TableContainer component={Paper} sx={{maxHeight: 800}}>
            <Table sx={{minWidth: 800}} aria-label="sticky table" stickyHeader>
                <TableHead>
                    <TableRow>
                        <TableCell align="center">描述</TableCell>
                        <TableCell align="center">v2内容</TableCell>
                        <TableCell align="center">v3内容</TableCell>
                        <TableCell align="center">备注</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rowsNormal.map((row) => (
                        <TableRow
                            key={row.name}
                            sx={{'&:last-child td, &:last-child th': {border: 0}}}
                        >
                            <TableCell component="th" scope="row">{row.name}</TableCell>
                            <TableCell align="left">{row.v2}</TableCell>
                            <TableCell align="left">{row.v3}</TableCell>
                            <TableCell align="left">{row.remark}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    )
}