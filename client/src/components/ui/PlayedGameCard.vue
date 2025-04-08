<template>
  <li class="flex justify-center items-center relative" :class="card.consequence === CardConsequence.NEUTRAL && 'bg-gray-300'">
    <img v-if="card.consequence !== CardConsequence.NEUTRAL" :src="cardImg" class="object-scale-down" />
    <div class="h-5/7 w-2/9 absolute overflow-hidden">
      <span
          class="text-gray-700 line-clamp-6 break-all font-serif text-sm"
      >{{ card.description }}</span>
    </div>
  </li>
</template>

<script setup lang="ts">
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import { computed } from 'vue';
import { type Card, CardConsequence } from '@/game/messages.ts';

const props = defineProps<{ card: Card; }>();

const cardImg = computed(() => props.card.consequence === CardConsequence.NEGATIVE ? NegativeCard : PositiveCard);
</script>