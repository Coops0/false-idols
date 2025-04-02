<template>
  <div class="h-full flex items-center justify-center p-8">
    <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6 max-w-xs">
      <h2 class="text-xl text-gray-900 mb-3">The chief is choosing a card to remove</h2>
      <div class="flex items-center gap-2">
        <div class="relative">
          <PlayerCard :player="chief" size="lg"/>
        </div>
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