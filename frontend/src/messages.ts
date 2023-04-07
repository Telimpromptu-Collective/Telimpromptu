export interface Message {
  type: string;
}
export const isMessage = (obj: any): obj is Message => obj?.type !== undefined;

//Messages to server

export interface CreateUserMessage extends Message {
  username: string;
}
export const isCreateUserMessage = (msg: Message): msg is CreateUserMessage =>
  msg.type === "createUser";

export interface StartGameMessage extends Message {}
export const isStartGameMessage = (msg: Message): msg is StartGameMessage =>
  msg.type === "startGame";

export interface HeartBeatMessage extends Message {}
export const isHeartBeatMessage = (msg: Message): msg is HeartBeatMessage =>
  msg.type === "heartbeat";

export interface PromptResponseMessage extends Message {
  response: string;
  id: string;
}
export const isPromptResponseMessage = (
  msg: Message
): msg is PromptResponseMessage => msg.type === "promptResponse";

//Messages to client

export interface ErrorMessage extends Message {
  message: string;
}
export const isErrorMessage = (msg: Message): msg is ErrorMessage =>
  msg.type === "error";

export interface UserStatus {
  username: string;
  connected: boolean;
  role?: string;
}
export interface UserNameUpdateMessage extends Message {
  statuses: UserStatus[];
}
export const isUserNameUpdateMessage = (
  msg: Message
): msg is UserNameUpdateMessage => msg.type === "usernameUpdate";

export interface ConnectionSuccessMessage extends Message {
  username: string;
}
export const isConnectionSuccessMessage = (
  msg: Message
): msg is ConnectionSuccessMessage => msg.type === "connectionSuccess";

export interface GameStartedMessage extends Message {
  statuses: UserStatus[];
}
export const isGameStartedMessage = (msg: Message): msg is GameStartedMessage =>
  msg.type === "gameStarted";

export interface PromptData {
  id: string;
  description: string;
}
export interface NewPromptsMessage extends Message {
  scriptPrompts: PromptData[];
}
export const isNewPromptsMessage = (msg: Message): msg is NewPromptsMessage =>
  msg.type === "newPrompts";

export interface PromptsCompleteMessage extends Message {}
export const isPromptsCompleteMessage = (
  msg: Message
): msg is PromptsCompleteMessage => msg.type === "promptsComplete";
