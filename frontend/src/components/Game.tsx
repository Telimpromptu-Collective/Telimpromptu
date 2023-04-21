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
      <UserList {...props} />
      <p>Story of the Night:</p>
      <p>{storyOfTheNight}</p>
      {!gameOver && <PromptProvider {...props} />}
      {gameOver && <p>Prompts complete!</p>}
    </>
  );
};
