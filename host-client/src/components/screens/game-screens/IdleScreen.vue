<template>
  <div></div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';

/*const props = */
defineProps<{ game: InProgressGameState }>();
// const gameState = computed(() => props.game.inner_game_state as IdleInnerGameState);
</script>