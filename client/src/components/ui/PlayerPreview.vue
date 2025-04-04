<template>
  <div class="flex flex-col items-center space-y-2">
    <p class="text-lg font-medium text-gray-800">{{ player.name }}</p>
    <div class="relative w-16 h-16 overflow-hidden">
      <img
          :alt="`${player.icon} ${iconVariant}`"
          :src="icon"
          class="w-full h-full object-cover"
          draggable="false"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Player } from '@/game/messages.ts';
import { PlayerIcon } from '@/game/player-icon.ts';
import { computed } from 'vue';

export type IconVariant = 'normal' | 'angel' | 'demon' | 'satan';

const props = withDefaults(defineProps<{
  player: Player;
  iconVariant?: IconVariant;
}>(), { iconVariant: 'normal' });

const icon = computed(() => {
  const i = props.player.icon;
  switch (props.iconVariant) {
    case 'normal':
      return PlayerIcon.normal(i);
    case 'angel':
      return PlayerIcon.angel(i);
    case 'demon':
      return PlayerIcon.demon(i);
    case 'satan':
      return PlayerIcon.satan(i);
  }
});
</script>

<style scoped>
img {
  image-rendering: pixelated;
}
</style>