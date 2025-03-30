<template>
  <div class="space-y-8">
    <ModernCard variant="highlighted">
      <div class="text-center space-y-8">
        <h2 class="text-3xl font-bold text-gray-900 tracking-tight">Election</h2>
        <div class="flex flex-col items-center gap-6">
          <div class="relative">
            <div class="w-40 h-40 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="nominee.name" :src="PlayerIcon.normal(nominee.icon)" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-3 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-blue-500 to-blue-600 text-white px-6 py-2 rounded-xl text-lg font-medium shadow-sm border border-blue-400/50">
              {{ nominee.name }}
            </div>
          </div>
          <p class="text-xl text-gray-700 max-w-md">Elect {{ nominee.name }} as advisor?</p>
        </div>
      </div>
    </ModernCard>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingElectionOutcomeInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import ModernCard from '@/components/ui/ModernCard.vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingElectionOutcomeInnerGameState);

const nominee = computed(() => props.game.players.find(p => p.name === gameState.value.nominee)!);
</script>