import styles from "../styles/components.module.css";

import React, { FormEvent, useCallback, useRef } from "react";
import { UserList } from "./UserList";
import { UserStatus } from "../messages";
import TeleprompterDrawing from "./TeleprompterDrawing";

interface LobbyProps {
  username?: string;
  connected: boolean;
  userList: UserStatus[];
  onConnect: (lobbyID: string, username: string) => void;
  onStartGame: () => void;
}

const buttonMessages = ["Fun times: Engage!","Play!","Letsa go!","Let's get gaming!!","Go!!!!!","Weeeeeeee!"]
const randomIndex = Math.floor(Math.random() * buttonMessages.length);

export const Lobby: React.FC<LobbyProps> = (props) => {
  const { username, connected, onConnect, onStartGame, userList } = props;

  const lobbyFieldRef = useRef<HTMLInputElement>(null);
  const userNameFieldRef = useRef<HTMLInputElement>(null);

  const onClickConnectButton = useCallback(
    (event: FormEvent) => {
      if (lobbyFieldRef.current && userNameFieldRef.current) {
        onConnect(lobbyFieldRef.current.value, userNameFieldRef.current.value);
      }
      event.preventDefault();
    },
    [onConnect]
  );

  return !connected ? (
    <div className={styles.connectionFormContainer}>
      <TeleprompterDrawing/>
      <form>
        <input className={styles.connectionFormInput} type={"text"} ref={userNameFieldRef} placeholder="Name" />
        <br/>
        <input className={styles.connectionFormInput} type={"text"} ref={lobbyFieldRef} placeholder="Lobby ID" />
        <br/>
        <button className={styles.coolButton} onClick={onClickConnectButton}>{buttonMessages[randomIndex]}</button>
        <br/>
        <div className={styles.connectTeleprompterButtonContainer}>
          <a className={styles.connectTeleprompterButton}>Go to teleprompter</a>
        </div>
      </form>
    </div>
  ) : (
    <div className={styles.userListContainer}>
      <UserList {...props} />
      <button onClick={onStartGame}>Start Game</button>
    </div>
  );
  
};
