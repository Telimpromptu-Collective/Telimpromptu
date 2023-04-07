import styles from "../styles/components.module.css";
import React, { FormEvent, useCallback, useRef } from "react";
import { UserStatus } from "../App";

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
      <UserList userList={userList} username={username} />
      <button onClick={onStartGame}>Start Game</button>
    </div>
  );
};

const UserList: React.FC<{
  userList: UserStatus[];
  username?: string;
}> = (props) => {
  const { userList, username } = props;

  return (
    <table>
      <th>Connected Users</th>
      <td>
        <ul className={styles.userList}>
          {userList.map((status) => (
            <li>
              {`${status.username}${status.connected ? "" : "-dc"}`}
              {status.username === username && <b> (you)</b>}
            </li>
          ))}
        </ul>
      </td>
    </table>
  );
};
