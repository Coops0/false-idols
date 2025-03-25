<template>
  <div>
    <PlayerPreview :player="advisor"/>
    <Card :card="null"/>
    <Card :card="null"/>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorCardChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import Card from '@/components/ui/Card.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorCardChoiceInnerGameState);

const advisor = computed(() => props.game.players.find(p => p.name === gameState.value.advisor_name)!);
</script>