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
import { ErrorHost, ErrorData } from "./components/ErrorHost";
import { nanoid } from "nanoid";

enum GameState {
  lobbyDisconnected,
  lobbyConnected,
  gameActive,
  gameOver,
  // gameComplete?
}

const App: React.FC = () => {
  //state
  const [socketUrl, setSocketUrl] = useState("");
  const [heartBeatInterval, setHeartBeatInterval] = useState<NodeJS.Timer>();
  const [userList, setUserList] = useState<UserStatus[]>([]);
  const [promptList, setPromptList] = useState<PromptData[]>([]);
  const [errorList, setErrorList] = useState<ErrorData[]>([]);
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
  }, [sendJsonMessage]);

  const onSubmitPrompt = useCallback(
    (id: string, response: string) => {
      setPromptList((promptList) =>
        promptList?.filter((prompt) => prompt.id !== id)
      );
      sendJsonMessage({ type: "promptResponse", response: response, id: id });
    },
    [setPromptList, promptList, sendJsonMessage]
  );

  const onDismissError = useCallback(
    (id: string) => {
      setErrorList((errorList) => errorList.filter((error) => error.id !== id));
    },
    [errorList, setErrorList]
  );
  //testing

 /*
  useEffect(() => {
    setErrorList([
      { message: "1", id: nanoid() },
      { message: "2", id: nanoid() },
      { message: "3", id: nanoid() },
    ]);
  }, []);
  */

  useEffect(() => {
    if (isMessage(lastJsonMessage)) {
      if (isUserNameUpdateMessage(lastJsonMessage)) {
        setUserList(lastJsonMessage.statuses);
      } else if (isConnectionSuccessMessage(lastJsonMessage)) {
        setGameState(GameState.lobbyConnected);
        setUsername(lastJsonMessage.username);
      } else if (isNewPromptsMessage(lastJsonMessage)) {
        setPromptList([...promptList, ...lastJsonMessage.scriptPrompts]);
      } else if (isGameStartedMessage(lastJsonMessage)) {
        setGameState(GameState.gameActive);
        setUserList(lastJsonMessage.statuses);
      } else if (isErrorMessage(lastJsonMessage)) {
        setErrorList([
          ...errorList,
          { message: lastJsonMessage.message, id: nanoid() }, //unique id for each error because of react shenanigans
        ]);
      } else if (isPromptsCompleteMessage(lastJsonMessage)) {
        setGameState(GameState.gameOver);
      }
    }
  }, [
    lastJsonMessage,
    setGameState,
    setUserList,
    setUsername,
    setPromptList,
    promptList,
  ]);

  const commonProps = {
    username: username,
    userList: userList,
  };

  // Main game element, only one should be active. Lobby/Game/Teleprompter(TODO)
  let mainElement: React.ReactElement;
  switch (gameState) {
    case GameState.lobbyDisconnected:
    case GameState.lobbyConnected:
      mainElement = (
        <div>
          <Lobby
            {...commonProps}
            connected={gameState === GameState.lobbyConnected}
            onConnect={onConnect}
            onStartGame={onStartGame}
          />
        </div>
      );
      break;
    case GameState.gameActive:
    case GameState.gameOver:
      mainElement = (
        <Game
          {...commonProps}
          gameOver={gameState === GameState.gameOver}
          promptList={promptList}
          onSubmitPrompt={onSubmitPrompt}
        />
      );
      break;
  }

  return (
    <>
      {errorList && (
        <ErrorHost errorList={errorList} onClose={onDismissError} />
      )}
      {mainElement}
    </>
  );
};

export default App;
