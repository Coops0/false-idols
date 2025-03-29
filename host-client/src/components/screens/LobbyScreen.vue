<template>
  <div class="space-y-8">
    <ModernCard>
      <div class="text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-3 tracking-tight">False Idols</h1>
        <p class="text-gray-600 text-lg mb-4">Join at</p>
        <div class="mt-2 p-4 bg-gray-50/50 backdrop-blur-sm rounded-xl inline-block border border-gray-100/50">
          <code class="text-xl font-mono text-gray-900">{{ domain }}</code>
        </div>
      </div>
    </ModernCard>

    <ModernCard>
      <div class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="player in game.players" :key="player.name"
               class="group relative bg-white/50 backdrop-blur-sm -z-1 rounded-xl p-6 border border-gray-100/50 transition-all duration-300">
            <div class="flex items-center gap-4">
              <div class="w-16 h-16 rounded-2xl overflow-hidden border-2 border-gray-100 shadow-sm">
                <img :alt="player.name" :src="PlayerIcon.normal(player.icon)" class="w-full h-full object-cover"/>
              </div>
              <div>
                <h3 class="text-lg font-medium text-gray-900">{{ player.name }}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ModernCard>
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