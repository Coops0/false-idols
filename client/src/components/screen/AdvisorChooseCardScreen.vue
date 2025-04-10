<template>
  <div class="size-full flex flex-col items-center justify-center">
    <p class="text-xl text-gray-600 text-center">
      Choose one card to <span class="font-bold text-green-600">play</span>
    </p>

    <div class="mt-6 flex items-center flex-row flex-wrap justify-evenly gap-6 relative">
      <PlayedGameCard
          v-for="card in gameState.cards"
          :key="card.id"
          :card
          class="w-30"
          @click="() => choose(card)"
      />
    </div>

    <p class="mt-14 text-xs text-gray-800 font-bold text-center">You cannot show anyone this screen</p>
    <p v-if="gameState.vetoable"
       class="mt-4 text-xs text-blue-800 font-bold text-center drop-shadow-red-blue/80 drop-shadow-lg">
      You may request a veto (out loud), and if the president agrees, all cards will be discarded.
    </p>
  </div>
</template>

<script lang="ts" setup>
import type { AdvisorChooseCardGameState, Game } from '@/game';
import { computed } from 'vue';
import type { Card } from '@/game/messages.ts';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as AdvisorChooseCardGameState);
const emit = defineEmits<{ choose: [cardId: number] }>();

function choose(card: Card) {
  emit('choose', card.id);
}
</script>