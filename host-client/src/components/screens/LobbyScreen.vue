<template>
  <div class="h-full flex flex-col">
    <div class="h-32 mb-4">
      <ModernCard class="h-full flex items-center justify-center">
        <div class="text-center">
          <h1 class="text-3xl font-bold text-gray-900 mb-2 tracking-tight">False Idols</h1>
          <p class="text-gray-600 text-sm mb-2">Join at</p>
          <div class="p-3 bg-gray-50/50 backdrop-blur-sm rounded-lg inline-block border border-gray-100/50">
            <code class="text-lg font-mono text-gray-900">{{ domain }}</code>
          </div>
        </div>
      </ModernCard>
    </div>

    <div class="flex-1 min-h-0">
      <ModernCard class="h-full">
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 h-full">
          <div v-for="player in game.players" :key="player.name"
               class="group relative bg-white/50 backdrop-blur-sm rounded-lg p-4 border border-gray-100/50">
            <div class="flex items-center gap-3">
              <div class="w-12 h-12 rounded-xl overflow-hidden border-2 border-gray-100 shadow-sm">
                <img :alt="player.name" :src="PlayerIcon.normal(player.icon)" class="w-full h-full object-cover"/>
              </div>
              <div>
                <h3 class="text-base font-medium text-gray-900">{{ player.name }}</h3>
              </div>
            </div>
          </div>
        </div>
      </ModernCard>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { LobbyGameState } from '@/game/state.ts';
import { computed } from 'vue';
import ModernCard from '@/components/ui/ModernCard.vue';
import { PlayerIcon } from '@/game/player-icon.ts';

defineProps<{ game: LobbyGameState }>();
const domain = computed(() => window?.location?.hostname ?? 'LOADING');
</script>