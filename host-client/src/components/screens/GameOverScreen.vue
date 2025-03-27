<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center space-y-4">
        <h1 :class="[
          game.winner === 'ANGELS' ? 'text-blue-600' : 'text-red-600'
        ]" class="text-4xl font-bold">
          {{ game.winner === 'ANGELS' ? 'Divine Victory' : 'Demonic Triumph' }}
        </h1>
        <p class="text-xl text-amber-900">{{ reasonText }}</p>
      </div>
    </DivineCard>

    <DivineCard>
      <div class="space-y-6">
        <div class="text-center">
          <h2 class="text-2xl font-semibold text-amber-900 mb-4">The Fallen</h2>
          <div class="flex flex-col items-center gap-4">
            <div class="relative">
              <div class="w-24 h-24 rounded-full overflow-hidden border-4 border-red-600 shadow-lg">
                <img :alt="satan.name" :src="satan.icon" class="w-full h-full object-cover"/>
              </div>
              <div
                  class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-red-600 text-white px-3 py-1 rounded-full text-sm font-medium">
                Satan
              </div>
            </div>
            <p class="text-lg text-amber-900">{{ satan.name }}</p>
          </div>
        </div>

        <div v-if="demons.length" class="text-center">
          <h3 class="text-xl font-semibold text-amber-900 mb-4">The Demons</h3>
          <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
            <div v-for="player in demons" :key="player.name" class="relative">
              <div class="w-20 h-20 rounded-full overflow-hidden border-4 border-red-500 shadow-lg">
                <img :alt="player.name" :src="player.icon" class="w-full h-full object-cover"/>
              </div>
              <div
                  class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-red-500 text-white px-3 py-1 rounded-full text-sm font-medium">
                {{ player.name }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import { type GameOverGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

const props = defineProps<{ game: GameOverGameState }>();

const reasonText = computed(() => {
  switch (props.game.reason) {
    case 'NEGATIVE_THRESHOLD_REACHED':
      return 'The demons played enough negative cards';
    case 'POSITIVE_THRESHOLD_REACHED':
      return 'The angels played enough positive cards';
    case 'ALL_ANGELS_DEAD':
      return 'All angels were killed';
    case 'SATAN_ELECTED_ADVISOR_LATE_GAME':
      return 'Satan was elected as advisor';
    case 'SATAN_KILLED':
      return 'Satan was killed';
  }
});

const demons = computed(() => props.game.demons.map(d => props.game.players.find(p => p.name === d)!));
const satan = computed(() => props.game.players.find(p => p.name === props.game.satan)!);
</script>