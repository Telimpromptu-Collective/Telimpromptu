import React, { useCallback, useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import { Lobby } from "./components/Lobby";
import {
  PromptData,
  UserStatus,
  isConnectionSuccessMessage,
  isErrorMessage,
  isGameStartedMessage,
  isMessage,
  isNewPromptsMessage,
  isPromptsCompleteMessage,
  isUserNameUpdateMessage,
} from "./messages";
import { Game } from "./components/Game";

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
  const [promptList, setPromptList] = useState<PromptData[]>([]);
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

  const onSubmitPrompt = useCallback(
    (id: string, response: string) => {
      setPromptList(promptList?.filter((prompt) => prompt.id !== id));
      sendJsonMessage({ type: "promptResponse", response: response, id: id });
    },
    [setPromptList, promptList, sendJsonMessage]
  );

  useEffect(() => {
    if (isMessage(lastJsonMessage)) {
      if (isUserNameUpdateMessage(lastJsonMessage)) {
        setUserList(lastJsonMessage.statuses);
      } else if (isConnectionSuccessMessage(lastJsonMessage)) {
        setGameState(GameState.lobbyConnected);
        setUsername(lastJsonMessage.username);
      } else if (isNewPromptsMessage(lastJsonMessage)) {
        console.log(
          `NEW MESSAGES ${lastJsonMessage.scriptPrompts[0].description}`
        );
        setPromptList([...promptList, ...lastJsonMessage.scriptPrompts]);
      } else if (isGameStartedMessage(lastJsonMessage)) {
        setGameState(GameState.gameActive);
        setUserList(lastJsonMessage.statuses);
      } else if (isPromptsCompleteMessage(lastJsonMessage)) {
        console.log("DONE");
        setGameState(GameState.gameOver);
      } else if (isErrorMessage(lastJsonMessage)) {
      }
    }
  }, [lastJsonMessage]);

  const commonProps = {
    username: username,
    userList: userList,
  };

  switch (gameState) {
    case GameState.lobbyDisconnected:
    case GameState.lobbyConnected:
      return (
        <Lobby
          {...commonProps}
          connected={gameState === GameState.lobbyConnected}
          onConnect={onConnect}
          onStartGame={onStartGame}
        />
      );
    case GameState.gameActive:
    case GameState.gameOver:
      return (
        <Game
          {...commonProps}
          gameOver={gameState === GameState.gameOver}
          promptList={promptList}
          onSubmitPrompt={onSubmitPrompt}
        />
      );
  }
};

export default App;
