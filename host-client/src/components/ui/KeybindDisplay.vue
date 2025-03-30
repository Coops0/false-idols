<template>
  <div class="bg-gray-900/50 backdrop-blur-sm rounded-xl shadow-lg p-6 text-gray-50 z-20 font-mono">
    <div class="space-y-3 text-sm">
      <div class="flex items-center gap-2">
        <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">H</kbd>
        <span class="text-gray-300">Hide</span>
      </div>
      <div class="flex items-center gap-2">
        <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">F</kbd>
        <span class="text-gray-300">Fullscreen</span>
      </div>

      <template v-if="game.type === 'lobby'">
        <div class="flex items-center gap-2">
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Enter</kbd>
          <span class="text-gray-300">Start game</span>
        </div>
        <div class="flex items-center gap-2">
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Escape</kbd>
          <span class="text-gray-300">Reset players</span>
        </div>
      </template>

      <template v-if="isSkippableState">
        <div class="flex items-center gap-2">
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Space</kbd>
          <span class="text-gray-300">Skip</span>
        </div>
      </template>

      <template
          v-if="game.type === 'game_in_progress' && game.inner_game_state.type === 'awaiting_election_outcome'">
        <div class="flex items-center gap-2">
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Enter</kbd>
          <span class="text-gray-300">or</span>
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Y</kbd>
          <span class="text-gray-300">Pass election</span>
        </div>
        <div class="flex items-center gap-2">
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Backspace</kbd>
          <span class="text-gray-300">or</span>
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">N</kbd>
          <span class="text-gray-300">Reject election</span>
        </div>
      </template>

      <template v-if="game.type === 'game_over'">
        <div class="flex items-center gap-2">
          <kbd class="px-2.5 py-1.5 bg-gray-800/50 rounded-lg text-gray-200 border border-gray-700/50">Enter</kbd>
          <span class="text-gray-300">Return to lobby</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { GameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: GameState }>();

const isSkippableState = computed(() => {
  if (props.game.type !== 'game_in_progress') {
    return false;
  }

  // all other inner game states are skippable except for election resolution
  return props.game.inner_game_state.type !== 'awaiting_election_outcome';
});
</script>

<style scoped>
kbd {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}
</style>