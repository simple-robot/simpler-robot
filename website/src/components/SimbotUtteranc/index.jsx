import React, {useEffect} from 'react';
import {useColorMode} from '@docusaurus/theme-common';

const elementId = 'inject-comments-by-utteranc'

export default function SimbotUtteranc() {
    function setScriptAttribute(script, configs) {
        script.setAttribute('id', elementId + '-script')
        script.setAttribute('src', 'https://utteranc.es/client.js');
        script.setAttribute('crossorigin', 'anonymous');
        script.setAttribute('async', 'true');
        script.setAttribute('repo', 'simple-robot-library/simbot3-website');
        script.setAttribute('issue-term', 'pathname');
        script.setAttribute('theme',  configs.theme);
        script.setAttribute('label', '💬评论');
        script.setAttribute('input-position', 'top');
        script.setAttribute('comment-order', 'desc');
    }

    function componentDidMount(isDark) {
        const anchor = document.getElementById(elementId)
        if (anchor != null) {
            anchor.innerHTML = ""
        }

        const script = document.createElement('script');
        setScriptAttribute(script, {
            theme: isDark ? 'github-dark' : 'github-light'
        })
        anchor.appendChild(script);
    }

    const {colorMode} = useColorMode();
    const isDark = colorMode === 'dark'

    useEffect(() => {
        componentDidMount(isDark);
    });


    return <div id={elementId} />;
}
