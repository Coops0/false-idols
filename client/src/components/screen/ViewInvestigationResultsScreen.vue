<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-md">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-800 text-center">Investigation Results</h1>
      </template>

      <div class="space-y-6">
        <div class="text-center space-y-2">
          <h2 class="text-xl font-semibold text-gray-700">{{ gameState.player.name }}</h2>
          <p class="text-lg text-gray-600">
            Role: <span class="font-medium">{{ roleName(gameState.role) }}</span>
          </p>
        </div>

        <div class="flex justify-center">
          <BaseButton variant="primary" @click="() => emit('confirm')">
            Continue
          </BaseButton>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import type { Game, ViewInvestigationResultsGameState } from '@/game';
import { roleName } from '@/game/messages.ts';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewInvestigationResultsGameState);
</script>