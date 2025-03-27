<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center space-y-4">
        <h2 class="text-2xl font-semibold text-amber-900">The Chief's Turn</h2>
        <div class="flex justify-center">
          <div class="relative">
            <div class="w-24 h-24 rounded-full overflow-hidden border-4 border-amber-400 shadow-lg">
              <img :alt="chief.name" :src="chief.icon" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-amber-500 text-white px-3 py-1 rounded-full text-sm font-medium">
              {{ chief.name }}
            </div>
            <div
                class="absolute -top-2 -right-2 w-6 h-6 bg-amber-500 rounded-full flex items-center justify-center text-white text-xs font-bold">
              👑
            </div>
          </div>
        </div>
        <p class="text-amber-700">The chief is making their decision...</p>
      </div>
    </DivineCard>

    <DivineCard>
      <div class="flex justify-center items-center gap-4">
        <div class="text-center">
          <h3 class="text-xl font-semibold text-amber-900">Advisor</h3>
          <p class="text-amber-700">{{ gameState.advisor_name }}</p>
        </div>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefCardDiscardInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>