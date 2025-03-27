<template>
  <div class="bg-amber-900/90 backdrop-blur-sm rounded-lg shadow-xl p-4 text-amber-50">
    <div class="flex items-center justify-between mb-3">
      <h3 class="text-sm font-medium">Keybinds</h3>
    </div>

    <div class="space-y-2 text-sm">
      <div class="flex items-center gap-2">
        <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">H</kbd>
        <span>Hide commands</span>
      </div>

      <template v-if="game.type === 'lobby'">
        <div class="flex items-center gap-2">
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Enter</kbd>
          <span>Start game</span>
        </div>
        <div class="flex items-center gap-2">
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Escape</kbd>
          <span>Reset players</span>
        </div>
      </template>

      <template v-if="isSkippableState">
        <div class="flex items-center gap-2">
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Space</kbd>
          <span>Skip</span>
        </div>
      </template>

      <template
          v-if="game.type === 'game_in_progress' && game.inner_game_state.type === 'awaiting_election_resolution'">
        <div class="flex items-center gap-2">
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Enter</kbd>
          <span>or</span>
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Y</kbd>
          <span>Pass election</span>
        </div>
        <div class="flex items-center gap-2">
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Backspace</kbd>
          <span>or</span>
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">N</kbd>
          <span>Reject election</span>
        </div>
      </template>

      <template v-if="game.type === 'game_over'">
        <div class="flex items-center gap-2">
          <kbd class="px-2 py-1 bg-amber-800 rounded text-amber-200">Enter</kbd>
          <span>Return to lobby</span>
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
  return props.game.inner_game_state.type !== 'awaiting_election_resolution';
});
</script>

<style scoped>
kbd {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}
</style>