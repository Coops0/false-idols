<template>
  <li class="flex justify-center items-center relative">
    <img :src="cardImg" class="object-scale-down" :class="props.selected && 'border-2 border-blue-500'"/>
    <div class="h-5/7 w-4/9 absolute overflow-hidden">
      <span
          class="text-gray-700 line-clamp-6 break-all font-serif text-xs"
      >{{ card.description }}</span>
    </div>
  </li>
</template>

<script lang="ts" setup>
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import NeutralCard from '@/assets/cards/neutral-card.png';
import { computed } from 'vue';
import { type Card, CardConsequence } from '@/game/messages.ts';

const props = defineProps<{ card: Card; selected?: boolean; }>();

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