<template>
  <div>
    <UnlockNotification :show="showSatanElectionWinCase === true" color="border-red-500">
      Satan will win if successfully elected as advisor
    </UnlockNotification>
  </div>
</template>

<script lang="ts" setup>
import type { GameState } from '@/game/state.ts';
import { ref, watch } from 'vue';
import UnlockNotification from '@/components/ui/UnlockNotification.vue';

const NEGATIVE_COUNT_SATAN_ELECTION_WIN = 3;

const props = defineProps<{ game: GameState | null }>();

// null = not shown at all
// true = show right now
// false = shown and hidden
const showSatanElectionWinCase = ref<boolean | null>(null);

let storedPreviousPositivePoints: number | null = null;
let storedPreviousNegativeCards: number | null = null;

watch(() => props.game, g => {
  // Reset all local stuff since new game
  if (!g || g.type !== 'game_in_progress') {
    showSatanElectionWinCase.value = null;
    storedPreviousPositivePoints = 0;
    storedPreviousNegativeCards = 0;
    return;
  }

  const prevNegativeCards = storedPreviousNegativeCards;
  const negativeCards = g.deck.negative_cards_played;

  const prevPositiveCards = prevNegativeCards;
  const positiveCards = g.deck.positive_cards_played;

  storedPreviousNegativeCards = negativeCards;
  storedPreviousPositivePoints = positiveCards;

  // We just refreshed or something, don't fire off either of these
  if (prevNegativeCards === null || prevPositiveCards === null) {
    return;
  }

  if (negativeCards >= NEGATIVE_COUNT_SATAN_ELECTION_WIN && showSatanElectionWinCase.value === null) {
    showSatanElectionWinCase.value = true;
    setTimeout(() => (showSatanElectionWinCase.value = false), 5000);
  }
}, { deep: true, immediate: true });
</script>