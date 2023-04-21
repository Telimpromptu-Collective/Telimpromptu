import React, { useCallback, useEffect, useRef, useState } from "react";
import useWebSocket from "react-use-websocket";
import { Lobby } from "./components/Lobby";
import {
  PromptData,
  UserStatus,
  isConnectionSuccessMessage,
  isErrorMessage,
  isMessage,
  isNewPromptsMessage,
  isPromptsCompleteMessage,
  isUserNameUpdateMessage,
  isEnterPromptAnsweringStateMessage,
  isEnterStoryVotingStateMessage,
  isStoryVotingStateUpdateMessage,
  Story,
} from "./messages";
import { Game } from "./components/Game";
import { ErrorHost, ErrorData } from "./components/ErrorHost";
import { nanoid } from "nanoid";
import { StoryVoting } from "./components/StoryVoting";

enum GameState {
  lobbyDisconnected,
  lobbyConnected,
  gameVoting,
  gameActive,
  gameOver,
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
  const [stories, setStories] = useState<Story[]>([]);
  const storyOfTheNight = useRef<string>("");

  const { sendJsonMessage, lastJsonMessage } = useWebSocket(socketUrl);

  const onConnect = useCallback(
    (lobbyID: string, username: string) => {
      setSocketUrl(
        `ws://${window.location.hostname}:${window.location.port}/games/${lobbyID}`
      );
      sendJsonMessage({ type: "userConnect", username: username });
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

  const onSubmitStory = useCallback(
    (story: string) => {
      sendJsonMessage({ type: "storySubmission", story: story });
    },
    [sendJsonMessage]
  );

  const onVoteForStory = useCallback(
    (storyId: number) => {
      sendJsonMessage({ type: "storyVote", storyId: storyId });
    },
    [sendJsonMessage]
  );

  const onEndVoting = useCallback(() => {
    sendJsonMessage({ type: "endStoryVoting" });
  }, []);

  const onDismissError = useCallback(
    (id: string) => {
      setErrorList((errorList) => errorList.filter((error) => error.id !== id));
    },
    [errorList, setErrorList]
  );
  //testing
  useEffect(() => {
    setErrorList([
      { message: "1", id: nanoid() },
      { message: "2", id: nanoid() },
      { message: "3", id: nanoid() },
    ]);
  }, []);

  useEffect(() => {
    if (isMessage(lastJsonMessage)) {
      if (isErrorMessage(lastJsonMessage)) {
        setErrorList([
          ...errorList,
          { message: lastJsonMessage.message, id: nanoid() }, //unique id for each error because of react shenanigans
        ]);
      }
      if (isUserNameUpdateMessage(lastJsonMessage)) {
        setUserList(lastJsonMessage.statuses);
      }
      if (isConnectionSuccessMessage(lastJsonMessage)) {
        setGameState(GameState.lobbyConnected);
        setUsername(lastJsonMessage.username);
      }
      if (isNewPromptsMessage(lastJsonMessage)) {
        setPromptList([...promptList, ...lastJsonMessage.scriptPrompts]);
      }
      if (isEnterPromptAnsweringStateMessage(lastJsonMessage)) {
        setGameState(GameState.gameActive);
        setUserList(lastJsonMessage.statuses);
        storyOfTheNight.current = lastJsonMessage.storyOfTheNight;
      }
      if (isEnterStoryVotingStateMessage(lastJsonMessage)) {
        setGameState(GameState.gameVoting);
      }
      if (isStoryVotingStateUpdateMessage(lastJsonMessage)) {
        setStories(lastJsonMessage.stories);
      }
      if (isPromptsCompleteMessage(lastJsonMessage)) {
        setGameState(GameState.gameOver);
      }
    }
  }, [lastJsonMessage]);

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
        <Lobby
          {...commonProps}
          connected={gameState === GameState.lobbyConnected}
          onConnect={onConnect}
          onStartGame={onStartGame}
        />
      );
      break;
    case GameState.gameVoting:
      mainElement = (
        <StoryVoting
          stories={stories}
          onSubmitStory={onSubmitStory}
          onVote={onVoteForStory}
          onEndVoting={onEndVoting}
        />
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
