import React, {useEffect} from "react";
import mermaid from "mermaid";
import styles from "./mermaid.module.css";

// see https://github.com/facebook/docusaurus/issues/1258#issuecomment-594393744

mermaid.initialize({
    startOnLoad: true
});

const Mermaid = ({ chart }) => {
    useEffect(() => {
        mermaid.contentLoaded();
    }, []);
    return <div className={`${styles.mermaid} mermaid`}>{chart}</div>;
};

export default Mermaid;
