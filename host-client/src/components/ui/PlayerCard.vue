<template>
  <div class="group relative bg-white/50 backdrop-blur-sm -z-1 rounded-xl p-6 border border-gray-100/50  transition-all duration-300">
    <div class="flex items-center gap-4">
      <div class="relative">
        <div class="w-16 h-16 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
          <img :alt="player.name" :src="icon" class="w-full h-full object-cover"/>
        </div>
        <div v-if="player.is_chief"
             class="absolute -top-2 -right-2 w-6 h-6 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold shadow-md border border-blue-400">
          👑
        </div>
      </div>
      <div class="flex-1">
        <h3 class="text-lg font-medium text-gray-900">{{ player.name }}</h3>
        <div class="flex items-center gap-2 mt-1">
          <span v-if="!player.is_alive"
                class="text-sm px-2.5 py-1 rounded-full bg-gray-100/50 text-gray-600 border border-gray-200/50">
            Deceased
          </span>
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