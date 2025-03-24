<template>
  <div>
    <PlayerPreview :player="chief"/>
    <Card :card="null"/>
    <Card :card="null"/>
    <Card :card="null"/>
  </div>
</template>

<script setup lang="ts">
import type { AwaitingChiefCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import Card from '@/components/ui/Card.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefCardDiscardInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>