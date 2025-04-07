<template>
  <div class="relative w-full">
    <img class="object-scale-down" :src="BoardImage" ref="angelBoardImageEl"/>
    <PlayedGameCard
        v-for="(card, index) in positiveCards"
        :key="index"
        :card
        :font-size
        variant="angel"
        class="absolute"
        :style="{
          top: `${topOffset}px`,
          left: `${leftOffset.initial + (leftOffset.offset * index)}px`,
          width: `${cardWidth}px`
        }"
    />

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
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import { computed, useTemplateRef } from 'vue';
import BoardImage from '@/assets/board/board-angel.png';
import PlayedGameCard from '@/components/PlayedGameCard.vue';
import FailedElectionTracker from '@/assets/board/board-tracker.png';
import { useElementBounds } from '@/util/element-bounds.composable.ts';

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
  return { initial: w / 5.5, offset: w / 7.1 };
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