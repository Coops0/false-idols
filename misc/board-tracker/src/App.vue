<template>
  <div>
    <div class="flex flex-col gap-4">
      <div class="flex flex-row gap-4">
        <div class="flex flex-col gap-1">
          <DumbButton @click="addPositiveCard">+ Positive Card</DumbButton>
          <DumbButton @click="removePositiveCard">- Positive Card</DumbButton>
        </div>
        <div class="flex flex-col gap-1">
          <DumbButton @click="addNegativeCard">+ Negative Card</DumbButton>
          <DumbButton @click="removeNegativeCard">- Negative Card</DumbButton>
        </div>
      </div>

      <div class="flex flex-row gap-4">
        <div class="flex flex-col gap-1">
          <DumbButton @click="() => updateElections(1)">+ Failed Elections</DumbButton>
          <DumbButton @click="() => updateElections(-1)">- Failed Elections</DumbButton>
          <p>{{ failedElections }}</p>
        </div>

        <div class="flex flex-col gap-1">
          <DumbButton @click="() => updatePlayers(1)">+ Players</DumbButton>
          <DumbButton @click="() => updatePlayers(-1)">- Players</DumbButton>
          {{ playersSize }}
        </div>
      </div>
    </div>
  </div>

  <div>
    <div class="relative justify-center items-center flex flex-row gap-2">
      <AngelBoard :cards :players-size :failed-elections/>
      <DemonBoard :cards :players-size/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import AngelBoard from '@/components/AngelBoard.vue';
import DemonBoard from '@/components/DemonBoard.vue';
import DumbButton from '@/components/DumbButton.vue';

export type Card = {
  id: number;
  description: string;
  consequence: 'NEUTRAL' | 'NEGATIVE' | 'POSITIVE';
}

const cards = ref<Card[]>([
  { id: 2, description: 'Card 2', consequence: 'NEGATIVE' },
  { id: 3, description: 'Card 3', consequence: 'POSITIVE' },
]);

const failedElections = ref(1);
const playersSize = ref(6);

function addPositiveCard() {
  const newCard: Card = {
    id: cards.value.length + 1,
    description: `Card ${cards.value.length + 1}`,
    consequence: 'POSITIVE'
  };

  cards.value.push(newCard);
}

function addNegativeCard() {
  const newCard: Card = {
    id: cards.value.length + 1,
    description: `Card ${cards.value.length + 1}`,
    consequence: 'NEGATIVE'
  };

  cards.value.push(newCard);
}

function removePositiveCard() {
  const index = cards.value.findIndex(card => card.consequence === 'POSITIVE');
  if (index !== -1) {
    cards.value = cards.value.filter((_, i) => i !== index);
  }
}

function removeNegativeCard() {
  const index = cards.value.findIndex(card => card.consequence === 'NEGATIVE');
  if (index !== -1) {
    cards.value = cards.value.filter((_, i) => i !== index);
  }
}

function updatePlayers(num: number) {
  playersSize.value = Math.max(4, playersSize.value + num);
}

function updateElections(num: number) {
  failedElections.value = Math.min(4, Math.max(0, failedElections.value + num));
}
</script>