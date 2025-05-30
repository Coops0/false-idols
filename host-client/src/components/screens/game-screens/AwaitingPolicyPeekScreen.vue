<template>
  <div class="flex flex-col items-center">
    <div class="flex justify-center items-center flex-col">
      <div class="relative">
        <PlayerPreview :player="president" size="2xl"/>
      </div>
    </div>
    <p class="text-gray-700 font-amazonia font-light text-center text-2xl">Policy peeking</p>
  </div>
</template>

<script setup lang="ts">

import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import type { InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();

const president = computed(() => props.game.players.find(p => p.is_president)!);
</script>