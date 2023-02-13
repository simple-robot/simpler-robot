import React from 'react';
import styles from './styles.module.css';


export default function YES({value}) {
    return <span className={`${styles.yesFont}`}>{value || 'yes'}</span>
}