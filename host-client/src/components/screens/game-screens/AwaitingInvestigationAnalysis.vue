<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center space-y-6">
        <h2 class="text-3xl font-bold text-amber-900">Investigation</h2>
        <div class="flex flex-col items-center gap-8">
          <div class="relative">
            <div class="w-32 h-32 rounded-full overflow-hidden border-4 border-amber-400 shadow-lg">
              <img :alt="chief.name" :src="chief.icon" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-amber-500 text-white px-4 py-1 rounded-full text-lg font-medium">
              {{ chief.name }}
            </div>
            <div
                class="absolute -top-2 -right-2 w-6 h-6 bg-amber-500 rounded-full flex items-center justify-center text-white text-xs font-bold">
              👑
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="w-1 h-16 bg-gradient-to-b from-amber-400 to-amber-200"></div>
            <p class="text-xl text-amber-700">is investigating</p>
            <div class="w-1 h-16 bg-gradient-to-b from-amber-200 to-amber-400"></div>
          </div>

          <div class="relative">
            <div class="w-32 h-32 rounded-full overflow-hidden border-4 border-amber-400 shadow-lg">
              <img :alt="target.name" :src="target.icon" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-amber-500 text-white px-4 py-1 rounded-full text-lg font-medium">
              {{ target.name }}
            </div>
          </div>
        </div>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingInvestigationAnalysisInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingInvestigationAnalysisInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
const target = computed(() => props.game.players.find(p => p.name === gameState.value.target)!);
</script>