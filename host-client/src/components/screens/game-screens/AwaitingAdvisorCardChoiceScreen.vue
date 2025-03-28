<template>
  <div class="space-y-8">
    <ModernCard variant="highlighted">
      <div class="text-center space-y-6">
        <div class="flex justify-center">
          <div class="relative">
            <div class="w-32 h-32 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="advisor.name" :src="PlayerIcon.normal(advisor.icon)" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-3 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-purple-500 to-purple-600 text-white px-5 py-2 rounded-xl text-lg font-medium shadow-sm border border-purple-400/50">
              {{ advisor.name }}
            </div>
          </div>
        </div>
        <p class="text-xl text-gray-700">The advisor is making their choice...</p>
      </div>
    </ModernCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorCardChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import ModernCard from '@/components/ui/ModernCard.vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorCardChoiceInnerGameState);

const advisor = computed(() => props.game.players.find(p => p.name === gameState.value.advisor_name)!);
</script>