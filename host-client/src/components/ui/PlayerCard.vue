<template>
  <div class="bg-white/50 backdrop-blur-sm rounded-lg p-3 border border-gray-100/50 h-full">
    <div class="flex flex-col h-full">
      <div class="flex items-center gap-2">
        <div class="relative flex-shrink-0">
          <div class="w-12 h-12 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
            <img :alt="player.name" :src="icon" class="w-full h-full object-cover"/>
          </div>
          <div v-if="player.is_chief"
               class="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold shadow-sm border border-blue-400">
            👑
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <h3 class="text-sm font-medium text-gray-900 truncate">{{ player.name }}</h3>
          <div v-if="!player.is_alive" class="mt-1">
            <span class="text-xs px-1.5 py-0.5 rounded-full bg-red-100 text-red-600 border border-red-200/50">
              Dead
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { GamePlayer } from '@/game/state.ts';
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