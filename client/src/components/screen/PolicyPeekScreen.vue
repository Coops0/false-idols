<template>
  <div class="size-full flex flex-col items-center justify-center">
    <p class="text-sm text-gray-600 text-center">
      These are the top three cards of the deck.
    </p>

    <div class="mt-6 flex items-center flex-row flex-wrap justify-evenly gap-2 relative">
      <PlayedGameCard
          v-for="card in gameState.cards"
          :key="card.id"
          :card
          class="w-30"
      />
    </div>

    <BaseButton class="w-full" variant="primary" @click="() => emit('confirm')">
      Continue
    </BaseButton>
  </div>
</template>

<script setup lang="ts">
import { Game, type PolicyPeekGameState } from '@/game';
import { computed } from 'vue';
import BaseButton from '@/components/ui/BaseButton.vue';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as PolicyPeekGameState);
const emit = defineEmits<{ confirm: [] }>();
</script>