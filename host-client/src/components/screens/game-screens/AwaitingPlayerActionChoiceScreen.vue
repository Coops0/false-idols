<template>
  <div>
    <PlayerPreview :player="chief"/>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefActionChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefActionChoiceInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>