<template>
  <div class="h-full flex items-center justify-center">
    <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6 max-w-xs">
      <div class="flex items-center gap-2">
        <div class="relative">
          <div class="size-32 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
            <img :alt="chief.name" :src="PlayerIcon.normal(chief.icon)" class="w-full h-full object-cover"/>
          </div>
          <div
              class="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold shadow-sm border border-blue-400/50">
            👑
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefCardDiscardInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>