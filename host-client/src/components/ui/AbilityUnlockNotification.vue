<template>
  <div>
    <UnlockNotification :show="investigationState === true" color="text-blue-500">
      Investigation ability is now available
    </UnlockNotification>

    <UnlockNotification :show="killState === true" color="text-red-500">
      Kill ability is now available
    </UnlockNotification>
  </div>
</template>

<script setup lang="ts">
import type { GameState } from '@/game/state.ts';
import { ref, watch } from 'vue';
import UnlockNotification from '@/components/ui/UnlockNotification.vue';

const INVESTIGATION_THRESHOLD = 4;
const KILL_THRESHOLD = 8;

const props = defineProps<{ game: GameState | null }>();

// null = not shown at all
// true = show right now
// false = shown and hidden
const investigationState = ref<boolean | null>(null);
const killState = ref<boolean | null>(null);

let previousAbsolutePoints: number | null = null;

watch(() => props.game, g => {
  // Reset all local stuff since new game
  if (!g || g.type !== 'game_in_progress') {
    investigationState.value = null;
    killState.value = null;
    previousAbsolutePoints = null;
    return;
  }

  const prevPoints = previousAbsolutePoints;
  const absolutePoints = g.deck.played_cards.reduce((acc, card) => acc + Math.abs(card.consequence), 0);

  previousAbsolutePoints = absolutePoints;

  // We just refreshed or something, don't fire off either of these
  if (prevPoints === null) {
    return;
  }

  // Absolute points meet threshold && previous points did not && we haven't already shown notification

  if (absolutePoints >= INVESTIGATION_THRESHOLD && prevPoints < INVESTIGATION_THRESHOLD && investigationState.value === null) {
    investigationState.value = true;
    setTimeout(() => (investigationState.value = false), 5000);
  }

  if (absolutePoints >= KILL_THRESHOLD && prevPoints < KILL_THRESHOLD && killState.value === null) {
    killState.value = true;
    setTimeout(() => (killState.value = false), 5000);
  }
});
</script>