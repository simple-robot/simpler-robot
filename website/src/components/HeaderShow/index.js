import React from "react";
import style  from './styles.module.css';

function PaddingImg({src, alt}) {
    return <img src={src} alt={alt} className={`${style.p_img}`} />
}

export default function HeaderShow({}) {
    const img = require("@site/static/img/logo.png").default

    return (
        <div align="center">
            <img src={img} alt="logo" className={`${style.headLogo}`} draggable="false" />
            <h2>
                ✨ simply-robot ✨
            </h2>
            <small>
                ~ simbot v3 ~
            </small>
            <br/>
            <span>
<a href="https://github.com/simple-robot/simpler-robot" target="_blank">GitHub</a>
</span> &nbsp; | &nbsp; <span>
<a href="https://gitee.com/simple-robot/simpler-robot" target="_blank">Gitee</a>
</span> <br/>
            <small> &gt; 感谢 <a href="https://github.com/ForteScarlet/CatCode"
                               target="_blank">CatCode</a> 开发团队成员制作的simbot logo &lt; </small>
            <br />
                <small> &gt; 走过路过，不要忘记点亮一颗⭐喔~ &lt; </small>
                <br/>
                <br/>
                <a href="https://github.com/simple-robot/simpler-robot/releases/latest"><PaddingImg alt="release"
                                                                                             src="https://img.shields.io/github/v/release/simple-robot/simpler-robot"/></a>
                <a href="https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/" target="_blank">
                    <PaddingImg alt="release"
                         src="https://img.shields.io/maven-central/v/love.forte.simbot/simbot-api"/></a>
                <PaddingImg alt="stars" src="https://img.shields.io/github/stars/simple-robot/simpler-robot"/>
                <PaddingImg alt="forks" src="https://img.shields.io/github/forks/simple-robot/simpler-robot"/>
                <PaddingImg alt="watchers" src="https://img.shields.io/github/watchers/simple-robot/simpler-robot"/>
                <PaddingImg alt="repo-size" src="https://img.shields.io/github/repo-size/simple-robot/simpler-robot"/>
                <PaddingImg alt="issues"
                     src="https://img.shields.io/github/issues-closed/simple-robot/simpler-robot?color=green"/>
                <PaddingImg alt="last-commit" src="https://img.shields.io/github/last-commit/simple-robot/simpler-robot"/>
                <PaddingImg alt="search-hit" src="https://img.shields.io/github/search/simple-robot/simpler-robot/simbot"/>
                <PaddingImg alt="top-language"
                     src="https://img.shields.io/github/languages/top/simple-robot/simpler-robot"/>
                <a href="https://github.com/simple-robot/simpler-robot">
                    <PaddingImg alt="copying"
                         src="https://img.shields.io/github/license/simple-robot/simpler-robot"/></a>
                <br/>
            <hr />
        </div>

);
}
