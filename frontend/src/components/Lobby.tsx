import styles from "../styles/components.module.css";

import React, { FormEvent, useCallback, useRef } from "react";
import { UserList } from "./UserList";
import { UserStatus } from "../messages";

interface LobbyProps {
  username?: string;
  connected: boolean;
  userList: UserStatus[];
  onConnect: (lobbyID: string, username: string) => void;
  onStartGame: () => void;
}

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
      <form>
        <input type={"text"} ref={userNameFieldRef} placeholder="Name" />
        <input type={"text"} ref={lobbyFieldRef} placeholder="Lobby ID" />
        <button onClick={onClickConnectButton}>Connect</button>
      </form>
    </div>
  ) : (
    <div className={styles.userListContainer}>
      <UserList {...props} />
      <button onClick={onStartGame}>Start Game</button>
    </div>
  );
};
