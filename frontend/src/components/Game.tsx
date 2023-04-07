import { PromptData, UserStatus } from "../messages";
import { PromptProvider } from "./PromptProvider";
import { UserList } from "./UserList";
import React from "react";

interface GameProps {
  username: string;
  userList: UserStatus[];
  gameOver: boolean;
  promptList?: PromptData[];
  onSubmitPrompt: (id: string, description: string) => void;
}

export const Game: React.FC<GameProps> = (props) => {
  const { gameOver } = props;

  return (
    <>
      <UserList {...props} />
      {!gameOver && <PromptProvider {...props} />}
    </>
  );
};
