import React from 'react';
import clsx from 'clsx';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import styles from './index.module.css';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import BrowserOnly from "@docusaurus/core/lib/client/exports/BrowserOnly";

const documentButtonTexts = [
    '⏱️ 从文档上手',
    '📚 开始阅读文档',
    '👉 通过文档开始学习',
    '🌸 万花丛中过 🌸',
    '👀 随便逛逛?',
]

const taglines = [
    '欢迎，我的朋友',
    '等你好久了。',
    '最近过的怎么样？',
    '有没有想我呢？',
    '有没有好好喝水？',
    '「当你为错过太阳而哭泣的时候，你也要再错过群星了。」',
    '「我们热爱这个世界时，才真正活在这个世界上。」',
    '「即使翅膀折了，心也要飞翔。」',
    '「你可以从外表的美来评论一朵花或一只蝴蝶，但你不能这样来评论一个人。」',
    '「我们把世界看错了，反说它欺骗我们。」',
    '「不要着急，最好的总会在最不经意的时候出现。」',
    '「杯中的水是亮闪闪的，海里的水是黑沉沉的。小道理可用文字说清楚，大道理却只有伟大的沉默。」',
    '「相信，即使它给你带来悲哀也要相信。」',
    '「真理之川从他的错误之沟渠中流过。」',
    '「小人得志，定难长久。德不配位，必有余殃。」',
    '「真理是严酷的，我喜爱这个严酷，它永不欺骗。」',
    '「花朵以芬芳熏香了空气，但它的最终任务，是把自己献给你。」',
    '你是否仍保有梦想？',
    '你是否仍留存希望？',
    '记得多喝水。',
    '站起来活动一下吧。',
    '看看远处的风景。',
    '做一下眼保健操。',
]


function random(elements) {
    return elements[Math.floor(Math.random() * elements.length)]
}

function r() {
    const arr = [true].concat(Array(100).fill(false))
    return random(arr)
}

const otherValue = [
    '三', '叁', '③', 'Ⅲ', '仨',
    ':3', '4-1', '5-2', '☘️', '👌',
    'v3', '³', '₃', '⑶', '③', '⒊', '㈢', 'ヾ',
    '(❁´◡`❁)', '(≧▽≦*)o', 'ψ(｀∇´)ψ', '(～￣▽￣)～', 'φ(゜▽゜*)', '♪(*^▽^*)', '（*＾-＾*）',
    'q(≧▽≦q)', 'φ(*￣0￣)', '(❁´◡`❁)', '(*^_^*)', '(*^▽^*)', '（*＾-＾*）',
    '(≧∀≦)ゞ', 'o(*￣▽￣*)ブ', '♪(^∇^*)', 'o(*￣︶￣*)o'
]

function HomepageHeader({day}) {
    const {siteConfig} = useDocusaurusContext();
    const date = new Date()
    // const day = date.getDay()
    const isAprilFools = (date.getMonth() + 1) === 4 && date.getDate() === 1;
    const isBirthday = (date.getMonth() + 1) === 8 && date.getDate() === 3;
    // console.log('Today: ' + date.getMonth() + '-' + date.getDate() + '(' + date + ')')
    // console.log('Today: ' + day)

    // styles['heroBannerBg' + day]
    // const day = 0
    // const style = styles['heroBannerBg' + day]
    // const style = bgStyles[day][0]
    // console.log(styles.heroBanner)

    const numberVersionValue = (isAprilFools || isBirthday || r()) ? random(otherValue) : '3'

    const heroBannerBg = styles['heroBannerBg' + day]
    // console.log("heroBannerBg: " + heroBannerBg)
    // console.log(heroBannerBg)

    return (
        <header className={clsx('hero hero--primary', styles.heroBanner, heroBannerBg)}>
            <div className="container">
                <h1 className="hero__title">{siteConfig.title} <small>{numberVersionValue}</small></h1>
                <p className="hero__subtitle">{random(taglines)}</p>
                <div className={styles.buttons}>
                    <Link
                        className="button button--secondary button--lg"
                        to="/docs">
                        {random(documentButtonTexts)}
                    </Link>
                </div>
                <div className={styles.buttons}>
                    <Link
                        className="button button--outline button--md"
                        to="https://github.com/simple-robot/simpler-robot">
                        {<><span className="bi-github"> GitHub</span> </>}
                    </Link>
                </div>
            </div>
        </header>
    );
}

export default function Home() {
    const day = new Date().getDay()

    const {siteConfig} = useDocusaurusContext();
    return (
        <Layout
            title={`欢迎! ${siteConfig.title}`}
            // title={`Hello from ${siteConfig.title}`}
            description="Simple Robot official website.">
            <BrowserOnly>
                {() => {
                    require('@site/static/baiduStatistic.js')
                }
                }
            </BrowserOnly>
            <HomepageHeader day={day}/>
            <main>
                <HomepageFeatures/>
            </main>
        </Layout>
    );
}
