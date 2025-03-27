<template>
  <div class="relative group">
    <div
        class="absolute inset-0 bg-gradient-to-br from-amber-100 to-amber-200 rounded-lg shadow-lg transform transition-all duration-300"/>
    <div class="relative p-4 border-2 border-amber-300 rounded-lg bg-gradient-to-br from-amber-50 to-amber-100">
      <div class="flex items-center gap-4">
        <div class="relative">
          <div
              class="w-16 h-16 rounded-full overflow-hidden border-2 border-amber-400 shadow-lg bg-gradient-to-br from-amber-200 to-amber-300">
            <img :alt="player.name" :src="icon" class="w-full h-full object-cover"/>
          </div>
          <div v-if="player.is_chief"
               class="absolute -top-2 -right-2 w-6 h-6 bg-gradient-to-br from-yellow-400 to-yellow-600 rounded-full flex items-center justify-center text-white text-xs font-bold shadow-md border border-yellow-300">
            👑
          </div>
        </div>
        <div class="flex-1">
          <h3 class="text-lg font-semibold text-amber-900">{{ player.name }}</h3>
          <div class="flex items-center gap-2 mt-1">
            <span v-if="!player.is_alive"
                  class="text-sm px-2 py-1 rounded-full bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border border-gray-300 shadow-sm">
              Deceased
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { GamePlayer } from '@/game/state.ts';
import { roleName } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

export type IconVariant = 'normal' | 'angel' | 'demon' | 'satan' | 'dead';

const props = defineProps<{
  player: GamePlayer;
  iconVariant?: IconVariant;
}>();

const icon = computed(() => {
  const i = props.player.icon;
  switch (props.iconVariant ?? 'normal') {
    case 'normal':
      return PlayerIcon.normal(i);
    case 'angel':
      return PlayerIcon.angel(i);
    case 'demon':
      return PlayerIcon.demon(i);
    case 'satan':
      return PlayerIcon.satan(i);
    case 'dead':
      return PlayerIcon.dead(i);
  }
});
</script> 