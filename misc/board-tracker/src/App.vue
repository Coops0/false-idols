<template>
  <div>
    <button @click="addPositiveCard">+ Positive Card</button>
    <button @click="removePositiveCard">- Positive Card</button>
    <button @click="addNegativeCard">Add Negative Card</button>
    <button @click="removeNegativeCard">Remove Negative Card</button>

    <button @click="() => failedElections++">+ Failed Elections</button>
    <button @click="() => failedElections--">- Failed Elections</button>

    <button @click="() => playersSize++">+ Players</button>
    <button @click="() => playersSize--">- Players</button>
  </div>
  <div>
    <AngelBoard :cards :players-size :failed-elections/>
    <DemonBoard :cards :players-size/>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import AngelBoard from '@/components/AngelBoard.vue';
import DemonBoard from '@/components/DemonBoard.vue';

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
</script>