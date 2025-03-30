<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-md">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-800 text-center">Investigation</h1>
      </template>

      <div class="space-y-6">
        <div class="text-center space-y-2">
          <PlayerPreview :player="gameState.player" :game="props.game"
                         :icon-variant="gameState.role === Role.DEMON ? 'demon' : 'angel'" class="w-40 h-40 mx-auto"/>
          <p class="text-lg"
             :class="gameState.role === Role.DEMON ? 'text-red-500 font-bold' : 'font-medium text-blue-400'">
            {{ roleName(gameState.role) }}</p>
        </div>

        <div class="text-center space-y-2">
          <p class="text-sm text-gray-800 font-bold">You cannot show anyone this screen</p>
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
import { Role, roleName } from '@/game/messages.ts';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewInvestigationResultsGameState);
</script>