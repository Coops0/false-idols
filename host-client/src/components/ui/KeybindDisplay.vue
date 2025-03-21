<template>
  <div>
    <ul>
      <li>H: Hide this</li>
      <li v-if="game.type === 'lobby'">Enter: Start game</li>
      <li v-if="game.type === 'lobby'">Escape: Reset players</li>
      <li v-if="isSkippableState">Space: Skip</li>
      <li v-if="game.type === 'game_in_progress' && game.inner_game_state.type === 'awaiting_election_resolution'">
        Enter/Y: Pass
      </li>
      <li v-if="game.type === 'game_in_progress' && game.inner_game_state.type === 'awaiting_election_resolution'">
        Backspace/N: Reject
      </li>
      <li v-if="game.type === 'game_over'">Enter: Back to lobby</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
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