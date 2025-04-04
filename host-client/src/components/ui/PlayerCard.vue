<template>
  <div :class="sizeClasses.card" class="h-full">
    <div class="flex flex-col h-full">
      <div class="flex items-center gap-2 flex-col">
        <div class="relative flex-shrink-0">
          <div :class="sizeClasses.icon" class="overflow-hidden">
            <img :alt="player.name" :src="icon" class="size-full object-cover player-icon"/>
          </div>
          <div
              v-if="isChief"
              :class="sizeClasses.crown"
              class="absolute -top-1 -right-1 rounded-full flex items-center justify-center text-white font-bold shadow-sm bg-yellow-200/90 border-yellow-300/80 border-2"
          >
            👑
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <h3 :class="sizeClasses.name" class="font-medium text-gray-900 truncate">{{ player.name }}</h3>
          <div v-if="isDead" class="mt-1">
            <span :class="sizeClasses.badge" class="rounded-full bg-red-100 text-red-600 border border-red-200/50">
              Dead
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { GamePlayer, Player } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

export type IconVariant = 'normal' | 'angel' | 'demon' | 'satan' | 'dead';

const props = defineProps<{
  player: GamePlayer | Player;
  iconVariant?: IconVariant;
  ignoreModifiers?: boolean;
  size?: 'sm' | 'md' | 'lg' | 'xl';
}>();

const sizeClasses = computed(() => {
  switch (props.size ?? 'md') {
    case 'sm':
      return {
        card: 'p-2',
        icon: 'size-12',
        name: 'text-xs',
        badge: 'text-[10px] px-1 py-0.5',
        crown: 'w-4 h-4 text-[10px]'
      };
    case 'md':
      return {
        card: 'p-3',
        icon: 'size-16',
        name: 'text-sm',
        badge: 'text-xs px-1.5 py-0.5',
        crown: 'w-5 h-5 text-xs'
      };
    case 'lg':
      return {
        card: 'p-4',
        icon: 'size-24',
        name: 'text-lg',
        badge: 'text-sm px-2 py-1',
        crown: 'w-6 h-6 text-sm'
      };
    case 'xl':
      return {
        card: 'p-6',
        icon: 'size-32',
        name: 'text-2xl',
        badge: 'text-base px-3 py-1.5',
        crown: 'w-8 h-8 text-base'
      };
  }
});

const isGamePlayer = computed(() => 'role' in props.player);
const isChief = computed(() => !props.ignoreModifiers && isGamePlayer.value && (props.player as GamePlayer).is_chief);
const isDead = computed(() => !props.ignoreModifiers && isGamePlayer.value && !(props.player as GamePlayer).is_alive);

const icon = computed(() => {
  let variant = props.iconVariant;
  if (!variant && !props.ignoreModifiers) {
    if (isDead.value) {
      variant = 'dead';
    }
  }

  const i = props.player.icon;
  switch (variant ?? 'normal') {
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

<style scoped>
img {
  image-rendering: pixelated;
}
</style>