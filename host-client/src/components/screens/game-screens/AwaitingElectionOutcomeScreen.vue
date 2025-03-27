<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center space-y-6">
        <h2 class="text-3xl font-bold text-amber-900">Election</h2>
        <div class="flex flex-col items-center gap-4">
          <div class="relative">
            <div class="w-32 h-32 rounded-full overflow-hidden border-4 border-amber-400 shadow-lg">
              <img :alt="nominee.name" :src="nominee.icon" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-amber-500 text-white px-4 py-1 rounded-full text-lg font-medium">
              {{ nominee.name }}
            </div>
          </div>
          <p class="text-xl text-amber-700">Should {{ nominee.name }} be elected as the new advisor?</p>
        </div>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingElectionOutcomeInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingElectionOutcomeInnerGameState);

const nominee = computed(() => props.game.players.find(p => p.name === gameState.value.nominee)!);
</script>