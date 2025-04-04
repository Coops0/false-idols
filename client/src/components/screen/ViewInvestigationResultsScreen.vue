<template>
  <div class="min-h-screen flex items-center justify-center">
    <BaseCard class="w-full max-w-md mx-4">
      <template #header>
        <h1 class="text-xl md:text-2xl font-bold text-gray-800 text-center">Investigation</h1>
      </template>

      <div v-if="!gameState.hasConfirmed" class="space-y-6">
        <div class="text-center space-y-4">
          <p class="text-gray-600 text-sm md:text-base font-bold">
            You are about to see if {{ gameState.player.name }} is a demon or angel. You cannot show this screen to
            anyone.
          </p>
        </div>

        <div class="flex justify-center">
          <BaseButton class="px-6 py-3 text-base" variant="primary" @click="() => emit('confirm')">
            Continue
          </BaseButton>
        </div>
      </div>

      <div v-else class="space-y-6">
        <div class="text-center space-y-3">
          <PlayerPreview
              :game="props.game"
              :icon-variant="gameState.role === Role.DEMON ? 'demon' : 'angel'"
              :player="gameState.player"
              class="w-32 h-32 sm:w-40 sm:h-40 mx-auto"
          />
          <p :class="gameState.role === Role.DEMON ? 'text-red-500 font-bold' : 'font-medium text-blue-400'"
             class="text-base md:text-lg">
            {{ roleName(gameState.role) }}
          </p>
        </div>

        <div class="text-center space-y-2">
          <p class="text-xs md:text-sm text-gray-800 font-bold">You cannot show anyone this screen</p>
        </div>

        <div class="flex justify-center">
          <BaseButton class="px-6 py-3 text-base" variant="primary" @click="() => emit('confirm')">
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