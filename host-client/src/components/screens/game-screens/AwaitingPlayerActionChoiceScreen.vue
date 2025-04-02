<template>
  <div class="p-10">
    <div class="text-center">
      <div class="flex justify-center mb-4">
        <div class="relative">
          <PlayerCard :player="chief" size="xl"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefActionChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefActionChoiceInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>