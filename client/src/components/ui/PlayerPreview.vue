<template>
  <div class="flex flex-col items-center gap-y-2">
    <p v-if="!hideName" class="text-lg text-gray-700 truncate font-mono">{{ player.name }}</p>
    <div class="relative size-14">
      <img
          :alt="`${player.icon} ${iconVariant}`"
          :src="icon"
          class="size-full object-cover"
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
  hideName?: boolean;
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