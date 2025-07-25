<template>
  <div class="h-full flex flex-col m-2 gap-2">
    <div class="flex items-center justify-center">
      <div class="text-center">
        <h1 :class="game.winner === 'ANGEL' ? 'text-blue-600' : 'text-red-600'"
            class="text-9xl font-amazonia font-black mb-2">
          {{ game.winner === 'ANGEL' ? 'ANGELS WIN' : 'DEMONS WIN' }}
        </h1>
        <p class="text-lg font-amazonia text-gray-800 font-bold">{{ reasonText }}</p>
      </div>
    </div>

    <div class="flex size-full flex-grow flex-row justify-evenly gap-16 px-20 py-12">
      <div class="flex flex-col gap-4 basis-1/2">
        <p class="text-5xl font-amazonia font-bold text-red-500">Satan</p>
        <div class="flex items-center basis-full">
          <PlayerPreview :player="satan" class="relative" icon-variant="satan" ignore-modifiers size="2xl"/>
        </div>
      </div>

      <div v-if="demons.length" class="flex flex-col gap-4 basis-1/2">
        <p class="text-4xl font-amazonia font-semibold text-red-600 text-right">Demons</p>
        <div class="flex flex-row flex-wrap items-center basis-full justify-end">
          <PlayerPreview v-for="player in demons" :key="player.name" :player class="relative" icon-variant="demon"
                         ignore-modifiers size="xl"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type GameOverGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

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
      return 'Satan was elected after enough negative cards were played';
    case 'SATAN_KILLED':
      return 'Satan was killed';
  }
});

const demons = computed(() => props.game.demons.map(d => props.game.players.find(p => p.name === d)!));
const satan = computed(() => props.game.players.find(p => p.name === props.game.satan)!);
</script>