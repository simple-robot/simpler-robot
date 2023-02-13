import React from 'react';
import styles from './styles.module.css';


export default function Label({type, href, children, title}) {
    type = type || 'primary'
    title = title || ''
    const sp = <span className={styles.label} title={title}><span className={`badge badge--${type}`}>{children}</span></span>
    if (href != null && href.length > 0) {
        return <a href={href}>{sp}</a>
    }

    return sp
}
