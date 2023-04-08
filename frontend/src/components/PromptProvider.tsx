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

const Prompt: React.FC<PromptProps> = (props) => {
  const { id, description, onSubmitPrompt } = props;
  const textAreaRef = useRef<HTMLInputElement>(null);

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
    [textAreaRef, id, description, onSubmitPrompt]
  );

  return (
    <form className={styles.promptContainer}>
      <label className={`${styles.promptText}`}>{description}</label>
      <input
        className={`${styles.promptBox}`}
        type="textarea"
        ref={textAreaRef}
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
