<template>
  <div class="relative w-full">
    <img class="object-scale-down" :src="boardImage" ref="demonBoardImageEl"/>
    <GameCardTransition>
      <PlayedGameCard
          v-for="(card, index) in negativeCards"
          :key="index"
          :card
          class="absolute"
          :font-size
          :style="{
            top: `${topOffset}px`,
            left: `${leftOffset.initial + (leftOffset.offset * index)}px`,
            width: `${cardWidth}px`
        }"
      />
    </GameCardTransition>
  </div>
</template>

<script setup lang="ts">
import { computed, useTemplateRef } from 'vue';
import Board56Image from '@/assets/board/board-demon-5-6.png';
import Board78Image from '@/assets/board/board-demon-7-8.png';
import Board910Image from '@/assets/board/board-demon-9-10.png';
import { useElementBounds } from '@/util/element-bounds.composable.ts';
import type { Card } from '@/game/state.ts';
import GameCardTransition from '@/components/ui/GameCardTransition.vue';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';

const props = defineProps<{
  cards: Card[];
  playersSize: number;
}>();

const negativeCards = computed(() => props.cards.filter(card => card.consequence === 'NEGATIVE'));
const boardVariant = computed<1 | 2 | 3>(() => {
  const s = props.playersSize;
  if (s === 4 || s === 5 || s === 6) return 1;
  if (s === 7 || s === 8) return 2;
  return 3;
});

// all 1601 x 539
const boardImage = computed(() => {
  switch (boardVariant.value) {
    case 1:
      return Board56Image;
    case 2:
      return Board78Image;
    case 3:
      return Board910Image;
  }
});

const demonBoardImageEl = useTemplateRef('demonBoardImageEl');
const bounds = useElementBounds(demonBoardImageEl);

const topOffset = computed(() => bounds.value.height / 3.7);
const leftOffset = computed(() => {
  const w = bounds.value.width;
  return { initial: w / 11.5, offset: w / 6.95 };
});

const cardWidth = computed(() => {
  const b = bounds.value;
  return b.width / 9.5;
});

const fontSize = computed(() => {
  const w = cardWidth.value;
  return w / 10;
});
</script>