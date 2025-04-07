<template>
  <div class="flex justify-center items-center">
    <img :src="cardImg" class="object-scale-down" />
    <div class="h-5/7 w-3/5 absolute overflow-hidden">
      <span
          class="text-gray-700 line-clamp-6 break-all"
          :style="{ fontSize: `${fontSize}px` }"
      >{{ card.description }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import { computed } from 'vue';

const props = defineProps<{
  card: Card;
  variant: 'demon' | 'angel';
  fontSize: number;
}>();

const cardImg = computed(() => props.variant === 'demon' ? NegativeCard : PositiveCard);
</script>