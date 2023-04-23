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
      <ul>
        {stories.map((story) => (
          <li>
            {story.author ?? "DEFAULT"}: {story.story} - [{story.voters.join(", ")}]
            <button
              onClick={() => {
                onVote(story.id);
              }}
            >
              Vote
            </button>
          </li>
        ))}
      </ul>
      <Prompt
        id=""
        onSubmitPrompt={(_id, description) => onSubmitStory(description)}
        description="Enter a story of the night!"
      />
      <button onClick={onEndVoting}>Voting Complete</button>
    </>
  );
};
