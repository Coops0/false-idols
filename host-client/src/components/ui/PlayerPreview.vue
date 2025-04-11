<template>
  <div :class="sizeClasses.card">
    <div class="flex flex-col">
      <div class="flex items-center gap-2 flex-col">
        <div :class="iconVariant === 'satan' && 'drop-shadow-red-500/80 drop-shadow-xl'" class="relative flex-shrink-0">
          <div :class="sizeClasses.icon">
            <img
                :src="icon"
                class="size-full object-cover player-icon"
            />
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <p :class="sizeClasses.name" class="font-mono text-gray-700 truncate">{{ player.name }}</p>
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
  size?: 'sm' | 'md' | 'lg' | 'xl' | '2xl';
}>();

const sizeClasses = computed(() => {
  switch (props.size ?? 'md') {
    case 'sm':
      return {
        card: 'p-2',
        icon: 'size-12',
        name: 'text-xs',
        crown: 'size-4 text-[8px]'
      };
    case 'md':
      return {
        card: 'p-3',
        icon: 'size-16',
        name: 'text-sm',
        crown: 'size-5 text-xs'
      };
    case 'lg':
      return {
        card: 'p-4',
        icon: 'size-24',
        name: 'text-lg',
        crown: 'size-6 text-sm'
      };
    case 'xl':
      return {
        card: 'p-6',
        icon: 'size-32',
        name: 'text-2xl',
        crown: 'size-7 text-sm'
      };
    case '2xl':
      return {
        card: 'p-8',
        icon: 'size-40',
        name: 'text-3xl',
        crown: 'size-8 text-base'
      };
  }
});

const isGamePlayer = computed(() => 'role' in props.player);
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