import React, { useState, useEffect } from "react";
import styles from "../styles/components.module.css";

const TeleprompterDrawing = () => {
  const [currentImage, setCurrentImage] = useState("/teleprompter1.png");

  useEffect(() => {
    const intervalId = setInterval(() => {
      setCurrentImage((prevImage) =>
        prevImage === "/teleprompter1.png"
          ? "/teleprompter2.png"
          : "/teleprompter1.png"
      );
    }, 500);

    return () => clearInterval(intervalId);
  }, []);

  return <img className={styles.telepompterDrawing} src={window.location.origin + currentImage} alt="Teleprompter Drawing" />;
};

export default TeleprompterDrawing;