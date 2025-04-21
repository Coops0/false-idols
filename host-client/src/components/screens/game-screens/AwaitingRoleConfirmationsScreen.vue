<template>
  <div class="flex flex-col items-center">
    <div class="flex justify-center items-center flex-col">
      <p class="text-gray-700 font-amazonia font-light text-center text-3xl">Waiting for <span
          class="font-bold">{{ unconfirmed }}</span> {{ confirmations }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AwaitingRoleConfirmationsInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingRoleConfirmationsInnerGameState);

const unconfirmed = computed(() => props.game.players.length - gameState.value.confirmed.length);
const confirmations = computed(() => unconfirmed.value === 1 ? 'confirmation' : 'confirmations');
</script>