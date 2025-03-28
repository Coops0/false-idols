<template>
  <div class="space-y-8">
    <ModernCard variant="highlighted">
      <div class="text-center space-y-6">
        <div class="flex justify-center">
          <div class="relative">
            <div class="w-32 h-32 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="chief.name" :src="PlayerIcon.normal(chief.icon)" class="w-full h-full object-cover"/>
            </div>
            <div class="absolute -bottom-3 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-blue-500 to-blue-600 text-white px-5 py-2 rounded-xl text-lg font-medium shadow-sm border border-blue-400/50">
              {{ chief.name }}
            </div>
            <div class="absolute -top-2 -right-2 w-8 h-8 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center text-white text-sm font-bold shadow-sm border border-blue-400/50">
              👑
            </div>
          </div>
        </div>
        <p class="text-xl text-gray-700">The chief is making their decision...</p>
      </div>
    </ModernCard>

    <ModernCard>
      <div class="flex justify-center items-center gap-4">
        <div class="text-center">
          <h3 class="text-xl font-semibold text-gray-900">Advisor</h3>
          <p class="text-gray-600">{{ gameState.advisor_name }}</p>
        </div>
      </div>
    </ModernCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import ModernCard from '@/components/ui/ModernCard.vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefCardDiscardInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>