<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-4xl">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-800 text-center">Advisor's Choice</h1>
        <p class="text-gray-600 text-center mt-2">
          Choose one card to <span class="font-bold text-green-600">play</span>
        </p>
      </template>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
            v-for="card in gameState.cards"
            :key="card.id"
            class="cursor-pointer transition-all duration-200 hover:scale-105"
            @click="() => choose(card)"
        >
          <CardPreview :card="card"/>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import type { AdvisorChooseCardGameState, Game } from '@/game';
import { computed } from 'vue';
import type { Card } from '@/game/messages.ts';
import CardPreview from '@/components/ui/CardPreview.vue';
import BaseCard from '@/components/ui/BaseCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as AdvisorChooseCardGameState);
const emit = defineEmits<{ choose: [cardId: number] }>();

function choose(card: Card) {
  emit('choose', card.id);
}
</script>