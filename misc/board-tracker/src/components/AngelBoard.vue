<template>
  <div class="relative w-full">
    <img class="object-scale-down" :src="BoardImage" ref="angelBoardImageEl"/>
    <PlayedGameCard
        v-for="(card, index) in positiveCards"
        :key="index"
        :card
        class="absolute"
        :style="{
          top: `${topOffset}px`,
          left: `${leftOffset.initial + (leftOffset.offset * index)}px`,
          width: `${cardSize.width}px`,
          height: `${cardSize.height}px`,
        }"
    />
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import { computed, useTemplateRef } from 'vue';
import BoardImage from '@/assets/board/board-angel.png';
import PlayedGameCard from '@/components/PlayedGameCard.vue';
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

const topOffset = computed(() => bounds.value.height / 3.7);
const leftOffset = computed(() => {
  const w = bounds.value.width;
  return { initial: w / 6, offset: w / 6.95 };
});

const cardSize = computed(() => {
  const b = bounds.value;
  return { width: b.width / 9.5, height: b.height / 2.2 };
});
</script>