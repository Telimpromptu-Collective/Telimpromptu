import React, { useCallback, useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import { Lobby } from "./components/Lobby";
import {
  isConnectionSuccessMessage,
  isGameStartedMessage,
  isMessage,
  isUserNameUpdateMessage,
} from "./messages";

export interface UserStatus {
  username: string;
  connected: boolean;
}

enum GameState {
  lobbyDisconnected,
  lobbyConnected,
  gameActive,
  gameOver,
}

const App: React.FC = () => {
  //state
  const [socketUrl, setSocketUrl] = useState("");
  const [heartBeatInterval, setHeartBeatInterval] = useState<NodeJS.Timer>();
  const [userList, setUserList] = useState<UserStatus[]>([]);
  const [username, setUsername] = useState("");
  const [gameState, setGameState] = useState(GameState.lobbyDisconnected);

  const { sendJsonMessage, lastJsonMessage } = useWebSocket(socketUrl);

  const onConnect = useCallback(
    (lobbyID: string, username: string) => {
      setSocketUrl(
        `ws://${window.location.hostname}:${window.location.port}/games/${lobbyID}`
      );
      sendJsonMessage({ type: "createUser", username: username });
      setHeartBeatInterval(
        setInterval(() => sendJsonMessage({ type: "heartbeat" }), 5000)
      );
    },
    [setHeartBeatInterval, sendJsonMessage]
  );

  const onDisconnect = useCallback(() => {
    clearInterval(heartBeatInterval);
  }, [heartBeatInterval]);

  const onStartGame = useCallback(() => {
    sendJsonMessage({ type: "startGame" });
  }, []);

  useEffect(() => {
    if (isMessage(lastJsonMessage)) {
      if (isUserNameUpdateMessage(lastJsonMessage)) {
        setUserList(lastJsonMessage.statuses);
      } else if (isConnectionSuccessMessage(lastJsonMessage)) {
        setGameState(GameState.lobbyConnected);
        setUsername(lastJsonMessage.username);
      } else if (isGameStartedMessage(lastJsonMessage)) {
        setGameState(GameState.gameActive);
      }
    }
  }, [lastJsonMessage]);

  switch (gameState) {
    case GameState.lobbyDisconnected:
    case GameState.lobbyConnected:
      return (
        <Lobby
          username={username}
          userList={userList}
          connected={gameState === GameState.lobbyConnected}
          onConnect={onConnect}
          onStartGame={onStartGame}
        />
      );
    case GameState.gameActive:
      return <></>;
    case GameState.gameOver:
      return <></>;
  }
};

export default App;
