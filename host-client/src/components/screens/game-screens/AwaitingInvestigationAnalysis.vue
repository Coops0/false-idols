<template>
  <div class="flex flex-col items-center">
    <div class="flex items-center gap-6">
      <div class="flex flex-col items-center">
        <div class="relative">
          <PlayerPreview :player="president" size="xl"/>
        </div>
      </div>

      <div class="flex items-center">
        <p class="text-center text-2xl text-gray-700">is investigating</p>
      </div>

      <div class="flex flex-col items-center">
        <div class="relative">
          <PlayerPreview :player="target" size="xl"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingInvestigationAnalysisInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingInvestigationAnalysisInnerGameState);

const president = computed(() => props.game.players.find(p => p.is_president)!);
const target = computed(() => props.game.players.find(p => p.name === gameState.value.target)!);
</script>