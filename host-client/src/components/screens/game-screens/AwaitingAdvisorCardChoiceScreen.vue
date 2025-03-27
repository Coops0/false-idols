<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center space-y-4">
        <h2 class="text-2xl font-semibold text-amber-900">The Advisor's Turn</h2>
        <div class="flex justify-center">
          <div class="relative">
            <div class="w-24 h-24 rounded-full overflow-hidden border-4 border-amber-400 shadow-lg">
              <img :alt="advisor.name" :src="advisor.icon" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-amber-500 text-white px-3 py-1 rounded-full text-sm font-medium">
              {{ advisor.name }}
            </div>
          </div>
        </div>
        <p class="text-amber-700">The advisor is making their choice...</p>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorCardChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorCardChoiceInnerGameState);

const advisor = computed(() => props.game.players.find(p => p.name === gameState.value.advisor_name)!);
</script>