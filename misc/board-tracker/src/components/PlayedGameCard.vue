<template>
  <div class="flex justify-center items-center">
    <img :src="cardImg" class="object-scale-down" />
    <div class="h-5/7 w-3/5 flex absolute overflow-hidden">
      <p
          class="text-gray-700"
          :style="{ fontSize: `${fontSize}px` }"
      >{{ card.description }}</p>
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