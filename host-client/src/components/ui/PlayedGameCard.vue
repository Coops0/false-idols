<template>
  <li class="flex justify-center items-center">
    <img :src="cardImg" class="object-scale-down"/>
    <div class="h-5/7 w-3/5 absolute overflow-hidden">
      <span
          :style="{ fontSize: `${fontSize}px` }"
          class="text-gray-700 line-clamp-6 break-all font-serif"
      >{{ card.description }}</span>
    </div>
  </li>
</template>

<script lang="ts" setup>
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import NeutralCard from '@/assets/cards/neutral-card.png';
import { computed } from 'vue';
import { type Card, CardConsequence } from '@/game/state.ts';

const props = defineProps<{
  card: Card;
  fontSize: number;
}>();

const cardImg = computed(() => {
  switch (props.card.consequence) {
    case CardConsequence.NEGATIVE:
      return NegativeCard;
    case CardConsequence.POSITIVE:
      return PositiveCard;
    case CardConsequence.NEUTRAL:
      return NeutralCard;
  }
});
</script>