<template>
  <div>
    <p>{{ gameState.player.name }}</p>
    <p>Role: {{ roleName(gameState.role) }}</p>
    <button @click="() => emit('confirm')">Confirm</button>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import type { Game, ViewInvestigationResultsGameState } from '@/game';
import { roleName } from '@/game/messages.ts';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewInvestigationResultsGameState);
</script>