import React, { useCallback } from "react";
import { Story } from "../messages";
import { Prompt } from "./PromptProvider";

interface StoryVotingProps {
  onSubmitStory: (story: string) => void;
  onVote: (id: number) => void;
  onEndVoting: () => void;
  stories: Story[];
}

export const StoryVoting: React.FC<StoryVotingProps> = (props) => {
  const { onSubmitStory, onVote, onEndVoting, stories } = props;

  return (
    <>
      <Prompt
        id=""
        onSubmitPrompt={(_id, description) => onSubmitStory(description)}
        description="Enter a story of the night!"
      />
      <ul>
        {stories.map((story) => (
          <li>
            {story.author}
            {story.story}
            {story.voters}
            <button
              onClick={() => {
                onVote(story.id);
              }}
            >
              VOTE
            </button>
          </li>
        ))}
      </ul>
      <button onClick={onEndVoting}>WE ARE FUCKING DONE</button>
    </>
  );
};
