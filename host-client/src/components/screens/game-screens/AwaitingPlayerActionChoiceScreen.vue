<template>
  <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6">
    <div class="text-center">
      <div class="flex justify-center mb-4">
        <div class="relative">
          <PlayerCard :player="chief" size="lg"/>
        </div>
      </div>

      <div class="bg-gray-50 rounded-lg p-3 mx-auto max-w-xs">
        <p class="text-base text-gray-700">The chief is deciding...</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefActionChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefActionChoiceInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>