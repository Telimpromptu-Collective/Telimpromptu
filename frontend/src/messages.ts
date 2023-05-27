export const isMessageForClient = (obj: any): obj is MessageForClient => obj?.type !== undefined;

//Messages to server

export type UserConnectMessage = {
  type: "userConnect";
  username: string;
}

export type StartGameMessage = {
  type: "startGame";
}

export type HeartbeatMessage = {
  type: "heartbeat";
}

export type PromptResponseMessage = {
  type: "promptResponse";
  response: string;
  id: string;
}

export type StorySubmissionMessage = {
  type: "heartbeat";
  story: string;
}


export type StoryVoteMessage = {
  type: "storyVote";
  storyId: number;
}

export type endStoryVotingMessage = {
  type: "endStoryVoting";
}

//Messages to client

export type ErrorMessage = {
  type: "error";
  message: string;
}

export interface UserStatus {
  username: string;
  connected: boolean;
  role?: string;
}

export type UsernameUpdateMessage = {
  type: "usernameUpdate";
  statuses: UserStatus[];
}

export type ConnectionSuccessMessage = {
  type: "connectionSuccess";
  username: string;
}

export interface PromptData {
  id: string;
  description: string;
}

export type NewPromptsMessage = {
  type: "newPrompts";
  scriptPrompts: PromptData[];
}

export type PromptsCompleteMesssage = {
  type: "promptsComplete";
}

export type EnterPromptAnsweringStateMessage = {
  type: "enterPromptAnsweringState";
  storyOfTheNight: string;
  statuses: UserStatus[];
}

export interface Story {
  author?: string;
  story: string;
  voters: string[];
  id: number;
}

export type EnterStoryVotingStateUpdateMessage = {
  type: "enterStoryVotingStateUpdate";
  stories: Story[];
}

export type EnterStoryVotingStateMessage = {
  type: "enterStoryVotingState";
}

export type MessageForClient = ErrorMessage | UsernameUpdateMessage | ConnectionSuccessMessage | NewPromptsMessage | PromptsCompleteMesssage | EnterPromptAnsweringStateMessage | EnterStoryVotingStateUpdateMessage | EnterStoryVotingStateMessage