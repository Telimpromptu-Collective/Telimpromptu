export interface Message {
  type: string;
}
export const isMessage = (obj: any): obj is Message => obj?.type !== undefined;

//Messages to server

export interface UserConnect extends Message {
  username: string;
}
export const isUserConnectMessage = (msg: Message): msg is UserConnect =>
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

export interface StorySubmissionMessage extends Message {
  story: string;
}
export const isStorySubmissionMessage = (
  msg: Message
): msg is StorySubmissionMessage => msg.type === "storySubmission";

export interface StoryVoteMessage extends Message {
  storyId: number;
}
export const isStoryVoteMessage = (msg: Message): msg is StoryVoteMessage =>
  msg.type === "storyVote";

export interface EndStoryVotingMessage extends Message {}
export const isEndStoryVotingMessage = (
  msg: Message
): msg is EndStoryVotingMessage => msg.type === "endStoryVoting";

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

export interface EnterPromptAnsweringStateMessage extends Message {
  storyOfTheNight: string;
  statuses: UserStatus[];
}
export const isEnterPromptAnsweringStateMessage = (
  msg: Message
): msg is EnterPromptAnsweringStateMessage =>
  msg.type === "enterPromptAnsweringState";

export interface Story {
  author?: string;
  story: string;
  voters: string[];
  id: number;
}

export interface StoryVotingStateUpdateMessage extends Message {
  stories: Story[];
}
export const isStoryVotingStateUpdateMessage = (
  msg: Message
): msg is StoryVotingStateUpdateMessage =>
  msg.type === "storyVotingStateUpdate";

export interface EnterStoryVotingStateMessage extends Message {}
export const isEnterStoryVotingStateMessage = (
  msg: Message
): msg is EnterStoryVotingStateMessage => msg.type === "enterStoryVotingState";
