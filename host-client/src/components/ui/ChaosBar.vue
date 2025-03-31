<template>
  <div class="relative w-full max-w-md mx-auto">
    <div class="bg-white/50 backdrop-blur-sm rounded-xl p-6 border border-gray-100/50">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-3">
          <div
              class="w-10 h-10 rounded-xl bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center text-white font-bold shadow-sm border border-red-400/50">
            ⚠️
          </div>
          <span class="text-gray-900 font-medium text-lg">Failed Elections</span>
        </div>
        <div class="flex items-center gap-2">
          <div v-for="i in 3" :key="i"
               :class="[
                 'w-4 h-4 rounded-full border transition-all duration-300',
                 i <= game.failed_elections
                   ? 'bg-gradient-to-br from-red-500 to-red-600 border-red-400/50 shadow-sm' 
                   : 'bg-gray-100/50 border-gray-200/50'
               ]">
          </div>
        </div>
      </div>
      <div v-if="game.failed_elections === 3" class="text-sm text-gray-600">Chaos!</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';

defineProps<{ game: InProgressGameState }>();
</script>