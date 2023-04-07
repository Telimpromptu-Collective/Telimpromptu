import React, { useCallback, useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import { Lobby } from "./components/Lobby";
import {
  isConnectionSuccessMessage,
  isMessage,
  isUserNameUpdateMessage,
} from "./messages";

export interface UserStatus {
  username: string;
  connected: boolean;
}

const App: React.FC = () => {
  //state
  const [socketUrl, setSocketUrl] = useState("");
  const [heartBeatInterval, setHeartBeatInterval] = useState<NodeJS.Timer>();
  const [userList, setUserList] = useState<UserStatus[]>([]);
  const [username, setUsername] = useState("");
  const [connected, setConnected] = useState(false);

  const { sendJsonMessage, lastJsonMessage } = useWebSocket(socketUrl);

  const onConnect = useCallback(
    (lobbyID: string, username: string) => {
      setSocketUrl(
        `ws://${window.location.hostname}:${window.location.port}/games/${lobbyID}`
      );
      sendJsonMessage({ type: "createUser", username: username });
      setHeartBeatInterval(
        setInterval(() => sendJsonMessage({ type: "heartbeatMessage" }), 5000)
      );
    },
    [setHeartBeatInterval, sendJsonMessage]
  );

  const onDisconnect = useCallback(() => {
    clearInterval(heartBeatInterval);
  }, [heartBeatInterval]);

  const onStartGame = useCallback(() => {}, []);

  useEffect(() => {
    if (isMessage(lastJsonMessage)) {
      if (isUserNameUpdateMessage(lastJsonMessage)) {
        setUserList(lastJsonMessage.statuses);
      } else if (isConnectionSuccessMessage(lastJsonMessage)) {
        setConnected(true);
        setUsername(lastJsonMessage.username);
      }
    }
  }, [lastJsonMessage]);

  return (
    <Lobby
      username={username}
      userList={userList}
      connected={connected}
      onConnect={onConnect}
      onStartGame={onStartGame}
    />
  );
};

export default App;
