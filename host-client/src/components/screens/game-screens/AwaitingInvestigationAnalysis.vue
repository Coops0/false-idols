<template>
  <div class="space-y-8">
    <ModernCard variant="highlighted">
      <div class="text-center space-y-8">
        <div class="flex flex-col items-center gap-8">
          <div class="relative">
            <div class="w-40 h-40 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="chief.name" :src="PlayerIcon.normal(chief.icon)" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-3 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-blue-500 to-blue-600 text-white px-6 py-2 rounded-xl text-lg font-medium shadow-sm border border-blue-400/50">
              {{ chief.name }}
            </div>
            <div
                class="absolute -top-2 -right-2 w-8 h-8 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center text-white text-sm font-bold shadow-sm border border-blue-400/50">
              👑
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="w-1 h-20 bg-gradient-to-b from-blue-500/20 to-blue-500/5 rounded-full"/>
            <p class="text-xl text-gray-700">is investigating</p>
            <div class="w-1 h-20 bg-gradient-to-b from-blue-500/5 to-blue-500/20 rounded-full"/>
          </div>

          <div class="relative">
            <div class="w-40 h-40 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="target.name" :src="PlayerIcon.normal(target.icon)" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-3 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-purple-500 to-purple-600 text-white px-6 py-2 rounded-xl text-lg font-medium shadow-sm border border-purple-400/50">
              {{ target.name }}
            </div>
          </div>
        </div>
      </div>
    </ModernCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingInvestigationAnalysisInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import ModernCard from '@/components/ui/ModernCard.vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingInvestigationAnalysisInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
const target = computed(() => props.game.players.find(p => p.name === gameState.value.target)!);
</script>