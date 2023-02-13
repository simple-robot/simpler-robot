import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

const chicken = 4

function FeatureList() {
    const today = new Date().getDay()
    return [{
        title: '易于使用', // Svg: require('@site/static/img/kfc/Twister.jpeg').default,
        Svg: today === chicken ? function ({className}) {
            return (<a href='/img/kfc/Twister.jpeg' target='_blank' draggable={false}>
                    <img className={className} draggable={false}
                         src={require('@site/static/img/kfc/Twister_s1.jpg').default} alt={`疯狂星期四`}/>
                </a>)
        } : require('@site/static/img/features/3/偷懒.svg').default, description: (<>
                不管你信不信，但是它至少不会过多的拖累你。
            </>),
    }, {
        title: '组件协同', Svg: today === chicken ? function ({className}) {
            return (<a href='/img/kfc/TwisterBox.jpeg' target='_blank' draggable={false}>
                    <img className={className} draggable={false}
                         src={require('@site/static/img/kfc/TwisterBox_s1.jpg').default} alt={`奖励自己`}/>
                </a>)
        } : require('@site/static/img/features/3/看书.svg').default, description: (<>
                不同的组件之间可以更紧密的合作。<br/><small>在不同的平台中，也要卿卿我我！</small>
            </>),
    }, {
        title: (<span>
            <span>基于 </span>
            <a title='Copyright © 2022 JetBrains s.r.o. and the [Kotlin] logo are registered trademarks of JetBrains s.r.o.'
               href='https://kotlinlang.org/'>Kotlin</a>
  </span>), // Svg: require('@site/static/img/Kotlin.svg').default,
        Svg: require('@site/static/img/Kotlin_icon.svg').default,
        svgTitle: 'Copyright © 2022 JetBrains s.r.o. and the [Kotlin] logo are registered trademarks of JetBrains s.r.o.',
        description: (<>
                基础代码由 <a href='https://kotlinlang.org/'>Kotlin</a> 完成。提供全异步的API，并尽可能的提供 <a
                href='https://www.oracle.com/java/'>Java</a> 的友好兼容。
            </>),
    },];
}


function CodeSampleList() {
    const requireImg = (required, alt) => ({className}) => {
        return <img className={className} draggable={false} src={required.default} alt={alt}></img>
    }
    return [
        () => (
            <>
                <CodeShow title={"简单的应用"}
                          Img={requireImg(require('./simbot-application.png'), "simbot-application")}
                          description={<><small>简单而灵活</small></>} />
            </>
        ),

        () => (<CodeShow title={""}
                         Img={requireImg(require('./boot-listener.png'), "boot-listener")}
                         description={<><small>或者...更简单些?</small></>} />),
    ]
}

function Feature({Svg, title, svgTitle, description}) {
    return (<div className={clsx('col col--4')}>
            <div className="text--center" title={svgTitle}>
                <Svg className={`${styles.featureSvg} themedDocusaurus`} role="img" title={svgTitle}/>
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>);
}


function CodeShow({Img, title, svgTitle, description}) {
    return (<div className={clsx('col')}>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
            </div>
            <div className="text--center" title={svgTitle}>
                <Img className={`${styles.codeShowSvg} themedDocusaurus`} role="img" title={svgTitle}/>
            </div>
            <div className="text--center padding-horiz--md">
                <p>{description}</p>
            </div>
        </div>);
}

const CODE_SIMPLE_SAMPLE = `
suspend fun main() {
   val application = createSimpleApplication {
      installAll() // 注册当前环境下支持的组件和bot管理器
   }
   
   // 注册监听函数
   application.eventListenerManager.listeners {
      // 监听好友消息事件
      FriendMessageEvent { event ->
         event.reply("你也好")
      } onMatch { textContent == "你好" }
   }
   
   // 注册bot
   application.bots {
      // 注册bot
      register(
         File("bots/foo.bot").toResource()
            .toBotVerifyInfo(JsonBotVerifyInfoDecoder.create())
      ).start()
   }
   
   application.join()
   
}`
const CODE_SIMPLER_SAMPLE = `
@Listener
@Filter("你好")
suspend fun FriendMessageEvent.onFriendMessage() {
   reply("你也好")
}
`
const CONTINUOUS_SESSION = `
suspend fun main() {
    val application = createSimpleApplication {
        installAll() // 注册当前环境下支持的组件和bot管理器
    }
    
    // 注册监听函数
    application.eventListenerManager.listeners {
            listen(FriendMessageEvent) {
                match { textContent == "换个称呼" }
                process { event ->
                    event.useFriend { friend ->
                        friend.send("你希望我称呼你什么呢?")
                        // 等待下一个消息
                        val newName = inSession {
                            event.nextMessage(FriendMessageEvent).plainText
                        }
                        friend.send("好的，我会称呼你为「$newName」的")
                    }
                    
                }
            }
        }
        
    application.join()
}
`
const LISTENERS_REGISTER = `
suspend fun main() {
    val application = createSimpleApplication {
        installAll() // 注册当前环境下支持的组件和bot管理器
    }
    
    application.eventListenerManager.listeners {
            FriendMessageEvent { event ->
                event.friend().send("这是一次性监听哦~")
                // 注销当前监听函数
                this.listenerHandle.dispose()
            }
        }
    
    val handle = application.eventListenerManager.register(simpleListener(FriendMessageEvent) { event ->
        event.reply("这是5分钟后就消失的监听函数喔")
        EventResult.invalid() // return event result
    })
    
    // 5min后注销
    delay(5.minutes)
    handle.dispose()
    
    application.join()
}
`

const featureCodeInfos = [
    {
        code: CODE_SIMPLE_SAMPLE,
        title: '简单示例'
    }, {
        code: CODE_SIMPLER_SAMPLE,
        title: '更简单的'
    }, {
        code: CONTINUOUS_SESSION,
        title: '持续会话'
    }, {
        code: LISTENERS_REGISTER,
        title: '动态监听'
    }
]

export default function HomepageFeatures() {
    return (<>
            <HomepageFeaturesInternal/>

            {/*<CodeSamples/>*/}

            <FeatureCodes/>
        </>)
}

function FeatureCodes() {
    return (<section className={styles.features}>
        <div className="container">
            <div className="row">
                <div className={clsx('col col--1')} />
                <div className={clsx('col col--10')}>
                    <div className="padding-horiz--lg">
                        <Tabs groupId={`feature-code`}>
                            {featureCodeInfos.map(({code, title}) => {
                                return <TabItem value={title}>
                                    <CodeBlock language={`kotlin`}>
                                        {code.trimStart()}
                                    </CodeBlock>
                                </TabItem>
                            })}
                        </Tabs>
                    </div>
                </div>
                <div className={clsx('col col--1')} />
            </div>
        </div>
    </section>)
}



function HomepageFeaturesInternal() {
    return (<section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList().map((props, idx) => (<Feature key={idx} {...props} />))}
                </div>
            </div>
        </section>)
}

function CodeSamples() {
    return (<>
        <section className={styles.features}>
            <div className="container">
                {CodeSampleList().map((CodeSample) => (<div className="row"><CodeSample/></div>))}
            </div>
        </section>
    </>)
}


