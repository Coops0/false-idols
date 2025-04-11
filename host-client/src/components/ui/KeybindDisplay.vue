<template>
  <div
      class="bg-gray-900/50 backdrop-blur-xs rounded-lg shadow-lg p-1.5 text-gray-50 z-20 font-mono flex flex-col gap-y-1 text-xs">
    <div class="flex items-center gap-2">
      <CustomKbd>H</CustomKbd>
      <span class="text-gray-300">Hide</span>
    </div>

    <div class="flex items-center gap-2">
      <CustomKbd>F</CustomKbd>
      <span class="text-gray-300">Fullscreen</span>
    </div>

    <template v-if="game.type === 'lobby'">
      <div class="flex items-center gap-2">
        <CustomKbd>Enter</CustomKbd>
        <span class="text-gray-300">Start game</span>
      </div>
      <div class="flex items-center gap-2">
        <CustomKbd>Escape</CustomKbd>
        <span class="text-gray-300">Reset players</span>
      </div>
    </template>

    <template v-if="isSkippableState">
      <div class="flex items-center gap-2">
        <CustomKbd>Space</CustomKbd>
        <span class="text-gray-300">Skip</span>
      </div>
    </template>

    <template
        v-if="game.type === 'game_in_progress' && (game.inner_game_state.type === 'awaiting_president_election_outcome' || game.inner_game_state.type === 'awaiting_advisor_election_outcome')">
      <div class="flex items-center gap-2">
        <CustomKbd>Enter</CustomKbd>
        <span class="text-gray-300">or</span>
        <CustomKbd>Y</CustomKbd>
        <span class="text-gray-300">Pass election</span>
      </div>

      <div class="flex items-center gap-2">
        <CustomKbd>Backspace</CustomKbd>
        <span class="text-gray-300">or</span>
        <CustomKbd>N</CustomKbd>
        <span class="text-gray-300">Reject election</span>
      </div>
    </template>

    <template
        v-if="game.type === 'game_in_progress' && game.inner_game_state.type === 'awaiting_advisor_card_choice'">
      <div class="flex items-center gap-2">
        <CustomKbd>Backspace</CustomKbd>
        <span class="text-gray-300">or</span>
        <CustomKbd>V</CustomKbd>
        <span class="text-gray-300">Veto</span>
      </div>
    </template>

    <div class="flex items-center gap-2" v-if="game.type === 'game_in_progress'">
      <CustomKbd>Escape</CustomKbd>
      <span class="text-gray-300">End Game</span>
    </div>

    <template v-if="game.type === 'game_over'">
      <div class="flex items-center gap-2">
        <CustomKbd>Enter</CustomKbd>
        <span class="text-gray-300">Return to lobby</span>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import type { GameState } from '@/game/state.ts';
import { computed } from 'vue';
import CustomKbd from '@/components/ui/CustomKbd.vue';

const props = defineProps<{ game: GameState }>();

const isSkippableState = computed(() => {
  if (props.game.type !== 'game_in_progress') {
    return false;
  }

  // all other inner game states are skippable except for election resolution
  return props.game.inner_game_state.type !== 'awaiting_advisor_election_outcome' &&
      props.game.inner_game_state.type !== 'awaiting_president_election_outcome';
});
</script>