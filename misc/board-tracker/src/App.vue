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
  { id: 1, description: 'Card 1', consequence: 'NEGATIVE' },
  { id: 2, description: 'Card one two three four five six seven dasdiuh asduih asdhuo asduh asdihuas diuhasd hasihdu asdiuh asiudhasidhas iduhasidu sahd iash ', consequence: 'POSITIVE' },
]);

const failedElections = ref(1);
const playersSize = ref(6);

function addPositiveCard() {
  const newCard: Card = {
    id: Math.random(),
    description: 'Card',
    consequence: 'POSITIVE'
  };

  cards.value = [...cards.value, newCard];
}

function addNegativeCard() {
  const newCard: Card = {
    id: Math.random(),
    description: 'Card',
    consequence: 'NEGATIVE'
  };

  cards.value = [...cards.value, newCard];
}

function removePositiveCard() {
  const card = cards.value.find(card => card.consequence === 'POSITIVE');
  if (card) {
    cards.value = cards.value.filter(c => c.id !== card.id);
  }
}

function removeNegativeCard() {
  const card = cards.value.find(card => card.consequence === 'NEGATIVE');
  if (card) {
    cards.value = cards.value.filter(c => c.id !== card.id);
  }
}

function updatePlayers(num: number) {
  playersSize.value = Math.max(4, playersSize.value + num);
}

function updateElections(num: number) {
  failedElections.value = Math.min(4, Math.max(0, failedElections.value + num));
}
</script>