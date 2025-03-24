<template>
<div>
  <p>Failed elections in a row: {{ props.game.failed_elections }}</p>
</div>
</template>

<script setup lang="ts">
import type { InProgressGameState } from '@/game/state.ts';

const props = defineProps<{ game: InProgressGameState }>();
</script>