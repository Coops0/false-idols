<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <BaseCard class="w-full max-w-4xl mx-4">
      <template #header>
        <h1 class="text-xl md:text-2xl font-bold text-gray-800 text-center">Play a Card</h1>
        <p class="text-sm md:text-base text-gray-600 text-center mt-2">
          Choose one card to <span class="font-bold text-green-600">play</span>
        </p>
      </template>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
        <div
            v-for="card in gameState.cards"
            :key="card.id"
            class="cursor-pointer active:scale-95 transition-transform"
            @click="() => choose(card)"
        >
          <CardPreview :card="card"/>
        </div>
      </div>

      <div v-if="gameState.vetoable" class="mt-6">
        <p class="text-xs md:text-sm text-gray-800 font-bold">You cannot show anyone this screen</p>
        <p class="text-sm md:text-base text-gray-700 font-extrabold text-center">
          You may request a veto (out loud), and if the president agrees, all cards will be discarded.
        </p>
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