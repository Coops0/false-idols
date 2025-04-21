<template>
  <div class="size-full flex flex-col items-center justify-center">
    <p class="text-xl text-gray-600 text-center mt-2">
      Choose two cards to send to the advisor
    </p>

    <div class="mt-6 flex items-center flex-row flex-wrap justify-evenly gap-2 relative">
      <PlayedGameCard
          v-for="card in gameState.cards"
          :key="card.id"
          :card
          :selected="selectedCards.includes(card.id)"
          class="w-30"
          @click="() => select(card)"
      />
    </div>

    <BaseButton
        class="mt-8"
        variant="primary"
        :disabled="!canSubmit"
        @click="submit">
      Submit
    </BaseButton>

    <p class="mt-14 text-xs text-gray-800 font-bold">You cannot show anyone this screen</p>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { Game, type PresidentDiscardCardGameState } from '@/game';
import type { Card } from '@/game/messages.ts';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';
import BaseButton from '@/components/ui/BaseButton.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as PresidentDiscardCardGameState);
const emit = defineEmits<{ discard: [cardId: number] }>();

const selectedCards = ref<number[]>([]);
watch(gameState, () => (selectedCards.value = []), { deep: true });

function select(card: Card) {
  if (selectedCards.value.includes(card.id)) {
    selectedCards.value = selectedCards.value.filter(c => c !== card.id);
  } else if (selectedCards.value.length < 2) {
    selectedCards.value.push(card.id);
  }
}

const canSubmit = computed(() => selectedCards.value.length === 2);

function submit() {
  if (canSubmit.value) {
    const card = gameState.value.cards.find(c => !selectedCards.value.includes(c.id));
    emit('discard', card!!.id);
  }
}
</script>