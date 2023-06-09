import styles from "../styles/components.module.css";

import React, { FormEvent, useCallback, useRef } from "react";
import { PromptData } from "../messages";

interface PromptProviderProps {
  promptList?: PromptData[];
  onSubmitPrompt: (id: string, description: string) => void;
}

interface PromptProps {
  id: string;
  description: string;
  onSubmitPrompt: (id: string, description: string) => void;
}

export const PromptProvider: React.FC<PromptProviderProps> = (props) => {
  const { promptList, onSubmitPrompt } = props;

  return (
    <>
      {promptList &&
        promptList.map((promptData) => (
          <Prompt
            {...promptData}
            onSubmitPrompt={onSubmitPrompt}
            key={promptData.id}
          />
        ))}
    </>
  );
};

export const Prompt: React.FC<PromptProps> = (props) => {
  const { id, description, onSubmitPrompt } = props;
  const textAreaRef = useRef<HTMLTextAreaElement>(null);

  const onClickSubmitButton = useCallback(
    (event: FormEvent) => {
      const response = textAreaRef.current?.value;
      if (response && response !== "") {
        onSubmitPrompt(id, response);
        //containerRef.current?.remove();
        //textAreaRef.current.remove();
      }

      event.preventDefault();
    },
    [textAreaRef, id, onSubmitPrompt]
  );

  return (
    <form className={styles.promptContainer}>
      <label className={`${styles.promptText}`}>{description}</label>
      <textarea
        className={`${styles.promptBox}`}
        ref={textAreaRef}
        style={{ height: "100px" }}
      />
      <button
        className={`${styles.promptButton}`}
        onClick={onClickSubmitButton}
      >
        Submit
      </button>
    </form>
  );
};
