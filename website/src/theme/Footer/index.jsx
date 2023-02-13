import React from 'react';
import Footer from '@theme-original/Footer';
import BrowserOnly from '@docusaurus/BrowserOnly';
import SimbotUtteranc from "../../components/SimbotUtteranc";

function InjectSimbotUtteranc() {
    return <BrowserOnly>
        {() => {
            const pathname = location.pathname
            if (
                (pathname.startsWith('/blog/') && pathname.length > 6)
                || (pathname.startsWith('/docs/') && pathname.length > 6)
            ) {
                return <SimbotUtteranc/>
            } else {
                return <></>
            }
        }}
    </BrowserOnly>
}

export default function FooterWrapper(props) {
    return (
        <>
            <InjectSimbotUtteranc/>
            <Footer {...props}/>
        </>
    );
}
