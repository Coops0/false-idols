<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center">
        <h1 class="text-3xl font-bold text-amber-900 mb-2">False Idols</h1>
        <p class="text-amber-700">Join at</p>
        <div class="mt-2 p-3 bg-amber-100 rounded-lg inline-block">
          <code class="text-lg font-mono text-amber-900">{{ domain }}</code>
        </div>
      </div>
    </DivineCard>

    <DivineCard>
      <div class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="player in game.players" :key="player.name" class="relative group">
            <div
                class="absolute inset-0 bg-gradient-to-br from-amber-100 to-amber-200 rounded-lg shadow-lg transform transition-all duration-300"/>
            <div class="relative p-4 border-2 border-amber-300 rounded-lg bg-gradient-to-br from-amber-50 to-amber-100">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-full overflow-hidden border-2 border-amber-400 shadow-lg">
                  <img :alt="player.name" :src="player.icon" class="w-full h-full object-cover"/>
                </div>
                <div>
                  <h3 class="font-medium text-amber-900">{{ player.name }}</h3>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import type { LobbyGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

defineProps<{ game: LobbyGameState }>();
const domain = computed(() => window?.location?.hostname ?? 'LOADING');
</script>