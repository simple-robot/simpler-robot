// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/*
标签
https://infima.dev/docs/components/badge
<span class="badge badge--primary">Primary</span>
 */

const currentVersion = '3.0.0-RC.3'

const firstYear = 2022
const currentYear = new Date().getFullYear()
const copyrightYear = currentYear === firstYear ? firstYear : firstYear + '-' + currentYear

const styles = ['primary', 'dark', undefined]
const navbarStyle = styles['dark']

const keywords = ['forte', 'forte-scarlet', 'simbot', 'simple-robot', '机器人', 'qq机器人', 'kook机器人', 'qq频道机器人', 'bot', 'simple robot', '法欧特']

// https://docusaurus.io/zh-CN
// https://mui.com/zh/getting-started/installation/

// npm run docusaurus docs:version 3.0.0-preview.0.1

async function createConfig() {
    const mdxMermaid = await import('mdx-mermaid')

    /** @type {import('@docusaurus/types').Config} */
    return {
        title: 'Simple Robot',
        tagline: '欢迎，我的朋友。',
        url: 'https://simbot.forte.love',
        baseUrl: '/',
        onBrokenLinks: 'warn', //  [ignore, log, warn, error, throw]
        onBrokenMarkdownLinks: 'warn',
        favicon: 'img/favicon.png',
        organizationName: 'Simple Robot', // Usually your GitHub org/username.
        projectName: 'simbot website', // Usually your repo name.

        // Even if you don't use internalization, you can use this field to set useful
        // metadata like html lang. For example, if your site is Chinese, you may want
        // to replace "en" with "zh-Hans".
        i18n: {
            defaultLocale: 'zh-Hans',
            locales: ['zh-Hans'],
        },

        plugins: [
            // https://github.com/flexanalytics/plugin-image-zoom
            'plugin-image-zoom',
            // https://docusaurus.io/zh-CN/docs/api/plugins/@docusaurus/plugin-pwa
            // [
            //     '@docusaurus/plugin-pwa',
            //     {
            //         // debug: true,
            //         offlineModeActivationStrategies: [
            //             'appInstalled',
            //             // 'standalone',
            //             'queryString',
            //         ],
            //         pwaHead: [
            //             {
            //                 tagName: 'link',
            //                 rel: 'icon',
            //                 href: '/img/logo.png',
            //             },
            //             {
            //                 tagName: 'link',
            //                 rel: 'manifest',
            //                 href: '/manifest.json',
            //             },
            //             // {
            //             //   tagName: 'meta',
            //             //   name: 'theme-color',
            //             //   content: 'rgb(37, 194, 160)',
            //             // },
            //             {
            //                 tagName: 'meta',
            //                 name: 'apple-mobile-web-app-capable',
            //                 content: 'yes',
            //             },
            //             {
            //                 tagName: 'meta',
            //                 name: 'apple-mobile-web-app-status-bar-style',
            //                 content: '#000',
            //             },
            //             {
            //                 tagName: 'link',
            //                 rel: 'apple-touch-icon',
            //                 href: '/img/logo.png',
            //             },
            //             // {
            //             //   tagName: 'link',
            //             //   rel: 'mask-icon',
            //             //   href: '/img/logo.png',
            //             //   color: 'rgb(37, 194, 160)',
            //             // },
            //             {
            //                 tagName: 'meta',
            //                 name: 'msapplication-TileImage',
            //                 content: '/img/logo.png',
            //             },
            //             {
            //                 tagName: 'meta',
            //                 name: 'msapplication-TileColor',
            //                 content: '#000',
            //             },
            //             // {
            //             //   tagName: 'meta',
            //             //   name: 'theme-color',
            //             //   content: 'rgb(37, 194, 160)',
            //             // },
            //         ],
            //     }],
            // '常见问题' 文档
            [
                'content-docs',
                {
                    id: 'faq',
                    path: 'faq',
                    routeBasePath: 'faq',
                    sidebarPath: require.resolve('./sidebars-faq.js'),
                    breadcrumbs: true,
                    showLastUpdateAuthor: true,
                    showLastUpdateTime: true,
                    // editCurrentVersion: true,
                    // editUrl: 'https://github.com/simple-robot-library/simbot3-website/blob/main/faq',
                    // lastVersion: 'current',
                    // versions: {
                    //     current: {
                    //         label: '1.0.0',
                    //         // path: '3.0.0.preview',
                    //         // banner: '',
                    //         badge: true
                    //     },
                    // },
                },
            ],
        ],
        presets: [
            [
                'classic',
                /** @type {import('@docusaurus/preset-classic').Options} */
                ({
                    docs: {
                        breadcrumbs: true,
                        sidebarPath: require.resolve('./sidebars.js'),
                        showLastUpdateAuthor: true,
                        showLastUpdateTime: true,
                        editUrl: 'https://github.com/simple-robot/simpler-robot/tree/v3-dev/website/docs',
                        lastVersion: 'current',
                        versions: {
                            current: {
                                label: currentVersion,
                                // path: '3.0.0.preview',
                                // banner: '',
                                badge: true
                            },
                        },
                        // https://github.com/sjwall/mdx-mermaid
                        // remarkPlugins: [mdxMermaid.default]
                        remarkPlugins: [[mdxMermaid.default, {
                            theme: { light: 'neutral', dark: 'forest' }
                        }]]
                    },


                    blog: {
                        blogTitle: 'Simbot Blog',
                        blogDescription: '与simbot相关的或者不相关的各种博客！',
                        showReadingTime: true,
                        editUrl: 'https://github.com/simple-robot/simpler-robot/tree/v3-dev/website/blog',
                        blogSidebarTitle: 'All posts',
                        blogSidebarCount: 'ALL',
                        feedOptions: {
                            type: 'all',
                            title: 'Simple Robot Blog',
                            description: 'Simple Robot官方博客喵',
                            copyright: `Copyright © ${copyrightYear} Forte Scarlet.`,
                            language: 'zh'
                        }
                    },

                    gtag: {
                        trackingID: 'G-E106DT6R3J',
                    },

                    sitemap: {
                        changefreq: 'weekly',
                        priority: 0.5,
                        ignorePatterns: ['/tags/**'],
                        filename: 'sitemap.xml',
                    },

                    theme: {
                        customCss: [
                            require.resolve('./src/css/custom.css'),
                        ],
                    },
                }),
            ],
        ],

        // https://github.com/easyops-cn/docusaurus-search-local#installation
        themes: [
            [
                require.resolve("@easyops-cn/docusaurus-search-local"),
                {
                    hashed: true,
                    language: ['zh'],
                    explicitSearchResultPath: true
                }
            ]
        ],

        themeConfig:
        /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
            ({
                metadata: [{
                    name: 'keywords', content: keywords.join(',')
                }],

                image: 'img/logo.png',


                // "@docsearch/react": "^3.0.0",
                // https://www.algolia.com/account/api-keys/all?applicationId=XJ6OXX8I3C
                // TODO algolia not ready yet.
                // algolia: {
                //   appId: 'VLLZ4JZE8Z',
                //
                //   // Public API key: it is safe to commit it
                //   apiKey: 'e60d9ee16618a0ad3a338ecc73cb840e',
                //
                //   indexName: 'doc',
                //
                //   // Optional: see doc section below
                //   contextualSearch: true,
                //
                //   // Optional: Specify domains where the navigation should occur through window.location instead on history.push.
                //   // Useful when our Algolia config crawls multiple documentation sites and we want to navigate with window.location.href to them.
                //   // externalUrlRegex: 'external\\.com|domain\\.com',
                //
                //   // Optional: Algolia search parameters
                //   // searchParameters: {},
                //
                //   // Optional: path for search page that enabled by default (`false` to disable it)
                //   searchPagePath: 'search',
                // },

                docs: {
                    sidebar: {
                        hideable: true,
                        autoCollapseCategories: true,
                    }
                },

                // 公告
                announcementBar: {
                    id: 'announcementBar-still_under_construction',
                    content: `文档尚不完全，但也够用 🍵 v3.0.0 发布在即，可前往 <a href="https://github.com/orgs/simple-robot/discussions" target="_blank"><b>社区</b></a> 交流或通过 <a href="https://github.com/simple-robot/simpler-robot/issues" target="_blank"><b>Issues</b></a> 反馈问题 😊😊😊‍`,
                    // backgroundColor: '#FFB906',
                    // backgroundColor: 'linear-gradient(0deg,red 50%,green 50%)',
                    // textColor: '#142F48',
                    isCloseable: true

                },

                // autoCollapseSidebarCategories: true,
                navbar: {
                    style: navbarStyle,
                    hideOnScroll: true,
                    title: 'Simple Robot',
                    logo: {
                        alt: 'Simple Robot Logo',
                        src: 'img/logo.png',
                    },
                    items: [
                        {
                            type: 'doc',
                            docId: 'index',
                            position: 'left',
                            label: '文档',
                        },
                        {to: '/blog', label: '博客', position: 'left'},
                        {to: '/faq', label: '常见问题', position: 'left'},
                        {href: 'https://github.com/simple-robot-library', label: '图书馆', position: 'left'},
                        {href: 'https://github.com/orgs/simple-robot/discussions', label: '社区', position: 'left'},
                        {href: 'https://docs.simbot.forte.love', label: 'API文档', position: 'left'},
                        ////////
                        {
                            type: 'docsVersionDropdown',
                            position: 'right',
                            docsPluginId: 'default',
                            // dropdownItemsAfter: [{to: '/versions', label: 'All versions'}],
                            dropdownActiveClassDisabled: true,
                        },
                        {
                            href: 'https://github.com/simple-robot/simpler-robot',
                            position: 'right',
                            className: 'bi-github',
                            'aria-label': 'GitHub',
                        }
                    ],
                },
                footer: {
                    style: 'dark',
                    links: [
                        {
                            title: '文档',
                            items: [
                                {
                                    label: '文档',
                                    to: '/docs',
                                },
                                {
                                    label: 'API文档',
                                    to: 'https://docs.simbot.forte.love',
                                },
                                {
                                    label: '常见问题',
                                    to: '/faq',
                                },

                            ],
                        },
                        {
                            title: "博客",
                            items: [
                                {
                                    label: 'Blog',
                                    to: '/blog'
                                },
                                {
                                    html: `<a class="footer__link-item" href="/blog/rss.xml">Rss订阅</a> <sup><i class="bi bi-rss-fill"></i></sup></a>`
                                },
                                {
                                    html: `<a class="footer__link-item" href="/blog/atom.xml">Atom订阅 <sup><i class="bi bi-rss-fill"></i></sup></a>`
                                },
                                {
                                    html: `<a class="footer__link-item" href="/blog/feed.json">Json订阅</a> <sup><i class="bi bi-rss-fill"></i></sup></a>`
                                },
                            ]
                        },
                        {
                            title: '指路牌',
                            items: [
                                {
                                    label: 'GitHub ',
                                    href: 'https://github.com/simple-robot/simpler-robot',
                                },
                                {
                                    label: 'Gitee镜像 ',
                                    href: 'https://gitee.com/ForteScarlet/simpler-robot',
                                },
                                {
                                    label: '组织库 🏢',
                                    href: 'https://github.com/simple-robot',
                                },
                                {
                                    label: '图书馆 📚',
                                    href: 'https://github.com/simple-robot-library',
                                },
                            ],
                        },
                        {
                            title: '交流&反馈',
                            items: [
                                {
                                    label: '问题反馈',
                                    href: 'https://github.com/simple-robot/simpler-robot/issues',
                                },
                                {
                                    label: '交流社区',
                                    href: 'https://github.com/orgs/simple-robot/discussions',
                                },
                            ],
                        },
                    ],
                    copyright: `Built with <a href="https://www.docusaurus.io/zh-CN/">Docusaurus</a>. <br> Copyright © ${copyrightYear} Forte Scarlet.`,
                },
                prism: {
                    additionalLanguages: ['java', 'kotlin', 'groovy', 'properties'],
                    theme: lightCodeTheme,
                    darkTheme: darkCodeTheme,
                    magicComments: [
                        // Remember to extend the default highlight class name as well!
                        {
                            className: 'theme-code-block-highlighted-line',
                            line: 'highlight-next-line',
                            block: {start: 'highlight-start', end: 'highlight-end'},
                        },
                        {
                            className: 'code-block-error-line',
                            line: 'This will error',
                            block: {start: 'error-start', end: 'error-end'},
                        },
                        {
                            className: 'code-block-success-line',
                            line: 'This is success',
                            block: {start: 'success-start', end: 'success-end'},
                        },
                    ],
                },
            })
    };
}



module.exports = createConfig;


/*
DocCardList
{
  docId: "component-overview/mirai/index"
  href: "/docs/component-overview/mirai/"
  label: "Mirai组件"
  type: "link"
}
 */
