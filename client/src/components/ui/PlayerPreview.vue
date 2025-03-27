<template>
  <div class="flex flex-col items-center space-y-2">
    <p class="text-lg font-medium text-gray-800">{{ player.name }}</p>
    <div class="relative w-16 h-16 rounded-full overflow-hidden border-2 border-gray-200 shadow-md">
      <img
          :alt="`${player.icon} ${iconVariant}`"
          :src="icon"
          class="w-full h-full object-cover"
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