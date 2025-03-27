<template>
  <div class="relative w-full max-w-md mx-auto">
    <div class="absolute inset-0 bg-gradient-to-br from-red-100 to-red-200 rounded-lg shadow-lg"></div>
    <div class="relative p-4 border-2 border-red-300 rounded-lg bg-gradient-to-br from-red-50 to-red-100">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div
              class="w-8 h-8 rounded-full bg-gradient-to-br from-red-400 to-red-600 flex items-center justify-center text-white font-bold shadow-md border border-red-300">
            ⚠️
          </div>
          <span class="text-red-900 font-semibold">Failed Elections</span>
        </div>
        <div class="flex items-center gap-1">
          <div v-for="i in 3" :key="i"
               :class="[
                 'w-4 h-4 rounded-full border-2 transition-all duration-300',
                 i <= props.game.failed_elections 
                   ? 'bg-gradient-to-br from-red-500 to-red-700 border-red-400 shadow-md' 
                   : 'bg-gradient-to-br from-red-100 to-red-200 border-red-300'
               ]">
          </div>
        </div>
      </div>
      <div class="mt-2 text-sm text-red-800">
        {{
          props.game.failed_elections === 3 ? 'Chaos!' : `${3 - props.game.failed_elections} more failures until chaos`
        }}
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';

const props = defineProps<{ game: InProgressGameState }>();
</script>