<template>
  <li class="flex justify-center items-center">
    <img :src="cardImg" class="object-scale-down" />
    <div class="h-5/7 w-3/5 absolute overflow-hidden">
      <span
          class="text-gray-700 line-clamp-6 break-all font-serif"
          :style="{ fontSize: `${fontSize}px` }"
      >{{ card.description }}</span>
    </div>
  </li>
</template>

<script setup lang="ts">
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import { computed } from 'vue';
import { type Card, CardConsequence } from '@/game/state.ts';

const props = defineProps<{
  card: Card;
  fontSize: number;
}>();

const cardImg = computed(() => props.card.consequence === CardConsequence.NEGATIVE ? NegativeCard : PositiveCard);
</script>