import { PromptData, UserStatus } from "../messages";
import { PromptProvider } from "./PromptProvider";
import { UserList } from "./UserList";
import React from "react";

interface GameProps {
  username: string;
  userList: UserStatus[];
  storyOfTheNight: string;
  gameOver: boolean;
  promptList?: PromptData[];
  onSubmitPrompt: (id: string, description: string) => void;
}

export const Game: React.FC<GameProps> = (props) => {
  const { gameOver, storyOfTheNight } = props;

  return (
    <>
      <h1>Story of the Night:</h1>
      <h2>{storyOfTheNight}</h2>
      <UserList {...props} />
      {!gameOver && <PromptProvider {...props} />}
      {gameOver && <p>Prompts complete!</p>}
    </>
  );
};
