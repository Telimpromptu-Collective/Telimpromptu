import "material-symbols/outlined.css";
import styles from "../styles/components.module.css";

import { useCallback } from "react";

interface ErrorHostProps {
  errorList: ErrorData[];
  onClose: (id: string) => void;
}

interface ErrorProps {
  error: string;
  id: string;
  onClose: (id: string) => void;
}

export interface ErrorData {
  message: string;
  id: string;
}

export const ErrorHost: React.FC<ErrorHostProps> = (props) => {
  const { errorList, onClose } = props;

  return (
    <div className={styles.errorHost}>
      {errorList.map((error) => {
        return (
          <Error
            error={error.message}
            key={error.id}
            id={error.id}
            onClose={onClose}
          />
        );
      })}
    </div>
  );
};

const Error: React.FC<ErrorProps> = (props) => {
  const { error, id, onClose } = props;

  const onCloseError = useCallback(() => {
    onClose(id);
  }, [id]);

  return (
    <>
      {error !== "" && (
        <div className={styles.errorContainer}>
          <button className={styles.errorCloseButton} onClick={onCloseError}>
            <span className="material-symbols-outlined" onClick={onCloseError}>
              close
            </span>
          </button>
          <span className={styles.errorText}>{error}</span>
        </div>
      )}
    </>
  );
};
