<template>
  <div>
    <p>{{ player.name }}</p>
    <img :src="icon" :alt="`${player.icon} ${iconVariant}`"/>
  </div>
</template>

<script setup lang="ts">
import type { Player } from '@/game/messages.ts';
import { PlayerIcon } from '@/game/player-icon.ts';
import { computed } from 'vue';

export type IconVariant = 'normal' | 'angel' | 'demon' | 'satan';

const props = withDefaults(defineProps<{
  player: Player;
  iconVariant: IconVariant;
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