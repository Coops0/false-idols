<template>
  <div>
    <UnlockNotification :show="investigationState === true" color="text-blue-500">
      Investigation ability is now available
    </UnlockNotification>

    <UnlockNotification :show="killState === true" color="text-red-300">
      Kill ability is now available
    </UnlockNotification>

    <UnlockNotification :show="showSatanElectionWinCase === true" color="text-red-500">
      Satan will win if successfully elected as advisor
    </UnlockNotification>
  </div>
</template>

<script setup lang="ts">
import type { GameState } from '@/game/state.ts';
import { ref, watch } from 'vue';
import UnlockNotification from '@/components/ui/UnlockNotification.vue';

const INVESTIGATION_THRESHOLD = 4;
const KILL_THRESHOLD = 8;
const SATAN_ELECTION_WIN_THRESHOLD = -2;

const props = defineProps<{ game: GameState | null }>();

// null = not shown at all
// true = show right now
// false = shown and hidden
const investigationState = ref<boolean | null>(null);
const killState = ref<boolean | null>(null);
const showSatanElectionWinCase = ref<boolean | null>(null);

let previousAbsolutePoints: number | null = null;
let previousPoints: number | null = null;

watch(() => props.game, g => {
  // Reset all local stuff since new game
  if (!g || g.type !== 'game_in_progress') {
    investigationState.value = null;
    killState.value = null;
    showSatanElectionWinCase.value = null;
    previousAbsolutePoints = null;
    previousPoints = null;
    return;
  }

  const prevPoints = previousPoints;
  const currentPoints = g.deck.played_cards.reduce((acc, card) => acc + card.consequence, 0);

  const prevAbsolutePoints = previousAbsolutePoints;
  const absolutePoints = g.deck.played_cards.reduce((acc, card) => acc + Math.abs(card.consequence), 0);

  previousPoints = currentPoints;
  previousAbsolutePoints = absolutePoints;

  // We just refreshed or something, don't fire off either of these
  if (prevAbsolutePoints === null || prevPoints === null) {
    return;
  }

  // Absolute points meet threshold && previous points did not && we haven't already shown notification

  if (absolutePoints >= INVESTIGATION_THRESHOLD && prevAbsolutePoints < INVESTIGATION_THRESHOLD && investigationState.value === null) {
    investigationState.value = true;
    setTimeout(() => (investigationState.value = false), 5000);
  }

  if (absolutePoints >= KILL_THRESHOLD && prevAbsolutePoints < KILL_THRESHOLD && killState.value === null) {
    killState.value = true;
    setTimeout(() => (killState.value = false), 5000);
  }

  if (currentPoints <= SATAN_ELECTION_WIN_THRESHOLD) {
    if (prevPoints > SATAN_ELECTION_WIN_THRESHOLD && showSatanElectionWinCase.value === null) {
      showSatanElectionWinCase.value = true;
      setTimeout(() => (showSatanElectionWinCase.value = false), 5000);
    }
  } else {
    // Reset since points can fluctuate
    showSatanElectionWinCase.value = null;
  }
});
</script>