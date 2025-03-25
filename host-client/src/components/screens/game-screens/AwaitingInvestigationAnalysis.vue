<template>
  <div>
    <PlayerPreview :player="chief"/>
    <p>is investigating</p>
    <PlayerPreview :player="target"/>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingInvestigationAnalysisInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingInvestigationAnalysisInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
const target = computed(() => props.game.players.find(p => p.name === gameState.value.target)!);
</script>