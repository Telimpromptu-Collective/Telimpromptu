import styles from "../styles/components.module.css";
import React, { FormEvent, useCallback, useRef } from "react";
import useWebSocket from "react-use-websocket";

export interface IPromptProviderProps {
  player?: string;
  lobbyId: string;
}

interface IPromptProps {
  promptText: string;
}

export const PromptProvider: React.FC<IPromptProviderProps> = (props) => {
  const { lobbyId } = props;
  const { sendJsonMessage, lastJsonMessage } = useWebSocket(
    `ws://${window.location.hostname}:${window.location.port}/games/${lobbyId}`
  );
  return <Prompt promptText="test" />;
};

const Prompt: React.FC<IPromptProps> = (props) => {
  const { promptText } = props;
  const textAreaRef = useRef<HTMLInputElement>(null);

  const onSubmit = useCallback(
    (event: FormEvent) => {
      if (textAreaRef.current) {
        textAreaRef.current.value += "test";
      }

      event.preventDefault();
    },
    [textAreaRef]
  );
  return (
    <form onSubmit={onSubmit} className={styles.promptContainer}>
      <label className={`${styles.promptText}`}>{promptText}</label>
      <input
        className={`${styles.promptBox}`}
        type="textarea"
        ref={textAreaRef}
      />
      <input
        className={`${styles.promptButton}`}
        type="submit"
        value="Submit"
      />
    </form>
  );
};
