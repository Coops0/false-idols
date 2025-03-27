<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="text-center space-y-6">
        <div class="flex justify-center">
          <div class="relative">
            <div class="w-32 h-32 rounded-full overflow-hidden border-4 border-amber-400 shadow-lg">
              <img :alt="chief.name" :src="chief.icon" class="w-full h-full object-cover"/>
            </div>
            <div
                class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-amber-500 text-white px-4 py-1 rounded-full text-lg font-medium">
              {{ chief.name }}
            </div>
            <div
                class="absolute -top-2 -right-2 w-6 h-6 bg-amber-500 rounded-full flex items-center justify-center text-white text-xs font-bold">
              👑
            </div>
          </div>
        </div>
        <p class="text-xl text-amber-700">The current chief</p>
      </div>
    </DivineCard>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import DivineCard from '@/components/ui/DivineCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>