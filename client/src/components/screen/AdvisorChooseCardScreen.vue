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
          :selected="selectedCard === card.id"
          class="w-30"
          @click="() => select(card)"
      />
    </div>

    <BaseButton
        class="mt-8"
        variant="primary"
        :disabled="selectedCard === null"
        @click="choose">
      Submit
    </BaseButton>

    <div class="mt-14 mx-auto space-y-4">
      <Alert type="info" size="large">
        You may request a veto (out loud), and if the president agrees, all cards will be discarded.
      </Alert>

      <Alert type="error">
        You cannot say <span class="font-bold">anything</span> until a card is played
      </Alert>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AdvisorChooseCardGameState, Game } from '@/game';
import { computed, ref, watch } from 'vue';
import type { Card } from '@/game/messages.ts';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';
import BaseButton from '@/components/ui/BaseButton.vue';
import Alert from '@/components/ui/Alert.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as AdvisorChooseCardGameState);
const emit = defineEmits<{ choose: [cardId: number] }>();

const selectedCard = ref<number | null>(null);
watch(gameState, () => (selectedCard.value = null), { deep: true });

function select(card: Card) {
  selectedCard.value = card.id;
}

function choose() {
  if (selectedCard.value !== null) {
    emit('choose', selectedCard.value);
  }
}
</script>