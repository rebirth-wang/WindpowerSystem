import { PlaywrightAiFixture, type PlayWrightAiFixtureType } from '@midscene/web/playwright';
import { expect, test as fastbeeTest } from './fastbeeMock';

export function hasMidsceneModelConfig(env: NodeJS.ProcessEnv = process.env) {
  return Boolean(
    env.MIDSCENE_MODEL_NAME && env.MIDSCENE_MODEL_FAMILY && (env.MIDSCENE_MODEL_API_KEY || env.OPENAI_API_KEY)
  );
}

export const test = fastbeeTest.extend<PlayWrightAiFixtureType>({
  ...PlaywrightAiFixture({
    cache: process.env.MIDSCENE_CACHE === 'true',
  }),
});

export { expect };
