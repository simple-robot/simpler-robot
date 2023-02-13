import React from 'react';
import styles from './styles.module.css';


export default function NO({value}) {
    return <span className={`${styles.noFont}`}>{value || 'no'}</span>
}