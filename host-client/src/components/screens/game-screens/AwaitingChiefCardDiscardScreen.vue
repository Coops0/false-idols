<template>
  <div class="p-8 max-w-md">
    <div class="text-center flex flex-col items-center">
      <h2 class="text-xl text-gray-900 mb-3">The chief is choosing a card to remove</h2>
      <div class="relative mb-3">
        <PlayerCard :player="chief" size="lg"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefCardDiscardInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>