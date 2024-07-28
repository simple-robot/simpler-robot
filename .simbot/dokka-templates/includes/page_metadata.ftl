<#macro display>
    <title>${pageName}</title>
    <@template_cmd name="pathToRoot">
        <link href="${pathToRoot}images/logo-icon.svg" rel="icon" type="image/svg">
    </@template_cmd>
    <!-- 百度统计 -->
    <!--suppress ES6ConvertVarToLetConst -->
    <script>
        var _hmt = _hmt || [];
        (function () {
            const hm = document.createElement("script");
            hm.src = "https://hm.baidu.com/hm.js?045f67a122223db9432c7856fc942fad";
            const s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
    <!-- Google tag (gtag.js) -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=G-1JWL2DDTB4"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());

        gtag('config', 'G-1JWL2DDTB4');
    </script>
</#macro>
