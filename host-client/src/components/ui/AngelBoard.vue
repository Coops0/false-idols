<template>
  <div class="relative w-full">
    <img class="object-scale-down" :src="BoardImage" ref="angelBoardImageEl"/>
    <GameCardTransition>
      <PlayedGameCard
          v-for="(card, index) in positiveCards"
          :key="card.id"
          class="absolute"
          :card
          :font-size
          variant="angel"
          :style="{
              top: `${topOffset}px`,
              left: `${leftOffset.initial + (leftOffset.offset * index)}px`,
              width: `${cardWidth}px`
            }"
      />
    </GameCardTransition>

    <TransitionGroup name="tracker">
      <img
          v-for="i in props.failedElections"
          :key="i"
          class="absolute object-scale-down"
          :style="{
            top: `${topTrackerOffset}px`,
            left: `${leftTrackerOffset.initial + (leftTrackerOffset.offset * (i - 1))}px`,
            width: `${trackerSize}px`,
            height: `${trackerSize}px`,
        }"
          :src="FailedElectionTracker"
      />
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import { computed, useTemplateRef } from 'vue';
import BoardImage from '@/assets/board/board-angel.png';
import FailedElectionTracker from '@/assets/board/board-tracker.png';
import { useElementBounds } from '@/util/element-bounds.composable.ts';
import GameCardTransition from '@/components/ui/GameCardTransition.vue';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';
import type { Card } from '@/game/state.ts';

// 1407x541

const props = defineProps<{
  cards: Card[];
  playersSize: number;
  failedElections: number;
}>();

const positiveCards = computed(() => props.cards.filter(card => card.consequence === 'POSITIVE'));

const angelBoardImageEl = useTemplateRef('angelBoardImageEl');
const bounds = useElementBounds(angelBoardImageEl);

const topOffset = computed(() => bounds.value.height / 3.55);
const leftOffset = computed(() => {
  const w = bounds.value.width;
  return { initial: w / 5.95, offset: w / 7.1 };
});

const cardWidth = computed(() => {
  const b = bounds.value;
  return b.width / 9.5;
});

const topTrackerOffset = computed(() => bounds.value.height * (4.9 / 6));

const leftTrackerOffset = computed(() => {
  const w = bounds.value.width;
  return { initial: w / 2.985, offset: w / 10.385 };
});

const trackerSize = computed(() => {
  const b = bounds.value;
  return b.width / 27.5;
});

const fontSize = computed(() => {
  const w = cardWidth.value;
  return w / 10;
});
</script>

<style>
.tracker-enter-active, .tracker-leave-active, .tracker-move {
  transition: all 0.20s ease-out;
}

.tracker-enter-from, .tracker-leave-to {
  opacity: 0;
  transform: scale(5);
}

.tracker-leave-to {
  filter: brightness(500%);
}

.tracker-enter-to {
  opacity: 1;
  transform: scale(1);
}
</style>